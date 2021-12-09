package byu.cs240.familymapclient.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.List;
import java.util.Map;

import byu.cs240.familymapclient.R;
import byu.cs240.familymapclient.model.DataCache;
import byu.cs240.familymapclient.model.Event;
import byu.cs240.familymapclient.model.Person;
import byu.cs240.familymapclient.model.Settings;

public class MapsFragment extends Fragment {
    private final static String TAG = "MapsFragment";
    private final static String PERSON_ID = "PERSON_ID";
    private final static String EVENT_TAG = "EVENT_TAG";
    private final static int REQUEST_CODE_EVENT_MAP = 3;


    private Settings settings;
    private DataCache data;

    private boolean mainMapActivity;

    private ImageView settingsImageView;
    private ImageView searchImageView;
    private ImageView upButton;

    private GoogleMap map;
    private Event currEvent;
    private Person currPerson;
    private TextView eventInfoTextView;
    private TextView personInfoTextView;
    private ImageView genderIconImageView;
    private LinearLayout infoBoxLinearLayout;


    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            Log.i(TAG, "onMapReadyCallback");
            map = googleMap;
            map.setOnMarkerClickListener(markerClickListener);

            drawMap();
            if (getArguments() != null) { setCurrEventAndPerson(getArguments().getString(EVENT_TAG)); }
            if (currEvent == null) {
                map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(0,0)));
            } else {
                displayInfo();
                Log.i(TAG + "::MRC", currEvent.toString());
                map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currEvent.getLatitude(), currEvent.getLongitude())));
                map.animateCamera(CameraUpdateFactory.zoomTo(3.0f));
                drawLines();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        data = DataCache.getInstance();
        mainMapActivity = true;

        View v = inflater.inflate(R.layout.fragment_maps, container, false);

        upButton = (ImageView)v.findViewById(R.id.maps_event_up_icon);
        upButton.setClickable(true);
        upButton.setOnClickListener(upBtnClickListener);

        settingsImageView = (ImageView)v.findViewById(R.id.settings_icon);
        settingsImageView.setClickable(true);
        settingsImageView.setOnClickListener(iconClickListener);

        searchImageView = (ImageView)v.findViewById(R.id.search_icon);
        searchImageView.setClickable(true);
        searchImageView.setOnClickListener(iconClickListener);

        drawToolbarIcons();

        eventInfoTextView = (TextView)v.findViewById(R.id.map_event_info);
        personInfoTextView = (TextView)v.findViewById(R.id.map_person_info);
        genderIconImageView = (ImageView)v.findViewById(R.id.text_map_icon);

        infoBoxLinearLayout = (LinearLayout)v.findViewById(R.id.info_box);
        infoBoxLinearLayout.setClickable(false);
        infoBoxLinearLayout.setOnClickListener(infoBoxClickListener);
        setDefaultInfoBox();

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        settings = Settings.getInstance();

        if (map != null) { drawMap(); }
        if (currEvent != null) {
            Person p = data.getPeople().get(currEvent.getPersonID());
            assert p != null;
            if (isGenderSettingOn(p.getGender())) { drawLines(); }
            else { setDefaultInfoBox(); }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult");
        if (!mainMapActivity) { getActivity().finish(); }
    }

    private GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {

        @Override
        public boolean onMarkerClick(Marker marker) {
            currEvent = (Event)marker.getTag();
            assert currEvent != null;

            infoBoxLinearLayout.setClickable(true);

            displayInfo();
            drawMap();
            drawLines();

            return true;
        }
    };

    private View.OnClickListener infoBoxClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(TAG + "::infoBox", currPerson.toString());
            Intent intent = new Intent(getActivity(), PersonActivity.class);
            intent.putExtra(PERSON_ID, currPerson.getPersonID());
            startActivityForResult(intent, REQUEST_CODE_EVENT_MAP);
        }
    };

    private View.OnClickListener iconClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent;
            switch (view.getId()) {
                case R.id.settings_icon:
                    intent = new Intent(getActivity(), SettingsActivity.class);
                    startActivity(intent);
                    break;
                case R.id.search_icon:
                    intent = new Intent(getActivity(), SearchActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_EVENT_MAP);
                    break;
                default: break;
            }
        }
    };

    private View.OnClickListener upBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getActivity().finish();
        }
    };

    private void setCurrEventAndPerson(String eventID) {
        currEvent = data.getEvents().get(eventID);
        currPerson = data.getPeople().get(currEvent.getPersonID());
        Log.i(TAG, "setP&E");
        Log.i(TAG, currEvent.toString());
        Log.i(TAG, currPerson.toString());
    }

    private void drawMap() {
        DataCache data = DataCache.getInstance();

        map.clear();
        for(Event e : data.getFilteredEvents()) { drawEventOnMap(e); }
    }
    private void drawEventOnMap(Event e) {
        LatLng location = new LatLng(e.getLatitude(), e.getLongitude());
        Marker mark = map.addMarker(new MarkerOptions().position(location));
        mark.setTag(e);
        mark.setIcon(BitmapDescriptorFactory.defaultMarker(findEventColor(e.getEventType())));
    }

    private Float findEventColor(String eventType) {
        String lowerCaseEventType = eventType.toLowerCase();
        for (String types : data.getEventTypes()) {
            if (lowerCaseEventType.equals(types.toLowerCase())) { return data.getColors().get(lowerCaseEventType); }
        }
        data.createNewColor(lowerCaseEventType);
        return data.getColors().get(lowerCaseEventType);
    }

    private boolean isGenderSettingOn(String gender) {
        if (!(settings.displayMaleEvents()) && gender.equals("m")) { return false; }
        if (!(settings.displayFemaleEvents()) && gender.equals("f")) { return false; }
        return true;
    }

    private void displayInfo() {

        Log.i(TAG, currEvent.toString());
        String eventInfo =  currEvent.getEventType().toUpperCase() + ": " + currEvent.getCity() + ", " + currEvent.getCountry() + " (" + currEvent.getYear() + ")";
        eventInfoTextView.setText(eventInfo);

        currPerson = data.getPeople().get(currEvent.getPersonID());
        assert currPerson != null;
        Log.i(TAG, currPerson.toString());

        String personName = currPerson.getFirstname() + " " + currPerson.getLastName();
        personInfoTextView.setText(personName);

        Drawable genderIcon = null;
        switch(currPerson.getGender()) {
            case "m": genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male).colorRes(R.color.colorMale).sizeDp(40); break;
            case "f": genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female).colorRes(R.color.colorFemale).sizeDp(40); break;
            default: setDefaultInfoBox();
        }
        genderIconImageView.setImageDrawable(genderIcon);
        infoBoxLinearLayout.setClickable(true);

    }
    private void drawLines() {
        Log.i(TAG + "::drawLines", currEvent.toString());

        if (settings.drawSpouseLines()) { drawSpouseLine(); }
        if (settings.drawLifeLines()) { drawLifeStoryLines(); }
        if (settings.drawFamilyLines()) { drawFamilyTreeLines(); }
    }

    private void drawSpouseLine() {
        Person spouse = data.getPeople().get(currPerson.getSpouseID());
        if (spouse != null) {
            if (!data.filter(spouse)) { return; }

            List<Event> events = data.getSortedPersonEvents().get(spouse.getPersonID());
            if (events != null && events.size() > 0) {
                Event spouseEvent = events.get(0);

                map.addPolyline(new PolylineOptions().add(
                        new LatLng(currEvent.getLatitude(), currEvent.getLongitude()),
                        new LatLng(spouseEvent.getLatitude(), spouseEvent.getLongitude())
                ).color(Color.BLUE));
            }
        }
    }

    private void drawLifeStoryLines() {
        List<Event> lifeEvents = data.getSortedPersonEvents().get(currPerson.getPersonID());

        if (lifeEvents != null && lifeEvents.size() > 0) {
            for (int i = 0; i < lifeEvents.size() - 1; i++) {
                Event e1 = lifeEvents.get(i);
                Event e2 = lifeEvents.get(i + 1);
                map.addPolyline(new PolylineOptions().add(
                        new LatLng(e1.getLatitude(), e1.getLongitude()),
                        new LatLng(e2.getLatitude(), e2.getLongitude())
                ).color(Color.GREEN));
            }
        }
    }

    private void drawFamilyTreeLines() {
        if (settings.displayFatherSide() && settings.displayMaleEvents() && currPerson.getFatherID() != null) { drawFamilyTreeLInes_Recurse(currEvent, currPerson.getFatherID(), 10, 255, 249, 0); }
        if (settings.displayMotherSide() && settings.displayFemaleEvents() && currPerson.getMotherID() != null) { drawFamilyTreeLInes_Recurse(currEvent, currPerson.getMotherID(), 10, 255, 249, 0); }
    }
    private void drawFamilyTreeLInes_Recurse(Event currBirthEvent, String personID, float width, int r, int g, int b) {

        Event parentEvent = null;
        List<Event> events = data.getSortedPersonEvents().get(personID);
        if (events != null && events.size() > 0) { parentEvent = events.get(0); }
        if (parentEvent != null) {
            map.addPolyline(new PolylineOptions().width(width).add(
                    new LatLng(currBirthEvent.getLatitude(), currBirthEvent.getLongitude()),
                    new LatLng(parentEvent.getLatitude(), parentEvent.getLongitude())
            ).color(Color.rgb(r,g,b)));

            String fatherID = data.getPeople().get(parentEvent.getPersonID()).getFatherID();
            String motherID = data.getPeople().get(parentEvent.getPersonID()).getMotherID();
            if (settings.displayMaleEvents() && fatherID != null)   { drawFamilyTreeLInes_Recurse(parentEvent, fatherID, (width - 2), r, ++g, (b + 51)); }
            if (settings.displayFemaleEvents() && motherID != null) { drawFamilyTreeLInes_Recurse(parentEvent, motherID, (width - 2), r, ++g, (b + 51)); }
        }
    }
    private void setDefaultInfoBox() {
        currEvent = null;
        currPerson = null;
        Drawable genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_leaf).colorRes(R.color.green).sizeDp(40);
        genderIconImageView.setImageDrawable(genderIcon);
        eventInfoTextView.setText(R.string.info_box_default_description);
        personInfoTextView.setText(R.string.info_box_default_message);

        infoBoxLinearLayout.setClickable(false);
    }

    private void drawToolbarIcons() {
        if (getArguments() == null) {
            searchImageView.setImageDrawable(new IconDrawable(getActivity(), FontAwesomeIcons.fa_search).colorRes(R.color.bg_color).sizeDp(30));
            settingsImageView.setImageDrawable(new IconDrawable(getActivity(), FontAwesomeIcons.fa_gear).colorRes(R.color.bg_color).sizeDp(30));
        } else {
            upButton.setImageDrawable(new IconDrawable(getActivity(), FontAwesomeIcons.fa_arrow_left).colorRes(R.color.bg_color).sizeDp(20));
            mainMapActivity = false;
        }
    }

}