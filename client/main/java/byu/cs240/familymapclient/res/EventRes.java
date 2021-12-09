package byu.cs240.familymapclient.res;

import java.util.Arrays;

import byu.cs240.familymapclient.model.Event;

public class EventRes {
    private String associatedUsername;
    private String eventID;
    private String personID;
    private float latitude;
    private float longitude;
    private String country;
    private String city;
    private String eventType;
    private int year;
    private boolean success;
    private Event[] data;
    private String message;

    /**
     * parameterized constructor for single event
     *
     * @param userName associated username
     * @param eventID ID of event
     * @param personID ID of person associated with event
     * @param latitude latitudinal coordinates of event
     * @param longitude longitudinal coordinates of event
     * @param country country of event
     * @param city city of event
     * @param eventType type of event
     * @param year year of event
     */
    public EventRes(String userName, String eventID, String personID, float latitude, float longitude, String country,
                    String city, String eventType, int year) {
        this.associatedUsername = userName;
        this.eventID = eventID;
        this.personID = personID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
        this.eventType = eventType;
        this.year = year;
        this.success = true;

        this.data = null;
        this.message = null;
    }

    /**
     * parameterized constructor for array of events
     *
     * @param data array of events
     */
    public EventRes(Event[] data) {
        this.data = data;
        this.success = true;

        this.associatedUsername = null;
        this.eventID = null;
        this.personID = null;
        this.latitude = 0;
        this.longitude = 0;
        this.country = null;
        this.city = null;
        this.eventType = null;
        this.year = 0;
        this.message = null;
    }

    /**
     * parameterized constructor for failed request
     *
     * @param err error message
     */
    public EventRes(String err) {
        this.message = err;
        this.success = false;

        this.associatedUsername = null;
        this.eventID = null;
        this.personID = null;
        this.latitude = 0;
        this.longitude = 0;
        this.country = null;
        this.city = null;
        this.eventType = null;
        this.year = 0;
        this.data = null;
    }

    public String getUserName() { return associatedUsername; }
    public String getEventID() { return eventID; }
    public String getPersonID() { return personID; }
    public float getLatitude() { return latitude; }
    public float getLongitude() { return longitude; }
    public String getCountry() { return country; }
    public String getCity() { return city; }
    public String getEventType() { return eventType; }
    public int getYear() { return year; }
    public boolean isSuccess() { return success; }
    public Event[] getData() { return data; }
    public String getMessage() { return message; }

    public void setUserName(String userName) { this.associatedUsername = userName; }
    public void setEventID(String eventID) { this.eventID = eventID; }
    public void setPersonID(String personID) { this.personID = personID; }
    public void setLatitude(float latitude) { this.latitude = latitude; }
    public void setLongitude(float longitude) { this.longitude = longitude; }
    public void setCountry(String country) { this.country = country; }
    public void setCity(String city) { this.city = city; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public void setYear(int year) { this.year = year; }
    public void setSuccess(boolean success) { this.success = success; }
    public void setData(Event[] data) { this.data = data; }
    public void setMessage(String message){ this.message = message; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventRes eventRes = (EventRes) o;
        return  latitude == eventRes.latitude &&
                longitude == eventRes.longitude &&
                year == eventRes.year &&
                success == eventRes.success &&
                associatedUsername.equals(eventRes.associatedUsername) &&
                eventID.equals(eventRes.eventID) &&
                personID.equals(eventRes.personID) &&
                country.equals(eventRes.country) &&
                city.equals(eventRes.city) &&
                eventType.equals(eventRes.eventType) &&
                message.equals(eventRes.message) &&
                Arrays.equals(data, eventRes.data);
    }

    @Override
    public String toString() {
        return "{ " +
                "userName='" + associatedUsername + '\'' +
                ", eventID='" + eventID + '\'' +
                ", personID='" + personID + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", eventType='" + eventType + '\'' +
                ", year=" + year +
                ", success=" + success +
                ", data=" + data +
                ", message='" + message + '\'' +
                " }";
    }
}
