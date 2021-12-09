package byu.cs240.familymapclient.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.List;

import byu.cs240.familymapclient.R;
import byu.cs240.familymapclient.model.DataCache;
import byu.cs240.familymapclient.model.Event;
import byu.cs240.familymapclient.model.Person;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";

    private static final int PERSON_ITEM_VIEW = 0;
    private static final int EVENT_ITEM_VIEW = 1;
    private static final String PERSON_ID = "PERSON_ID";
    private static final String EVENT_ID = "EVENT_ID";
    private static final int    REQUEST_CODE_SEARCH = 2;

    private DataCache data;

    private ImageView upButton;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private SearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        data = DataCache.getInstance();

        upButton = (ImageView)findViewById(R.id.search_up_icon);
        upButton.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_arrow_left).colorRes(R.color.bg_color).sizeDp(20));
        upButton.setOnClickListener(upBtnClickListener);

        searchView = (SearchView)findViewById(R.id.search_bar);
        searchView.setIconifiedByDefault(true);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
        searchView.setOnQueryTextListener(searchListener);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        adapter = null;
        recyclerView.setAdapter(adapter);

    }

    private SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            Log.i(TAG, "submit: " + s);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            Log.i(TAG, s);
            displayResults(s);
            return true;
        }
    };

    private View.OnClickListener upBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };

    private void displayResults(String s) {
        s = s.toLowerCase();
        List<Person> people = data.searchPeople(s);
        List<Event> events = data.searchEvents(s);

        adapter = new SearchAdapter(people, events);
        recyclerView.setAdapter(adapter);
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
        private List<Person> people;
        private List<Event> events;

        public SearchAdapter(List<Person> p, List<Event> e) {
            people = p;
            events = e;
        }

        @Override
        public int getItemViewType(int position) { return position < people.size() ? PERSON_ITEM_VIEW : EVENT_ITEM_VIEW; }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.i(TAG, "onCreateViewHolder");
            View view;

            if (viewType == PERSON_ITEM_VIEW) { view = getLayoutInflater().inflate(R.layout.person_item, parent, false); }
            else { view = getLayoutInflater().inflate(R.layout.event_item, parent, false); }

            return new SearchViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            Log.i(TAG, "onBindViewHolder");
            if (position < people.size()) { holder.bind(people.get(position)); }
            else { holder.bind(events.get(position - people.size())); }
        }

        @Override
        public int getItemCount() { return people.size() + events.size(); }
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView icon;
        private TextView personName;
        private TextView eventInfo;

        private final int viewType;
        private Person person;
        private Event event;


        public SearchViewHolder(@NonNull View view, int viewType) {
            super(view);
            Log.i(TAG, "SearchViewHolder constructor");
            this.viewType = viewType;

            view.setOnClickListener(this);

            if (viewType == PERSON_ITEM_VIEW) {
                Log.i(TAG, "\tbinding human");
                icon = (ImageView)view.findViewById(R.id.person_item_icon);
                personName = (TextView)view.findViewById(R.id.person_item_name);
            } else {
                Log.i(TAG, "\tbinding event");
                icon = (ImageView)view.findViewById(R.id.event_item_icon);
                personName = (TextView)view.findViewById(R.id.event_item_person_name);
                eventInfo = (TextView)view.findViewById(R.id.event_item_info);
            }
        }

        public void bind(Person p) {
            this.person = p;
            Drawable genderIcon = null;
            switch(person.getGender()) {
                case "m": genderIcon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_male).colorRes(R.color.colorMale).sizeDp(40); break;
                case "f": genderIcon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_female).colorRes(R.color.colorFemale).sizeDp(40); break;
            }
            icon.setImageDrawable(genderIcon);
            String name = person.getFirstname() + " " + person.getLastName();
            personName.setText(name);
        }

        public void bind(Event e) {
            this.event = e;
            Person p = data.getPeople().get(e.getPersonID());
            Float hue = findEventColor(e.getEventType().toLowerCase());
            icon.setImageDrawable(colorEventIcon(hue));
            assert p != null;
            String name = p.getFirstname() + " " + p.getLastName();
            personName.setText(name);
            String info = event.getEventType() + ": " + event.getCity() + ", " + event.getCountry() + " (" + event.getYear() + ")";
            eventInfo.setText(info);
        }
        private Float findEventColor(String eventType) {
            for (String types : data.getEventTypes()) {
                if (eventType.equals(types)) { return data.getColors().get(eventType); }
            }
            return 0.0f;
        }

        private Drawable colorEventIcon(Float hue) {
            Drawable eventIcon;
            switch(hue.intValue()) {
                case 30: eventIcon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.orange).sizeDp(40); break;
                case 60: eventIcon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.yellow).sizeDp(40); break;
                case 120: eventIcon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.green).sizeDp(40); break;
                case 180: eventIcon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.cyan).sizeDp(40); break;
                case 210: eventIcon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.azure).sizeDp(40); break;
                case 240: eventIcon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.blue).sizeDp(40); break;
                case 270: eventIcon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.violet).sizeDp(40); break;
                case 300: eventIcon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.magenta).sizeDp(40); break;
                case 330: eventIcon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.rose).sizeDp(40); break;
                default: eventIcon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.red).sizeDp(40);
            }
            return eventIcon;
        }

        @Override
        public void onClick(View view) {
            Intent intent = null;
            if (viewType == PERSON_ITEM_VIEW) {
                intent = new Intent(SearchActivity.this, PersonActivity.class);
                intent.putExtra(PERSON_ID, person.getPersonID());
            }
            if (viewType == EVENT_ITEM_VIEW) {
                intent = new Intent(SearchActivity.this, EventActivity.class);
                intent.putExtra(EVENT_ID, event.getEventID());
            }
            startActivityForResult(intent, REQUEST_CODE_SEARCH);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult");
        finish();
    }
}