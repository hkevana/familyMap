package byu.cs240.familymapclient.res;

public class LoginRes {
    private String userName;
    private String personID;
    private String authToken;
    private String message;
    private boolean success;

    public LoginRes(String userName, String personID, String authToken) {
        this.userName = userName;
        this.personID = personID;
        this.authToken = authToken;

        this.message = null;
        this.success = true;
    }

    public LoginRes(String err) {
        this.message = err;
        this.success = false;

        this.userName = null;
        this.personID = null;
        this.authToken = null;
    }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getPersonID() { return personID; }
    public void setPersonID(String personID) { this.personID = personID; }

    public String getAuthToken() { return authToken; }
    public void setAuthToken(String authToken) { this.authToken = authToken; }

    public String getMessage() { return message; }
    public void setMessage(String err) { this.message = err; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    @Override
    public String toString() {
        return "{ " +
                "userName='" + userName + '\'' +
                ", personID='" + personID + '\'' +
                ", authToken='" + authToken + '\'' +
                ", message='" + message + '\'' +
                ", success=" + success +
                " }";
    }
}
