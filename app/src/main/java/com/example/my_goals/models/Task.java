package com.example.my_goals.models;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Task {

    private String name, repetition, day_of_week, day_start, day_end, time, goal_id, goal_type;
    private boolean done_today = false;
    private List<TaskDate> list_of_dates;
    private Integer task_duration;
    private Integer number_dates;


    public Task() {
    }

    public Task(String name, String repetition, String day_of_week, String day_start, String day_end,String time, String goal_id, List<TaskDate> list_of_dates,Boolean done_today, Integer task_duration,Integer number_dates, String goal_type) {
        this.name = name;
        this.repetition = repetition;
        this.day_of_week = day_of_week;
        this.day_start = day_start;
        this.day_end = day_end;
        this.time = time;
        this.goal_id = goal_id;
        this.list_of_dates = list_of_dates;
        this.done_today = done_today;
        this.task_duration = task_duration;
        this.number_dates = number_dates;
        this.goal_type = goal_type;
    }

    public String getDay_of_week() {return day_of_week;}

    public void setDay_of_week(String day_of_week) { this.day_of_week = day_of_week;}

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRepetition() {
        return repetition;
    }

    public void setRepetition(String repetition) {
        this.repetition = repetition;
    }

    public String getDay_start() {
        return day_start;
    }

    public void setDay_start(String day_start) {
        this.day_start = day_start;
    }

    public String getDay_end() {return day_end;}

    public void setDay_end(String day_end) {this.day_end = day_end;}

    public String getGoal_id() {
        return goal_id;
    }

    public void setGoal_id(String goal_id) {
        this.goal_id = goal_id;
    }

    public boolean isDone_today() {return done_today; }

    public void setDone_today(boolean done_today) { this.done_today = done_today;}

    public Integer getTask_duration() { return task_duration; }

    public void setTask_duration(Integer task_duration) {this.task_duration = task_duration;}

    public Integer getNumber_dates() {return number_dates;}

    public void setNumber_dates(Integer number_dates) {this.number_dates = number_dates; }

    public String getGoal_type() {return goal_type; }

    public void setGoal_type(String goal_type) { this.goal_type = goal_type;}
}
