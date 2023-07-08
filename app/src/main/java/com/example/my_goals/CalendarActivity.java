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
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.my_goals.models.Session;
import com.example.my_goals.models.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CalendarActivity extends AppCompatActivity implements AdapterTask.Holder.TaskInterface{
    TextView tv_today_date, tv_next_week_day;
    ImageView im_check_box_done, im_task_done;
    CalendarView calendarView;

    AdapterTask adapter;
    DatabaseReference dbref;
    Query query_task;
    ArrayList<Task> list_task = new ArrayList<>();
    HashMap<Integer, String> current_task = new HashMap<>();
    RecyclerView rv_task_list;
    RecyclerView.LayoutManager manager;

    SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yy");
    final LocalDate d = LocalDate.now();
    LocalDate calendarview_date =d;
    final String  today_dateofWeek = DayOfWeek.from(d).name();
    LocalDate selected_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        tv_today_date = findViewById(R.id.tv_today_date);
        tv_next_week_day = findViewById(R.id.tv_calendar_next_week_day);
        rv_task_list = findViewById(R.id.rv_calendar_tasks_list);
        calendarView = findViewById(R.id.calendarView_calendar_activity);
        setTitle("Calendar of tasks");

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yy");
        Date d = new Date();
        String today_date = sdf.format(d);

        tv_today_date.setText(today_date);
        tv_next_week_day.setText(find_day_week(d));
        //getting an instance and to access location in the database
        dbref = FirebaseDatabase.getInstance().getReference("Tasks");
        query_task  = dbref.orderByChild("time");
        //displaying list in vertical scrolling
        rv_task_list.setLayoutManager(new LinearLayoutManager(CalendarActivity.this));

        //assigning an adapter for recyclerview
        adapter= new AdapterTask(list_task, CalendarActivity.this);
        rv_task_list.setAdapter(adapter);

        query_task.addListenerForSingleValueEvent(listener);

        //represent list of task for selected date
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                //define the format of date
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                //get date from CalendarView
                LocalDate date_calendar_formatted = LocalDate.of(year,month+1, dayOfMonth);
                calendarview_date = date_calendar_formatted;
                //get day of week from selected date on CalendarView
                String day_of_week = date_calendar_formatted.getDayOfWeek().name();
                list_task.clear();

                //retrieve tasks from DB for selected date
                dbref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list_task.clear();
                        Integer position = 0;
                        for(DataSnapshot dss:snapshot.getChildren()){
                            Task task = dss.getValue(Task.class);
                            //filtering list only with tasks for today`s day of week
                            LocalDate end_date_formatted = LocalDate.parse(task.getDay_end().toString(), dateFormatter);
                            Log.i("date_end", end_date_formatted.toString() + " selected:" + date_calendar_formatted.toString());
                            if((task.getDay_of_week().equalsIgnoreCase(day_of_week)) && (date_calendar_formatted.isBefore(end_date_formatted))){
                                list_task.add(task);
                                current_task.put(position, dss.getKey());
                                position++;
                            }
                            //assigning an adapter for recyclerview
                            adapter= new AdapterTask(list_task, CalendarActivity.this);
                            rv_task_list.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
    private String find_day_week(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yy");
        SimpleDateFormat ddf = new SimpleDateFormat("EEEE");
        Date new_date = new Date();
        date.setDate(date.getDate()+1);
        String n_date = ddf.format(date);
        return sdf.format(date);
       }

    //To read data at a path and listen for changes
    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
            int position=0;
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            list_task.clear();
            for(DataSnapshot dss:snapshot.getChildren()){
                Task task = dss.getValue(Task.class);
                //filtering list only with tasks for today`s day of week
                LocalDate end_date_formatted = LocalDate.parse(task.getDay_end().toString(), dateFormatter);
                Log.i("date_end", end_date_formatted.toString() + " today:" + d.toString());
                if((task.getDay_of_week().equalsIgnoreCase(today_dateofWeek)) && (d.isBefore(end_date_formatted))){
                list_task.add(task);
                current_task.put(position, dss.getKey());
                position++;
                }
                //assigning an adapter for recyclerview
                adapter= new AdapterTask(list_task, CalendarActivity.this);
                rv_task_list.setAdapter(adapter);
                }
        }

        @Override
        public void onCancelled(@NonNull @NotNull DatabaseError error) {

        }
    };

    @Override
    public void onTaskClick(int i) {
       if(calendarview_date.compareTo(d) != 0){
            Toast.makeText(CalendarActivity.this, "The task can be done only for today", Toast.LENGTH_SHORT).show();
            return;
        }
        dbref =FirebaseDatabase.getInstance().getReference("Tasks");
        if(list_task.get(i).isDone_today())
        {
            dbref.child(current_task.get(i)).child("done_today").setValue(false);
            list_task.get(i).setDone_today(false);
            adapter.notifyDataSetChanged();
            Toast.makeText(CalendarActivity.this, "The task is not done", Toast.LENGTH_SHORT).show();
        }
        else if (!list_task.get(i).isDone_today())
        {
            dbref.child(current_task.get(i)).child("done_today").setValue(true);
            list_task.get(i).setDone_today(true);
            adapter.notifyDataSetChanged();
            Toast.makeText(CalendarActivity.this, "The task is done", Toast.LENGTH_SHORT).show();
        }
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