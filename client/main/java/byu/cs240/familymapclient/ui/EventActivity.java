package byu.cs240.familymapclient.ui;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;

import byu.cs240.familymapclient.R;
import byu.cs240.familymapclient.model.DataCache;
import byu.cs240.familymapclient.model.Event;

public class EventActivity extends FragmentActivity {
    private static final String TAG = "EventActivity";
    private static final String EVENT_TAG = "EVENT_TAG";

    private Event event;
    private MapsFragment mapFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_event);

        String eventID = getIntent().getStringExtra("EVENT_ID");
        event = DataCache.getInstance().getEvents().get(eventID);
        if (event != null) { Log.i(TAG, event.toString()); }

        FragmentManager fm = this.getSupportFragmentManager();
        mapFrag = (MapsFragment)fm.findFragmentById(R.id.event_fragment_container);
        if (mapFrag == null) {
            mapFrag = new MapsFragment();
            Bundle args = new Bundle();
            args.putString(EVENT_TAG, event.getEventID());
            mapFrag.setArguments(args);
            fm.beginTransaction()
                    .add(R.id.event_fragment_container, mapFrag)
                    .commit();
        }
    }
}