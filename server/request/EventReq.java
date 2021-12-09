package request;

/**
 * Request Body Class for Event requests
 */
public class EventReq {
    private String eventID;
    private String authToken;

    /**
     * parameterized constructor
     *
     * @param id ID of event
     * @param token authentication token
     */
    public EventReq(String id, String token) {
        this.eventID = id;
        this.authToken = token;
    }

    public String getEventID() { return this.eventID; }
    public void setEventID(String id) { this.eventID = id; }

    public String getAuthToken() { return authToken; }
    public void setAuthToken(String authToken) { this.authToken = authToken; }

    @Override
    public String toString() {
        return "{ " +
                "eventID='" + eventID + '\'' +
                ", authToken='" + authToken + '\'' +
                " }";
    }
}
