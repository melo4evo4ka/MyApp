package com.example.myapp.viewholder;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.Event;
import com.example.myapp.R;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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
    public Button btnDeleteEvent;
    public Button btnAgreeEvent;
    public LinearLayout eventItem;

    public EventViewHolder(View itemView) {
        super(itemView);

        //titleView = itemView.findViewById(R.id.tv_event_name);
        eventCategory = itemView.findViewById(R.id.tv_event_cat);
        //authorView = itemView.findViewById(R.id.username);
        eventName= itemView.findViewById(R.id.tv_event_name);
        starView = itemView.findViewById(R.id.star);
       // btnDeleteEvent = itemView.findViewById(R.id.btn_del_event);
        btnAgreeEvent = itemView.findViewById(R.id.btn_agree);
        numStarsView = itemView.findViewById(R.id.tv_event_count);
        bodyView = itemView.findViewById(R.id.tv_event_date);
        timeView = itemView.findViewById(R.id.tv_event_time);
        eventItem = itemView.findViewById(R.id.itemEvent);
        timeEndView = itemView.findViewById(R.id.tv_date_end);
        eventPeople = itemView.findViewById(R.id.tv_people);
    }

    public void bindToPost(Event event, View.OnClickListener starClickListener, View.OnClickListener agreeEventClickListener) throws ParseException {
        eventName.setText(event.nameEvent);
        eventCategory.setText(event.categoryEvent);
        numStarsView.setText(String.valueOf(event.starCount));
        bodyView.setText(event.dataEvent);
        timeView.setText(event.time);
        eventPeople.setText(String.valueOf(event.peopleCount));
        starView.setOnClickListener(starClickListener);
        btnAgreeEvent.setOnClickListener(agreeEventClickListener);
        currentData();

        if (eventCategory.getText()=="СПОРТ"){
            eventItem.setBackgroundColor(Color.parseColor("#3C3838"));
        }
        //btnDeleteEvent.setOnClickListener(delEventClickListener);

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
