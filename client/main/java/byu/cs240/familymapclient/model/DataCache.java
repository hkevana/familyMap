package byu.cs240.familymapclient.model;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import byu.cs240.familymapclient.res.DataRes;

public class DataCache {
    private static final String TAG = "DataCache";

    private static DataCache _instance;

    public static DataCache getInstance() {
        if (_instance == null) { _instance = new DataCache(); }
        return _instance;
    }
    private Settings settings;

    private Map<String, Person> people;
    private Map<String, Event> events;
    private Map<String, List<Event>> sortedPersonEvents;
    private Set<String> eventTypes;
    private Map<String, Float> eventTypeColors;
    private Map<String, Set<Person>> children;
    private Set<String> fatherSide;
    private List<Event> fatherSideEvents;
    private Set<String> motherSide;
    private List<Event> motherSideEvents;
    private Person user;

    private DataCache() {
        settings = Settings.getInstance();
        people = new HashMap<>();
        events = new HashMap<>();
        sortedPersonEvents = new HashMap<>();
        eventTypes = new HashSet<>();
        eventTypeColors = new HashMap<>();
        children = new HashMap<>();
        fatherSide = new HashSet<>();
        fatherSideEvents = new ArrayList<>();
        motherSide = new HashSet<>();
        motherSideEvents = new ArrayList<>();
        user = null;
    }
    public void logout() {
        people.clear();
        events.clear();
        sortedPersonEvents.clear();
        eventTypes.clear();
        eventTypeColors.clear();
        children.clear();
        fatherSideEvents.clear();
        motherSideEvents.clear();
        user.clear();
    }

    public Map<String, Person> getPeople() { return people; }
    public Map<String, Event> getEvents() { return events; }
    public Map<String, List<Event>> getSortedPersonEvents() { return sortedPersonEvents; }
    public Set<String> getEventTypes() { return eventTypes; }
    public Map<String, Float> getColors() { return eventTypeColors; }
    public Map<String, Set<Person>> getChildren() { return children; }
    public Set<String> getFatherSide() { return fatherSide; }
    public List<Event> getFatherSideEvents() { return fatherSideEvents; }
    public Set<String> getMotherSide() { return motherSide; }
    public List<Event> getMotherSideEvents() { return  motherSideEvents; }
    public Person getUser() { return user; }

    public boolean storeData(DataRes data) {
        storeEvents(data.getEventData());
        storePersons(data.getPersonData());
        storeChildren();
        storeUser(data.getUserID());
        storeFamilyEvents();
        createInitialColors();

        return this.user != null;
    }

    private void storeEvents(Event[] events) {
        for (Event e: events) { this.events.put(e.getEventID(), e); }
    }

    private void storePersons(Person[] persons) {
        for (Person p : persons) {
            this.people.put(p.getPersonID(), p);
            storeAssociatedEvents(p.getPersonID());
        }
    }

    private void storeChildren() {
        for (Person p : people.values()) {
            Person father = people.get(p.getFatherID());
            Person mother = people.get(p.getMotherID());

            Set<Person> child = new HashSet<>();
            child.add(p);
            if (father != null) {
                Set<Person> prevChildren = children.get(father.getPersonID());
                if (prevChildren != null) {
                    prevChildren.add(p);
                    children.put(father.getPersonID(), prevChildren);
                } else {
                    children.put(father.getPersonID(), child);
                }
            }
            if (mother != null) {
                Set<Person> prevChildren = children.get(mother.getPersonID());
                if (prevChildren != null) {
                    prevChildren.add(p);
                    children.put(mother.getPersonID(), prevChildren);
                } else {
                    children.put(mother.getPersonID(), child);
                }
            }
        }
    }

    private void storeUser(String userID) { this.user = this.people.get(userID); }

    private void storeAssociatedEvents(String personID) {
        Set<Event> associatedEvents = new HashSet<>();
        Map<String, Set<Event>> personEvents = new HashMap<>();
        for (Map.Entry<String, Event> event : this.events.entrySet()) {
            if (event.getValue().getPersonID().equals(personID)) { associatedEvents.add(event.getValue()); }
        }
        personEvents.put(personID, associatedEvents);
        sortLifeEvents(personEvents);
    }

    @SuppressLint("NewApi")
    private void sortLifeEvents(Map<String, Set<Event>> personEvents) {
        for (Map.Entry<String, Set<Event>> pair : personEvents.entrySet()) {
            List<Event> lifeEvents = new ArrayList<>(pair.getValue());
            lifeEvents.sort(new DataCache.EventComparator());
            sortedPersonEvents.put(pair.getKey(), lifeEvents);
        }
    }
    private static class EventComparator implements Comparator<Event> {

        @Override
        public int compare(Event e1, Event e2) {
            // Sorted by year then alphabetically
            if (e1.getYear() != e2.getYear()) { return e1.getYear() - e2.getYear(); }
            else { return e1.getEventType().compareTo(e2.getEventType()); }
        }
    }

    private void storeFamilyEvents() {
        if (user.getFatherID() != null) { storeFatherEvents_Recurse(people.get(user.getFatherID())); }
        if (user.getMotherID() != null) { storeMotherEvents_Recurse(people.get(user.getMotherID())); }
//        Log.i(TAG, "father side");
//        for (Event e : fatherSideEvents) { Log.i(TAG, e.toString()); }
//        Log.i(TAG, "mother side");
//        for (Event e : motherSideEvents) { Log.i(TAG, e.toString()); }
    }
    private void storeFatherEvents_Recurse(Person currPerson) {
        fatherSide.add(currPerson.getPersonID());
        List<Event> events = sortedPersonEvents.get(currPerson.getPersonID());
        if (events != null) { fatherSideEvents.addAll(events); }
        if (currPerson.getFatherID() != null) { storeFatherEvents_Recurse(people.get(currPerson.getFatherID())); }
        if (currPerson.getMotherID() != null) { storeFatherEvents_Recurse(people.get(currPerson.getMotherID())); }
    }
    private void storeMotherEvents_Recurse(Person currPerson) {
        motherSide.add(currPerson.getPersonID());
        List<Event> events = sortedPersonEvents.get(currPerson.getPersonID());
        if (events != null) { motherSideEvents.addAll(events); }
        if (currPerson.getFatherID() != null) { storeMotherEvents_Recurse(people.get(currPerson.getFatherID())); }
        if (currPerson.getMotherID() != null) { storeMotherEvents_Recurse(people.get(currPerson.getMotherID())); }

    }

    private void createInitialColors() {
        MapColor colors = MapColor.getInstance();

        eventTypeColors.put("death", colors.getNextColor());
        eventTypes.add("death");

        eventTypeColors.put("birth", colors.getNextColor());
        eventTypes.add("birth");

        eventTypeColors.put("marriage", colors.getNextColor());
        eventTypes.add("marriage");
    }
    public void createNewColor(String newEventType) {
        MapColor colors = MapColor.getInstance();
        eventTypeColors.put(newEventType, colors.getNextColor());
        eventTypes.add(newEventType);
    }

    public Set<Event> getFilteredEvents() {
        Set<Event> filteredEvents = new HashSet<>();

        for (Event e : events.values()) {
            Person p = people.get(e.getPersonID());
            if (p != null && filter(p)) { filteredEvents.add(e); }
        }
        return filteredEvents;
    }

    public boolean filter(Person p) {
//        Log.i(TAG, "filter: " + p.getGender() + " " + p.getPersonID());
        if (!settings.displayFemaleEvents() && p.getGender().equals("f")) { return false; }
        if (!settings.displayMaleEvents() && p.getGender().equals("m")) { return false; }
        if (!settings.displayFatherSide() && fatherSide.contains(p.getPersonID())) { return false; }
        if (!settings.displayMotherSide() && motherSide.contains(p.getPersonID())) { return false; }
        return true;
    }

    public List<Person> searchPeople(String s) {
        List<Person> list = new ArrayList<>();
        for (Person p : people.values()) {
            String firstName = p.getFirstname().toLowerCase();
            String lastName = p.getLastName().toLowerCase();

            if (firstName.contains(s) || lastName.contains(s)) {
                if (filter(p)) { list.add(p); }
            }
        }
//        Log.i(TAG, "Persons Found: " + list.size());
        return list;
    }

    public List<Event> searchEvents(String s) {
//        Log.i(TAG, "searchEvents");

        List<Event> list = new ArrayList<>();
        for (Event e : events.values()) {
            String type = e.getEventType().toLowerCase();
            String country = e.getCountry().toLowerCase();
            String city = e.getCity().toLowerCase();
            String year = Integer.toString(e.getYear());

            if (type.contains(s) || country.contains(s) || city.contains(s) || year.contains(s)) {
                if (filter(people.get(e.getPersonID()))) { list.add(e); }
            }
        }
//        Log.i(TAG, "Events Found: " + list.size());
        return list;
    }

    public List<Person> getFamily(Person p) {
//        Log.i(TAG, "getFamily");
        List<Person> family = new ArrayList<>();
        Person father = people.get(p.getFatherID());
        Person mother = people.get(p.getMotherID());
        Person spouse = people.get(p.getSpouseID());
        Set<Person> kids = children.get(p.getPersonID());

        if (father != null) { family.add(father); }
        if (mother != null) { family.add(mother); }
        if (spouse != null) { family.add(spouse); }
        if (kids != null) { family.addAll(kids); }

        return family;
    }

    @Override
    public String toString() {
        return "DataCache{" +
                "people=" + people +
                ", events=" + events +
                ", eventTypes=" + eventTypes +
                ", eventTypeColors=" + eventTypeColors +
                ", user=" + user +
                '}';
    }
}
