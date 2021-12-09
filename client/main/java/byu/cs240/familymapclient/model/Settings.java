package byu.cs240.familymapclient.model;

public class Settings {

    private static Settings _instance;

    public static Settings getInstance() {
        if (_instance == null) { _instance = new Settings(); }
        return _instance;
    }

    private boolean drawLifeLines;
    private boolean drawFamilyLines;
    private boolean drawSpouseLine;
    private boolean displayFatherSide;
    private boolean displayMotherSide;
    private boolean displayMaleEvents;
    private boolean displayFemaleEvents;

    private Settings() {
        turnOnAllSettings();
    }

    public boolean drawLifeLines()    { return drawLifeLines; }
    public boolean drawFamilyLines()  { return drawFamilyLines; }
    public boolean drawSpouseLines()  { return drawSpouseLine; }
    public boolean displayFatherSide()   { return displayFatherSide; }
    public boolean displayMotherSide()   { return displayMotherSide; }
    public boolean displayMaleEvents()   { return displayMaleEvents; }
    public boolean displayFemaleEvents() { return displayFemaleEvents; }

    public void toggleDrawLifeLines()    { drawLifeLines   = !drawLifeLines; }
    public void toggleDrawFamilyLines()  { drawFamilyLines = !drawFamilyLines; }
    public void toggleDrawSpouseLine()   { drawSpouseLine  = !drawSpouseLine; }
    public void toggleDisplayFatherEvents() { displayFatherSide = !displayFatherSide; }
    public void toggleDisplayMotherEvents() { displayMotherSide = !displayMotherSide; }
    public void toggleDisplayMaleEvents()   { displayMaleEvents = !displayMaleEvents; }
    public void toggleDisplayFemaleLines()  { displayFemaleEvents = !displayFemaleEvents; }

    public void turnOnAllSettings() {
        drawLifeLines   = true;
        drawFamilyLines = true;
        drawSpouseLine  = true;
        displayFatherSide = true;
        displayMotherSide = true;
        displayMaleEvents = true;
        displayFemaleEvents = true;
    }

    public void turnOffAllSettings() {
        drawLifeLines   = false;
        drawFamilyLines = false;
        drawSpouseLine  = false;
        displayFatherSide = false;
        displayMotherSide = false;
        displayMaleEvents = false;
        displayFemaleEvents = false;
    }

    @Override
    public String toString() {
        return "Settings: { " +
                "LifeLines=" + drawLifeLines +
                ", FamilyLines=" + drawFamilyLines +
                ", SpouseLine=" + drawSpouseLine +
                ", FatherSide=" + displayFatherSide +
                ", MotherSide=" + displayMotherSide +
                ", MaleEvents=" + displayMaleEvents +
                ", FemaleEvents=" + displayFemaleEvents +
                " }";
    }
}
