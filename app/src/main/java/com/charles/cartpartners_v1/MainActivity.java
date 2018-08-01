package com.charles.cartpartners_v1;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Parcelable;
import android.preference.PreferenceManager;
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
import android.widget.Toast;

import com.charles.cookingapp.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private int MY_PERMISSIONS_REQUEST = 1;
    private DbHelper dbHelper;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    enum specifications {
        MEXICAN, CHINESE, ITALIAN, AMERICAN, KOREAN, JAPANESE
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        setupRecyclerView();

        //make the spinner
        //spinnerSetup();
    }


    //TODO: need a method to pull in data from online


    public void setupRecyclerView() {
        ArrayList<Item> itemListings = dbHelper.fetchItemListings();
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
                R.array.cuisine_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        //makes the listener for when the user makes selections. Just like with an onClick listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int pos, long id) {
                if (pos == 0) {
                    Toast.makeText(arg1.getContext(), "Any Selected", Toast.LENGTH_SHORT).show();
//                    chooseAllRecipes();

                } else if (pos == 1) {
                    Toast.makeText(arg1.getContext(), "Chinese Selected", Toast.LENGTH_SHORT).show();


                    //NOT SURE IF THeSE 3 LINES IS NECESSARY, maybe delete?
                    recyclerView = findViewById(R.id.recipe_listRecyclerView);
                    layoutManager = new LinearLayoutManager(arg1.getContext());
                    recyclerView.setLayoutManager(layoutManager);

                    //chooseCuisine(specifications.CHINESE);


                } else if (pos == 2) {
                    Toast.makeText(arg1.getContext(), "Italian Selected", Toast.LENGTH_SHORT).show();
                    //chooseCuisine(specifications.ITALIAN);
                } else if (pos == 3) {
                    Toast.makeText(arg1.getContext(), "American Selected", Toast.LENGTH_SHORT).show();
                    //chooseCuisine(specifications.AMERICAN);
                } else if (pos == 4) {
                    Toast.makeText(arg1.getContext(), "Korean Selected", Toast.LENGTH_SHORT).show();
                    //chooseCuisine(specifications.KOREAN);
                } else if (pos == 5) {
                    Toast.makeText(arg1.getContext(), "Japanese Selected", Toast.LENGTH_SHORT).show();
                    //chooseCuisine(specifications.JAPANESE);
                } else if (pos == 6) {
                    Toast.makeText(arg1.getContext(), "Mexican Selected", Toast.LENGTH_SHORT).show();
                    //chooseCuisine(specifications.MEXICAN);
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
