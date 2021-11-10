package com.vishal.kaitka.videoplayer.models;

/**
 * Created by Vishal on 10,November,2021
 */
public class IconModel {
    private int imageView;
    private String iconTitle;

    public IconModel() {
    }

    public IconModel(int imageView, String iconTitle) {
        this.imageView = imageView;
        this.iconTitle = iconTitle;
    }

    public int getImageView() {
        return imageView;
    }

    public void setImageView(int imageView) {
        this.imageView = imageView;
    }

    public String getIconTitle() {
        return iconTitle;
    }

    public void setIconTitle(String iconTitle) {
        this.iconTitle = iconTitle;
    }
}
