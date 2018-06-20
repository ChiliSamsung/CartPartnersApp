package com.charles.cartpartners_v1;

import android.provider.BaseColumns;


public class RecipeContract {


    private RecipeContract() {
    }

    static class RecipeEntry implements BaseColumns {
        static final String TABLE_NAME = "recipe";
        static final String COLUMN_NAME = "name";
        static final String COLUMN_DOLLARS = "Price";
        static final String COLUMN_PICTURE = "Image";
        static final String COLUMN_DESCRIPTION = "Description";
        static final String COLUMN_CUISINE = "Cuisine";
    }

    static class Recipe {
        String name;
        String dollars;
        String imageName;
        String description;
        String cuisine;

        Recipe(String name, String dollars, String imageName, String description, String cuisine) {
            this.name = name;
            this.dollars = dollars;
            this.imageName = imageName;
            this.description = description;
            this.cuisine = cuisine;
        }
    }




}
