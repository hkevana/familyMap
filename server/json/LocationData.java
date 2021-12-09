package json;

import java.util.Random;

public class LocationData {
    private Location[] data;

    public LocationData(Location[] data) { this.data = data; }


    public Location[] getData() { return data; }
    public void setData(Location[] data) { this.data = data; }

    public Location getRandLocation() { return this.data[getRandIndex(this.data.length)]; }

    private int getRandIndex(int maxIndex) { return new Random().nextInt(maxIndex); }

}
