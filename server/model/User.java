package model;

import java.util.Objects;

/**
 * Data Model Class for User Objects
 */
public class User {
    private String userName;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String gender;
    private String personID;

    /**
     * parameterized constructor
     *
     * @param username username of user
     * @param password encrypted password of user
     * @param email email of user
     * @param firstName first name of user
     * @param lastName last name of user
     * @param gender gender of user
     * @param userID ID of user
     */
    public User(String username, String password, String email, String firstName,
                String lastName, String gender, String userID) {
        this.userName = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.personID = userID;
    }

    public String getUserName() { return userName; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getGender() { return gender; }
    public String getUserID() { return personID; }

    public void setUserName(String username) { this.userName = username; }
    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email) { this.email = email; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setGender(String gender) { this.gender = gender; }
    public void setUserID(String userID) { this.personID = userID; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userName, user.userName) &&
                Objects.equals(password, user.password) &&
                Objects.equals(email, user.email) &&
                Objects.equals(firstName, user.firstName) &&
                Objects.equals(lastName, user.lastName) &&
                Objects.equals(gender, user.gender) &&
                Objects.equals(personID, user.personID);
    }
}
