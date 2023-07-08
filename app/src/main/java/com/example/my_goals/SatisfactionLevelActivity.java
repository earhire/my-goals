package com.example.my_goals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my_goals.models.Session;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SatisfactionLevelActivity extends AppCompatActivity {
    TextView tv_category1, tv_progress_level1,tv_progress_level2, tv_progress_level3, tv_progress_level4, tv_progress_level5,tv_progress_level6;
    SeekBar seekBar1, seekBar2, seekBar3, seekBar4, seekBar5, seekBar6;
    Button bt_submit;
    DatabaseReference dbref;

     final  ArrayList<String> categories = new ArrayList<String>(Arrays.asList("Family","Education", "Health and body",
             "Mental development","Professional development", "Others"));
     HashMap<String, Integer> satisfaction_levels = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_satisfaction_level);
        tv_category1 = findViewById(R.id.tv_satisfaction_Lv_category1);

        tv_progress_level1 = findViewById(R.id.tv_progress_1);
        tv_progress_level2 = findViewById(R.id.tv_progress_2);
        tv_progress_level3 = findViewById(R.id.tv_progress_3);
        tv_progress_level4 = findViewById(R.id.tv_progress_4);
        tv_progress_level5 = findViewById(R.id.tv_progress_5);
        tv_progress_level6 = findViewById(R.id.tv_progress_6);
        seekBar1 =findViewById(R.id.seekBar_category1);
        seekBar2 =findViewById(R.id.seekBar_category2);
        seekBar3 =findViewById(R.id.seekBar_category3);
        seekBar4 =findViewById(R.id.seekBar_category4);
        seekBar5 =findViewById(R.id.seekBar_category5);
        seekBar6 =findViewById(R.id.seekBar_category6);
        setTitle("Satisfaction level");
        bt_submit = findViewById(R.id.bt_satisfation_level_submit);

        dbref = FirebaseDatabase.getInstance().getReference("Satisfaction_Levels");

        tv_category1.setText(categories.get(0));

        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            Integer progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {

                tv_progress_level1.setText(progressChangedValue.toString() + " /100");
                satisfaction_levels.put(categories.get(0), progressChangedValue);
            }
        });
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            Integer progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {

                tv_progress_level2.setText(progressChangedValue.toString() + " /100");
                satisfaction_levels.put(categories.get(1), progressChangedValue);
            }
        });
        seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            Integer progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {

                tv_progress_level3.setText(progressChangedValue.toString() + " /100");
                satisfaction_levels.put(categories.get(2), progressChangedValue);
            }
        });
        seekBar4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            Integer progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {

                tv_progress_level4.setText(progressChangedValue.toString() + " /100");
                satisfaction_levels.put(categories.get(3), progressChangedValue);
            }
        });
        seekBar5.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            Integer progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {

                tv_progress_level5.setText(progressChangedValue.toString() + " /100");
                satisfaction_levels.put(categories.get(4), progressChangedValue);
            }
        });
        seekBar6.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            Integer progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {

                tv_progress_level6.setText(progressChangedValue.toString() + " /100");
                satisfaction_levels.put(categories.get(5), progressChangedValue);
            }
        });
        //user clicked button Submit
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(Map.Entry<String, Integer> level : satisfaction_levels.entrySet()) {
                    String key = level.getKey();
                    Integer value = level.getValue();
                    dbref.child(key).setValue(value).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(SatisfactionLevelActivity.this, "Satisfaction level were updated", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(SatisfactionLevelActivity.this, DashboardActivity.class));
                        }
                    });
                }
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
            case R.id.menu_progress_analytics:s:
            startActivity(new Intent(this, ProgressAnalytics.class));
                return true;

            case R.id.menu_sign_out: {
                Session.SessionDetails.logout();
                startActivity(new Intent(this, WelcomeActivity.class));
            }
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}