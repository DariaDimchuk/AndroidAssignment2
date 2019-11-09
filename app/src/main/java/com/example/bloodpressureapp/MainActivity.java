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
import java.util.List;


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
        //TODO Calculate Month to date by checking year AND date
        String year = date.substring(0,4);
        String month = date.substring(5, 7);
        return month;
    }

    /**
     * Calcualte Month to Date by user and current month.
     */
    private void calculateAverage() {
        avgSys = 0;
        avgDias = 0;
        count = 0;

        EditText edtMTD = findViewById(R.id.edtMTD);
        String user = edtMTD.getText().toString();

        Calendar now = Calendar.getInstance();
        String currMonth = String.valueOf(now.get(Calendar.MONTH) + 1);

        // Select all systolic and diastolic dates for user in current month
        for (TaskItem task: taskItemList) {
            if (task.getUserId().equalsIgnoreCase(user) &&
                currMonth.equals(parseMonth(task.getDate()))) {
                avgSys += task.getSystolic();
                avgDias += task.getDiastolic();
                count++;
            }
        }

        // Calculate average
        avgSys /= count;
        avgDias /= count;

        //TODO Format decimal output
        TextView tvAvg = findViewById(R.id.txtAvg);
        String averages = "Avg. Sys: " + avgSys + "\nAvg. Dias: " + avgDias;
        tvAvg.setText(averages);

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


