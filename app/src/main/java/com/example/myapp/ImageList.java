package com.example.myapp;

public class ImageList {
    private String imageUrl;

    public ImageList(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public ImageList(){

    }

    public String getImageUrl(){
        return imageUrl;
    }

    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }

}
