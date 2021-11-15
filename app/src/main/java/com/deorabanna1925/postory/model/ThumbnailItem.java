package com.deorabanna1925.postory.model;

import android.graphics.Bitmap;

import com.zomato.photofilters.imageprocessors.Filter;
public class ThumbnailItem {
    public String name;
    public Bitmap image;
    public Filter filter;

    public ThumbnailItem() {
        image = null;
        name = null;
        filter = new Filter();
    }
}
