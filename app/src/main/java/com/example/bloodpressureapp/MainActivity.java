package com.example.bloodpressureapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    DatabaseReference taskDb;

    ListView lvTasks;
    List<TaskItem> taskItemList;

    Button btnAddNew;
    Button btnCalc;
    int count;
    float avgSys;
    float avgDias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskDb = FirebaseDatabase.getInstance().getReference("tasks");

        lvTasks = findViewById(R.id.lvTasks);
        lvTasks.setEmptyView(findViewById(R.id.empty_list_item));

        taskItemList = new ArrayList<TaskItem>();

        btnAddNew = findViewById(R.id.btnAddItem);
        btnCalc = findViewById(R.id.btnCalc);

        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });

        btnCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateAverage();
            }
        });


        lvTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                TaskItem taskItem = taskItemList.get(position);
                updateTask(taskItem.getTaskId());
            }
        });

    }


    /**
     * Bring user to EditTaskActivity.
     */
    private void addTask() {
        Intent intent = new Intent(this, EditTaskActivity.class);
        startActivity(intent);
    }


    private void updateTask(String id) {
        Intent intent = new Intent(this, EditTaskActivity.class);
        intent.putExtra("taskId", id);
        startActivity(intent);
    }

    /**
     * Testing calculateAverage by returning month only.
     * @param date
     * @return
     */
    private String parseMonth(String date) {
        return date.substring(5, 7);
    }

    /**
     * Testing calculateAverage by returning month only.
     * @param date
     * @return
     */
    private String parseYear(String date) {
        return date.substring(0,4);
    }

    /**
     * Calcualte Month to Date by user and current month.
     */
    private void calculateAverage() {
        avgSys = 0;
        avgDias = 0;
        count = 0;

        ArrayList<String> conditions = new ArrayList<>();

        EditText edtMTD = findViewById(R.id.edtMTD);
        String user = edtMTD.getText().toString();


        Calendar now = Calendar.getInstance();
        String currMonth = String.valueOf(now.get(Calendar.MONTH) + 1);
        String currYear = String.valueOf(now.get(Calendar.YEAR));

        // Select all systolic and diastolic dates for user in current month
        for (TaskItem task: taskItemList) {
            if (task.getUserId().equalsIgnoreCase(user)
                    && currMonth.equals(parseMonth(task.getDate()))
                    && currYear.equals(parseYear(task.getDate()))) {

                avgSys += task.getSystolic();
                avgDias += task.getDiastolic();
                count++;

                conditions.add(task.getCondition());
            }
        }

        // Calculate average
        avgSys /= count;
        avgDias /= count;


        //TODO Format decimal output
        TextView tvAvg = findViewById(R.id.txtAvg);
        String averages = "Avg Pressure: " + Math.round(avgSys) + "/" + Math.round(avgDias);


        String avgCondition = determineMostCommonCondition(conditions);
        if(avgCondition != null){
            averages += "\nAvg Condition: " + avgCondition;
        }

        tvAvg.setText(averages);

    }


    private String determineMostCommonCondition(ArrayList<String> conditions){
        String normalStr = getResources().getString(R.string.conditionNormal);
        String elevatedStr = getResources().getString(R.string.conditionElevated);
        String stage1Str = getResources().getString(R.string.conditionStage1);
        String stage2Str = getResources().getString(R.string.conditionStage2);
        String crisisStr = getResources().getString(R.string.conditionCrisis);


        HashMap<String, Integer> counts = new HashMap<>();
        counts.put(normalStr, 0);
        counts.put(elevatedStr, 0);
        counts.put(stage1Str, 0);
        counts.put(stage2Str, 0);
        counts.put(crisisStr, 0);

        for (String c : conditions) {
            if(c.compareTo(normalStr) == 0){
                counts.put(normalStr, counts.get(normalStr) + 1);
            } else if (c.compareTo(elevatedStr) == 0){
                counts.put(elevatedStr, counts.get(elevatedStr) + 1);
            } else if (c.compareTo(stage1Str) == 0){
                counts.put(stage1Str, counts.get(stage1Str) + 1);
            } else if (c.compareTo(stage2Str) == 0){
                counts.put(stage2Str, counts.get(stage2Str) + 1);
            } else if (c.compareTo(crisisStr) == 0){
                counts.put(crisisStr, counts.get(crisisStr) + 1);
            }
        }

        int maxValueInMap = (Collections.max(counts.values()));

        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            if (entry.getValue() == maxValueInMap) {
                //TODO avg may be multiple conditions. If we want to show not just the first one,
                // we can make an array, collect all, and display them
                return entry.getKey();
            }
        }

        return null;
    }


    @Override
    protected void onStart() {
        super.onStart();

        taskDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (taskItemList != null) {
                    taskItemList.clear();
                }

                for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                    TaskItem taskItem = taskSnapshot.getValue(TaskItem.class);
                    taskItemList.add(taskItem);
                }

                TaskListAdapter adapter = new TaskListAdapter(MainActivity.this, taskItemList);

                if (lvTasks != null) {
                    lvTasks.setAdapter(adapter);
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this,
                        "Task cancelled.",Toast.LENGTH_LONG).show();
            }

        });

    }

}


