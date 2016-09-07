package com.monash.eric.mytestingdemo.Entity;

import java.io.Serializable;

/**
 * Created by IBM on 5/09/2016.
 */
@SuppressWarnings("serial") //With this annotation we are going to hide compiler warnings
public class Event implements Serializable {

    private String description;
    private String date;
    private String time;
    private String venue;
    private String sport;
    private String title;
    private String hostId;
    private String longtitue;
    private String latitute;

    public Event() {
    }


    public String displayAll() {
        return "Event{" +
                "description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", venue='" + venue + '\'' +
                ", sport='" + sport + '\'' +
                ", title='" + title + '\'' +
                ", hostId='" + hostId + '\'' +
                ", longtitue='" + title + '\'' +
                ", latitute='" + hostId + '\'' +
                '}';
    }

    public Event(String description, String date, String time, String venue, String sport, String title, String hostId , String latitute, String longtitue) {
        this.description = description;
        this.date = date;
        this.time = time;
        this.venue = venue;
        this.sport = sport;
        this.title = title;
        this.hostId = hostId;
        this.latitute = latitute;
        this.longtitue = longtitue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getLongtitue() {
        return longtitue;
    }

    public void setLongtitue(String longtitue) {
        this.longtitue = longtitue;
    }

    public String getLatitute() {
        return latitute;
    }

    public void setLatitute(String latitute) {
        this.latitute = latitute;
    }
}
