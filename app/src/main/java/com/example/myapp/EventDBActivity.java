package com.example.myapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EventDBActivity extends  BaseActivity  {
    private static final CharSequence REQUIRED ="requred" ;
    private static final String TAG = "NewEventActivity";
    private EditText eventName;
    private EditText eventCategory;
    private EditText eventMaxPeople;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Button addPost;
    private TextView mDisplayDate,mDisplayTime;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventdb);
        eventName = findViewById(R.id.ed_event_name);
        eventMaxPeople = findViewById(R.id.count_people);
        //eventStartData = findViewById(R.id.start_date);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        addPost = findViewById(R.id.addPost);
        mDisplayDate = findViewById(R.id.tvData);
        mDisplayTime = findViewById(R.id.tvTime);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();

                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(EventDBActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                Log.d(TAG,"onDataSet: data" + dayOfMonth +" "+month+" "+year);

                String date = dayOfMonth +"."+month+"."+year;
                mDisplayDate.setText(date);
            }
        };
        mDisplayTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int mHour = calendar.get(Calendar.HOUR_OF_DAY);
                int mMinute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(EventDBActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String dateAdd = hourOfDay + ":" +minute;
                        try {
                            Date date1=new SimpleDateFormat("HH:mm").parse(dateAdd);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        mDisplayTime.setText(dateAdd);
                    }
                },mHour, mMinute, false);
                timePickerDialog.show();
                }

        });

        spinner = findViewById(R.id.sp_types);
        ArrayAdapter<String> adapterSpin = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.types));
        adapterSpin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterSpin);
        spinner.setSelection(3);
        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // eventCategory.setText(spinner.getSelectedItem().toString());
                submitEvent();
            }
        });

    }

    private void submitEvent() {


        final String evCat = spinner.getSelectedItem().toString();
        final String evName = eventName.getText().toString();
        final String tvData = mDisplayDate.getText().toString();
        final String tvTime = mDisplayTime.getText().toString();
        String sTextFromET = eventMaxPeople.getText().toString();
        final int tvMaxPeolpes = new Integer(sTextFromET).intValue();

        final int count = 0;
        final int countPeople = 0;

        if (TextUtils.isEmpty(evName)) {
            eventName.setError(REQUIRED);
            return;
        }
        // Body is required
        if (TextUtils.isEmpty(evCat)) {
            eventCategory.setError(REQUIRED);
            return;
        }

        if (TextUtils.isEmpty(tvData)) {
            mDisplayDate.setError(REQUIRED);
            return;
        }
        if (TextUtils.isEmpty(tvTime)){
            mDisplayTime.setError(REQUIRED);
        }

      Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();

        // [START single_value_read]
        final String userId = getUid();
        mDatabase.child("Users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        UsersData user = dataSnapshot.getValue(UsersData.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(EventDBActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            writeNewPost(userId, evCat, evName, tvData, tvTime, count, countPeople,tvMaxPeolpes);
                        }
                        // Finish this Activity, back to the stream
                        // setEditingEnabled(true);
                        finish();
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }


    private void writeNewPost(String userId, String evCat, String evNam, String evStartData, String time,int starcount,int countPeople,int tvMaxPeolpes) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("events").push().getKey();
        Event event = new Event(userId, evCat, evNam,evStartData,time, starcount, countPeople,tvMaxPeolpes);
        Map<String, Object> postValues = event.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/events/" + key, postValues);
        childUpdates.put("/user-events/" + userId + "/" + key, postValues);
        mDatabase.updateChildren(childUpdates);
    }

}

