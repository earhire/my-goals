package com.example.my_goals.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Goal implements Parcelable {
    String title, category, description, start_date, end_date,  url_image, user_id;

    public Goal() {
    }

    public Goal(String title, String category, String description, String start_date, String end_date,  String url_image, String user_id) {
        this.title = title;
        this.category = category;
        this.description = description;
        this.start_date = start_date;
        this.end_date = end_date;

        this.url_image = url_image;
        this.user_id = user_id;
    }

    protected Goal(Parcel in) {
        title = in.readString();
        category = in.readString();
        description = in.readString();
        start_date = in.readString();
        end_date= in.readString();

        url_image= in.readString();
        user_id = in.readString();
    }

    public static final Creator<Goal> CREATOR = new Creator<Goal>() {
        @Override
        public Goal createFromParcel(Parcel in) {
            return new Goal(in);
        }

        @Override
        public Goal[] newArray(int size) {
            return new Goal[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getUrl_image() {
        return url_image;
    }

    public void setUrl_image(String url_image) {
        this.url_image = url_image;
    }

    public String getUser_id() {return user_id;}

    public void setUser_id(String user_id) { this.user_id = user_id; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(category);
        parcel.writeString(description);
        parcel.writeString(start_date);
        parcel.writeString(end_date);
        parcel.writeString(url_image);
        parcel.writeString(user_id);

    }

    @Override
    public int describeContents() {
        return 0;
    }
}
