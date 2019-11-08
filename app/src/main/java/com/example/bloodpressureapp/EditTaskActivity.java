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

    List<TaskItem> taskItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edittask);

        taskDb = FirebaseDatabase.getInstance().getReference("tasks");

        txtDate = findViewById(R.id.txtDate);
        txtTime = findViewById(R.id.txtTime);

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

            Date date = new Date();

            txtDate.setText(TaskItem.getDateString(date));
            txtTime.setText(TaskItem.getTimeString(date));
        }


        taskItemList = new ArrayList<TaskItem>();

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

        //TODO
        String condition = ""; //TODO generate condition

        TaskItem taskItem = new TaskItem(id, user, Integer.parseInt(sys), Integer.parseInt(dias), condition);

        Task setValueTask = taskDb.child(id).setValue(taskItem);
        setTaskListeners(setValueTask);
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
