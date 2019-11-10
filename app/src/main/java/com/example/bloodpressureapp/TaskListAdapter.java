package com.example.bloodpressureapp;

import android.app.Activity;
import android.graphics.Color;
import android.widget.ArrayAdapter;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class TaskListAdapter extends ArrayAdapter<TaskItem> {
    private Activity context;
    private List<TaskItem> taskItemList;

    public TaskListAdapter(Activity context, List<TaskItem> taskItemList) {
        super(context, R.layout.list_layout, taskItemList);
        this.context = context;
        this.taskItemList = taskItemList;
    }


    public TaskListAdapter(Context context, int resource, List<TaskItem> objects,
                           Activity context1, List<TaskItem> taskItemList)
    {
        super(context, resource, objects);
        this.context = context1;
        this.taskItemList = taskItemList;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_layout, null, true);

        TextView tvUserId = listViewItem.findViewById(R.id.txtUserIdName);
        TextView tvDateTime = listViewItem.findViewById(R.id.txtDatetime);
        TextView tvSysDiasScore = listViewItem.findViewById(R.id.txtSysDiasScore);
        TextView tvCondition = listViewItem.findViewById(R.id.txtCondition);



        TaskItem taskItem = taskItemList.get(position);


        int sys = taskItem.getSystolic();
        int dias = taskItem.getDiastolic();

        tvUserId.setText(taskItem.getUserId());

        String dateString = taskItem.getDate() + " " + taskItem.getTime();

        tvDateTime.setText(dateString);
        tvSysDiasScore.setText(sys + "/" + dias);
        tvCondition.setText(taskItem.getCondition());

//        listViewItem.setBackgroundColor(getColor(sys, dias));
        listViewItem.setBackgroundColor(getColor(taskItem.getCondition()));


        return listViewItem;
    }

    private int getColor(String condition) {
        switch(condition) {
            case "Normal":
                return Color.rgb(110, 196, 86);
            case "Elevated":
                return Color.rgb(252, 224, 121);
            case "High Blood Pressure (Stage 1)":
                return Color.rgb(252, 189, 144);
            case "High Blood Pressure (Stage 2)":
                return Color.rgb(245, 128, 95);
            case "Hypertensive Crisis":
                return Color.rgb(191, 0, 0);
            default:
                return Color.GRAY;
        }

    }

//    private int getColor(int systolic, int diastolic){
//        if(systolic < 120 && diastolic < 180){
//            return GREEN;
//        }
//
//        if(systolic >= 120 && systolic <= 129 && diastolic < 80){
//            return YELLOW;
//        }
//
//        if((systolic >= 130 && systolic <= 139) || (diastolic >= 80 && diastolic <= 89)){
//            return Color.rgb(255, 128, 128);
//        }
//
//        if((systolic >= 140) || (diastolic >= 90)){
//            return Color.rgb(255, 51, 51);
//        }
//
//        if((systolic >= 180) || (diastolic >= 120)){
//            return Color.RED;
//        }
//
//        return Color.GRAY;
//    }

}