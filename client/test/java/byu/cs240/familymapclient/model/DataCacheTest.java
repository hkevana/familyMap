package byu.cs240.familymapclient.model;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import byu.cs240.familymapclient.net.ServerProxy;
import byu.cs240.familymapclient.req.DataReq;
import byu.cs240.familymapclient.req.RegisterReq;
import byu.cs240.familymapclient.res.DataRes;
import byu.cs240.familymapclient.res.RegisterRes;

import static org.junit.Assert.*;

public class DataCacheTest {

    private static DataCache cache;
    private static Settings settings;

    @BeforeClass
    public static void setup() {
        System.out.println("setup");
        ServerProxy server = ServerProxy.getInstance();
        cache = DataCache.getInstance();
        settings = Settings.getInstance();

        ServerProxy.setHostName("localhost");
        server.clear();
        RegisterRes user = server.register(new RegisterReq("Kevan", "password", "Email", "Kev", "Hend", "m"));

        DataRes data = server.getData(new DataReq(user.getPersonID(), user.getAuthToken()));
        cache.storeData(data);
    }

    @Test
    public void checkSortedLifeEvents() {
        Map<String, List<Event>> allEvents = cache.getSortedPersonEvents();
        int countMapEntries = 0;

        for (Map.Entry<String, List<Event>> pair : allEvents.entrySet()) {
//            System.out.println("MapEntry: " + countMapEntries++);
            List<Event> lifeEvents = pair.getValue();

            int countLifeEvents = 0;
            for (int i = 0; i < lifeEvents.size() - 1; i++) {
                Event e1 = lifeEvents.get(i);
                Event e2 = lifeEvents.get(i + 1);

                int diffInYear = e1.getYear() - e2.getYear();
                if (diffInYear == 0) {
                    assertTrue("Unsorted Event Names: \n\t" + e1.toString() + "\n\t" + e2.toString(), e1.getEventType().compareTo(e2.getEventType()) <= 0);
                } else {
                    assertTrue("Unsorted Event Years: \n\t" + e1.toString() + "\n\t" + e2.toString(), diffInYear < 0);
                }

            }
        }
    }

    @Test
    public void testSearchPeople_pass() {
        List<Person> peeps = cache.searchPeople("");

        // SEARCHING EMPTY STRING
        assertEquals("Incorrect number of people found - Search string length: 0", 31, peeps.size());

        // SEARCHING A SINGLE CHARACTER
        String searchStr = "a";
        peeps = cache.searchPeople(searchStr);
        for (Person p : peeps) {
            assertTrue("Person found not matching search string: a \n\t" + p.toString(), (p.getFirstname().toLowerCase().contains(searchStr) || p.getLastName().toLowerCase().contains(searchStr)));
        }

        // SEARCHING A STRING
        searchStr = "Kev";
        peeps = cache.searchPeople(searchStr);
        searchStr = searchStr.toLowerCase();
        for (Person p : peeps) {
            assertTrue("Person found not matching search string: Kev \n\t" + p.toString(), (p.getFirstname().toLowerCase().contains(searchStr) || p.getLastName().toLowerCase().contains(searchStr)));
        }
    }

    @Test
    public void testSearchPeople_empty() {
        List<Person> peeps = cache.searchPeople("a/");

        assertEquals("People found with impossible string", 0, peeps.size());
    }

    @Test
    public void testSearchEvents_pass() {
        List<Event> events = cache.searchEvents("");

        // EMPTY SEARCH STRING
        assertEquals("Incorrect number of events found - Search string length: 0", 91, events.size());

        // SEARCHING A SINGLE CHARACTER
        String searchStr = "a";
        events = cache.searchEvents(searchStr);
        for (Event e : events) {
            assertTrue("Person found not matching search string: a \n\t" + e.toString(), (e.getCountry().toLowerCase().contains(searchStr) || e.getCity().toLowerCase().contains(searchStr) || e.getEventType().toLowerCase().contains(searchStr)));
        }

        // SEARCHING A STRING
        searchStr = "United";
        events = cache.searchEvents(searchStr);
        searchStr = searchStr.toLowerCase();
        for (Event e : events) {
            assertTrue("Person found not matching search string: United \n\t" + e.toString(), (e.getCountry().toLowerCase().contains(searchStr) || e.getCity().toLowerCase().contains(searchStr) || e.getEventType().toLowerCase().contains(searchStr)));
        }

        // SEARCHING A NUMBER
        searchStr = "95";
        events = cache.searchEvents(searchStr);
        for (Event e : events) {
            assertTrue("Person found not matching search string: 95 \n\t" + e.toString(), (Integer.toString(e.getYear()).contains(searchStr)));
        }

    }

    @Test
    public void testSearchEvents_empty() {
        List<Event> events = cache.searchEvents("s/");

        assertEquals("incorrect number of events found with impossible search string", 0, events.size());

    }

    @Test
    public void testFilterEvents_noFilters() {
        settings.turnOnAllSettings();
        Set<Event> events = cache.getFilteredEvents();

        assertEquals("Incorrect number of events", 91, events.size());
    }

    @Test
    public void testFilterEvents_individualFilters() {
        settings.turnOnAllSettings();
        // MALE FILTER
        settings.toggleDisplayMaleEvents();
        Set<Event> events = cache.getFilteredEvents();
        for (Event e : events) {
            Person p = cache.getPeople().get(e.getPersonID());
            assertTrue("Unfiltered male event: \n\t" + e.toString(), (p != null && !p.getGender().equals("m")));
        }
        settings.toggleDisplayMaleEvents();

        // FEMALE FILTER
        settings.toggleDisplayFemaleLines();
        events = cache.getFilteredEvents();
        for (Event e : events) {
            Person p = cache.getPeople().get(e.getPersonID());
            assertTrue("Unfiltered female event: \n\t" + e.toString(), (p != null && !p.getGender().equals("f")));
        }
        settings.toggleDisplayFemaleLines();

        // FATHER'S SIDE FILTER
        settings.toggleDisplayFatherEvents();
        events = cache.getFilteredEvents();
        for (Event e : events) {
            Person p = cache.getPeople().get(e.getPersonID());
            assertTrue("Unfiltered event on father's side: \n\t" + e.toString(), (p != null && !cache.getFatherSide().contains(p.getPersonID())));
        }
        settings.toggleDisplayFatherEvents();

        // MOTHER'S SIDE FILTER
        settings.toggleDisplayMotherEvents();
        events = cache.getFilteredEvents();
        for (Event e : events) {
            Person p = cache.getPeople().get(e.getPersonID());
            assertTrue("Unfiltered event on mother's side: \n\t" + e.toString(), (p != null && !cache.getMotherSide().contains(p.getPersonID())));
        }
        settings.toggleDisplayMotherEvents();
    }

    @Test
    public void testFilterEvents_allFilters() {
        settings.turnOffAllSettings();
        Set<Event> events = cache.getFilteredEvents();

        assertEquals("Events returned when all filters are on", 0, events.size());

    }
}
