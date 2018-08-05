package com.charles.cartpartners_v1;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.charles.cookingapp.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private DbHelper dbHelper;
    private RecyclerView recyclerView;

    enum specifications {
        ANY, PRICE, ALPHABETICAL, QUANTITY
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int MY_PERMISSIONS_REQUEST = 1;

        //get all the permissions
        int permissionCheck2 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST);
        }

        //setup notifications channel
        createNotificationChannel();

        //update the whole database
        updateOffline();

        //Recycler View SETUP
        recyclerView = findViewById(R.id.recipe_listRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        setupRecyclerView(specifications.ANY);

        //make the spinner
        spinnerSetup();
    }


    //TODO: need a method to pull in data from online


    public void setupRecyclerView(specifications s) {
        ArrayList<Item> itemListings = dbHelper.fetchItemListings();
        CustomComparator comparator = new CustomComparator(s);
        Collections.sort(itemListings, comparator);
        MainRecyclerAdapter adapter = new MainRecyclerAdapter(itemListings);
        recyclerView.setAdapter(adapter);
    }




    //pull data from offline database (.txt file)
    //this needs to store to a different table though since the data is different here
    public void updateOffline() {
        dbHelper = new DbHelper(this);
        dbHelper.clearDb();

        //reading in the sales data stuff
        BufferedReader br = new BufferedReader( new InputStreamReader(getResources().openRawResource(R.raw.fakelistings)));
        try{

            while(true) {

                String itemId = br.readLine();
                String itemName = br.readLine();
                String itemPrice = br.readLine();
                String itemQuantity = br.readLine();
                String itemType = br.readLine();

                br.readLine(); //skip an extra line down
                if(itemId == null || itemName == null || itemPrice == null) {
                    break; //EOF
                }

                dbHelper.insertItemListing(itemId, itemName, itemPrice, itemType, itemQuantity);
            }

        } catch (IOException e) {
            //do stuff
        }
    }


    //TODO: get the spinner to allow "sorting" of the elements
    //setup the spinner
    public void spinnerSetup() {
        //SPINNER SETUP
        Spinner spinner = findViewById(R.id.sort_by_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_by_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        //makes the listener for when the user makes selections. Just like with an onClick listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int pos, long id) {
                switch(pos) {
                    case 0:
                        setupRecyclerView(specifications.ANY);
                        break;
                    case 1:
                        setupRecyclerView(specifications.ALPHABETICAL);
                        break;
                    case 2:
                        setupRecyclerView(specifications.PRICE);
                        break;
                    case 3:
                        setupRecyclerView(specifications.QUANTITY);
                        break;
                }

                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    //required for API 26 and higher to post notifications
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Test Channel";
            String description = "Required to Post Notifications";
            String CHANNEL_ID = "123";

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    //custom comparator class
    private class CustomComparator implements Comparator<Item> {

        private specifications comparisonMethod;

        public CustomComparator(specifications s) {
            comparisonMethod = s;
        }

        public int compare(Item a, Item b) {
            switch(comparisonMethod) {
                case ANY:
                    return 1;
                case PRICE:
                    //descending order, high price to low price hence b - a
                    return (int) (b.getPrice() * 100 - a.getPrice() * 100);
                case ALPHABETICAL:
                    //ascending order, low string value to high hence a - b
                    return a.getName().compareTo(b.getName());
                case QUANTITY:
                    //descending order, high quantity to low quantity hence b - a
                    return b.getQuantity() - a.getQuantity();
                default:
                    return 1;
            }
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
