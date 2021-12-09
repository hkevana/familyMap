package service;

import dao.DataAccessException;
import dao.Database;
import response.ClearRes;

/**
 * Service Class for Clear requests
 */
public class ClearSer {

    /**
     * clear all data from database - Person, User, Event, AuthToken
     *
     * @return Response Body: success or failure of request
     */
    public ClearRes clear() {
        Database db = new Database();
        ClearRes res;

        try {
            db.openConnection();
            db.clear();
            db.closeConnection(true);

            res = new ClearRes("Clear succeeded", true);
        } catch (DataAccessException e) {
            res = new ClearRes("error: " + e.getMessage(), false);

            try { db.closeConnection(false); }
            catch (DataAccessException ex) { throw new RuntimeException("Unable to close connection to database"); }
        }
        return res;
    }
}
