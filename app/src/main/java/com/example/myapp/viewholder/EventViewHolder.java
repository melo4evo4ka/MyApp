package com.example.myapp.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.Event;
import com.example.myapp.R;

public class EventViewHolder extends RecyclerView.ViewHolder {

   // public TextView titleView;
    public TextView eventCategory;
//  public TextView authorView;
    public TextView eventName;
    public ImageView starView;
    public TextView numStarsView;
    public TextView bodyView;

    public EventViewHolder(View itemView) {
        super(itemView);

        //titleView = itemView.findViewById(R.id.tv_event_name);
        eventCategory = itemView.findViewById(R.id.tv_event_cat);
        //authorView = itemView.findViewById(R.id.username);
        eventName= itemView.findViewById(R.id.tv_event_name);
        starView = itemView.findViewById(R.id.star);
        numStarsView = itemView.findViewById(R.id.tv_event_count);
        bodyView = itemView.findViewById(R.id.tv_event_date);
    }

    public void bindToPost(Event event, View.OnClickListener starClickListener) {
        eventName.setText(event.nameEvent);
        eventCategory.setText(event.categoryEvent);
        numStarsView.setText(String.valueOf(event.starCount));
        bodyView.setText(event.dataEvent);
        starView.setOnClickListener(starClickListener);
    }
}
