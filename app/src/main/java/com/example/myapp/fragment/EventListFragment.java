package com.example.myapp.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.myapp.Event;
import com.example.myapp.EventDetailActivity;
import com.example.myapp.R;
import com.example.myapp.viewholder.EventViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public abstract class EventListFragment extends Fragment {

    private static final String TAG = "EventListFragment";

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<Event, EventViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private Button eventDelete;
    private Calendar Current_date;

    public EventListFragment() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.activity_event_list, container, false);
        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]
        mRecycler = rootView.findViewById(R.id.listRecyclerEvent);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query eventsQuery = getQuery(mDatabase);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(eventsQuery, Event.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<Event, EventViewHolder>(options) {

            @Override
            public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

                return new EventViewHolder(inflater.inflate(R.layout.event_item, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(EventViewHolder viewHolder, int position, final Event model) {
                final DatabaseReference eventRef = getRef(position);
                   // Set click listener for the whole post view
                final String eventKey = eventRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PostDetailActivity

                        Intent intent = new Intent(getActivity(), EventDetailActivity.class);
                        intent.putExtra(EventDetailActivity.EXTRA_EVENT_KEY, eventKey);
                        startActivity(intent);
                    }
                });

                // Determine if the current user has liked this post and set UI accordingly
                if (model.stars.containsKey(getUid())) {
                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_24);
                } else {
                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_outline_24);
                }
                if (model.peoples.containsKey(getUid())) {
                    viewHolder.btnAgreeEvent.setText("Уже записан");
//                    viewHolder.itemView.setBackgroundColor(Integer.parseInt("#4CAF50"));
                } else {
                    viewHolder.btnAgreeEvent.setText("Пойду");
                }

                // Bind Post to ViewHolder, setting OnClickListener for the star button
                try {
                    viewHolder.bindToPost(model, new View.OnClickListener() {
                        @Override
                        public void onClick(View starView) {
                            // Need to write to both places the post is stored
                            DatabaseReference globalEventRef = mDatabase.child("events").child(eventRef.getKey());
                            DatabaseReference userEventRef = mDatabase.child("user-events").child(model.uid).child(eventRef.getKey());

                            // Run two transactions
                            onStarClicked(globalEventRef);
                            onStarClicked(userEventRef);
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatabaseReference globalEventRef = mDatabase.child("events").child(eventRef.getKey());
                            DatabaseReference userEventRef = mDatabase.child("user-events").child(model.uid).child(eventRef.getKey());
                            onCountClicked(globalEventRef);
                            onCountClicked(userEventRef);
                        }
                    });
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };
        mRecycler.setAdapter(mAdapter);
    }


    // [START event_stars_transaction]
    private void onStarClicked(DatabaseReference eventRef) {
                        eventRef.runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                Event p = mutableData.getValue(Event.class);
                                if (p == null) {
                                    return Transaction.success(mutableData);
                                }

                                if (p.stars.containsKey(getUid())) {
                                    // Unstar the event and remove self from stars
                                    p.starCount = p.starCount - 1;
                                    p.stars.remove(getUid());
                                } else {
                                    // Star the event and add self to stars
                                    p.starCount = p.starCount + 1;
                                    p.stars.put(getUid(), true);
                                }

                                // Set value and report transaction success
                                mutableData.setValue(p);
                                return Transaction.success(mutableData);
                            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   DataSnapshot currentData) {
                // Transaction completed
                Log.d(TAG, "eventTransaction:onComplete:" + databaseError);
            }
        });
    }
    // [END event_stars_transaction]

    private void onCountClicked(DatabaseReference eventRef) {
        eventRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Event e = mutableData.getValue(Event.class);
                if (e == null) {
                    return Transaction.success(mutableData);
                }

                if (e.peoples.containsKey(getUid())) {
                    // Unstar the event and remove self from stars
                    e.peopleCount = e.peopleCount - 1;
                    e.peopleMax = e.peopleMax + 1;
                    e.peoples.remove(getUid());

                } else {
                    // Star the event and add self to stars
                    e.peopleCount = e.peopleCount + 1;
                    e.peopleMax = e.peopleMax - 1;
                    e.peoples.put(getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(e);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   DataSnapshot currentData) {
                // Transaction completed
                Log.d(TAG, "eventTransaction:onComplete:" + databaseError);
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);

}
