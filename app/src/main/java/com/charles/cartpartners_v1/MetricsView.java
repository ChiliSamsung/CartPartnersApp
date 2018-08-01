package com.charles.cartpartners_v1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.charles.cookingapp.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MetricsView extends AppCompatActivity{

    private DbHelper database;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.metrics_layout);

        //first get the data fetched.
        updateOffline();

        //setup the seek bar to select # of days ago
        final SeekBar seekbar = findViewById(R.id.timelineDays);
        seekbar.setMax(180);
        seekbar.setProgress(90);
        TextView numDaysView = findViewById(R.id.numDays);
        numDaysView.setText(90 + " " + "Days Ago");

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                TextView numDaysView = findViewById(R.id.numDays);
                numDaysView.setText(i + " " + "Days Ago");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //setup the spinner to select month to view data
        final Spinner spinner = findViewById(R.id.month_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.months_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        //NOTE: can access the value of the spinner via:
        spinner.getSelectedItem();

        //Setup the "Timeline" button
        Button timelineButton = findViewById(R.id.TimelineButton);
        timelineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String month = spinner.getSelectedItem().toString();
                ArrayList<Parcelable> items = database.fetchSalesData(seekbar.getProgress(), month);

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                //put the parcelables into the bundle, then give that bundle to a new Fragment
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("ItemsList", items); //the key is the string
                bundle.putString("Month", month);
                Fragment timelineFrag = new TimelineFragment();
                timelineFrag.setArguments(bundle);

                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                fragmentTransaction.replace(R.id.fragView, timelineFrag);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        //Setup the "Graph" button
        Button graphButton = findViewById(R.id.GraphButton);
        graphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String month = spinner.getSelectedItem().toString();
                ArrayList<Parcelable> items = database.fetchSalesData(seekbar.getProgress(), month);

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                //put the parcelables into the bundle
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("ItemsList", items); //the key is the string
                Fragment graphFrag = new GraphFragment();
                graphFrag.setArguments(bundle);

                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                fragmentTransaction.replace(R.id.fragView, graphFrag); //populates to the same part of metrics_layout
                fragmentTransaction.addToBackStack(null);

                fragmentTransaction.commit();
            }
        });

        //Setup the "Pie Chart" button
        Button pieChartButton = findViewById(R.id.PieButton);
        pieChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String month = spinner.getSelectedItem().toString();
                ArrayList<Parcelable> items = database.fetchSalesData(seekbar.getProgress(), month);

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                //put the parcelables into the bundle
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("ItemsList", items); //the key is the string
                Fragment pieFrag = new PieFragment();
                pieFrag.setArguments(bundle);

                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                fragmentTransaction.replace(R.id.fragView, pieFrag); //populates to the same part of metrics_layout
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

    }


    //pull data from offline database (.txt file)
    public void updateOffline() {
        database = new DbHelper(this);
        database.clearDb();
        //reading in the sales data stuff
        BufferedReader br = new BufferedReader( new InputStreamReader(getResources().openRawResource(R.raw.fakedata)));
        try{
            while(true) {
                String itemId = br.readLine();
                String itemName = br.readLine();
                String itemPrice = br.readLine();
                String itemType = br.readLine();
                String itemQuantity = br.readLine();
                String date = br.readLine();
                br.readLine(); //skip an extra line down
                if(itemId == null || itemName == null || itemPrice == null) {
                    break; //EOF
                }

                database.insertSale(itemId, itemName, itemPrice, itemType, itemQuantity, date);
            }
        } catch (IOException e) {
            //do stuff
        }
    }


    /* Setups the navigation bar at the top right corner: */
    //make the actionbar based on the menu_items menu XML
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);
        return true;
    }

    //when options are selected, creates an Intent to send the user to that view
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sales_metrics:
                Intent i_1 = new Intent(this, MetricsView.class);
                startActivity(i_1);
                break;
            case R.id.action_settings:
                Intent i_2 = new Intent(this, SettingsActivity.class);
                startActivity(i_2);
                break;

            case R.id.action_main:
                Intent i_3 = new Intent(this, MainActivity.class);
                startActivity(i_3);
                break;

            default:
                break;
        }
        return true;
    }
}
