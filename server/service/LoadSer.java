package service;

import dao.*;
import model.*;
import request.LoadReq;
import response.LoadRes;

/**
 * Service Class for Load requests
 */
public class LoadSer {

    /**
     * loads data into database
     *
     * @param req Request Body: array of User/Person/Event Objects to insert into database
     * @return Response Body: success of request
     */
    public LoadRes load(LoadReq req) {
        Database db = new Database();
        LoadRes res;

        try {
            // clear database of all data
            db.openConnection();
            db.clear();

            // Load new Events, Users, and Persons into database
            // Events
            EventDao eDao = new EventDao(db.getConnection());
            int newEvents = 0;
            for (Event e : req.getEvents()) {
                eDao.insert(e);
                newEvents++;
            }

            // Users
            UserDao uDao = new UserDao(db.getConnection());
            int newUsers = 0;
            for (User u : req.getUsers()) {
                uDao.registerNewUser(u);
                newUsers++;
            }

            // Persons
            PersonDao pDao = new PersonDao(db.getConnection());
            int newPersons = 0;
            for (Person p : req.getPersons()) {
                pDao.insert(p);
                newPersons++;
            }

            // Return success message
            String message = "Successfully added " + newUsers + " users, " + newPersons + " persons, and " + newEvents + " events to the database.";
            res = new LoadRes(message, true);

            db.closeConnection(true);
        } catch(DataAccessException ex) {
            res = new LoadRes(ex.getMessage(), false);

            try { db.closeConnection(false); }
            catch(DataAccessException e) { throw new RuntimeException("Unable to close connection to database"); }
        }
        return res;
    }
}
