package com.example.myapp.viewholder;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.Event;
import com.example.myapp.R;
import com.example.myapp.StartActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;


import static com.example.myapp.R.*;


public class EventViewHolder extends RecyclerView.ViewHolder {

   // public TextView titleView;
    public TextView eventCategory;
//  public TextView authorView;
    public TextView eventName;
    public ImageView starView;
    public TextView numStarsView;
    public TextView bodyView;
    public TextView timeView;
    public TextView timeEndView;
    public TextView eventPeople;
    public TextView eventMaxPeople;

    public Button btnDeleteEvent;
    public Button btnAgreeEvent;
    public LinearLayout eventItem;

    public EventViewHolder(View itemView) {
        super(itemView);

        //titleView = itemView.findViewById(R.id.tv_event_name);
        eventCategory = itemView.findViewById(id.tv_event_cat);
        //authorView = itemView.findViewById(R.id.username);
        eventName= itemView.findViewById(id.tv_event_name);
        starView = itemView.findViewById(id.star);
       // btnDeleteEvent = itemView.findViewById(R.id.btn_del_event);
        btnAgreeEvent = itemView.findViewById(id.btn_agree);
        numStarsView = itemView.findViewById(id.tv_event_count);
        bodyView = itemView.findViewById(id.tv_event_date);
        timeView = itemView.findViewById(id.tv_event_time);
        eventItem = itemView.findViewById(id.itemEvent);
        timeEndView = itemView.findViewById(id.tv_date_end);
        eventPeople = itemView.findViewById(id.tv_people);
        eventMaxPeople = itemView.findViewById(id.tvMaxPeoples);

    }



    public void bindToPost(Event event, View.OnClickListener starClickListener, View.OnClickListener agreeEventClickListener) throws ParseException {
        eventName.setText(event.nameEvent);
        eventCategory.setText(event.categoryEvent);
        numStarsView.setText(String.valueOf(event.starCount));
        bodyView.setText(event.dataEvent);
        timeView.setText(event.time);
        eventPeople.setText(String.valueOf(event.peopleCount));
        eventMaxPeople.setText(String.valueOf(event.peopleMax));

        starView.setOnClickListener(starClickListener);
        btnAgreeEvent.setOnClickListener(agreeEventClickListener);

        currentData();

        if (eventCategory.getText().equals("Sport"))
        {
            eventItem.setBackgroundResource(drawable.white_border);
        }
        if(eventCategory.getText().equals("Meeting"))
        {
            eventItem.setBackgroundResource(drawable.green_border);
        }
        if(eventCategory.getText().equals("Party"))
        {
            eventItem.setBackgroundResource(drawable.yellow_border);
        }
        if(eventCategory.getText().equals("Other"))
        {
            eventItem.setBackgroundResource(drawable.pink_border);
        }


    }

    public void currentData() throws ParseException {
        Calendar cal = Calendar.getInstance();
        String dateEnd;
        dateEnd = bodyView.getText().toString() +" "+timeView.getText().toString();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month+1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int mHour = cal.get(Calendar.HOUR_OF_DAY);
        int mMinute = cal.get(Calendar.MINUTE);
        String dateInit;
        dateInit = day+"."+month+"."+year+" "+mHour+":"+mMinute;
        Date date1=new SimpleDateFormat("dd.MM.yyyy HH:mm").parse(dateEnd);
        Date date2=new SimpleDateFormat("dd.MM.yyyy HH:mm").parse(dateInit);
        String result = substractDates(date1, date2, new SimpleDateFormat("HH:mm"));
        timeEndView.setText(result);

    }

    private String substractDates(Date date1, Date date2, SimpleDateFormat format) {
        long restDatesinMillis = date1.getTime()-date2.getTime();
        Date restdate = new Date(restDatesinMillis);

        int restYears = date1.getYear() - date2.getYear();
        int restMonths  = date1.getMonth() - date2.getMonth();
        int restDays  = date1.getDate() - date2.getDate();
        String str1 = "";
        String str = format.format(restdate);
        if(restDatesinMillis < 0) {
            str1 = "Мероприятия прошло";
        } else {
            if (restYears > 0) {
                str1 += restYears + " л. ";
            }
            if (restYears >= 0) {
                if (restMonths > 0) {
                    str1 += restMonths + " м. ";
                }
            }
            if(restMonths >= 0)
            {
                if (restDays > 0) {  str1 += restDays + " д. ";
                            }
                        }
            }

        return str1 +" "+ str;
    }
}
