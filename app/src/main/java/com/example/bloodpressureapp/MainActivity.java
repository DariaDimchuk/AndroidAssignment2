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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Main page for Blood Pressure Reading App.
 */
public class MainActivity extends AppCompatActivity {

    DatabaseReference taskDb;

    ListView lvTasks;
    List<TaskItem> taskItemList;

    Button btnAddNew;

    Button btnCalc;
    Button btnClearCalc;
    String calculationUser;

    Button btnSearch;
    Button btnClearSearch;
    String searchedUser;

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
        btnClearCalc = findViewById(R.id.btnClearCalculation);
        btnSearch = findViewById(R.id.btnSearch);
        btnClearSearch = findViewById(R.id.btnClearFilter);

        addButtonListeners();

        lvTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                TaskItem taskItem = taskItemList.get(position);
                updateTask(taskItem);
            }
        });

    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("searchedUser", searchedUser);
        savedInstanceState.putString("calculationUser", calculationUser);

        TextView tvAvg = findViewById(R.id.txtAvg);
        savedInstanceState.putString("avgCalculationTxt", tvAvg.getText().toString());
        savedInstanceState.putBoolean("calculationVisible", tvAvg.getVisibility() == 0);
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        searchedUser = savedInstanceState.getString("searchedUser");
        calculationUser = savedInstanceState.getString("calculationUser");

        TextView tvAvg = findViewById(R.id.txtAvg);
        tvAvg.setText(savedInstanceState.getString("avgCalculationTxt"));
        boolean isVisible = savedInstanceState.getBoolean("calculationVisible");
        tvAvg.setVisibility(isVisible ? View.VISIBLE : View.GONE);

        if(searchedUser != null && !searchedUser.isEmpty()){
            findData();
        }
    }



    private void addButtonListeners(){
        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });

        btnCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edtMTD = findViewById(R.id.edtMTD);
                calculationUser = edtMTD.getText().toString();

                if(calculationUser == null || calculationUser.isEmpty()){
                    Toast.makeText(MainActivity.this,
                            "Must enter user name for calculation",Toast.LENGTH_LONG).show();
                } else {
                    calculateAverage();
                }
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edtSearch = findViewById(R.id.edtSearch);
                searchedUser = edtSearch.getText().toString();

                if(searchedUser == null || searchedUser.isEmpty()){
                    Toast.makeText(MainActivity.this,
                            "Search cannot be empty",Toast.LENGTH_LONG).show();
                } else {
                    findData();
                }
            }
        });

        btnClearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edtSearch = findViewById(R.id.edtSearch);
                edtSearch.setText("");
                searchedUser = "";
                resetList();
            }
        });

        btnClearCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edtMTD = findViewById(R.id.edtMTD);
                edtMTD.setText("");
                calculationUser = "";
                TextView tvAvg = findViewById(R.id.txtAvg);
                tvAvg.setVisibility(View.GONE);
            }
        });
    }


    /**
     * Search and replace taskItemList with tasks that match the userId.
     */
    public void findData() {
        taskDb = FirebaseDatabase.getInstance().getReference("tasks");
        Query query = taskDb.orderByChild("userId").startAt(searchedUser)
                .endAt(searchedUser+"\uf8ff");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (taskItemList != null) {
                    taskItemList.clear();
                }

                for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                    TaskItem taskItem = taskSnapshot.getValue(TaskItem.class);
                    if (taskItem.getUserId().equals(searchedUser)) {
                        taskItemList.add(taskItem);
                    }
                }

                TaskListAdapter adapter = new TaskListAdapter(MainActivity.this, taskItemList);

                if (lvTasks != null) {
                    lvTasks.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Search cancelled", Toast.LENGTH_LONG).show();
            }
        });

        TextView emptyList = findViewById(R.id.empty_list_item);
        emptyList.setText(getResources().getString(R.string.emptyList_filterPrompt));
    }



        /**
         * Bring user to EditTaskActivity.
         */
    private void addTask() {
        Intent intent = new Intent(this, EditTaskActivity.class);
        startActivity(intent);
    }


    /**
     * Bring user to EditTaskActivity with taskId passed.
     * @param task TaskItem
     */
    private void updateTask(TaskItem task) {
        Intent intent = new Intent(this, EditTaskActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("task", task);
        intent.putExtra("bundle", b);

        startActivity(intent);
    }


    /**
     * Parse month from date as String.
     * @param date String
     * @return String
     */
    private String parseMonth(String date) {
        return date.substring(5, 7);
    }


    /**
     * Parse year from date as String.
     * @param date String
     * @return String
     */
    private String parseYear(String date) {
        return date.substring(0,4);
    }


    /**
     * Calcualte Month to Date by user and current month.
     */
    private void calculateAverage() {
        taskDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                avgSys = 0;
                avgDias = 0;
                count = 0;

                Calendar now = Calendar.getInstance();
                String currMonth = String.valueOf(now.get(Calendar.MONTH) + 1);
                String currYear = String.valueOf(now.get(Calendar.YEAR));

                for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                    TaskItem task = taskSnapshot.getValue(TaskItem.class);

                    if (task.getUserId().equalsIgnoreCase(calculationUser)
                            && currMonth.equals(parseMonth(task.getDate()))
                            && currYear.equals(parseYear(task.getDate()))) {

                        avgSys += task.getSystolic();
                        avgDias += task.getDiastolic();
                        count++;
                    }
                }

                TextView tvAvg = findViewById(R.id.txtAvg);

                if(count == 0){
                    tvAvg.setText(calculationUser + " not found");
                } else {
                    avgSys /= count;
                    avgDias /= count;

                    int sysInt = Math.round(avgSys);
                    int diasInt =  Math.round(avgDias);

                    String avgCondition = getConditionString(String.valueOf(sysInt),
                            String.valueOf(diasInt));

                    String averages = calculationUser + "\nAvg Pressure: " + sysInt + "/" + diasInt;

                    averages += "\nAvg Condition: " + avgCondition;
                    tvAvg.setText(averages);
                }

                tvAvg.setVisibility(View.VISIBLE);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this,
                        "Task cancelled.",Toast.LENGTH_LONG).show();
            }

        });
    }

    /**
     * Get the name of the condition (e.g. Normal, Elevated)
     * @param systolic as an int
     * @param diastolic as an int
     * @return String
     */
    private String getConditionString(String systolic, String diastolic){

        int sysInt = Integer.parseInt(systolic);
        int diasInt = Integer.parseInt(diastolic);

        if(sysInt < 120 && diasInt < 80){
            return getResources().getString(R.string.conditionNormal);
        } else if (sysInt >= 120 && sysInt <= 129 && diasInt < 80){
            return getResources().getString(R.string.conditionElevated);
        } else if ((sysInt >= 130 && sysInt <= 139) || (diasInt >= 80 && diasInt <= 89)){
            return getResources().getString(R.string.conditionStage1);
        } else if ((sysInt >= 180) || (diasInt >= 120)){
            return getResources().getString(R.string.conditionCrisis);
        } else if ((sysInt >= 140) || (diasInt >= 90)){
            return getResources().getString(R.string.conditionStage2);
        }

        return getResources().getString(R.string.conditionDefault);
    }

    @Override
    protected void onStart() {
        super.onStart();

        resetList();

    }

    private void resetList(){
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

        TextView emptyList = findViewById(R.id.empty_list_item);
        emptyList.setText(getResources().getString(R.string.emptyList_addPrompt));
    }

}


