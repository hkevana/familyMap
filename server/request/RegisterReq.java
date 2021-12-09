package request;

/**
 * Request Body Class for Register requests
 */
public class RegisterReq {
    private String userName;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String gender;

    /**
     * parameterized constructor
     *
     * @param userName associated username
     * @param password associated username
     * @param email email of user
     * @param firstname first name of user
     * @param lastName last name of user
     * @param gender gender of user
     */
    public RegisterReq(String userName, String password, String email,
                       String firstname, String lastName, String gender) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.firstName = firstname;
        this.lastName = lastName;
        this.gender = gender;
    }

    public String getUserName() { return userName; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getGender() { return gender; }

    public void setUserName(String userName) { this.userName = userName; }
    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email) { this.email = email; }
    public void setFirstName(String firstname) { this.firstName = firstname; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setGender(String gender) { this.gender = gender; }

    @Override
    public String toString() {
        return "{ " +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                " }";
    }
}
