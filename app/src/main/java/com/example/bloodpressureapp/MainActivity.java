package com.example.bloodpressureapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.widget.ListView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import java.util.Date;
import java.util.List;
import android.app.DatePickerDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;


public class MainActivity extends AppCompatActivity {

    DatabaseReference taskDb;

    ListView lvTasks;
    List<TaskItem> taskItemList;

    Button btnAddNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskDb = FirebaseDatabase.getInstance().getReference("tasks");

        lvTasks = findViewById(R.id.lvTasks);
        taskItemList = new ArrayList<TaskItem>();


        btnAddNew = findViewById(R.id.btnAddItem);
        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });


        lvTasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                makeText(MainActivity.this, "Long click", LENGTH_LONG);
                TaskItem taskItem = taskItemList.get(position);

                showUpdateDialog(taskItem.getUserId(),
                        taskItem.getDate(),
                        taskItem.getSystolic(),
                        taskItem.getDiastolic());

                return false;
            }
        });

    }


    /**
     * Add task to database.
     */
    private void addTask() {
        Intent intent = new Intent(this, EditTaskActivity.class);

        startActivity(intent);

        showEmptyListMessage();

//        String task = edtTask.getText().toString().trim();
//        String who = edtWho.getText().toString().trim();
//​
//        if (TextUtils.isEmpty(task)) {
//            makeText(this, "You must enter a task.", LENGTH_LONG).show();
//            return;
//        }
//​
//        if (TextUtils.isEmpty(who)) {
//            makeText(this, "You must enter a task person.", LENGTH_LONG).show();
//            return;
//        }
//​
//        String id = taskDb.push().getKey();
//        TaskItem taskItem = new TaskItem(id, task, who, dueDate, false);
//​
//        Task setValueTask = taskDb.child(id).setValue(taskItem);
//​
//        setValueTask.addOnSuccessListener(new OnSuccessListener() {
//            @Override
//            public void onSuccess(Object o) {
//                makeText(MainActivity.this,
//                        "Task added.", LENGTH_LONG).show();
//​
//                edtTask.setText("");
//                edtWho.setText("");
//                btnDate.setText(R.string.hintDate);
//            }
//        });
//​
//        setValueTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                makeText(MainActivity.this,
//                        "something went wrong.\n" + e.toString(),
//                        LENGTH_SHORT).show();
//            }
//        });
    }


    private void showEmptyListMessage(){
        //TextView emptyTxt = findViewById(R.id.txtEmpty);

        taskDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView emptyTxt = findViewById(R.id.txtEmpty);

                if(dataSnapshot.getChildrenCount() > 0){
                    emptyTxt.setVisibility(View.GONE); //hide
                } else {
                    emptyTxt.setVisibility(View.VISIBLE); //hide
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this,
                        "onCancelled called.",Toast.LENGTH_LONG).show();
            }

        });


//        if(taskItemList.size() > 0){
//            emptyTxt.setVisibility(View.GONE); //hide
//        } else {
//            emptyTxt.setVisibility(View.VISIBLE);
//        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        taskDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showEmptyListMessage();

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
            public void onCancelled(@NonNull DatabaseError databaseError) { }

        });

    }

    private void updateTask(String id, String task, String who, String dueDate, boolean isDone) {
        System.out.println("ID: " + id);
        DatabaseReference dbRef = taskDb.child(id);

        TaskItem taskItem = new TaskItem(id); //id, task, who, dueDate, isDone);

        Task setValueTask = dbRef.setValue(taskItem);

        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                makeText(MainActivity.this,
                        "Task Updated.", LENGTH_LONG).show();
            }
        });

        setValueTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                makeText(MainActivity.this,
                        "Something went wrong.\n" + e.toString(),
                        LENGTH_SHORT).show();
            }
        });
    }


    private void showUpdateDialog(final String id, Date readDate, int systolic, int diastolic) {
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
//​
//        LayoutInflater inflater = getLayoutInflater();
//​
//        final View dialogView = inflater.inflate(R.layout.activity_edittask, null);
//        dialogBuilder.setView(dialogView);
//​
//        final EditText edtUserId = dialogView.findViewById(R.id.edtUserId);
//        edtUserId.setText(id);
//
//        final Button date = dialogView.findViewById(R.id.btnDate);
//        date.setText(readDate.toString()); //TODO
//        //dialogDate = tvDate;
//​
//        final EditText edtSystolic = dialogView.findViewById(R.id.etSystolic);
//        edtSystolic.setText(systolic);
//​
//        final EditText edtDiastolic = dialogView.findViewById(R.id.etDiastolic);
//        edtDiastolic.setText(diastolic);
//​
//        final Spinner sprDone = dialogView.findViewById(R.id.sprDone);
//        sprDone.setSelection(((ArrayAdapter<Boolean>)sprDone.getAdapter()).getPosition(done));
//​
//        final Button btnUpdate = dialogView.findViewById(R.id.btnUpdate);
//​
//        dialogBuilder.setTitle("Update '" + task + "' (" + who + ")");
//​
//        final AlertDialog alertDialog = dialogBuilder.create();
//        alertDialog.show();
//​
//        tvDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //TO DO
//                showDatePickerDialog();
//            }
//        });
//​


//​
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
//​
//                if (TextUtils.isEmpty(task)) {
//                    edtTask.setError("Task is required");
//                    return;
//                } else if (TextUtils.isEmpty(who)) {
//                    edtWho.setError("Task Person is required");
//                    return;
//                }
//​
//                updateTask(id, task, who, date, isDone);
//​
//                alertDialog.dismiss();
//            }
//        });
//​
//        final Button btnDelete = dialogView.findViewById(R.id.btnDelete);
//        btnDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                deleteTask(id);
//​
//                alertDialog.dismiss();
//            }
//        });
//​
    }



    private void deleteTask(String id) {
        DatabaseReference dbRef = taskDb.child(id);

        Task setRemoveTask = dbRef.removeValue();
        setRemoveTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                makeText(MainActivity.this,
                        "Item Deleted.", LENGTH_LONG).show();
            }
        });

        setRemoveTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                makeText(MainActivity.this,
                        "Something went wrong.\n" + e.toString(),
                        LENGTH_SHORT).show();
            }
        });
    }


}


