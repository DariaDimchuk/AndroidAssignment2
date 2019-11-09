package com.example.bloodpressureapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class EditTaskActivity extends AppCompatActivity {

    boolean editMode;
    String passedId;
    TaskItem passedItem;

    EditText etUserId;
    EditText etSystoic;
    EditText etDiastoic;
    TextView txtDate;
    TextView txtTime;

    Button btnCancel;
    Button btnSave;
    Button btnDelete;


    DatabaseReference taskDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edittask);

        Date date = new Date();

        taskDb = FirebaseDatabase.getInstance().getReference("tasks");

        txtDate = findViewById(R.id.txtDate);
        txtTime = findViewById(R.id.txtTime);

        String dateTime = TaskItem.getDateString(date) + " " + TaskItem.getTimeString(date);
        txtDate.setText(dateTime);
        etUserId = findViewById(R.id.edtUserId);
        etSystoic = findViewById(R.id.etSystolic);
        etDiastoic = findViewById(R.id.etDiastolic);

        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        Intent intent = getIntent();
        passedId = intent.getStringExtra("taskId");

        if(passedId != null){
            editMode = true;


            taskDb.orderByChild("taskId").equalTo(passedId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            HashMap obj = (HashMap)dataSnapshot.getValue();

                            if(obj != null){
                                HashMap elements = (HashMap)obj.get(passedId);

                                passedItem = new TaskItem(passedId);
                                passedItem.setUserId(elements.get("userId").toString());
                                passedItem.setSystolic(Integer.parseInt(elements.get("systolic").toString()));
                                passedItem.setDiastolic(Integer.parseInt(elements.get("diastolic").toString()));
                                passedItem.setCondition(elements.get("condition").toString());
                                passedItem.setDate(elements.get("date").toString());
                                passedItem.setTime(elements.get("time").toString());

                                populateFieldsWithEditValues(passedItem);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        } else {
            btnDelete.setVisibility(View.GONE);
            txtDate.setText(TaskItem.getDateString(date));
            txtTime.setText(TaskItem.getTimeString(date));
        }


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItem();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTask();
            }
        });

    }


    public void populateFieldsWithEditValues(TaskItem t){
        etUserId.setText(t.getUserId());
        etSystoic.setText(String.valueOf(t.getSystolic()));
        etDiastoic.setText(String.valueOf(t.getDiastolic()));
        txtDate.setText(t.getDate());
        txtTime.setText(t.getTime());
    }


    private void updateItem(){
        String user = etUserId.getText().toString().trim();
        String sys = etSystoic.getText().toString().trim();
        String dias = etDiastoic.getText().toString().trim();

        if(!validateValues(user, sys, dias)){
            return; //stop adding if invalid
        }

        if(editMode){
            editTask(user, sys, dias);
        } else addTask(user, sys, dias);
    }



    /**
     * Add task to database.
     */
    private void addTask(String user, String sys, String dias) {
        String id = taskDb.push().getKey();

        String condition = getConditionString(sys, dias);

        TaskItem taskItem = new TaskItem(id, user, Integer.parseInt(sys), Integer.parseInt(dias), condition);

        Task setValueTask = taskDb.child(id).setValue(taskItem);
        setTaskListeners(setValueTask);
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

    private void editTask(String user, String sys, String dias) {
        passedItem.setUserId(user);
        passedItem.setSystolic(Integer.parseInt(sys));
        passedItem.setDiastolic(Integer.parseInt(dias));

        Task setValueTask = taskDb.child(passedId).setValue(passedItem);
        setTaskListeners(setValueTask);
    }



    private void setTaskListeners(Task task){
        task.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(EditTaskActivity.this,
                        "Item added.",Toast.LENGTH_LONG).show();
                finish();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditTaskActivity.this,
                        "Something went wrong.\n" + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        task.addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Toast.makeText(EditTaskActivity.this,
                        "Task cancelled.",Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Validate input fields are filled in.
     * Validate systolic and diastolic values are valid.
     * @param userId User name as String
     * @param sys Systolic value as String
     * @param dias Diastolic value as String
     * @return boolean
     */
    private boolean validateValues(String userId, String sys, String dias){
        if (TextUtils.isEmpty(userId)) {
            Toast.makeText(this, "You must enter a user name.", Toast.LENGTH_LONG).show();
            return false;
        }

        if (TextUtils.isEmpty(sys) || TextUtils.isEmpty(dias)) {
            Toast.makeText(this, "You must enter both systolic and diastolic blood pressure values",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        int sysInt = Integer.parseInt(sys);
        int diasInt = Integer.parseInt(dias);

        if(sysInt < 90 || sysInt > 250){
            Toast.makeText(this, "Systolic blood pressure values range on average between 90 and 250.",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if(diasInt < 60 || diasInt > 140){
            Toast.makeText(this, "Diastolic blood pressure values range on average between 60 and 140.",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }



    private void deleteTask() {
        Task setRemoveTask = taskDb.child(passedId).removeValue();

        passedId = null;
        editMode = false;
        passedItem = null;


        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);


        setRemoveTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(EditTaskActivity.this,
                        "Task Deleted.",Toast.LENGTH_LONG).show();
            }
        });

        setRemoveTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditTaskActivity.this,
                        "Something went wrong.\n" + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


}
