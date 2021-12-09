package dao;

import com.google.gson.Gson;
import json.Names;
import model.Person;
import model.User;

import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * Data Access Object Class for Person
 */
public class PersonDao {
    private final Connection connect;

    private Names fNames;
    private Names mNames;
    private Names sNames;
    private int newPersons;

    /**
     * parameterized constructor
     *
     * @param c established connection to database
     */
    public PersonDao(Connection c) {
        this.connect = c;
        this.fNames = null;
        this.mNames = null;
        this.sNames = null;
        this.newPersons = 0;
    }

    /**
     * inserts a person object into the database
     *
     * @param p particular person object to be inserted into database
     * @throws DataAccessException error while inserting into database
     */
    public void insert(Person p) throws DataAccessException {
        final String SQL = "INSERT INTO Person (PersonID, AssociateUserName, FirstName, LastName, " +
                           "Gender, FatherID, MotherID, SpouseID) VALUES(?,?,?,?,?,?,?,?);";
        try (PreparedStatement stmt = this.connect.prepareStatement(SQL)) {
            stmt.setString(1, p.getPersonID());
            stmt.setString(2, p.getUsername());
            stmt.setString(3, p.getFirstname());
            stmt.setString(4, p.getLastName());
            stmt.setString(5, p.getGender());
            stmt.setString(6, p.getFatherID());
            stmt.setString(7, p.getMotherID());
            stmt.setString(8, p.getSpouseID());

            stmt.executeUpdate();
        } catch(SQLException ex) { throw new DataAccessException(ex.getMessage()); }
    }

    /**
     * remove all Person Objects from database assoicated with given username
     *
     * @param username associated username of Person objects to remove from database
     * @throws DataAccessException error while searching database
     */
    public void remove(String username) throws DataAccessException {
        final String SQL = "DELETE FROM Person WHERE AssociateUserName = ?;";
        try (PreparedStatement stmt = this.connect.prepareStatement(SQL)) {
            stmt.setString(1, username);

            stmt.executeUpdate();
        } catch (SQLException ex) { throw new DataAccessException(ex.getMessage()); }
    }

    /**
     * Clear all Person objects from database
     *
     * @throws DataAccessException error while clearing database
     */
    public void clear() throws DataAccessException {
        final String SQL = "DELETE FROM Person";

        try(Statement stmt = this.connect.createStatement()) { stmt.executeUpdate(SQL); }
        catch(SQLException ex) { throw new DataAccessException(ex.getMessage()); }
    }

    /**
     * finds a particular person in database
     *
     * @param personID particular id of person in database
     * @return person object found in database or null if not found
     * @throws DataAccessException error while searching database
     */
    public Person find(String personID, String username) throws DataAccessException {
        final String SQL = "SELECT * FROM Person WHERE PersonID = ? AND AssociateUserName = ?;";
        try (PreparedStatement stmt = this.connect.prepareStatement(SQL)) {
            stmt.setString(1, personID);
            stmt.setString(2, username);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Person(rs.getString("PersonID"), rs.getString("AssociateUserName"), rs.getString("FirstName"),
                        rs.getString("LastName"), rs.getString("Gender"), rs.getString("FatherID"),
                        rs.getString("MotherID"), rs.getString("SpouseID"));
            } else { return null; }
        } catch(SQLException ex) { throw new DataAccessException(ex.getMessage()); }
    }

    /**
     * Find all Person Objects associated with given uername
     *
     * @param user assocaited username of person objects to find
     *
     * @return set of all Person Objects found in database assocaited with given username
     * @throws DataAccessException error while seraching database
     */
    public Set<Person> findAll(String user) throws DataAccessException {
        final String SQL = "SELECT * FROM Person WHERE AssociateUserName = ?;";

        try (PreparedStatement stmt = this.connect.prepareStatement(SQL)) {
            stmt.setString(1, user);

            ResultSet rs = stmt.executeQuery();
            Set<Person> people = new HashSet<>();
            while (rs.next()) {
                people.add(new Person(rs.getString("PersonID"), rs.getString("AssociateUserName"), rs.getString("FirstName"),
                        rs.getString("LastName"), rs.getString("Gender"), rs.getString("FatherID"),
                        rs.getString("MotherID"), rs.getString("SpouseID")));
            }
            return people;
        }
        catch (SQLException ex) { throw new DataAccessException(ex.getMessage()); }
    }

    /**
     * fills generations associated with username
     *
     * @param username associated username
     * @param generations number of generations to fill
     * @throws DataAccessException error while inserting into database
     */
    public Person fill(String username, int generations) throws DataAccessException {
        User u = findUser(username);
        if (u != null) { return createPersonFromUser(u, generations); }
        else { return null; }
    }
    private User findUser(String username) throws DataAccessException {
        UserDao ud = new UserDao(this.connect);
        return ud.findByUsername(username);
    }
    private Person createPersonFromUser(User u, int gens) throws DataAccessException {
        // load arrays of Female, Male, and Sur names
        this.loadNameFiles();

        // Create unique ID for father and mother
        String fatherID;
        String motherID;
        boolean unique = true;
        do {
            fatherID = generateID();
            motherID = generateID();
            if (findID(fatherID) || findID(motherID)) { unique = false; }
        } while (!unique);

        // Recursive Descent - generate tree of parents
        generateTree(fatherID, motherID, gens, 1, u.getUserName(), "m");
        generateTree(motherID, fatherID, gens, 1, u.getUserName(), "f");

        Person p = new Person(u.getUserID(), u.getUserName(), u.getFirstName(), u.getLastName(), u.getGender(), fatherID, motherID, null);
        this.insert(p);
        this.newPersons++;
        return p;
    }
    private void generateTree(String curr, String spouse, int gens, int curGen, String user, String gender) throws DataAccessException {
        if (gens == curGen) {
            // Insert Person object with null parent ID's as they are the last generation to generate
            this.insert(new Person(curr, user, generateRandName(gender), generateRandName("s"), gender, null, null, spouse));
        }
        else {
            // Generate unique parent ID's
            String fatherID;
            String motherID;
            boolean unique = true;
            do {
                fatherID = generateID();
                motherID = generateID();
                if (findID(fatherID) || findID(motherID)) { unique = false; } // If ID exists in database regenerate
            } while (!unique);

            // Recursive Descent - generate Trees of parents increment current generation
            generateTree(fatherID, motherID, gens, (curGen + 1), user, "m");
            generateTree(motherID, fatherID, gens, (curGen + 1), user, "f");

            // Insert current Person object
            this.insert(new Person(curr, user, generateRandName(gender), generateRandName("surname"), gender, fatherID, motherID, spouse));
        }
        // Increment number of new Persons in database
        this.newPersons++;
    }

    /**
     * generate name based on gender
     *
     * @param gender f = female, m = male, default = surname
     * @return randomly selected name from associated list of names
     */
    private String generateRandName(String gender) {
        return switch (gender) {
            case "f" -> this.fNames.getRandName();
            case "m" -> this.mNames.getRandName();
            default -> this.sNames.getRandName();
        };
    }
    private void loadNameFiles() throws DataAccessException {
        // Load file of female names for random name generation
        try (Reader read = new FileReader("json/fnames.json")) {
            Gson gson = new Gson();
            this.fNames = gson.fromJson(read, Names.class);
        }
        catch (IOException ex) { throw new DataAccessException(ex.getMessage()); }

        // Load file of male names for random name generation
        try (Reader read = new FileReader("json/mnames.json")) {
            Gson gson = new Gson();
            this.mNames = gson.fromJson(read, Names.class);
        }
        catch (IOException ex) { throw new DataAccessException(ex.getMessage()); }

        // Load fiel of surnames for random name generation
        try (Reader read = new FileReader("json/snames.json")) {
            Gson gson = new Gson();
            this.sNames = gson.fromJson(read, Names.class);
        }
        catch (IOException ex) { throw new DataAccessException(ex.getMessage()); }
    }

    /**
     * generate unique Person ID
     *
     * @return unique Person ID
     */
    public String generateID() {
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            int rand = 0;
            // Valid ASCII characters: a-z | 1-9 | A-Z
            do { rand = getRandChar(); }
            while((58 <= rand && rand <= 64) || (91 <= rand && rand <= 96)); // Invalid characters: regenerate charcter
            token.append((char)rand);
        }
        return token.toString();
    }
    private char getRandChar() {
        // ASCII chars: 0-48 to Z-122
        Random randInt = new Random();
        return (char)(randInt.nextInt(123 - 48) + 48);
    }

    /**
     * Used to validate uniqueness of generated Person ID
     *
     * @param ID Person ID to find in database
     * @return true if not unique ID, else false
     * @throws DataAccessException
     */
    private boolean findID(String ID) throws DataAccessException {
        final String SQL = "SELECT PersonID FROM Person WHERE PersonID = ?;";
        ResultSet rs;
        try (PreparedStatement stmt = this.connect.prepareStatement(SQL)) {
            stmt.setString(1, ID);
            rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException ex) { throw new DataAccessException(ex.getMessage()); }
    }

    public int getNewPersons() { return newPersons; }
}
