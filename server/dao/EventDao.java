package dao;

import com.google.gson.Gson;
import json.Location;
import json.LocationData;
import model.Event;
import model.Person;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * Data Access Object Class for Events
 */
public class EventDao {
    private final Connection connect;

    private LocationData data;
    private int newEvents;

    /**
     * parameterized constructor
     *
     * @param c established connection to database
     */
    public EventDao(Connection c) {
        this.connect = c;
        this.data = null;
        this.newEvents = 0;
    }

    /**
     * inserts an event into database
     *
     * @param e event object to be inserted
     * @throws DataAccessException error while inserting into database
     */
    public void insert(Event e) throws DataAccessException {
        final String sql = "INSERT INTO Event (EventID, UserName, PersonID, Latitude, Longitude, " +
                           "Country, City, EventType, Year) VALUES(?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = this.connect.prepareStatement(sql)) {
            stmt.setString(1, e.getEventID());
            stmt.setString(2, e.getUsername());
            stmt.setString(3, e.getPersonID());
            stmt.setFloat(4, e.getLatitude());
            stmt.setFloat(5, e.getLongitude());
            stmt.setString(6, e.getCountry());
            stmt.setString(7, e.getCity());
            stmt.setString(8, e.getEventType());
            stmt.setInt(9, e.getYear());

            stmt.executeUpdate();
        } catch (SQLException ex) { throw new DataAccessException(ex.getMessage()); }
    }

    /**
     * remove all events associated with given username
     *
     * @param username assocaited username of events to remove
     * @throws DataAccessException error while removing from database
     */
    public void remove(String username) throws DataAccessException {
        final String SQL = "DELETE FROM Event WHERE UserName = ?;";
        try (PreparedStatement stmt = this.connect.prepareStatement(SQL)) {
            stmt.setString(1, username);

            stmt.executeUpdate();
        } catch(SQLException ex) { throw new DataAccessException(ex.getMessage()); }
    }

    /**
     * finds an individual event within database
     *
     * @param eventID particular id of event to find in database
     * @param username associated username of event to find in database
     *
     * @return event found in database or null if not found
     * @throws DataAccessException error while searching database
     */
    public Event find(String eventID, String username) throws DataAccessException {
        Event event;
        String sql = "SELECT * FROM Event WHERE EventID = ? AND UserName =?;";
        try (PreparedStatement stmt = this.connect.prepareStatement(sql)) {
            stmt.setString(1, eventID);
            stmt.setString(2, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                event = new Event(rs.getString("EventID"), rs.getString("UserName"),
                        rs.getString("PersonID"), rs.getFloat("Latitude"), rs.getFloat("Longitude"),
                        rs.getString("Country"), rs.getString("City"), rs.getString("EventType"),
                        rs.getInt("Year"));
                return event;
            }
            else { return null; }
        }
        catch (SQLException ex) { throw new DataAccessException(ex.getMessage()); }
    }

    /**
     * Find all events in database associated with given username
     *
     * @param user associated username of events to find in database
     * @return set of events found in database, null if no events found
     * @throws DataAccessException error while searching database
     */
    public Set<Event> findAll(String user) throws DataAccessException {
        final String SQL = "SELECT * FROM Event WHERE UserName = ?;";
        try (PreparedStatement stmt = this.connect.prepareStatement(SQL)) {
            stmt.setString(1, user);

            ResultSet rs = stmt.executeQuery();
            Set<Event> events = new HashSet<>();
            while (rs.next()) {
                Event e = new Event(rs.getString("EventID"), rs.getString("UserName"),
                        rs.getString("PersonID"), rs.getFloat("Latitude"), rs.getFloat("Longitude"),
                        rs.getString("Country"), rs.getString("City"), rs.getString("EventType"),
                        rs.getInt("Year"));
                events.add(e);
            }
            return events;
        }
        catch (SQLException ex) { throw new DataAccessException(ex.getMessage()); }
    }

    /**
     * create family tree events for given username
     * Resursive Descent - see: createFamilyEvents_Recurse
     *
     * @param user username for which to generate family tree
     * @throws DataAccessException error while inserting into database
     */
    public void createFamilyEvents(Person user) throws DataAccessException {
        // Load Country and City information for random event location generation
        loadLocationFile();

        // Generate random age of user and given birth year based off current year
        int curYear = Calendar.getInstance().get(Calendar.YEAR);
        int age = getRandInt(15, 80);
        int birthYear = curYear - age;

        // Create birth event of user
        createEvent(user.getPersonID(), user.getUsername(), "Birth", birthYear);

        // Find Father and Mother Person objects of user in database
        Person father = findParent(user.getFatherID(), user.getUsername());
        Person mother = findParent(user.getMotherID(), user.getUsername());
        if (father != null && mother != null) {
            // Create Marriage of users parents
            int parentMarriageYear = birthYear - getRandInt(5, 20);
            createMarriage(father.getPersonID(), mother.getPersonID(), user.getUsername(), parentMarriageYear);

            // Recursive Descent - create events for father's parents and mother's parents
            createFamilyEvents_Recurse(father, user.getUsername(), parentMarriageYear);
            createFamilyEvents_Recurse(mother, user.getUsername(), parentMarriageYear);
        }
    }
    private void createFamilyEvents_Recurse(Person p, String user, int yearMarried) throws DataAccessException {
        // Create random birth year and event based off of marriage year
        int birthYear = yearMarried - getRandInt(17, 35);
        createEvent(p.getPersonID(), user, "Birth", birthYear);

        // Create random death year and event based off of birth year
        int deathYear = birthYear + getRandInt(75, 102);
        createEvent(p.getPersonID(), user, "Death", deathYear);

        // Find father and mother of current Person
        Person father = findParent(p.getFatherID(), user);
        Person mother = findParent(p.getMotherID(), user);

        // Acts as base case - if no parents stop recursive descent
        if (father != null && mother != null) {
            // Create marriage of current Person parents
            int parentMarriageYear = birthYear - getRandInt(2, 15);
            createMarriage(father.getPersonID(), mother.getPersonID(), user, parentMarriageYear);

            // Recursive Descent - create events for father's parents and mother's parents
            createFamilyEvents_Recurse(father, user, parentMarriageYear);
            createFamilyEvents_Recurse(mother, user, parentMarriageYear);
        }

    }

    /**
     * Create a new event
     *
     * @param PID ID of Person associated with event
     * @param user username of assocaited user with event
     * @param type event type
     * @param year year of event
     * @throws DataAccessException error inserting into database
     */
    public void createEvent(String PID, String user, String type, int year) throws DataAccessException {
        // Generate random location of event
        Location eventLoc = getRandLocation();
        // Generate EventID until unique
        boolean unique = true;
        String eventID;
        do {
            eventID = generateID();
            if (findID(eventID)) { unique = false; }
        } while (!unique);

        // Insert new event into database and increment number of new events
        this.insert(new Event(generateID(), user, PID, eventLoc.getLatitude(), eventLoc.getLongitude(),
                eventLoc.getCountry(), eventLoc.getCity(), type, year));
        this.newEvents++;
    }

    /**
     * Create a new marriage for father and mother Person objects
     *
     * @param FID ID of Father Person
     * @param MID ID of Mother Person
     * @param user associated username of event
     * @param year event year occurred
     * @throws DataAccessException error while inserting into database
     */
    private void createMarriage(String FID, String MID, String user, int year) throws DataAccessException {
        // Generate random location of marriage
        Location eventLoc = getRandLocation();

        // Insert event associated with father Person and increment number of new events
        this.insert(new Event(generateID(), user, FID, eventLoc.getLatitude(), eventLoc.getLongitude(),
                eventLoc.getCountry(), eventLoc.getCity(), "Marriage", year));
        this.newEvents++;
        // Insert event associated with mother Person and increment number of new events
        this.insert(new Event(generateID(), user, MID, eventLoc.getLatitude(), eventLoc.getLongitude(),
                eventLoc.getCountry(), eventLoc.getCity(), "Marriage", year));
        this.newEvents++;
    }
    private Location getRandLocation() { return this.data.getRandLocation(); }

    /**
     * Load location file into EventDao
     *
     * @throws DataAccessException error reading file
     */
    private void loadLocationFile() throws DataAccessException {
        try (Reader read = new FileReader("json/locations.json")) {
            Gson gson = new Gson();
            this.data = gson.fromJson(read, LocationData.class);
        } catch(IOException ex) { throw new DataAccessException(ex.getMessage()); }
    }

    private int getRandInt(int min, int max) {
        Random randInt = new Random();
        return (randInt.nextInt(max - min) + min);
    }
    private String generateID() {
        StringBuilder token = new StringBuilder();
        // Generate unique Event ID
        for (int i = 0; i < 16; i++) {
            int rand = 0;
            // Valid Chars: a-z | 1-9 | A-Z
            do { rand = getRandInt(48, 123); }
            while((58 <= rand && rand <= 64) || (91 <= rand && rand <= 96)); // Invalid chars generate new random int
            token.append((char)rand);
        }
        return token.toString();
    }
    private Person findParent(String ID, String username) throws DataAccessException {
        PersonDao pd = new PersonDao(this.connect);
        return pd.find(ID, username);
    }
    private boolean findID(String ID) throws DataAccessException {
        final String SQL = "SELECT EventID FROM Event WHERE EventID = ?;";
        try (PreparedStatement stmt = this.connect.prepareStatement(SQL)) {
            stmt.setString(1, ID);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException ex) { throw new DataAccessException(ex.getMessage()); }
    }

    public int getNumNewEvents() { return newEvents; }
}
