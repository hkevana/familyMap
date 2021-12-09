package service;

import dao.AuthTokenDao;
import dao.DataAccessException;
import dao.Database;
import model.AuthToken;

public class AuthTokenSer {

    /**
     * Insert new authentication token into database
     *
     * @param ID User ID associated with new authentication token
     * @return generated authentication token or null if error occurred
     */
    public String insertToken(String ID) {
        Database db = new Database();
        String token = null;

        try {
            db.openConnection();

            // Generate new authentication token and insert into database
            AuthTokenDao aDao = new AuthTokenDao(db.getConnection());
            token = aDao.generateToken();
            AuthToken at = new AuthToken(token, ID);
            aDao.insert(at);

            db.closeConnection(true);
        } catch(DataAccessException ex) {
            try { db.closeConnection(false); }
            catch(DataAccessException e) { throw new RuntimeException("Unable to close connection to database"); }
        }
        return token;
    }

    /**
     * Validate authentication tokens given in request headers
     *
     * @param token authentication token to validate
     * @return valid authentication token or null if invalid
     */
    public String validateToken(String token) {
        Database db = new Database();
        String returnToken = null;

        try {
            db.openConnection();

            AuthTokenDao aDao = new AuthTokenDao(db.getConnection());
            returnToken = aDao.validateToken(token);

            db.closeConnection(true);
        } catch (DataAccessException ex) {
            try { db.closeConnection(false); }
            catch(DataAccessException e) { throw new RuntimeException("Unable to close connection to database"); }
        }
        return returnToken;
    }

    /**
     * Find a given authentication token
     *
     * @param token authentication token to find
     * @return found authentication token w/ associated User ID or null if not found
     */
    public AuthToken findToken(String token) {
        Database db = new Database();
        AuthToken returnToken = null;

        try {
            db.openConnection();

            AuthTokenDao DAO = new AuthTokenDao(db.getConnection());
            returnToken = DAO.findToken(token);

            db.closeConnection(true);
        } catch(DataAccessException ex) {
            try { db.closeConnection(false); }
            catch(DataAccessException e) { throw new RuntimeException("Unable to close connection to database"); }
        }
        return returnToken;
    }

}
