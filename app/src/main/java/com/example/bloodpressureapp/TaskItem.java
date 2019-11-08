package com.example.bloodpressureapp;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TaskItem {
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");

    private String taskId;
    private String userId;
    private String date;
    private String time;
    private int systolic;
    private int diastolic;
    private String condition;

    public TaskItem(){}

    public TaskItem(String taskId) {
        this.taskId = taskId;
        this.setDate();
        this.setTime();
    }


    public TaskItem(String taskId, String userId, int systolic, int diastolic, String condition) {
        this.taskId = taskId;
        this.userId = userId;
        this.setDate();
        this.setTime();
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.condition = condition;
    }


    public TaskItem(String taskId, String userId, Date date, int systolic, int diastolic, String condition) {
        this.taskId = taskId;
        this.userId = userId;
        this.date = dateFormat.format(date);
        this.time = timeFormat.format(date);
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.condition = condition;
    }


    public String getTaskId() {
        return taskId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate() {
        Date date = new Date();
        this.date = dateFormat.format(date);
    }

    public void setDate(String date) { this.date = date; }


    public String getTime(){
        return this.time;
    }

    public void setTime(){
        Date date = new Date();
        this.time = timeFormat.format(date);
    }

    public void setTime(String time) { this.time = time; }

    public int getSystolic() {
        return systolic;
    }

    public void setSystolic(int systolic) {
        this.systolic = systolic;
    }

    public int getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(int diastolic) {
        this.diastolic = diastolic;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }



    public static String getDateString(Date date){
        return dateFormat.format(date);
    }

    public static String getTimeString(Date date){
        return timeFormat.format(date);
    }

}
