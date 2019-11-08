package com.example.bloodpressureapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class EditTaskActivity extends AppCompatActivity {

    EditText etUserId;
    EditText etSystoic;
    EditText etDiastoic;
    TextView txtDate;
    TextView txtTime;
    Button btnCancel;
    Button btnSave;
    Date date;

    DatabaseReference taskDb;

    ListView lvTasks;
    List<TaskItem> taskItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edittask);

        taskDb = FirebaseDatabase.getInstance().getReference("tasks");


        date = new Date();
        txtDate = findViewById(R.id.txtDate);
        txtTime = findViewById(R.id.txtTime);

        txtDate.setText(TaskItem.getDateString(date));
        txtTime.setText(TaskItem.getTimeString(date));


        etUserId = findViewById(R.id.edtUserId);
        etSystoic = findViewById(R.id.etSystolic);
        etDiastoic = findViewById(R.id.etDiastolic);

        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);

        taskItemList = new ArrayList<TaskItem>();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    /**
     * Add task to database.
     */
    private void addTask() {
        String user = etUserId.getText().toString().trim();
        String sys = etSystoic.getText().toString().trim();
        String dias = etDiastoic.getText().toString().trim();

        //Date value should be auto added so no need to check it

        if(!validateValues(user, sys, dias)){
            return; //stop adding if invalid
        }

        String id = taskDb.push().getKey();

        //TODO
        String condition = ""; //TODO generate condition

        TaskItem taskItem = new TaskItem(id, user, new Date(), Integer.parseInt(sys), Integer.parseInt(dias), condition);

        Task setValueTask = taskDb.child(id).setValue(taskItem);

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


    private void updateTask(String taskId, String userId, Date date, String sys, String dias) {
        System.out.println("ID: " + taskId);
        DatabaseReference dbRef = taskDb.child(taskId);

        //TODO CONDITIIOn
        String condition = "";

        TaskItem taskItem = new TaskItem(taskId, userId, date, Integer.parseInt(sys), Integer.parseInt(dias), condition);

        Task setValueTask = dbRef.setValue(taskItem);

        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(EditTaskActivity.this,
                        "Task Updated.",Toast.LENGTH_LONG).show();
            }
        });

        setValueTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditTaskActivity.this,
                        "Something went wrong.\n" + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private void showUpdateDialog(final String id, String task, String who, String dueDate, boolean done) {
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
//
//        LayoutInflater inflater = getLayoutInflater();
//
//        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
//        dialogBuilder.setView(dialogView);
//
//        final EditText edtTask = dialogView.findViewById(R.id.edtTaskUpdate);
//        edtTask.setText(task);
//
//        final EditText edtWho = dialogView.findViewById(R.id.edtWhoUpdate);
//        edtWho.setText(who);
//
//        final TextView tvDate = dialogView.findViewById(R.id.txtDateUpdate);
//        tvDate.setText(dueDate);
//        dialogDate = tvDate;
//
//        final Spinner sprDone = dialogView.findViewById(R.id.sprDone);
//        sprDone.setSelection(((ArrayAdapter<Boolean>)sprDone.getAdapter()).getPosition(done));
//
//        final Button btnUpdate = dialogView.findViewById(R.id.btnUpdate);
//
//        dialogBuilder.setTitle("Update '" + task + "' (" + who + ")");
//
//        final AlertDialog alertDialog = dialogBuilder.create();
//        alertDialog.show();
//
//        tvDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //TO DO
//                showDatePickerDialog();
//            }
//        });
//
//
//
//        btnUpdate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String task = edtTask.getText().toString().trim();
//                String who = edtWho.getText().toString().trim();
//                String done = sprDone.getSelectedItem().toString().trim();
//                String date = tvDate.getText().toString().trim();
//                boolean isDone = false;
//                System.out.println("DONE: " + done);
//                if (done.equals("Complete")) {
//                    isDone = true;
//                }
//
//                if (TextUtils.isEmpty(task)) {
//                    edtTask.setError("Task is required");
//                    return;
//                } else if (TextUtils.isEmpty(who)) {
//                    edtWho.setError("Task Person is required");
//                    return;
//                }
//
//                updateTask(id, task, who, date, isDone);
//
//                alertDialog.dismiss();
//            }
//        });
//
//
//        //TODO DELETE FUNC
////        final Button btnDelete = dialogView.findViewById(R.id.btnDelete);
////        btnDelete.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                deleteTask(id);
////
////                alertDialog.dismiss();
////            }
////        });
//
//    }

    private void deleteTask(String id) {
        DatabaseReference dbRef = taskDb.child(id);

        Task setRemoveTask = dbRef.removeValue();
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
