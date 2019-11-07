package com.example.bloodpressureapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class EditTaskActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener {

    EditText etUserId;
    Button btnDate;
    Button btnTime;
    EditText etSystoic;
    EditText etDiastoic;
    Button btnCancel;
    Button btnSave;
    String date;

    DatabaseReference taskDb;

    ListView lvTasks;
    List<TaskItem> taskItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnDate = findViewById(R.id.btnDate);

        /**
         * Set click listener for selecting task due date.
         */
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        taskDb = FirebaseDatabase.getInstance().getReference("tasks");

        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);

        taskItemList = new ArrayList<TaskItem>();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });

    }

    /**
     * Display calendar for selecting task due date.
     */
    public void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    /**
     * Once task due date is selected, display date.
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month += 1;
        dueDate = month + "/" + dayOfMonth  + "/" + year;
        btnDate.setText("Due date: " + dueDate);
        if (dialogDate != null) {
            dialogDate.setText(dueDate);
        }

    }

    /**
     * Add task to database.
     */
    private void addTask() {
        String task = edtTask.getText().toString().trim();
        String who = edtWho.getText().toString().trim();

        if (TextUtils.isEmpty(task)) {
            Toast.makeText(this, "You must enter a task.", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(who)) {
            Toast.makeText(this, "You must enter a task person.", Toast.LENGTH_LONG).show();
            return;
        }

        String id = taskDb.push().getKey();
        TaskItem taskItem = new TaskItem(id, task, who, dueDate, false);

        Task setValueTask = taskDb.child(id).setValue(taskItem);

        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(MainActivity.this,
                        "Task added.",Toast.LENGTH_LONG).show();

                edtTask.setText("");
                edtWho.setText("");
                btnDate.setText(R.string.hintDate);
            }
        });

        setValueTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,
                        "something went wrong.\n" + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTask(String id, String task, String who, String dueDate, boolean isDone) {
        System.out.println("ID: " + id);
        DatabaseReference dbRef = taskDb.child(id);

        TaskItem taskItem = new TaskItem(id, task, who, dueDate, isDone);

        Task setValueTask = dbRef.setValue(taskItem);

        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(MainActivity.this,
                        "Task Updated.",Toast.LENGTH_LONG).show();
            }
        });

        setValueTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,
                        "Something went wrong.\n" + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUpdateDialog(final String id, String task, String who, String dueDate, boolean done) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edtTask = dialogView.findViewById(R.id.edtTaskUpdate);
        edtTask.setText(task);

        final EditText edtWho = dialogView.findViewById(R.id.edtWhoUpdate);
        edtWho.setText(who);

        final TextView tvDate = dialogView.findViewById(R.id.txtDateUpdate);
        tvDate.setText(dueDate);
        dialogDate = tvDate;

        final Spinner sprDone = dialogView.findViewById(R.id.sprDone);
        sprDone.setSelection(((ArrayAdapter<Boolean>)sprDone.getAdapter()).getPosition(done));

        final Button btnUpdate = dialogView.findViewById(R.id.btnUpdate);

        dialogBuilder.setTitle("Update '" + task + "' (" + who + ")");

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TO DO
                showDatePickerDialog();
            }
        });



        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String task = edtTask.getText().toString().trim();
                String who = edtWho.getText().toString().trim();
                String done = sprDone.getSelectedItem().toString().trim();
                String date = tvDate.getText().toString().trim();
                boolean isDone = false;
                System.out.println("DONE: " + done);
                if (done.equals("Complete")) {
                    isDone = true;
                }

                if (TextUtils.isEmpty(task)) {
                    edtTask.setError("Task is required");
                    return;
                } else if (TextUtils.isEmpty(who)) {
                    edtWho.setError("Task Person is required");
                    return;
                }

                updateTask(id, task, who, date, isDone);

                alertDialog.dismiss();
            }
        });

        final Button btnDelete = dialogView.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTask(id);

                alertDialog.dismiss();
            }
        });

    }

    private void deleteTask(String id) {
        DatabaseReference dbRef = taskDb.child(id);

        Task setRemoveTask = dbRef.removeValue();
        setRemoveTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(MainActivity.this,
                        "Task Deleted.",Toast.LENGTH_LONG).show();
            }
        });

        setRemoveTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,
                        "Something went wrong.\n" + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


}
