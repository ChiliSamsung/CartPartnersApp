package com.charles.cartpartners_v1;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.charles.cookingapp.R;

public class RecipeActivity extends AppCompatActivity {


    //receives the intent and then represents all the content
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_activity);

        String name = "";
        String description = "";
        String imageName = "";
        if(getIntent().hasExtra("Name")) {
            name = getIntent().getExtras().getString("Name");
        }
        if(getIntent().hasExtra("Description")) {
            description = getIntent().getExtras().getString("Description");
        }
        if(getIntent().hasExtra("imageName")) {
            imageName = getIntent().getExtras().getString("imageName");
        }

        TextView description_tv = (TextView) findViewById(R.id.recipe_text);
        TextView name_tv = (TextView) findViewById(R.id.recipe_cuisine_name);
        ImageView iv = (ImageView) findViewById(R.id.recipe_image);

        description_tv.setText(description);
        name_tv.setText(name);

        Resources resources = this.getResources();
        int resourceID = resources.getIdentifier(imageName, "drawable", this.getPackageName());  //don't want the .png suffix etc. on the imageName
        iv.setImageResource(resourceID);

    }
}
