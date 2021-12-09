package byu.cs240.familymapclient.res;

import byu.cs240.familymapclient.model.Event;
import byu.cs240.familymapclient.model.Person;

public class DataRes {
    private Person[] personData;
    private Event[] eventData;
    private String userID;
    private boolean success;
    private String message;

    public DataRes(Person[] personData, Event[] eventData, String userID, boolean success, String message) {
        this.personData = personData;
        this.eventData = eventData;
        this.userID = userID;
        this.success = success;
        this.message = message;
    }

    public Person[] getPersonData() { return personData; }
    public void setPersonData(Person[] data) { this.personData = data; }

    public Event[] getEventData() { return eventData; }
    public void setEventData(Event[] data) { this.eventData = data; }

    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    @Override
    public String toString() {
        return "{ " +
                "personData=" + personData +
                "eventData=" + eventData +
                ", success=" + success +
                ", message='" + message + '\'' +
                " }";
    }
}
