package dao;

import model.AuthToken;
import java.sql.*;
import java.util.Random;

/**
 * Data Access Object Class for authentication tokens
 */
public class AuthTokenDao {
    private final Connection connect;

    /**
     * parameterized constructor
     *
     * @param c established connection to database
     */
    public AuthTokenDao(Connection c) { this.connect = c; }

    /**
     * inserts authentication token into database
     *
     * @param at authentication token
     * @throws DataAccessException error while inserting into database
     */
    public void insert(AuthToken at) throws DataAccessException {
        // Remove any previous authentication token associated with user from database
        AuthToken exists = this.findToken(at.getToken());
        if (exists != null) { this.remove(exists); }

        // Insert new authentication token into Database
        final String SQL = "INSERT INTO AuthToken(Token, UserID) VALUES (?,?);";
        try (PreparedStatement stmt = this.connect.prepareStatement(SQL)) {
            stmt.setString(1, at.getToken());
            stmt.setString(2, at.getUserID());

            stmt.executeUpdate();
        } catch(SQLException ex) { throw new DataAccessException(ex.getMessage()); }
    }

    /**
     * remove authentication token from Database
     *
     * @param at authentication token object to delete from Database
     * @throws DataAccessException error while removing from database
     */
    public void remove(AuthToken at) throws DataAccessException {
        final String SQL = "DELETE FROM AuthToken WHERE UserID = ?;";
        try (PreparedStatement stmt = this.connect.prepareStatement(SQL)) {
            stmt.setString(1, at.getUserID());

            stmt.executeUpdate();
        } catch(SQLException ex) { throw new DataAccessException(ex.getMessage()); }
    }

    /**
     * Find authentication token object from the token
     *
     * @param token authentication token associated with UserID
     * @return found authentication token or null if not found
     * @throws DataAccessException error while searching database
     */
    public AuthToken findToken(String token) throws DataAccessException {
        final String SQL = "SELECT * FROM AuthToken WHERE Token = ?;";
        try (PreparedStatement stmt = this.connect.prepareStatement(SQL)) {
            stmt.setString(1, token);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) { return new AuthToken(rs.getString("Token"), rs.getString("UserID")); }
            else { return null; }
        } catch(SQLException ex) { throw new DataAccessException(ex.getMessage()); }
    }

    /**
     * Find autentication token object from UserID
     *
     * @param ID UserID associated with authentication token
     * @return found authentication token or null if not found
     * @throws DataAccessException error while searching database
     */
    public AuthToken findID(String ID) throws DataAccessException {
        final String SQL = "SELECT * FROM AuthToken WHERE UserID = ?;";
        try (PreparedStatement stmt = this.connect.prepareStatement(SQL)) {
            stmt.setString(1, ID);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) { return new AuthToken(rs.getString("Token"), rs.getString("UserID")); }
            else { return null; }
        } catch(SQLException ex) { throw new DataAccessException(ex.getMessage()); }
    }

    /**
     * Validate that token exists in Database
     *
     * @param token authentication token to be found in Database
     * @return found: authentication token not found: null
     * @throws DataAccessException error while searching database
     */
    public String validateToken(String token) throws DataAccessException {
        final String SQL = "SELECT Token FROM AuthToken WHERE Token = ?;";
        try (PreparedStatement stmt = this.connect.prepareStatement(SQL)) {
            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) { return rs.getString("Token"); }
            else { return null; }
        } catch(SQLException ex) { throw new DataAccessException(ex.getMessage()); }
    }

    /**
     * Generate a new authentication token
     *
     * @return new authentication token
     */
    public String generateToken() {
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            int rand = 0;
            // Generate random character:
            // Valid chars: a-z | 1-9 | A-Z
            do { rand = getRandChar(48, 123); }
            while((58 <= rand && rand <= 64) || (91 <= rand && rand <= 96)); // Invalid chars - generate new char
            token.append((char)rand);
        }
        return token.toString();
    }

    /**
     * Generates random number between two values
     *
     * @param min minimum random value
     * @param max maximum random value
     * @return random number between min and max
     */
    private char getRandChar(int min, int max) {
        Random randInt = new Random();
        return (char)(randInt.nextInt(max - min) + min);
    }


}
