package com.example.bloodpressureapp;

import java.util.Date;

public class TaskItem {
    private String taskId;
    private String userId;
    private Date date;
    private int systolic;
    private int diastolic;
    private String condition;

    public TaskItem(){}

    public TaskItem(String taskId) {
        this.taskId = taskId;
    }


    public TaskItem(String taskId, String userId, Date date, int systolic, int diastolic, String condition) {
        //TODO maybe generate task id only on adding to list?
        this.taskId = taskId;
        this.userId = userId;
        this.date = date;
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.condition = condition;
    }


    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

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
        String dateTxt = (date.getMonth() + 1)+ "/" + date.getDate() + "/" + (date.getYear() + 1900);
        return dateTxt;
    }

    public static String getTimeString(Date date){
        String timeTxt = date.getHours() + ":" + date.getMinutes();
        return timeTxt;
    }

}
