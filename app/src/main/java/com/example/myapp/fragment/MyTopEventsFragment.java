package com.example.myapp.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyTopEventsFragment extends EventListFragment {

    public MyTopEventsFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // [START my_top_events_query]
        // My top events by number of stars
        String myUserId = getUid();
        Query myTopEventsQuery = databaseReference.child("user-events").child(myUserId)
                .orderByChild("starCount");
        // [END my_top_events_query]

        return myTopEventsQuery;
    }
}
