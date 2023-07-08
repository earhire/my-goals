package com.example.my_goals;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.my_goals.models.Goal;
import com.example.my_goals.models.Session;
import com.squareup.picasso.Picasso;

public class GoalDetailActivity extends AppCompatActivity {

    TextView tv_goal_title, tv_goal_type, tv_goal_period;
    ImageView im_goal;
    Button bt_add_tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_detail);

        tv_goal_title = findViewById(R.id.tv_goal_det_title);
        tv_goal_type = findViewById(R.id.tv_goal_det_type);
        tv_goal_period = findViewById(R.id.tv_goal_det_period);

        im_goal = findViewById(R.id.im_goal_det_image);
        bt_add_tasks = findViewById(R.id.bt_goal_det_add_tasks);


        //get data from previous activity
        Intent i_data = getIntent();
        Goal goal_det = i_data.getParcelableExtra("Goals");
        String current_goal_id = i_data.getStringExtra("goal_id");

        tv_goal_title.setText(goal_det.getTitle());
        tv_goal_type.setText(goal_det.getCategory());
        tv_goal_period.setText(goal_det.getStart_date() + " - " + goal_det.getEnd_date());

        Picasso.get().load(goal_det.getUrl_image()).fit().into(im_goal);

        bt_add_tasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GoalDetailActivity.this, AddingTasksActivity.class);
                intent.putExtra("goal_id", current_goal_id);
                intent.putExtra("goal_name", goal_det.getTitle());
                intent.putExtra("goal_type", goal_det.getCategory());
                startActivity(intent);

            }
        });

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