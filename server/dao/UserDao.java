package dao;

import model.User;

import javax.xml.crypto.Data;
import java.sql.*;

/**
 * Data Access Object Class for users
 */
public class UserDao {
    private final Connection connect;

    /**
     * parameterized constructor
     *
     * @param c established connection to database
     */
    public UserDao(Connection c) { this.connect = c; }

    /**
     * creates a new user in database
     *
     * @param u user object to be inserted into database
     * @throws DataAccessException error while inserting into database
     */
    public void registerNewUser(User u) throws DataAccessException {
        final String SQL = "INSERT INTO User (UserName, Password, Email, Firstname, LastName, Gender, PersonID) VALUES(?,?,?,?,?,?,?)";

        try (PreparedStatement stmt = this.connect.prepareStatement(SQL)) {
            stmt.setString(1, u.getUserName());
            stmt.setString(2, u.getPassword());
            stmt.setString(3, u.getEmail());
            stmt.setString(4, u.getFirstName());
            stmt.setString(5, u.getLastName());
            stmt.setString(6, u.getGender());
            stmt.setString(7, u.getUserID());

            stmt.executeUpdate();
        } catch(SQLException ex) { throw new DataAccessException(ex.getMessage()); }
    }

    /**
     * finds a user in database
     *
     * @param u user object to find in database
     * @return User if exists, otherwise null
     * @throws DataAccessException error while searching database
     */
    public User login(String u, String p) throws DataAccessException {
        final String SQL = "SELECT * FROM User WHERE UserName = ? AND Password = ?;";
        try (PreparedStatement stmt = this.connect.prepareStatement(SQL)) {
            stmt.setString(1, u);
            stmt.setString(2, p);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getString("UserName"), rs.getString("Password"), rs.getString("Email"),
                        rs.getString("FirstName"), rs.getString("LastName"), rs.getString("Gender"), rs.getString("PersonID"));
            }
            else { return null; }
        } catch (SQLException ex) { throw new DataAccessException(ex.getMessage()); }
    }

    /**
     * Find User Object by ID
     *
     * @param ID ID of User to find
     * @return User found in database associated with given User ID
     * @throws DataAccessException error while searching the database
     */
    public User findByID(String ID) throws DataAccessException {
        final String SQL = "SELECT * FROM User WHERE PersonID = ?;";

        try (PreparedStatement stmt = this.connect.prepareStatement(SQL)) {
            stmt.setString(1, ID);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getString("UserName"), rs.getString("Password"), rs.getString("Email"),
                        rs.getString("FirstName"), rs.getString("LastName"), rs.getString("Gender"), rs.getString("PersonID"));
            }
            else { return null; }
        } catch(SQLException ex) { throw new DataAccessException(ex.getMessage()); }
    }

    /**
     * Find User Object from username
     *
     * @param username username of User to find in database
     * @return User Object found in database associated with given username
     * @throws DataAccessException error while searching the database
     */
    public User findByUsername(String username) throws DataAccessException {
        final String SQL = "SELECT * FROM User WHERE UserName = ?;";

        try (PreparedStatement stmt = this.connect.prepareStatement(SQL)) {
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getString("UserName"), rs.getString("Password"), rs.getString("Email"),
                        rs.getString("FirstName"), rs.getString("LastName"), rs.getString("Gender"), rs.getString("PersonID"));
            } else { return null; }
        } catch(SQLException ex) { throw new DataAccessException(ex.getMessage()); }
    }

    /**
     * Clear all Users from database
     *
     * @throws DataAccessException error while searching database
     */
    public void clear() throws DataAccessException {
        final String SQL = "DELETE FROM User";

        try (Statement stmt = this.connect.createStatement()){ stmt.executeUpdate(SQL); }
        catch (SQLException ex) { throw new DataAccessException("error while clearing data from database"); }
    }



}
