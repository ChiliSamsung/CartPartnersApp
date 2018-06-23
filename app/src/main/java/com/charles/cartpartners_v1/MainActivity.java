package com.charles.cartpartners_v1;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import java.util.List;

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
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);

        int permissionCheck2 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST);
        }

        if (permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST);
        }


        //setup sharedpreferences storage

        //PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(this);


        boolean userIsSignedIn = sharedPref.getBoolean("userSignedIn", false);
        System.out.println("SIGNED IN BOOLEAN: " + userIsSignedIn);

        if (userIsSignedIn) {
            //TODO: go fetch the user's accountId so that will inform the pulling the data


        } else {
            //pull up the SignInActivity
            Intent i = new Intent(this, SignInActivity.class);
            startActivity(i);

        }



        Boolean switchPref = sharedPref.getBoolean
                (SettingsView.example_switch_key, false);

        Toast.makeText(this, "Switch is set to: " + switchPref.toString(), Toast.LENGTH_SHORT).show();

        String marketPref = sharedPref.getString("sync_frequency", "-1");
        Toast.makeText(this, marketPref, Toast.LENGTH_SHORT).show();


        //update the whole database
        update();

        //RECYCLERVIEW SETUP
        recyclerView = (RecyclerView) findViewById(R.id.recipe_listRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        chooseAll();


        //make the spinner
        spinnerSetup();
    }

    //update all the data pulled from the database
    public void update() {
        dbHelper = new DbHelper(this);
        dbHelper.clearDb();

        BufferedReader br = new BufferedReader( new InputStreamReader(getResources().openRawResource(R.raw.recipedata)));
        try{
            //right now there are 9 recipes in the data txt file
            for (int i = 0; i < 9; i++) {
                String name = br.readLine();
                String cuisine = br.readLine();
                String dollar = br.readLine();
                String description = br.readLine();
                String imageName = br.readLine();
                br.readLine(); //skip an extra line down
                dbHelper.insertRecipe(name, description, dollar, cuisine, imageName);
            }

        } catch (IOException e) {
            //do stuff
        }
    }

    //setup the spinner
    public void spinnerSetup() {
        //SPINNER SETUP
        Spinner spinner = (Spinner) findViewById(R.id.sort_by_spinner);
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
                    chooseAll();

                } else if (pos == 1) {
                    Toast.makeText(arg1.getContext(), "Chinese Selected", Toast.LENGTH_SHORT).show();


                    //NOT SURE IF THeSE 3 LINES IS NECESSARY, maybe delete?
                    recyclerView = (RecyclerView) findViewById(R.id.recipe_listRecyclerView);
                    layoutManager = new LinearLayoutManager(arg1.getContext());
                    recyclerView.setLayoutManager(layoutManager);

                    chooseCuisine(specifications.CHINESE);


                } else if (pos == 2) {
                    Toast.makeText(arg1.getContext(), "Italian Selected", Toast.LENGTH_SHORT).show();
                    chooseCuisine(specifications.ITALIAN);
                } else if (pos == 3) {
                    Toast.makeText(arg1.getContext(), "American Selected", Toast.LENGTH_SHORT).show();
                    chooseCuisine(specifications.AMERICAN);
                } else if (pos == 4) {
                    Toast.makeText(arg1.getContext(), "Korean Selected", Toast.LENGTH_SHORT).show();
                    chooseCuisine(specifications.KOREAN);
                } else if (pos == 5) {
                    Toast.makeText(arg1.getContext(), "Japanese Selected", Toast.LENGTH_SHORT).show();
                    chooseCuisine(specifications.JAPANESE);
                } else if (pos == 6) {
                    Toast.makeText(arg1.getContext(), "Mexican Selected", Toast.LENGTH_SHORT).show();
                    chooseCuisine(specifications.MEXICAN);
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


    //sets the RecyclerView to display all the cuisines that satisfy the specification cuisine
    public void chooseCuisine(specifications s) {
        List<RecipeContract.Recipe> recipes = dbHelper.getRecipes(s);

        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> dollars = new ArrayList<String>();
        ArrayList<String> cuisines = new ArrayList<String>();
        ArrayList<String> descriptions = new ArrayList<String>();
        ArrayList<String> imageNames = new ArrayList<String>();

        for(RecipeContract.Recipe r : recipes) {
            names.add(r.name);
            dollars.add(r.dollars);
            cuisines.add(r.cuisine);
            descriptions.add(r.description);
            imageNames.add(r.imageName);
        }

        MainRecyclerAdapter recyclerAdapter = new MainRecyclerAdapter(names, dollars, cuisines, descriptions, imageNames);
        recyclerView.setAdapter(recyclerAdapter);
    }

    //sets the RecyclerView to display all the recipes that there are
    public void chooseAll() {
        List<RecipeContract.Recipe> recipes = dbHelper.getAllRecipes();

        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> dollars = new ArrayList<String>();
        ArrayList<String> cuisines = new ArrayList<String>();
        ArrayList<String> descriptions = new ArrayList<String>();
        ArrayList<String> imageNames = new ArrayList<String>();

        for(RecipeContract.Recipe r : recipes) {
            names.add(r.name);
            dollars.add(r.dollars);
            cuisines.add(r.cuisine);
            descriptions.add(r.description);
            imageNames.add(r.imageName);
        }

        MainRecyclerAdapter recyclerAdapter = new MainRecyclerAdapter(names, dollars, cuisines, descriptions, imageNames);
        recyclerView.setAdapter(recyclerAdapter);
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
                Toast.makeText(this, "Item Listings", Toast.LENGTH_SHORT).show();

                //redirect me to metrics activity
                Intent i_1 = new Intent(this, MetricsView.class);
                startActivity(i_1);

                break;
            case R.id.action_settings:
                Toast.makeText(this, "Sales Metrics", Toast.LENGTH_SHORT).show();


                //redirect me to the Scoreboard activity
                Intent i_2 = new Intent(this, SettingsActivity.class);
                startActivity(i_2);

                break;

            // ADDED:clear the scoreboard if this was pressed
            case R.id.action_main:
                Toast.makeText(this, "Settings", Toast.LENGTH_LONG).show();

//                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
//                SharedPreferences.Editor editor = settings.edit();
//
//                editor.clear();
//                editor.apply();

                //redirect me to the Scoreboard activity, thereby showing the changes
                Intent i_3 = new Intent(this, MainActivity.class);
                startActivity(i_3);

                break;

            default:
                break;
        }

        return true;
    }

}
