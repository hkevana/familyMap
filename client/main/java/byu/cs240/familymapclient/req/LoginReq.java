package byu.cs240.familymapclient.req;

public class LoginReq {

    private String userName;
    private String password;

    public LoginReq(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return "{ userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                " }";
    }
}
