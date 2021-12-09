package byu.cs240.familymapclient.model;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.HashMap;
import java.util.Map;

public class MapColor {

    private static MapColor _instance;
    private static int currColorIndex;

    public static MapColor getInstance() {
        if (_instance == null) { _instance = new MapColor(); }
        return _instance;
    }

    private MapColor() {
        colors = new HashMap<>();
        currColorIndex = 0;
        createColors();
    }

//  private enum colorIndex { RED, CYAN,  MAGENTA, YELLOW, BLUE, ORANGE, GREEN, ROSE, AZURE, VIOLET } --> Order of colors
    private Map<String, Float> colors;

    private void createColors() {
        colors.put("RED", BitmapDescriptorFactory.HUE_RED);
        colors.put("CYAN", BitmapDescriptorFactory.HUE_CYAN);
        colors.put("YELLOW", BitmapDescriptorFactory.HUE_YELLOW);
        colors.put("MAGENTA", BitmapDescriptorFactory.HUE_MAGENTA);
        colors.put("AZURE", BitmapDescriptorFactory.HUE_AZURE);
        colors.put("BLUE", BitmapDescriptorFactory.HUE_BLUE);
        colors.put("GREEN", BitmapDescriptorFactory.HUE_GREEN);
        colors.put("ROSE", BitmapDescriptorFactory.HUE_ROSE);
        colors.put("VIOLET", BitmapDescriptorFactory.HUE_VIOLET);
        colors.put("ORANGE", BitmapDescriptorFactory.HUE_ORANGE);
    }

    public Float getColor(String color) { return colors.get(color); }
    public Float getNextColor() {
        currColorIndex %= 10;

        switch(currColorIndex++) {
            case 1: return colors.get("CYAN");
            case 2: return colors.get("MAGENTA");
            case 3: return colors.get("YELLOW");
            case 4: return colors.get("BLUE");
            case 5: return colors.get("ORANGE");
            case 6: return colors.get("GREEN");
            case 7: return colors.get("ROSE");
            case 8: return colors.get("AZURE");
            case 9: return colors.get("VIOLET");
            default: return colors.get("RED");
        }
    }
}
