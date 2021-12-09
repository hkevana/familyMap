package byu.cs240.familymapclient.req;

public class DataReq {
    private String personID;
    private String authToken;

    public DataReq(String personID, String authToken) {
        this.personID = personID;
        this.authToken = authToken;
    }

    public String getPersonID() { return personID; }
    public void setPersonID(String personID) { this.personID = personID; }

    public String getAuthToken() { return authToken; }
    public void setAuthToken(String authToken) { this.authToken = authToken; }

    @Override
    public String toString() {
        return "{ " +
                "personID='" + personID + '\'' +
                ", authToken='" + authToken + '\'' +
                " }";
    }
}
