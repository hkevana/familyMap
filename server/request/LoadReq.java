package request;

import model.*;

import java.util.Arrays;

/**
 * Request Body Class for Load requests
 */
public class LoadReq {
    private User[] users;
    private Person[] persons;
    private Event[] events;

    /**
     * parameterized constructor
     *
     * @param users array of User Objects
     * @param persons array of Person Objects
     * @param events array of Event Objects
     */
    public LoadReq(User[] users, Person[] persons, Event[] events) {
        this.users = users;
        this.persons = persons;
        this.events = events;
    }

    public User[] getUsers() { return users; }
    public Person[] getPersons() { return persons; }
    public Event[] getEvents() { return events; }

    public void setUsers(User[] users) { this.users = users; }
    public void setPersons(Person[] persons) { this.persons = persons; }
    public void setEvents(Event[] events) { this.events = events; }

    @Override
    public String toString() {
        return "LoadReq {" +
                "users=" + Arrays.toString(users) +
                ", persons=" + Arrays.toString(persons) +
                ", events=" + Arrays.toString(events) +
                '}';
    }
}
