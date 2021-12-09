package model;

import java.util.Objects;

/**
 * Data Model Class for Person Objects
 */
public class Person {
    private String personID;
    private String associatedUsername;
    private String firstName;
    private String lastName;
    private String gender;
    private String fatherID;
    private String motherID;
    private String spouseID;

    /**
     * parameterized constructor
     *
     * @param personID ID of person object
     * @param username associated username of person object
     * @param firstname first name of person
     * @param lastName last name of person
     * @param gender gender of person
     * @param fatherID ID of person's father person object
     * @param motherID ID of person's mother person object
     * @param spouseID ID of person's spouse person object
     */
    public Person(String personID, String username, String firstname, String lastName,
                  String gender, String fatherID, String motherID, String spouseID) {
        this.personID = personID;
        this.associatedUsername = username;
        this.firstName = firstname;
        this.lastName = lastName;
        this.gender = gender;
        this.fatherID = fatherID;
        this.motherID = motherID;
        this.spouseID = spouseID;
    }

    public String getPersonID() { return personID; }
    public String getUsername() { return associatedUsername; }
    public String getFirstname() { return firstName; }
    public String getLastName() { return lastName; }
    public String getGender() { return gender; }
    public String getFatherID() { return fatherID; }
    public String getMotherID() { return motherID; }
    public String getSpouseID() { return spouseID; }

    public void setPersonID(String personID) { this.personID = personID; }
    public void setUsername(String username) { this.associatedUsername = username; }
    public void setFirstname(String firstname) { this.firstName = firstname; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setGender(String gender) { this.gender = gender; }
    public void setFatherID(String fatherID) { this.fatherID = fatherID; }
    public void setMotherID(String motherID) { this.motherID = motherID; }
    public void setSpouseID(String spouseID) { this.spouseID = spouseID; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(personID, person.personID) &&
                Objects.equals(associatedUsername, person.associatedUsername) &&
                Objects.equals(firstName, person.firstName) &&
                Objects.equals(lastName, person.lastName) &&
                Objects.equals(gender, person.gender) &&
                Objects.equals(fatherID, person.fatherID) &&
                Objects.equals(motherID, person.motherID) &&
                Objects.equals(spouseID, person.spouseID);
    }
}
