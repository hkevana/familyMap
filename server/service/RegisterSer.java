package service;

import dao.*;
import model.*;
import request.RegisterReq;
import response.RegisterRes;


/**
 * Service Class for Register requests
 */
public class RegisterSer {

    /**
     * register a new user into database
     *
     * @param req Request Body: User Information to validate with database
     * @return Response Body: authentication token, success of request
     */
    public RegisterRes registerUser(RegisterReq req) {
        Database db = new Database();
        RegisterRes res;

        try {
            db.openConnection();

            // Create unique PersonID for user
            PersonDao pDao = new PersonDao(db.getConnection());
            String ID = pDao.generateID();

            // Create Unique Authentication Token for user
            // Insert Auth Token with paired ID into AuthToken table
            AuthTokenDao aDao = new AuthTokenDao(db.getConnection());
            String token = aDao.generateToken();
            aDao.insert(new AuthToken(token, ID));

            // Create User - insert into database
            UserDao uDao = new UserDao(db.getConnection());
            User user = new User(req.getUserName(), req.getPassword(), req.getEmail(), req.getFirstName(), req.getLastName(), req.getGender(), ID);
            uDao.registerNewUser(user);

            // Generate Person objects w/ associated events for user
            // Fill generations of Persons - default of 4
            Person person = pDao.fill(req.getUserName(), 4);

            // Create related events for each Person in database related to user - 31
            EventDao eDao = new EventDao(db.getConnection());
            eDao.createFamilyEvents(person);

            // Generate Response Data for Response Body
            res = new RegisterRes(token, req.getUserName(), ID);

            db.closeConnection(true);
        } catch (DataAccessException ex) {
            res = new RegisterRes("error:" + ex.getMessage());

            try { db.closeConnection(false); }
            catch (DataAccessException e) { throw new RuntimeException("Unable to close connection to database"); }
        }
        return res;
    }
}
