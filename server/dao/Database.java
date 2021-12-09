package dao;

import java.sql.*;

/**
 * Database Class: connects to SQLite database
 */
public class Database {
    private Connection connect;

    /**
     * loads database driver at runtime
     *
     * @throws ClassNotFoundException no database driver found
     */
    public void loadDriver() throws ClassNotFoundException {
        final String driver = "org.sqlite.JDBC";
        Class.forName(driver);
    }

    /**
     * Opens a connection to SQLite database
     *
     * @return established connection to database
     * @throws DataAccessException error while connecting to database
     */
    public Connection openConnection() throws DataAccessException {
        try  {
            // Structure for Connection is driver:language:path
            // Path starts in root of project unless otherwise specified
            final String CONNECTION_URL = "jdbc:sqlite:FamilyMap.sqlite";

            // Open a database connection to file given in CONNECTION_URL
            this.connect = DriverManager.getConnection(CONNECTION_URL);
            // Start a transaction
            this.connect.setAutoCommit(false);

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException("Unable to establish connection to database");
        }
        return this.connect;
    }

    /**
     * get established connection to database or open new one if null
     *
     * @return established connection
     * @throws DataAccessException
     */
    public Connection getConnection() throws DataAccessException {
        if (this.connect == null) { return openConnection(); }
        else { return this.connect; }
    }

    /**
     * closes connection to database
     *
     * @param commit true: commit changes to database, false: rollback changes to start of transaction
     * @throws DataAccessException error closing connection to database
     */
    public void closeConnection(boolean commit) throws DataAccessException {
        try {
            // If no problems were encountered commit changes to database
            // Otherwise rollback any changes made since start of transaction
            if (commit) { this.connect.commit(); }
            else { connect.rollback(); }

            this.connect.close();
            this.connect = null;
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException("Unable to close database connection");
        }
    }

    /**
     * clears all data inside of database
     *
     * @throws DataAccessException error while clearing database
     */
    public void clear() throws DataAccessException {
        final String SQL_CLEAR_TABLES = "DELETE FROM User;" +
                                        "DELETE FROM Person;" +
                                        "DELETE FROM AuthToken;" +
                                        "DELETE FROM Event;";
        try (Statement stmt = this.connect.createStatement()) { stmt.executeUpdate(SQL_CLEAR_TABLES); }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException(ex.getMessage());
        }
    }

    /**
     * Drop all tables from database - Person, User, AuthToken, Event
     */
    public void dropTables() throws DataAccessException {
        final String SQL_DROP_TABLES = "DROP TABLE IF EXISTS Person; " +
                                       "DROP TABLE IF EXISTS User; " +
                                       "DROP TABLE IF EXISTS Event; " +
                                       "DROP TABLE IF EXISTS AuthToken;";
        try (Statement stmt = this.connect.createStatement()) { stmt.executeUpdate(SQL_DROP_TABLES); }
        catch(SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException(ex.getMessage());
        }
    }

    /**
     * Creates Person, User, Event, AuthToken tables in Database
     */
    public void createTables() throws DataAccessException {
        final String USER = getCreateUserTableStmt();
        final String PERSON = getCreatePersonTableStmt();
        final String AUTH_TOKEN = getCreateAuthTokenTableStmt();
        final String EVENT = getCreateEventTableStmt();

        final String SQL_CREATE_ALL_TABLES = USER + PERSON + AUTH_TOKEN + EVENT;

        try (Statement stmt = this.connect.createStatement()) { stmt.executeUpdate(SQL_CREATE_ALL_TABLES); }
        catch(SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException(ex.getMessage());
        }
    }
    private String getCreatePersonTableStmt() {
        return "CREATE TABLE IF NOT EXISTS Person (" +
                "PersonID TEXT NOT NULL UNIQUE," +
                "AssociateUserName TEXT NOT NULL," +
                "FirstName TEXT NOT NULL," +
                "LastName TEXT NOT NULL," +
                "Gender TEXT NOT NULL," +
                "FatherID TEXT," +
                "MotherID TEXT," +
                "SpouseID TEXT," +
                "CONSTRAINT checkGender CHECK (Gender in ('f', 'm'))," +
                "FOREIGN KEY(AssociateUserName) REFERENCES User(UserName)" +
                ");";
    }
    private String getCreateUserTableStmt() {
        return "CREATE TABLE IF NOT EXISTS User (" +
                "UserName TEXT NOT NULL UNIQUE," +
                "Password TEXT NOT NULL," +
                "Email TEXT NOT NULL," +
                "FirstName TEXT NOT NULL," +
                "LastName TEXT NOT NULL," +
                "Gender TEXT NOT NULL," +
                "PersonID TEXT NOT NULL UNIQUE PRIMARY KEY," +
                "CONSTRAINT checkGender CHECK (Gender in ('f', 'm'))" +
                ");";
    }
    private String getCreateEventTableStmt() {
        return "CREATE TABLE IF NOT EXISTS Event (" +
                "EventID TEXT NOT NULL UNIQUE PRIMARY KEY," +
                "UserName TEXT NOT NULL," +
                "PersonID TEXT NOT NULL," +
                "Latitude FLOAT NOT NULL," +
                "Longitude FLOAT NOT NULL," +
                "Country TEXT NOT NULL," +
                "City TEXT NOT NULL," +
                "EventType TEXT NOT NULL," +
                "Year INT NOT NULL," +
                "FOREIGN KEY(PersonID) REFERENCES Person(PersonID)," +
                "FOREIGN KEY(UserName) REFERENCES User(UserName)" +
                ");";
    }
    private String getCreateAuthTokenTableStmt() {
        return "CREATE TABLE IF NOT EXISTS AuthToken (" +
                "Token TEXT NOT NULL UNIQUE PRIMARY KEY," +
                "UserID TEXT NOT NULL UNIQUE," +
                "FOREIGN KEY(UserID) REFERENCES User(UserID)" +
                ");";
    }
}
