package com.charles.cartpartners_v1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.charles.cookingapp.R;

public class SettingsView extends AppCompatActivity {

    public static final String example_switch_key = "example_switch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        //adds a fragment to the activity so that the fragment appears as its main content
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

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
                Intent i_2 = new Intent(this, SettingsView.class);
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
