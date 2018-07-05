package com.charles.cartpartners_v1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.charles.cookingapp.R;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class MetricsView extends AppCompatActivity{

    private String data;
    private DbHelper database;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.metrics_layout);

        //first get the data fetched.
        Bundle data = new Bundle();
        updateOffline();

        //setup the seekbar
        final SeekBar seekbar = findViewById(R.id.timelineDays);
        seekbar.setMax(180);
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



        //TODO: likely use fragments here. One for the timeline view, one for the graph view

        Button timelineButton = findViewById(R.id.TimelineButton);
        timelineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //test the fetching works?
                List<Item> items = database.fetchSalesData(seekbar.getProgress());

                for (Item i : items) {
                    System.out.println(i.getDate());
                }

                //TODO: probably parcel the objects and then get them later...?




                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                Fragment timelineFrag = new TimelineFragment();


                fragmentTransaction.replace(R.id.fragView, timelineFrag);
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







    //make the actionbar based on the menu_items menu XML
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);
        return true;
    }

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
