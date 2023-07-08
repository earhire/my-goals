package com.example.my_goals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my_goals.models.Session;
import com.example.my_goals.models.Task;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;


public class AddingTasksActivity extends AppCompatActivity implements AdapterTask.Holder.TaskInterface{
    RecyclerView rv_task_list;
    TextView tv_goal_title;
    EditText ed_task_title, ed_task_start_date, ed_task_end_date;
    Spinner sp_task_repetition, sp_task_time;
    Button bt_add_task;
    AdapterTask adapter;
    DatabaseReference dbref;
    Query query_task;
    ArrayList<Task> list_task = new ArrayList<>();
    HashMap<Integer, String> current_goal = new HashMap<>();
   //for spinner time slots
    LocalTime ldt = LocalTime.of( 00, 0); // 2021-03-20 10:00:00
    //LocalTime last = LocalTime.of(00, 0);
    ArrayList<LocalTime> slots = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_tasks);

        tv_goal_title = findViewById(R.id.tv_adding_tasks_title_goal);
        rv_task_list = findViewById(R.id.rv_task_list);
        bt_add_task = findViewById(R.id.bt_add_task);
        ed_task_title = findViewById(R.id.ed_name_task);
        sp_task_repetition = findViewById(R.id.sp_task_repetition);
        sp_task_time = findViewById(R.id.spinner_add_task_time);
        ed_task_start_date = findViewById(R.id.et_adding_task_start_date);
        ed_task_end_date = findViewById(R.id.et_adding_task_end_date);
        setTitle("Adding new task");

        //generating time slots for spinner
            for (int i = 0; i <= 24; i++) {
                slots.add(ldt);
                ldt = ldt.plusHours(1);
            }

        // Create the instance of ArrayAdapter
        // having the list of courses
        ArrayAdapter ad = new ArrayAdapter(this, android.R.layout.simple_spinner_item, slots);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_task_time.setAdapter(ad);


        //get data from previous activity
        Intent i_data = getIntent();
        String current_goal_id = i_data.getStringExtra("goal_id");
        String current_goal_title = i_data.getStringExtra("goal_name");
        String current_goal_type = i_data.getStringExtra("goal_type");
        tv_goal_title.setText("Goal: " + current_goal_title);

        //getting an instance and to access location in the database
        dbref = FirebaseDatabase.getInstance().getReference("Tasks");
        query_task  = FirebaseDatabase.getInstance().getReference("Tasks").orderByChild("goal_id").equalTo(current_goal_id);
        //displaying list in vertical scrolling
        rv_task_list.setLayoutManager(new LinearLayoutManager(AddingTasksActivity.this));
        Integer array_size = 0;
        array_size = list_task.size();
        Log.i("size of array", array_size.toString());
        //assigning an adapter for recyclerview Tasks
        adapter= new AdapterTask(list_task, AddingTasksActivity.this);
        rv_task_list.setAdapter(adapter);
        //
        // retrieve data from database for recyclerview Tasks
        query_task.addListenerForSingleValueEvent(listener);

       //click button Add task
        bt_add_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Formatting start date and end date to use to get list task dates
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                String textFromStartDate = ed_task_start_date.getText().toString();
                String textFromEndDate = ed_task_end_date.getText().toString();
                LocalDate start_date_formatted = LocalDate.parse(textFromStartDate, dateFormatter);
                Log.i("start_date_formated", start_date_formatted.toString());
                LocalDate end_date_formatted = LocalDate.parse(textFromEndDate, dateFormatter);
                Log.i("end_date_formated", end_date_formatted.toString());
                // get day of week selected by user task repetition
                String day_of_week_calc = get_day_of_week(sp_task_repetition.getSelectedItemId());

                //checking data are completed
                if(ed_task_title.getText().toString().isEmpty() ||  sp_task_repetition.getSelectedItem().toString().isEmpty() ||
                        ed_task_start_date.getText().toString().isEmpty() ||
                        ed_task_end_date.getText().toString().isEmpty() ||
                        sp_task_time.getSelectedItem().toString().isEmpty()) {
                    Toast.makeText(AddingTasksActivity.this, "All fields should be completed", Toast.LENGTH_SHORT).show();
                    return;
                }

                //checking that start date is less then end date
                if(start_date_formatted.isAfter(end_date_formatted)){
                    Toast.makeText(AddingTasksActivity.this, "Start date should be less than end date", Toast.LENGTH_LONG).show();
                    return;
                }


                ArrayList<String> list_date = new ArrayList<>(getListDates(day_of_week_calc, start_date_formatted,end_date_formatted));



                Task new_task = new Task(ed_task_title.getText().toString(), sp_task_repetition.getSelectedItem().toString(),
                                         day_of_week_calc,ed_task_start_date.getText().toString(),
                                         ed_task_end_date.getText().toString(), sp_task_time.getSelectedItem().toString(),
                                         current_goal_id, null, false, 30, 0,current_goal_type);

                String key = dbref.push().getKey();
                dbref.child(key).setValue(new_task).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Integer number_tasks = 0;
                        for(String task_date : list_date) {
                            //adding dates of tasks
                           Log.i("Adding task_dates to db", task_date);
                           dbref.child(key).child("dates").child(task_date).setValue(true);
                           number_tasks ++;
                        }
                        dbref.child(key).child("number_dates").setValue(number_tasks);
                        //on successful adding new topic, showing user message
                        Toast.makeText(AddingTasksActivity.this, "New task was added successful", Toast.LENGTH_LONG).show();
                        //moving to Topic list activity
                        adapter.addTask(new_task);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //in case unsuccessful adding new topic in db
                        Toast.makeText(AddingTasksActivity.this, "Task adding failed, check connection", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    //To read data at a path and listen for changes
    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
            int position=0;
            for(DataSnapshot dss:snapshot.getChildren()){
                Task task = dss.getValue(Task.class);
                list_task.add(task);
                position++;
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull @NotNull DatabaseError error) {

        }
    };

    @Override
    public void onTaskClick(int i) {

    }
    private ArrayList<String> getListDates(String day_of_week, LocalDate start_date, LocalDate end_date){

        LocalDate start = start_date;
        LocalDate end = end_date;
        ArrayList<String> totalDates = new ArrayList<>();
        LocalDate next_date = start;
        String string_list_dates;
        int daysToAdvance = 1;

        while (!next_date.isAfter(end)) {
            if (next_date.getDayOfWeek().name().equalsIgnoreCase(day_of_week)){
                daysToAdvance = 7;
                totalDates.add(next_date.toString());
                Log.i("AddingTasksActivity-list",next_date.toString());
            }
            next_date = next_date.plusDays(daysToAdvance);
        }
        return totalDates;
    }

    private String get_day_of_week(Long repetition){
         String day_of_week;
         Integer repetition_id = repetition.intValue();
        Log.i("AddingTasksActivity", repetition_id.toString());
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yy");
        LocalDate d = LocalDate.now();
        String today_date = DayOfWeek.from(d).name();
        switch (repetition_id){
            case 0 : day_of_week="Monday";
            break;
            case 1 : day_of_week = "Tuesday";
            break;
            case 2: day_of_week = "Wednesday";
            break;
            case 3: day_of_week = "Thursday";
            break;
            case 4: day_of_week = "Friday";
            break;
            case 5: day_of_week = "Saturday";
            break;
            case 6: day_of_week = "Sunday";
            break;
            default: day_of_week = today_date;
        }
        return  day_of_week.toUpperCase();
    }

    //add menu of app
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }
    //indicated where to navigate for each option of menu
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_dashboad:
                startActivity(new Intent(this,DashboardActivity.class));
                return true;
            case R.id.menu_calendar:
                startActivity(new Intent(this, CalendarActivity.class));
                return true;
            case R.id.menu_progress_analytics:s:
                startActivity(new Intent(this, ProgressAnalytics.class));
                return true;

            case R.id.menu_sign_out: {
                Session.SessionDetails.logout();
                startActivity(new Intent(this, RegisterActivity.class));
            }
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}