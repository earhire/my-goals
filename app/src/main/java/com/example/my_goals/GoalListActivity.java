package com.example.my_goals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.my_goals.models.Goal;
import com.example.my_goals.models.Session;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class GoalListActivity extends AppCompatActivity implements AdapterGoal.Holder.GoalInterface{
    RecyclerView rv_list_goals;
    Button bt_add_goal;

    RecyclerView.LayoutManager manager;
    AdapterGoal adapter;
    DatabaseReference dbref;
    Query query_goal;
    ArrayList<Goal> list_goal = new ArrayList<>();
    HashMap<Integer, String> current_goal = new HashMap<>();
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_list);
        //get userID from current session
        user_id = Session.SessionDetails.userUI;

        rv_list_goals = findViewById(R.id.id_rv_goals);
        bt_add_goal = findViewById(R.id.btn_goal_list_add);

        //getting an instance and to access location in the database
        dbref = FirebaseDatabase.getInstance().getReference("Goals");
        query_goal  = dbref.orderByChild("title");
        //displaying list in vertical scrolling
        rv_list_goals.setLayoutManager(new LinearLayoutManager(GoalListActivity.this));
        //retrieving data for list of goals
        query_goal.addListenerForSingleValueEvent(listener);
        //when user pressed button Add new goal
        bt_add_goal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GoalListActivity.this, AddingGoalActivity.class));
            }
        });


    }
    //To read data at a path and listen for changes
    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
            int position=0;
            for(DataSnapshot dss:snapshot.getChildren()){
                Goal goal = dss.getValue(Goal.class);
                if(goal.getUser_id().equals(user_id)) {
                    list_goal.add(goal);
                    current_goal.put(position, dss.getKey());
                    position++;
                }
            }
            //assigning an adapter for recyclerview
            adapter= new AdapterGoal(list_goal, GoalListActivity.this);
            rv_list_goals.setAdapter(adapter);
        }

        @Override
        public void onCancelled(@NonNull @NotNull DatabaseError error) {

        }
    };

    @Override
    public void onGoalClick(int i) {
        Intent intent = new Intent(GoalListActivity.this, GoalDetailActivity.class);
        intent.putExtra("Goals", list_goal.get(i));
        intent.putExtra("goal_id", current_goal.get(i));
        startActivity(intent);
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