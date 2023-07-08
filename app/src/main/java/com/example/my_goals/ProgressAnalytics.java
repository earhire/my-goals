package com.example.my_goals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.my_goals.models.Session;
import com.example.my_goals.models.Task;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ProgressAnalytics extends AppCompatActivity {
    PieChart pieChart;
    BarChart barChart_satisf_lv, barChart_daily_workload;
    LineChart lineChart;
    PieData pieData;
    PieDataSet pieDataSet;
    TextView tv_data_list;
    ArrayList pieEntries;
    ArrayList<Task> list = new ArrayList<>();
    HashMap<String, Long> category_level = new HashMap<>();
    DatabaseReference dbref_tasks, dbref_satisf_levels;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_analytics);
        setTitle("Progress and balance analytics");
        ArrayList<Task> list_task = new ArrayList<>();
        pieChart = findViewById(R.id.progress_analytics_chart_view);
        barChart_satisf_lv = findViewById(R.id.chart_satisfaction_level);
        barChart_daily_workload = findViewById(R.id.chart_daily_wrokload);
        tv_data_list = findViewById(R.id.textView10);
        //get userID from session
        user_id= Session.SessionDetails.userUI;
        //getting an instance and to access location in the database
        dbref_tasks = FirebaseDatabase.getInstance().getReference("Tasks");
        dbref_satisf_levels = FirebaseDatabase.getInstance().getReference("Satisfaction_Levels");
        dbref_satisf_levels.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dss: snapshot.getChildren()){
                    category_level.put(dss.getKey(), (Long) dss.getValue());
                }
                createBarChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

               dbref_tasks.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                     int position=0;
                    for(DataSnapshot dss:snapshot.getChildren()){
                        Task task = dss.getValue(Task.class);
                        position++;
                        list.add(task);
                    }

                    getEntries();
                    createBarChart_daily_workload();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
    }

    private void getEntries() {
        //grouping data by category of goal
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        for (Task task : list) {
            String key  = task.getGoal_type();
            if(map.containsKey(key)){
                Integer list_task = map.get(key) + task.getNumber_dates();
                map.put(key,list_task);
            }
            else{
                Integer list_task;
                list_task = task.getNumber_dates();
                map.put(key, list_task);
            }
        }
        tv_data_list.setText("Time balance of personal goals");
        pieEntries = new ArrayList<>();

        //create pieEntry for each item in Map
        for(Map.Entry<String, Integer> entry : map.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            pieEntries.add(new PieEntry(value.floatValue(),key));

        }
        createPieChart();
    }
    private void createBarChart_daily_workload(){
        //grouping task by day of week
        HashMap<String, Integer> dayly_workload = new HashMap<String, Integer>();
        for (Task task : list) {
            String key  = task.getDay_of_week();
            if(dayly_workload.containsKey(key)){
                Integer count_tasks = dayly_workload.get(key) +1;
                dayly_workload.put(key,count_tasks);
            }
            else{
                Integer list_task;
                list_task = 1;
                dayly_workload.put(key, list_task);
            }
        }
        Log.i("dayly tasks", dayly_workload.toString());

        barChart_daily_workload.getDescription().setEnabled(false);
        barChart_daily_workload.setDrawGridBackground(false);
        XAxis xAxis = barChart_daily_workload.getXAxis();
        xAxis.setDrawGridLines(false);
        YAxis leftAxis = barChart_daily_workload.getAxisLeft();
        YAxis rightAxis = barChart_daily_workload.getAxisRight();
        rightAxis.setDrawGridLines(false);
        Legend l = barChart_daily_workload.getLegend();
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
        l.setTextSize(10f);
        // set custom labels and colors
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(this, R.color.teal_200));
        colors.add(ContextCompat.getColor(this, R.color.teal_700));
        colors.add(ContextCompat.getColor(this, R.color.blue_block));
        colors.add(ContextCompat.getColor(this, R.color.green_block));
        colors.add(ContextCompat.getColor(this, R.color.green_200));
        colors.add(ContextCompat.getColor(this, R.color.purple_700));
        colors.add(ContextCompat.getColor(this, R.color.navy_blue));

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<LegendEntry> legendEntries = new ArrayList<>();
        pieEntries = new ArrayList<>();

        //create data entries of barChart, creating entries for the legend
        int i = 0;
        for(Map.Entry<String, Integer> entry: dayly_workload.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            i++;
            BarEntry barEntry = new BarEntry(i, value.floatValue(), key);
            LegendEntry l1=new LegendEntry(key,Legend.LegendForm.CIRCLE,10f,2f,null,colors.get(i));
            legendEntries.add(l1);
            entries.add(barEntry);
        }
        l.setCustom(legendEntries);
        BarDataSet barDataSet = new BarDataSet(entries,"");
        barDataSet.setDrawValues(true);
        BarData data = new BarData(barDataSet);
        barChart_daily_workload.setData(data);
        barChart_daily_workload.invalidate();
        barDataSet.setColors(colors);
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

    void createPieChart(){
        //create dataset
        pieDataSet = new PieDataSet(pieEntries, "");
        pieData = new PieData(pieDataSet);
        // add colors for pie slides
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(this, R.color.teal_200));
        colors.add(ContextCompat.getColor(this, R.color.teal_700));
        colors.add(ContextCompat.getColor(this, R.color.blue_block));
        colors.add(ContextCompat.getColor(this, R.color.green_block));
        colors.add(ContextCompat.getColor(this, R.color.green_200));
        colors.add(ContextCompat.getColor(this, R.color.purple_700));
        colors.add(ContextCompat.getColor(this, R.color.navy_blue));
        colors.add(ColorTemplate.getHoloBlue());
        pieDataSet.setColors(colors);

        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(20f);
        pieDataSet.setSliceSpace(5f);
        pieChart.setUsePercentValues(true);
        pieDataSet.setValueFormatter(new PercentFormatter());
        pieDataSet.setDrawValues(true); //remove value inside the pie
        pieChart.getDescription().setEnabled(false);
        pieData.setValueTextSize(14);
        pieChart.setDrawEntryLabels(false);
        pieChart.setDrawCenterText(true);
        pieChart.setCenterText("Family");
        //animation during loading the chart
        pieChart.animateY(1400, Easing.EaseInOutQuad);
        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        //set up a legend for this chart
        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
        l.setTextSize(10f);

        pieChart.setData(pieData);
    }
    void createBarChart(){
        barChart_satisf_lv.getDescription().setEnabled(false);

        // set custom labels and colors
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(this, R.color.teal_200));
        colors.add(ContextCompat.getColor(this, R.color.teal_700));
        colors.add(ContextCompat.getColor(this, R.color.blue_block));
        colors.add(ContextCompat.getColor(this, R.color.green_block));
        colors.add(ContextCompat.getColor(this, R.color.green_200));
        colors.add(ContextCompat.getColor(this, R.color.purple_700));
        colors.add(ContextCompat.getColor(this, R.color.navy_blue));
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<LegendEntry> legendEntries = new ArrayList<>();
        //fit the data into a bar
        if(category_level.size()<0){
            return;
        }
        //create entries for dataset and entries for legend
        int i = 0;
        for(Map.Entry<String, Long> entry: category_level.entrySet()) {
            String key = entry.getKey();
            Long value = entry.getValue();
            i++;
            BarEntry barEntry = new BarEntry(i, value.floatValue(), key);
            LegendEntry l1=new LegendEntry(key,Legend.LegendForm.CIRCLE,10f,2f,null,colors.get(i));
            legendEntries.add(l1);
            entries.add(barEntry);
        }

        Legend l = barChart_satisf_lv.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
        l.setTextSize(14f);
        l.setCustom(legendEntries);

        BarDataSet barDataSet = new BarDataSet(entries,"");
        barDataSet.setDrawValues(true);
        BarData data = new BarData(barDataSet);
        barChart_satisf_lv.setData(data);
        barChart_satisf_lv.invalidate();
        barDataSet.setColors(colors);

    }

    void group_goal_type(){
        Collections.sort(list, new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {

                return o1.getGoal_type().compareTo(o2.getGoal_type());
            }
        });
    }
}