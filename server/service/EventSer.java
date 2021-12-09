package service;

import dao.*;
import model.*;
import request.EventReq;
import response.EventRes;
import java.util.Set;

/**
 * Service Class for Event requests
 */
public class EventSer {

    /**
     * retrieves single event from database
     *
     * @param req Request Body: Event Object to retrieve from database
     * @return Response Body: Event Object, success of request
     */
    public EventRes event(EventReq req) {
        Database db = new Database();
        EventRes res = null;

        boolean success = false;
        try {
            db.openConnection();

            // Find associated User ID of given authentication token
            AuthTokenDao aDao = new AuthTokenDao(db.getConnection());
            AuthToken token = aDao.findToken(req.getAuthToken());

            if (token != null) {
                //  Find user associated with found User ID
                UserDao uDao = new UserDao(db.getConnection());
                User user = uDao.findByID(token.getUserID());

                // Find event given that the associated username matches given authentication token
                EventDao eDao = new EventDao(db.getConnection());
                Event event = eDao.find(req.getEventID(), user.getUserName());

                // Generate response
                // if event was found return it
                // else return error
                if (event != null) {
                    res = new EventRes(event.getUsername(), event.getEventID(), event.getPersonID(), event.getLatitude(), event.getLongitude(),
                            event.getCountry(), event.getCity(), event.getEventType(), event.getYear());
                    db.closeConnection(true);
                    success = true;
                }
            }
            if (!success) {
                res = new EventRes("error: could not find event ID in database");
                db.closeConnection(false);
            }
        }
        catch(DataAccessException ex) {
            res = new EventRes("error: " + ex.getMessage());
            try  { db.closeConnection(false); }
            catch (DataAccessException e) { throw new RuntimeException(e.getMessage()); }
        }
        return res;
    }

    /**
     * retrieves all events from the database associated with certain username
     *
     * @return Response Body: array of Event objects, success of request
     */
    public EventRes event(String token) {
        Database db = new Database();
        EventRes res = null;

        boolean success = false;
        try {
            db.openConnection();

            // Find UserID from given Authentication Token
            AuthTokenDao aDao = new AuthTokenDao(db.getConnection());
            AuthToken tokenObj = aDao.findToken(token);

            if (tokenObj != null) {

                // Find User from found UserID
                UserDao uDao = new UserDao(db.getConnection());
                User user = uDao.findByID(tokenObj.getUserID());

                // Find all Events that are associated with Username
                EventDao eDao = new EventDao(db.getConnection());
                Set<Event> events = eDao.findAll(user.getUserName());

                // Convert DAO response into response compatible array
                Event[] data = convertData(events);

                // If events were found return the data
                // Else return error message
                if (events.size() > 0) {
                    res = new EventRes(data);
                    db.closeConnection(true);
                    success = true;
                }
            }
            if (!success) {
                res = new EventRes("error: No events found associated with your username");
                db.closeConnection(false);
            }
        } catch(DataAccessException ex) {
            res = new EventRes("error: " + ex.getMessage());

            try { db.closeConnection(false); }
            catch(DataAccessException e) { throw new RuntimeException("Unable to close connection"); }
        }
        return res;
    }
    private Event[] convertData(Set<Event> events) {
        Event[] data = new Event[events.size()];
        int i = 0;
        for (Event e : events) {
            data[i++] = e;
        }
        return data;
    }
}
