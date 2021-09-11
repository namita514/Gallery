package com.example.gallery.models;

import android.graphics.Bitmap;

public class ItemModel {
    //image url
    public Bitmap image;
    public String url;
    //background color of label
    public Integer color;
    //label for image
    public String label;

    public ItemModel(Bitmap image,
                     String url,
                     Integer color,
                     String label) {
        this.image = image;
        this.url=url;
        this.color = color;
        this.label = label;
    }
}
