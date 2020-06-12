package com.example.myapp.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class AllEventsFragment extends EventListFragment {
    public AllEventsFragment() {}
    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All events
        return databaseReference.child("events");
    }
}
