package request;

/**
 * Request Body Class for Login requests
 */
public class LoginReq {
    private String userName;
    private String password;

    /**
     * parameterized constructor
     *
     * @param userName associated username
     * @param password associated password
     */
    public LoginReq(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() { return userName; }
    public String getPassword() { return password; }

    public void setUserName(String userName) { this.userName = userName; }
    public void setPassword(String password) { this.password = password; }
}
