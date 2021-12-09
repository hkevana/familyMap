package response;

/**
 * Response Body Class for Login requests
 */
public class LoginRes {
    private String authToken;
    private String userName;
    private String personID;
    private boolean success;
    private String message;

    /**
     * parameterized constructor for successful requests
     *
     * @param authToken generated authentication token associated with user
     * @param userName associated username
     * @param personID associated ID of Person Object
     */
    public LoginRes(String authToken, String userName, String personID) {
        this.authToken = authToken;
        this.userName = userName;
        this.personID = personID;
        this.success = true;

        this.message = null;
    }

    /**
     * parameterized constructor for failed requests
     *
     * @param err error message
     */
    public LoginRes(String err) {
        this.message = err;
        this.success = false;

        this.authToken = null;
        this.userName = null;
        this.personID = null;
    }

    public String getAuthToken() { return authToken; }
    public String getUserName() { return userName; }
    public String getPersonID() { return personID; }
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }

    public void setAuthToken(String authToken) { this.authToken = authToken; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setPersonID(String personID) { this.personID = personID; }
    public void setSuccess(boolean success) { this.success = success; }
    public void setMessage(String errMessage) { this.message = errMessage; }

    @Override
    public String toString() {
        return "{ " +
                "authToken='" + authToken + '\'' +
                ", userName='" + userName + '\'' +
                ", personID='" + personID + '\'' +
                ", success=" + success +
                ", message='" + message + '\'' +
                " }";
    }
}
