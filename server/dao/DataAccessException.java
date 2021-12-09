package dao;

/**
 * Data Access Exception Class
 */
public class DataAccessException extends Exception {
    /**
     * throws exception
     *
     * @param s message received to pass to super class
     */
    DataAccessException(String s) { super(s); }

    /**
     * throws exception
     */
    DataAccessException() { super(); }
}
