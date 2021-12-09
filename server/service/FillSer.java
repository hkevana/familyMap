package service;

import dao.*;
import model.Person;
import model.User;
import request.FillReq;
import response.FillRes;

/**
 * Service Class for Fill requests
 */
public class FillSer {

    /**
     * fills geneology of user given
     *
     * @param req Request Body: Username and number of generations to fill
     * @return Response Body: success of request
     */
    public FillRes fill(FillReq req) {
        Database db = new Database();
        FillRes res;

        try {
            db.openConnection();

            UserDao uDao = new UserDao(db.getConnection());
            User user = uDao.findByUsername(req.getUserName());

            if (user != null) {
                // Remove any previous persons associated with this username
                // Fill database with new data
                PersonDao pDao = new PersonDao(db.getConnection());
                pDao.remove(req.getUserName());
                Person p = pDao.fill(req.getUserName(), req.getGenerations());

                // Remove any previous events associated with this username
                // Create related events for each Person in database related to user
                EventDao eDao = new EventDao(db.getConnection());
                eDao.remove(req.getUserName());
                eDao.createFamilyEvents(p);

                // Return success message
                String message = "Successfully added " + pDao.getNewPersons() + " persons and " + eDao.getNumNewEvents() + " events to the database";
                res = new FillRes(message, true);

                db.closeConnection(true);
            } else {
                res = new FillRes("error: Username not found", false);
                db.closeConnection(false);
            }
        } catch (DataAccessException ex) {
            res = new FillRes("error: " + ex.getMessage(), false);

            try { db.closeConnection(false); }
            catch(DataAccessException e) { throw new RuntimeException("Unable to close connection to database"); }
        }
        return res;
    }
}
