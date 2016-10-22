package com.example.tainc.sateco_1.noimportant;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.tainc.sateco_1.LineChartTime;
import com.example.tainc.sateco_1.R;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

//import com.example.tainc.sateco_1.RealtimeLineChartActivity;

/**
 * Created by project on 10/10/16.
 */

public class MainActivity extends Activity implements AdapterView.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        setTitle("MPAndroidChart Example");

        // initialize the utilities
        Utils.init(this);

        ArrayList<ContentItem> objects = new ArrayList<ContentItem>();

        objects.add(new ContentItem("Line Chart", "A simple demonstration of the linechart."));
        objects.add(new ContentItem("Line Chart (Dual YAxis)",
                "Demonstration of the linechart with dual y-axis."));
        objects.add(new ContentItem("Bar Chart", "A simple demonstration of the bar chart."));
        objects.add(new ContentItem("Horizontal Bar Chart",
                "A simple demonstration of the horizontal bar chart."));
        objects.add(new ContentItem("Combined Chart",
                "Demonstrates how to create a combined chart (bar and line in this case)."));
        objects.add(new ContentItem("Pie Chart", "A simple demonstration of the pie chart."));
        objects.add(new ContentItem("Pie Chart with value lines", "A simple demonstration of the pie chart with polyline notes."));
        objects.add(new ContentItem("Scatter Chart", "A simple demonstration of the scatter chart."));
        objects.add(new ContentItem("Bubble Chart", "A simple demonstration of the bubble chart."));
        objects.add(new ContentItem("Stacked Bar Chart",
                "A simple demonstration of a bar chart with stacked bars."));
        objects.add(new ContentItem("Stacked Bar Chart Negative",
                "A simple demonstration of stacked bars with negative and positive values."));
        objects.add(new ContentItem("Another Bar Chart",
                "Implementation of a BarChart that only shows values at the bottom."));
        objects.add(new ContentItem("Multiple Lines Chart",
                "A line chart with multiple DataSet objects. One color per DataSet."));
        objects.add(new ContentItem("Multiple Bars Chart",
                "A bar chart with multiple DataSet objects. One multiple colors per DataSet."));
        objects.add(new ContentItem(
                "Charts in ViewPager Fragments",
                "Demonstration of charts inside ViewPager Fragments. In this example the focus was on the design and look and feel of the chart."));
        objects.add(new ContentItem(
                "BarChart inside ListView",
                "Demonstrates the usage of a BarChart inside a ListView item."));
        objects.add(new ContentItem(
                "Multiple charts inside ListView",
                "Demonstrates the usage of different chart types inside a ListView."));
        objects.add(new ContentItem(
                "Inverted Line Chart",
                "Demonstrates the feature of inverting the y-axis."));
        objects.add(new ContentItem(
                "Candle Stick Chart",
                "Demonstrates usage of the CandleStickChart."));
        objects.add(new ContentItem(
                "Cubic Line Chart",
                "Demonstrates cubic lines in a LineChart."));
        objects.add(new ContentItem(
                "Radar Chart",
                "Demonstrates the use of a spider-web like (net) chart."));
        objects.add(new ContentItem(
                "Colored Line Chart",
                "Shows a LineChart with different background and line color."));
        objects.add(new ContentItem(
                "Realtime Chart",
                "This chart is fed with new data in realtime. It also restrains the view on the x-axis."));
        objects.add(new ContentItem(
                "Dynamical data adding",
                "This Activity demonstrates dynamical adding of Entries and DataSets (real time graph)."));
        objects.add(new ContentItem(
                "Performance Line Chart",
                "Renders up to 30.000 objects smoothly."));
        objects.add(new ContentItem(
                "Sinus Bar Chart",
                "A Bar Chart plotting the sinus function with 8.000 values."));
        objects.add(new ContentItem(
                "Chart in ScrollView",
                "This demonstrates how to use a chart inside a ScrollView."));
        objects.add(new ContentItem(
                "BarChart positive / negative",
                "This demonstrates how to create a BarChart with positive and negative values in different colors."));

        ContentItem realm = new ContentItem(
                "Realm.io Database",
                "This demonstrates how to use this library with Realm.io mobile database.");
        objects.add(realm);

        ContentItem time = new ContentItem(
                "Time Chart",
                "Simple demonstration of a time-chart. This chart draws one line entry per hour originating from the current time in milliseconds.");
        time.isNew = true;
        objects.add(time);
        objects.add(new ContentItem(
                "Filled LineChart",
                "This demonstrates how to fill an area between two LineDataSets."));
        objects.add(new ContentItem(
                "Half PieChart",
                "This demonstrates how to create a 180 degree PieChart."));

        MyAdapter adapter = new MyAdapter(this, objects);

        ListView lv = (ListView) findViewById(R.id.listView1);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> av, View v, int pos, long arg3) {

        Intent i;

        switch (pos) {
            case 1:
//                i = new Intent(this, LineChartActivity2.class);
//                startActivity(i);
//                break;
//            case 22:
//                i = new Intent(this, RealtimeLineChartActivity.class);
//                startActivity(i);
//                break;
            case 29:
                i = new Intent(this, LineChartTime.class);
                startActivity(i);
                break;

        }

        overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent i = null;

        switch (item.getItemId()) {
            case R.id.viewGithub:
                i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://github.com/PhilJay/MPAndroidChart"));
                startActivity(i);
                break;
            case R.id.report:
                i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "philjay.librarysup@gmail.com", null));
                i.putExtra(Intent.EXTRA_SUBJECT, "MPAndroidChart Issue");
                i.putExtra(Intent.EXTRA_TEXT, "Your error report here...");
                startActivity(Intent.createChooser(i, "Report Problem"));
                break;
            case R.id.blog:
                i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://www.xxmassdeveloper.com"));
                startActivity(i);
                break;
            case R.id.website:
                i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://at.linkedin.com/in/philippjahoda"));
                startActivity(i);
                break;
        }

        return true;
    }
}