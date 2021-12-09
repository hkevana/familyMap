package byu.cs240.familymapclient.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.List;

import byu.cs240.familymapclient.R;
import byu.cs240.familymapclient.model.DataCache;
import byu.cs240.familymapclient.model.Event;
import byu.cs240.familymapclient.model.Person;

public class PersonActivity extends AppCompatActivity {
    private static final String TAG = "PersonActivity";
    private static final String PERSON_ID = "PERSON_ID";
    private static final String EVENT_ID = "EVENT_ID";
    private static final int REQUEST_CODE_PERSON = 1;

    private Person currPerson;

    private ImageView upButton;
    private TextView firstNameTextView;
    private TextView lastNameTextView;
    private TextView genderTextView;
    private ExpandableListView listExpListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_person);

        String personID = getIntent().getStringExtra("PERSON_ID");
        currPerson = DataCache.getInstance().getPeople().get(personID);
        assert currPerson != null;
        Log.i(TAG, currPerson.toString());

        DataCache data = DataCache.getInstance();

        upButton = (ImageView)findViewById(R.id.person_up_icon);
        upButton.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_arrow_left).colorRes(R.color.bg_color).sizeDp(20));
        upButton.setOnClickListener(upBtnClickListener);

        firstNameTextView = (TextView)findViewById(R.id.first_name_title);
        firstNameTextView.setText(currPerson.getFirstname());
        lastNameTextView = (TextView)findViewById(R.id.last_name_title);
        lastNameTextView.setText(currPerson.getLastName());
        genderTextView = (TextView)findViewById(R.id.gender_title);
        switch (currPerson.getGender()) {
            case "m": genderTextView.setText(R.string.gender_male); break;
            case "f": genderTextView.setText(R.string.gender_female); break;
            default: throw new IllegalArgumentException("Unable to Recognize gender: " + currPerson.getGender());
        }

        listExpListView = (ExpandableListView)findViewById(R.id.expandable_list);

        List<Event> lifeEvents = null;
        if (data.filter(currPerson)) { lifeEvents = data.getSortedPersonEvents().get(currPerson.getPersonID()); }
        else { lifeEvents = new ArrayList<>(); }
        List<Person> family = data.getFamily(currPerson);
        Log.i(TAG, "List sizes: " + lifeEvents.size() + " " + family.size());
        ListAdapter adapter = new ListAdapter(lifeEvents, family);
        listExpListView.setAdapter(adapter);
        listExpListView.setOnChildClickListener(adapter.ListClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    private class ListAdapter extends BaseExpandableListAdapter {
        private List<Event> lifeEvents;
        private List<Person> family;

        private static final int LIFE_EVENT_GROUP_POSITION = 0;
        private static final int FAMILY_GROUP_POSITION = 1;

        public ListAdapter(List<Event> lifeEvents, List<Person> family) {
            this.lifeEvents = lifeEvents;
            this.family = family;
        }
        @Override
        public int getGroupCount() { return 2; }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch(groupPosition) {
                case LIFE_EVENT_GROUP_POSITION: return lifeEvents.size();
                case FAMILY_GROUP_POSITION: return family.size();
                default: throw new IllegalArgumentException("Unrecognized group position " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            switch(groupPosition) {
                case LIFE_EVENT_GROUP_POSITION: return "LIFE EVENTS";
                case FAMILY_GROUP_POSITION: return "FAMILY";
                default: throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getChild(int groupPosition, int childIndex) {
            switch (groupPosition) {
                case LIFE_EVENT_GROUP_POSITION: return lifeEvents.get(childIndex);
                case FAMILY_GROUP_POSITION: return family.get(childIndex);
                default: throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public long getGroupId(int group) { return group; }

        @Override
        public long getChildId(int group, int childIndex) { return group; }

        @Override
        public boolean hasStableIds() { return false; }

        @Override
        public View getGroupView(int group, boolean b, View view, ViewGroup parent) {
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.list_item_group, parent,false);
            }

            TextView title = (TextView)view.findViewById(R.id.list_title);

            switch (group) {
                case LIFE_EVENT_GROUP_POSITION:
                    if (lifeEvents.size() == 0) { title.setText(R.string.event_list_title_settings_off); }
                    else { title.setText(R.string.events_list_title); }
                    break;
                case FAMILY_GROUP_POSITION: title.setText(R.string.family_list_title); break;
                default: throw new IllegalArgumentException("Unrecognized group position: " + group);
            }
            return view;
        }

        @Override
        public View getChildView(int group, int childIndex, boolean b, View view, ViewGroup parent) {
            View itemView;

            switch(group) {
                case LIFE_EVENT_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.event_item, parent, false);
                    initEventView(itemView, childIndex);
                    break;
                case FAMILY_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.person_item, parent, false);
                    initPersonView(itemView, childIndex);
                    break;
                default: throw new IllegalArgumentException("Unrecognized group position: " + group);
            }
            return itemView;
        }


        private void initEventView(View eventView, final int childIndex) {
            Event event = lifeEvents.get(childIndex);

            ImageView icon = (ImageView)eventView.findViewById(R.id.event_item_icon);
            Float hue = findEventColor(event.getEventType().toLowerCase());
            icon.setImageDrawable(colorEventIcon(hue));

            TextView info = (TextView)eventView.findViewById(R.id.event_item_info);
            String eventInfo = event.getEventType().toUpperCase() + " - " + event.getYear();
            info.setText(eventInfo);

            TextView loc = (TextView)eventView.findViewById(R.id.event_item_person_name);
            String eventLoc = event.getCity() + ", " + event.getCountry();
            loc.setText(eventLoc);
        }

        private void initPersonView(View personView, final int childIndex) {
            Person person = family.get(childIndex);

            ImageView icon = (ImageView)personView.findViewById(R.id.person_item_icon);
            Drawable genderIcon;
            switch (person.getGender()) {
                case "f": genderIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_female).colorRes(R.color.colorFemale).sizeDp(40); break;
                case "m": genderIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_male).colorRes(R.color.colorMale).sizeDp(40); break;
                default: throw new IllegalArgumentException("Unrecognized gender: " + person.getGender());
            }
            icon.setImageDrawable(genderIcon);

            TextView name = (TextView)personView.findViewById(R.id.person_item_name);
            String fullName = person.getFirstname() + " " + person.getLastName();
            name.setText(fullName);

            TextView role = (TextView)personView.findViewById(R.id.person_item_role);

            if (person.getPersonID().equals(currPerson.getFatherID())) { role.setText(R.string.role_father); }
            else if (person.getPersonID().equals(currPerson.getMotherID())) { role.setText(R.string.role_mother); }
            else if (person.getPersonID().equals(currPerson.getSpouseID())) { role.setText(R.string.role_spouse); }
            else { role.setText(R.string.role_child); }
        }

        private Float findEventColor(String eventType) {
            DataCache data = DataCache.getInstance();
            for (String types : data.getEventTypes()) {
                if (eventType.equals(types)) { return data.getColors().get(eventType); }
            }
            return 0.0f;
        }

        private Drawable colorEventIcon(Float hue) {
            Drawable eventIcon;
            switch(hue.intValue()) {
                case 30: eventIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.orange).sizeDp(40); break;
                case 60: eventIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.yellow).sizeDp(40); break;
                case 120: eventIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.green).sizeDp(40); break;
                case 180: eventIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.cyan).sizeDp(40); break;
                case 210: eventIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.azure).sizeDp(40); break;
                case 240: eventIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.blue).sizeDp(40); break;
                case 270: eventIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.violet).sizeDp(40); break;
                case 300: eventIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.magenta).sizeDp(40); break;
                case 330: eventIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.rose).sizeDp(40); break;
                default: eventIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.red).sizeDp(40);
            }
            return eventIcon;
        }

        @Override
        public boolean isChildSelectable(int group, int childIndex) { return true; }

        private ExpandableListView.OnChildClickListener ListClickListener = new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int group, int childIndex, long l) {
                Intent intent;
                switch(group) {
                    case LIFE_EVENT_GROUP_POSITION:
                        Log.i(TAG, "Event clicked");
                        intent = new Intent(PersonActivity.this, EventActivity.class);
                        intent.putExtra(EVENT_ID, lifeEvents.get(childIndex).getEventID());
                        break;
                    case FAMILY_GROUP_POSITION:
                        Log.i(TAG, "Person clicked");
                        intent = new Intent(PersonActivity.this, PersonActivity.class);
                        intent.putExtra(PERSON_ID, family.get(childIndex).getPersonID());
                        break;
                    default: throw new IllegalArgumentException("Unrecognized group position: " + group);
                }
                startActivityForResult(intent, REQUEST_CODE_PERSON);
                return false;
            }
        };
    }

    private View.OnClickListener upBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult");
        finish();
    }
}