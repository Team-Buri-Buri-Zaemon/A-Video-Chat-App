package com.example.avideochatapp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.avideochatapp.Models.TaskModel;
import com.example.avideochatapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

public class ToDoList extends AppCompatActivity  implements SearchView.OnQueryTextListener {
    FirebaseRecyclerAdapter<TaskModel, MyViewHolder> adapter;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FloatingActionButton addTaskButton, floatingActionButton2;
    private FloatingActionButton bk;
    private TextView mTextViewShowUploads;
    private DatabaseReference reference;
    String username;
    private FirebaseAuth mAuth;
    private FirebaseUser mUse;
    String onlineuserId;
    String datepatt="^(?:(?:31(\\/)(?:0?[13578]|1[02]|(?:Jan|Mar|May|Jul|Aug|Oct|Dec)))\\1|(?:(?:29|30)(\\/)(?:0?[1,3-9]|1[0-2]|(?:Jan|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec))\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/)(?:0?2|(?:Feb))\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/)(?:(?:0?[1-9]|(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep))|(?:1[0-2]|(?:Oct|Nov|Dec)))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";
    String timepatt="^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$";
    private ProgressDialog loader;
    private String key="",task,description,mdateset,mtimeset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        setContentView(R.layout.activity_to_do_list);

        getSupportActionBar().hide(); // hide the title bar

        recyclerView=findViewById(R.id.recycleview);
        Intent intent = getIntent();
        username=intent.getStringExtra("username");

        mAuth = FirebaseAuth.getInstance();

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        loader =new ProgressDialog(this);


        mUse =mAuth.getCurrentUser();
        onlineuserId= Objects.requireNonNull(mUse).getUid();
        reference= FirebaseDatabase.getInstance().getReference().child("Users").child(onlineuserId).child("To Do List");
//        backButton
        addTaskButton =findViewById(R.id.add_task);

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //addTask(category);
                Intent intent8=new Intent(ToDoList.this,AddTask.class);
                intent8.putExtra("username", username);
                intent8.putExtra("category", "To Do List");
                startActivity(intent8);
            }
        });

        // BACK btN


        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //addTask(category);
                Intent intent8=new Intent(ToDoList.this, com.example.avideochatapp.MainActivity.class);
                startActivity(intent8);
            }
        });

    }

    private void addTask(String Category) {
        AlertDialog.Builder my =new AlertDialog.Builder(this);
        LayoutInflater inflater =LayoutInflater.from(this);
        View myview;


        myview = inflater.inflate(R.layout.todoinput, null);






        my.setView(myview);
        AlertDialog dialog =my.create();
        dialog.setCancelable(false);
        final EditText task =myview.findViewById(R.id.task);
        final EditText description =myview.findViewById(R.id.description);
        final EditText mdate1=myview.findViewById(R.id.date);
        final EditText mtime1=myview.findViewById(R.id.time);
        Button save =myview.findViewById(R.id.save);
        Button cancel =myview.findViewById(R.id.cancel);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setCancelable(true);
                dialog.dismiss();
            }
        });
        //
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mTask =task.getText().toString().trim();
                String mDescription =description.getText().toString().trim();
                String id =reference.push().getKey();
                String mdate=mdate1.getText().toString().trim();
                String mtime=mtime1.getText().toString().trim();
                String date = DateFormat.getDateInstance().format(new Date());


                if(TextUtils.isEmpty(mTask)){
                    task.setError("Task REQUIRED");
                    return;
                }
                else if(TextUtils.isEmpty(mDescription)){
                    description.setError("Task REQUIRED");
                    return;
                }
                else if(TextUtils.isEmpty(mdate)) {
                    mdate1.setError("date REQUIRED");
                    return;
                }
                else if(TextUtils.isEmpty(mtime)){
                    mtime1.setError("time REQUIRED");
                    return;}



                else if(!mdate.matches(datepatt)) {
                    mdate1.setError("Please enter a valid date of format dd/mm/yyyy");
                    return;
                }

                else if(!mtime.matches(timepatt)){
                    mtime1.setError("Please enter a valid time of format hh:mm");
                    return;
                }

                else{
                  /*  loader.setMessage("ADDing your data");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();
                    Model model;

                    model = new Model(mTask, mDescription, id, date, mdate, mtime);


                    reference.child(id).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ToDoList.this, "Task has been inserted succesfully", Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                            }
                            else
                            {
                                String error =task.getException().toString();
                                Toast.makeText(ToDoList.this, "Failed"+error, Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                            }
                        }
                    });*/

                }
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<TaskModel> options =new FirebaseRecyclerOptions.Builder<TaskModel>()
                .setQuery(reference, TaskModel.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<TaskModel, MyViewHolder>(options) {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull TaskModel model) {
                holder.setDate(model.getDate());
                holder.setTask(model.getTask());
                holder.setDEsc(model.getDescription());
                holder.setmDAte(model.getmdate());
                holder.setmTIme(model.getmtime());
                holder.setP(model.getMpriority());
                String s1=model.getMpriority();
                if(s1.equals("Low Priority")) holder.mview.setBackground(getDrawable(R.drawable.blue_priority));
                else holder.mview.setBackground(getDrawable(R.drawable.red_imp));





                holder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        key=getRef(position).getKey();


                        task=model.getTask();
                        description=model.getDescription();
                        mdateset=model.getmdate();
                        mtimeset=model.getmtime();
                        String priority=model.getMpriority();

                        updateTask();

                    }
                });

            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view;
                view =LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieved_date,parent,false);
                return new MyViewHolder(view);
            }
        }  ;
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }






    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        return false;
    }

    public void onBackClick(View view) {
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{
        View mview;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mview =itemView;

        }

        public void setTask(String task){
            TextView taskTextVIew =mview.findViewById(R.id.tasktv);
            taskTextVIew.setText(task);

        }

        public void setDEsc(String desc )
        {
            TextView descTextVIew =mview.findViewById(R.id.desctv);
            descTextVIew.setText(desc);
        }
        public void setDate(String date)
        {
            TextView dateTextVIew =mview.findViewById(R.id.dateTv);
            dateTextVIew.setText(date);

        }
        public void setmDAte(String mdate3)
        {
            TextView dateTextVIew =mview.findViewById(R.id.mdatetv);
            dateTextVIew.setText(mdate3);

        }
        public void setmTIme(String mtime3)
        {
            TextView dateTextVIew =mview.findViewById(R.id.mtimetv);
            dateTextVIew.setText(mtime3);

        }
        public void setP(String priorityy){
            TextView prioritytv=mview.findViewById(R.id.mpriortv);
            prioritytv.setText(priorityy);

        }


    }

    private void updateTask() {
        AlertDialog.Builder myDialouge = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.update_date, null);
        myDialouge.setView(view);


        AlertDialog dialog = myDialouge.create();
        EditText mtask = view.findViewById(R.id.medittask);
        EditText mdes = view.findViewById(R.id.meditdesc);
        EditText mda = view.findViewById(R.id.date1);
        EditText mti = view.findViewById(R.id.time);


        mtask.setText(task);
        mtask.setSelection(task.length());

        mda.setText(mdateset);
        mda.setSelection(mdateset.length());

        mti.setText(mtimeset);
        mti.setSelection(mtimeset.length());


        mdes.setText(description);
        mdes.setSelection(description.length());

        Button delbutton = view.findViewById(R.id.btndelete);
        Button updbutton = view.findViewById(R.id.btnupdate);

        /*updbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task =mtask.getText().toString().trim();
                description=mdes.getText().toString().trim();
                mdateset=mda.getText().toString().trim();
                mtimeset=mti.getText().toString().trim();
                String date =DateFormat.getDateInstance().format(new Date());

                Model model =new Model(task,description,key,date,mdateset,mtimeset);

                reference.child(key).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(ToDoList.this, "Data has been u[dated sucessfully", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            String ERR = task.getException().toString();
                            Toast.makeText(ToDoList.this, "update field "+ERR, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.dismiss();
            }
        });*/
        delbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ToDoList.this, "Task deleted succesfully", Toast.LENGTH_SHORT).show();
                        } else {
                            String err = task.getException().toString();
                            Toast.makeText(ToDoList.this, "Failed to delete task" + err, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
