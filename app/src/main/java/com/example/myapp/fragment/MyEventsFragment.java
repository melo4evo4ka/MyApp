package com.example.myapp.fragment;


import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyEventsFragment extends EventListFragment {
    public MyEventsFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my events
        return databaseReference.child("user-events")
                .child(getUid());
    }

}

