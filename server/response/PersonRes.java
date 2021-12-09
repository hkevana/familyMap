package response;

import model.Person;

import java.util.Arrays;
import java.util.Objects;

/**
 * Response Body Class for Person requests
 */
public class PersonRes {
    private String associatedUsername;
    private String personID;
    private String firstName;
    private String lastName;
    private String gender;
    private String fatherID;
    private String motherID;
    private String spouseID;
    private String message;
    private boolean success;
    private Person[] data;

    /**
     * parameterized constructor for single person
     *
     * @param userName associated username of Person Object
     * @param personID unique ID of Person Object
     * @param firstname first name of person
     * @param lastname last name of person
     * @param gender gender of person
     * @param fatherID associated ID of father Person Object
     * @param motherID associated ID of mother Person Object
     * @param spouseID associated ID of spouse Person Object
     */
    public PersonRes(String userName, String personID, String firstname, String lastname, String gender,
                     String fatherID, String motherID, String spouseID) {
        this.associatedUsername = userName;
        this.personID = personID;
        this.firstName = firstname;
        this.lastName = lastname;
        this.gender = gender;
        this.fatherID = fatherID;
        this.motherID = motherID;
        this.spouseID = spouseID;
        this.success = true;

        this.message = null;
        this.data = null;
    }

    /**
     * parameterized constructor for array of Person Objects
     *
     * @param data array of Person Objects
     */
    public PersonRes(Person[] data) {
        this.data = data;
        this.success = true;

        this.associatedUsername = null;
        this.personID = null;
        this.firstName = null;
        this.lastName = null;
        this.gender = null;
        this.fatherID = null;
        this.motherID = null;
        this.spouseID = null;
        this.message = null;
    }

    /**
     * Error message constructor for failed requests
     */
    public PersonRes(String err) {
        this.message = err;
        this.success = false;

        this.associatedUsername = null;
        this.personID = null;
        this.firstName = null;
        this.lastName = null;
        this.gender = null;
        this.fatherID = null;
        this.motherID = null;
        this.spouseID = null;
        this.data = null;
    }

    public String getUserName() { return associatedUsername; }
    public String getPersonID() { return personID; }
    public String getFirstname() { return firstName; }
    public String getLastname() { return lastName; }
    public String getGender() { return gender; }
    public String getFatherID() { return fatherID; }
    public String getMotherID() { return motherID; }
    public String getSpouseID() { return spouseID; }
    public String getMessage() { return message; }
    public boolean isSuccess() { return success; }
    public Person[] getData() { return data; }

    public void setUserName(String userName) { this.associatedUsername = userName; }
    public void setPersonID(String personID) { this.personID = personID; }
    public void setFirstname(String firstname) { this.firstName = firstname; }
    public void setLastname(String lastname) { this.lastName = lastname; }
    public void setGender(String gender) { this.gender = gender; }
    public void setFatherID(String fatherID) { this.fatherID = fatherID; }
    public void setMotherID(String motherID) { this.motherID = motherID; }
    public void setSpouseID(String spouseID) { this.spouseID = spouseID; }
    public void setMessage(String message) { this.message = message; }
    public void setSuccess(boolean success) { this.success = success; }
    public void setData(Person[] data) { this.data = data; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonRes personRes = (PersonRes) o;
        return success == personRes.success &&
                Objects.equals(associatedUsername, personRes.associatedUsername) &&
                Objects.equals(personID, personRes.personID) &&
                Objects.equals(firstName, personRes.firstName) &&
                Objects.equals(lastName, personRes.lastName) &&
                Objects.equals(gender, personRes.gender) &&
                Objects.equals(fatherID, personRes.fatherID) &&
                Objects.equals(motherID, personRes.motherID) &&
                Objects.equals(spouseID, personRes.spouseID) &&
                Objects.equals(message, personRes.message) &&
                Arrays.equals(data, personRes.data);
    }
    @Override
    public String toString() {
        return "{ " +
                "userName='" + associatedUsername + '\'' +
                ", personID='" + personID + '\'' +
                ", firstname='" + firstName + '\'' +
                ", lastname='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                ", fatherID='" + fatherID + '\'' +
                ", motherID='" + motherID + '\'' +
                ", spouseID='" + spouseID + '\'' +
                ", message='" + message + '\'' +
                ", success=" + success +
                ", data=" + data +
                " }";
    }
}
