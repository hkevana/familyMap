package json;

import java.util.Random;

public class Names {
    private String[] data;

    public Names(String[] data) { this.data = data; }


    public String[] getData() { return data; }
    public void setData(String[] data) { this.data = data; }

    public String getRandName() { return data[getRandIndex(this.data.length)]; }

    private int getRandIndex(int maxIndex) { return new Random().nextInt(maxIndex); }
}
