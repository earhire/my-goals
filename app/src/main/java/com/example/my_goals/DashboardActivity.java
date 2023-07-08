package com.example.my_goals;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.my_goals.models.Session;

public class DashboardActivity extends AppCompatActivity {
    Button bt_view_all_goals, bt_satisfaction_level;
    ImageView img_add_goal;
    TextView tv_user_name, tv_progress_analysis, tv_calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Link impostors to XML Views
        bt_view_all_goals = findViewById(R.id.bt_dashboard_view_goals);
        bt_satisfaction_level = findViewById(R.id.bt_dashboard_satisfaction_level);
        tv_user_name = findViewById(R.id.tv_dashboard_user_name);
        tv_progress_analysis = findViewById(R.id.tv_dashboard_progress_analysis);
        tv_calendar = findViewById(R.id.tv_dashboard_calendar);
        img_add_goal = findViewById(R.id.img_dashboard_add_goal);

        //greeting user
        String user_name = Session.SessionDetails.fn;
        tv_user_name.setText("Hello " + user_name);

        //button to add new goal
        img_add_goal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this, AddingGoalActivity.class));
            }
        });

        //button to view all user`s goals
        bt_view_all_goals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this, GoalListActivity.class));
            }
        });

        //button to view/update satisfaction balance
        bt_satisfaction_level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this, SatisfactionLevelActivity.class));
            }
        });

        //button to view calendar
        tv_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this, CalendarActivity.class));
            }
        });

        //button to view progress analytics
        tv_progress_analysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this, ProgressAnalytics.class));
            }
        });
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
            case R.id.menu_progress_analytics:
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