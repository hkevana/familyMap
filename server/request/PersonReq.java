package request;

/**
 * Request Body Class for Person requests
 */
public class PersonReq {
    private String personID;
    private String userID;

    /**
     * parameterized constructor
     *
     * @param PID Person ID of Person object
     * @param UID User ID of associated username
     */
    public PersonReq(String PID, String UID) {
        this.personID = PID;
        this.userID = UID;
    }

    public String getPersonID() { return this.personID; }
    public void setPersonID(String id) { this.personID = id; }

    public String getUserID() { return userID; }
    public void setUserID(String authToken) { this.userID = authToken; }

    @Override
    public String toString() {
        return "{ personID='" + personID + "', userID='" + userID + "' }";
    }
}
