package service;

import dao.*;
import model.AuthToken;
import model.Person;
import model.User;
import request.PersonReq;
import response.PersonRes;

import java.util.Set;
import java.util.concurrent.DelayQueue;

/**
 * Service Class for Person requests
 */
public class PersonSer {

    /**
     * find a specific person in database
     *
     * @param req Request Body: Person Object information to find
     * @return Response Body: Person Object found
     */
    public PersonRes findPerson(PersonReq req) {
        Database db = new Database();
        PersonRes res = null;

        boolean success = false;
        try {
            db.openConnection();

            // Find username associated with given User ID
            UserDao uDao = new UserDao(db.getConnection());
            User user = uDao.findByID(req.getUserID());

            if (user != null) {
                // Find Person from given Person ID given that it is associated with found username
                PersonDao pDao = new PersonDao(db.getConnection());
                Person person = pDao.find(req.getPersonID(), user.getUserName());

                // If found - return Person
                // Else error - Not associated with username or not found at all
                if (person != null) {
                    res = new PersonRes(person.getUsername(), person.getPersonID(), person.getFirstname(), person.getLastName(),
                            person.getGender(), person.getFatherID(), person.getMotherID(), person.getSpouseID());
                    db.closeConnection(true);
                    success = true;
                }
            }
            if (!success) {
                res = new PersonRes("error: Person associated with your username not found in database");
                db.closeConnection(false);
            }
        } catch (DataAccessException ex) {
            res = new PersonRes("error: " + ex.getMessage());

            try { db.closeConnection(false); }
            catch(DataAccessException e) { throw new RuntimeException("Unable to close connection to database"); }
        }
        return res;
    }

    /**
     * Find all Person objects associated with certain User
     *
     * @param token authentication token of current user
     * @return all Person objects found
     */
    public PersonRes findPerson(String token) {
        Database db = new Database();
        PersonRes res = null;

        try {
            db.openConnection();

            // Find given authentication token in database
            AuthTokenDao aDao = new AuthTokenDao(db.getConnection());
            AuthToken foundToken = aDao.findToken(token);

            boolean success = false;
            if (foundToken != null) {
                // Find user associated with found User ID
                UserDao uDao = new UserDao(db.getConnection());
                User user = uDao.findByID(foundToken.getUserID());

                if (user != null) {
                    // Find all Person objects associated with found username
                    PersonDao pDao = new PersonDao(db.getConnection());
                    Set<Person> people = pDao.findAll(user.getUserName());

                    // Convert found set into response compatible array
                    Person[] data = convertData(people);

                    // If persons were found - return them
                    // Else error - not found
                    if (people.size() > 0) {
                        res = new PersonRes(data);
                        db.closeConnection(true);
                        success = true;
                    }
                }
            }
            if (!success) {
                res = new PersonRes("error: No Persons found associated with your username");
                db.closeConnection(false);
            }
        } catch(DataAccessException ex) {
            res = new PersonRes("error: " + ex.getMessage());

            try { db.closeConnection(false); }
            catch(DataAccessException e) { throw new RuntimeException("Unable to close connection to database"); }
        }
        return res;
    }
    private Person[] convertData(Set<Person> people) {
        Person[] data = new Person[people.size()];
        int i = 0;
        for (Person p : people) { data[i++] = p; }
        return data;
    }
}
