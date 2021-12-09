package json;

public class Location {
    private float latitude;
    private float longitude;
    private String city;
    private String country;


    public Location(float latitude, float longitude, String city, String country) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.country = country;
    }

    public float getLatitude() { return latitude; }
    public String getCountry() { return country; }
    public float getLongitude() { return longitude; }
    public String getCity() { return city; }

    public void setLatitude(float latitude) { this.latitude = latitude; }
    public void setLongitude(float longitude) { this.longitude = longitude; }
    public void setCity(String city) { this.city = city; }
    public void setCountry(String country) { this.country = country; }
}
