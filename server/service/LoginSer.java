package service;

import dao.AuthTokenDao;
import dao.DataAccessException;
import dao.Database;
import dao.UserDao;
import model.AuthToken;
import model.User;
import request.LoginReq;
import response.LoginRes;

import javax.xml.crypto.Data;

/**
 * Service Class for Login requests
 */
public class LoginSer {

    /**
     * logs in a user
     *
     * @param req Request Body: User information to validate with database
     * @return Response Body: authentication token, Success of request
     */
    public LoginRes loginUser(LoginReq req) {
        Database db = new Database();
        LoginRes res;

        try {
            db.openConnection();

            // Find user object associated with given username and password
            UserDao uDao = new UserDao(db.getConnection());
            User user = uDao.login(req.getUserName(), req.getPassword());

            // If not null - generate new authentication token at login
            // Else error - either username or password are incorrect
            if (user != null) {
                // Find any previous authentication token associated with user
                // If found - Remove token from database
                AuthTokenDao aDao = new AuthTokenDao(db.getConnection());
                AuthToken foundToken = aDao.findID(user.getUserID());
                if (foundToken != null) { aDao.remove(foundToken); }

                // Generate and insert a new authentication token into database
                String token = aDao.generateToken();
                aDao.insert(new AuthToken(token, user.getUserID()));

                // Generate response
                res = new LoginRes(token, user.getUserName(), user.getUserID());

                db.closeConnection(true);
            } else {
                res = new LoginRes("error: Username or password incorrect");
                db.closeConnection(false);
            }
        } catch (DataAccessException ex) {
            res = new LoginRes("error: " + ex.getMessage());

            try { db.closeConnection(false); }
            catch(DataAccessException e) { throw new RuntimeException("Unable to close connection to database"); }
        }
        return res;
    }
}
