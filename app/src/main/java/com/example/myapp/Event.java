package com.example.myapp;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Event {

        String uid;
        String categoryEvent;
        String nameEvent;
        String dataEvent;
        String time;
         int starCount = 0;
         Map<String, Boolean> stars = new HashMap<>();

        public Event() {
            // Default constructor required for calls to DataSnapshot.getValue(Post.class)
        }

 public Event(String uid, String categoryEvent, String nameEvent, String dataEvent, String time, int starCount) {
        this.uid = uid;
        this.categoryEvent = categoryEvent;
        this.nameEvent = nameEvent;
        this.dataEvent = dataEvent;
        this.time = time;
        this.starCount = starCount;
    }

           // [START post_to_map]
        @Exclude
        public Map<String, Object> toMap() {
            HashMap<String, Object> result = new HashMap<>();
            result.put("uid", uid);
            result.put("categoryEvent", categoryEvent);
            result.put("nameEvent", nameEvent);
            result.put("dataEvent", dataEvent);
            result.put("time", time);
            result.put("starCount", starCount);
            result.put("stars", stars);
            return result;
        }
        // [END post_to_map]
    @Override
    public String toString() {
        return "Event{" +
                "uid='" + uid + '\'' +
                ", categoryEvent='" + categoryEvent + '\'' +
                ", nameEvent='" + nameEvent + '\'' +
                ", dataEvent='" + dataEvent + '\'' +
                ", timeEvent='" + dataEvent + '\'' +
                ", starCount=" + starCount +
                ", stars=" + stars +
                '}';
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNameEvent() {
        return nameEvent;
    }

    public void setNameEvent(String nameEvent) {
        this.nameEvent = nameEvent;
    }

    public String getCategoryEvent() {return categoryEvent;}

    public void setCategoryEvent(String categoryEvent) {
        this.categoryEvent = categoryEvent;
    }

    public String getDataEvent() {
        return dataEvent;
    }

    public void setDataEvent(String dataEvent) {
        this.dataEvent = dataEvent;
    }

    public int getStarCount() {
        return starCount;
    }

    public void setStarCount(int starCount) {
        this.starCount = starCount;
    }

    public Map<String, Boolean> getStars() {
        return stars;
    }

    public void setStars(Map<String, Boolean> stars) {
        this.stars = stars;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

