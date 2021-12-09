package model;

import java.util.Objects;

/**
 * Data Model Class for authentication token Objects
 */
public class AuthToken {
    private String token;
    private String userID;

    /**
     * parameterized constructor
     *
     * @param t  authentication token
     * @param ID Associated User ID
     */
    public AuthToken(String t, String ID) {
        this.token = t;
        this.userID = ID;
    }

    public String getToken() { return token; }
    public String getUserID() { return userID; }
    public void setToken(String token) { this.token = token; }
    public void setUserID(String userID) { this.userID = userID; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthToken authToken = (AuthToken) o;
        return Objects.equals(token, authToken.token) &&
                Objects.equals(userID, authToken.userID);
    }
}
