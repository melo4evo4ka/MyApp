package com.example.myapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;


public class EventAdapter extends FirebaseRecyclerAdapter<Event,EventAdapter.EventViewHolder> {

    public EventAdapter(@NonNull FirebaseRecyclerOptions<Event> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull EventViewHolder holder, int position, @NonNull Event event) {
        holder.eventCat.setText(event.getCategoryEvent());
        holder.eventN.setText(event.getNameEvent());
        holder.eventStartData.setText(event.getDataEvent());
        holder.eventStartTime.setText(event.getTime());
        holder.event_count.setText(event.getStarCount());
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item,parent,false);
        return new EventViewHolder(view);
    }

    class EventViewHolder extends RecyclerView.ViewHolder{

        TextView eventCat,eventN,eventStartData,eventStartTime,event_count;

        public EventViewHolder(@NonNull View itemView){
            super(itemView);
            eventCat = itemView.findViewById(R.id.tv_event_cat);
            eventN = itemView.findViewById(R.id.tv_event_name);
            eventStartData = itemView.findViewById(R.id.tv_event_date);
            eventStartTime = itemView.findViewById(R.id.tv_event_time);
            event_count = itemView.findViewById(R.id.tv_event_count);

        }
    }
}
