package byu.cs240.familymapclient.model;

public class Event {
    private String eventID;
    private String associatedUsername;
    private String personID;
    private float latitude;
    private float longitude;
    private String country;
    private String city;
    private String eventType;
    private int year;

    /**
     * parameterized constructor
     *
     * @param eventID ID associated with event
     * @param username associated username
     * @param personID ID of person object associated with event
     * @param latitude latitudinal coordinates of event
     * @param longitude longitudinal coordinates of event
     * @param country country of event
     * @param city city of event
     * @param eventType type of event
     * @param year year of event
     */
    public Event(String eventID, String username, String personID, float latitude,
                 float longitude,String country, String city, String eventType, int year) {
        this.eventID = eventID;
        this.associatedUsername = username;
        this.personID = personID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
        this.eventType = eventType;
        this.year = year;
    }

    public String getEventID() { return eventID; }
    public String getUsername() { return associatedUsername; }
    public String getPersonID() { return personID; }
    public float getLatitude() { return latitude; }
    public float getLongitude() { return longitude; }
    public String getCountry() { return country; }
    public String getCity() { return city; }
    public String getEventType() { return eventType; }
    public int getYear() { return year; }

    public void setEventID(String eventID) { this.eventID = eventID; }
    public void setUsername(String username) { this.associatedUsername = username; }
    public void setPersonID(String personID) { this.personID = personID; }
    public void setLatitude(float latitude) { this.latitude = latitude; }
    public void setLongitude(float longitude) { this.longitude = longitude; }
    public void setCountry(String country) { this.country = country; }
    public void setCity(String city) { this.city = city; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public void setYear(int year) { this.year = year; }

    @Override
    public boolean equals(Object o) {
        if (o == null) { return false; }
        if (o instanceof Event) {
            Event oEvent = (Event) o;
            return oEvent.getEventID().equals(this.getEventID()) &&
                    oEvent.getUsername().equals(this.getUsername()) &&
                    oEvent.getPersonID().equals(this.getPersonID()) &&
                    oEvent.getLatitude() == this.getLatitude() &&
                    oEvent.getLongitude() == this.getLongitude() &&
                    oEvent.getCountry().equals(this.getCountry()) &&
                    oEvent.getCity().equals(this.getCity()) &&
                    oEvent.getEventType().equals(this.getEventType()) &&
                    oEvent.getYear() == this.getYear();
        } else { return false; }
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventID='" + eventID + '\'' +
                ", associatedUsername='" + associatedUsername + '\'' +
                ", personID='" + personID + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", eventType='" + eventType + '\'' +
                ", year=" + year +
                '}';
    }
}
