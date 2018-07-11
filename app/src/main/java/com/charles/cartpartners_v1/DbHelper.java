package com.charles.cartpartners_v1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.charles.cartpartners_v1.RecipeContract.RecipeEntry;
import com.charles.cartpartners_v1.MainActivity.specifications;

public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "User.db";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ItemContract.ItemEntry.TABLE_NAME + " (" +
                    ItemContract.ItemEntry.COLUMN_ID + " TEXT," +
                    ItemContract.ItemEntry.COLUMN_NAME + " TEXT," +
                    ItemContract.ItemEntry.COLUMN_TYPE + " TEXT," +
                    ItemContract.ItemEntry.COLUMN_PRICE + " TEXT," +
                    ItemContract.ItemEntry.COLUMN_DATE + " TEXT," +
                    ItemContract.ItemEntry.COLUMN_DESCRIPTION + " TEXT," +
                    ItemContract.ItemEntry.COLUMN_QUANTITY + " TEXT" + ")";
    private static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + ItemContract.ItemEntry.TABLE_NAME;


    DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_TABLE);
        onCreate(db);
    }

    //fetches all the Recipes
    List<RecipeContract.Recipe> getAllRecipes() {

        //reads in all the current database first
        SQLiteDatabase db = getReadableDatabase();

        //this decides which columns to fetch. Must be a string[] format
        String[] projection = {
                RecipeEntry.COLUMN_NAME,
                RecipeEntry.COLUMN_DOLLARS,
                RecipeEntry.COLUMN_PICTURE,
                RecipeEntry.COLUMN_DESCRIPTION,
                RecipeEntry.COLUMN_CUISINE
        };

        //argument 1: which table to look at
        //argument 2: what which columns to select
        //argument 3: the where-clause, determines how to filter
        //argument 7: how to sort the elements in the Cursor
        String sortOrder = RecipeEntry.COLUMN_NAME + " DESC";
        Cursor cursor = db.query(
                RecipeEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );


        List<RecipeContract.Recipe> recipes = new ArrayList<>();

        //the Cursor is like an iterator and you can go down all the results of the query one at a time
        //the cursor has a column index so you use that to do some of it
        //also note the COLUMN names are what you use to access the data stored there. So it's the column index and the column name that do it
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(RecipeEntry.COLUMN_NAME));
            String dollars = cursor.getString(cursor.getColumnIndex(RecipeEntry.COLUMN_DOLLARS));
            String imageName = cursor.getString(cursor.getColumnIndex(RecipeEntry.COLUMN_PICTURE));
            String description = cursor.getString(cursor.getColumnIndex(RecipeEntry.COLUMN_DESCRIPTION));
            String cuisine = cursor.getString(cursor.getColumnIndex(RecipeEntry.COLUMN_CUISINE));

            recipes.add(new RecipeContract.Recipe(name, dollars, imageName, description, cuisine));
        }
        cursor.close();
        return recipes;
    }


    //fetches all sales data going "x" days back
    ArrayList<Parcelable> fetchSalesData(int x) {
        //reads in all the current database first
        SQLiteDatabase db = getReadableDatabase();

        //this decides which columns to fetch. Must be a string[] format
        String[] projection = {
                ItemContract.ItemEntry.COLUMN_ID,
                ItemContract.ItemEntry.COLUMN_NAME,
                ItemContract.ItemEntry.COLUMN_TYPE,
                ItemContract.ItemEntry.COLUMN_PRICE,
                ItemContract.ItemEntry.COLUMN_QUANTITY,
                ItemContract.ItemEntry.COLUMN_DATE
        };

        //get the start date of the selection


        SimpleDateFormat mdFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String startDateString = "'" + mdFormat.format(Calendar.getInstance().getTime()) + "'";


        //argument 1: which table to look at
        //argument 2: what which columns to select
        //argument 3: the where-clause, determines how to filter
        //argument 7: how to sort the elements in the Cursor
        String sortOrder = ItemContract.ItemEntry.COLUMN_DATE + " DESC";
        Cursor cursor = db.query(
                ItemContract.ItemEntry.TABLE_NAME,
                projection,
                ItemContract.ItemEntry.COLUMN_DATE + " BETWEEN " + daysBackHelper(x, startDateString) + " AND " + startDateString,
                null,
                null,
                null,
                sortOrder
        );


        ArrayList<Parcelable> sales = new ArrayList<>();

        //the Cursor is like an iterator and you can go down all the results of the query one at a time
        //the cursor has a column index so you use that to do some of it
        //also note the COLUMN names are what you use to access the data stored there. So it's the column index and the column name that do it
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_NAME));
            double price = cursor.getDouble(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_PRICE));
            String type = cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_TYPE));
            int quantity = cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_QUANTITY));
            String date = cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_DATE));

            sales.add(new Item(id, name, type, price, quantity, date));
        }

        cursor.close();

        return sales;
    }

    String daysBackHelper(int x, String date) {
        SimpleDateFormat mdFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(mdFormat.parse(date));
        } catch(java.text.ParseException e) {
            //do nothing
        }
        cal.add(Calendar.DAY_OF_MONTH, -x);

        return "'" + mdFormat.format(cal.getTime()) + "'";
    }



    //fetches particular recipes, given a specification ENUM
    List<RecipeContract.Recipe> getRecipes(specifications spec) {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                RecipeEntry.COLUMN_NAME,
                RecipeEntry.COLUMN_DOLLARS,
                RecipeEntry.COLUMN_PICTURE,
                RecipeEntry.COLUMN_DESCRIPTION,
                RecipeEntry.COLUMN_CUISINE
        };

        String whereClause = "";
        switch(spec) {
            case MEXICAN: whereClause = RecipeEntry.COLUMN_CUISINE + " like " + "'%MEXICAN%'";
                break;
            case CHINESE: whereClause = RecipeEntry.COLUMN_CUISINE + " like " + "'%CHINESE%'";
                break;
            case ITALIAN: whereClause = RecipeEntry.COLUMN_CUISINE + " like " + "'%ITALIAN%'";
                break;
            case AMERICAN: whereClause = RecipeEntry.COLUMN_CUISINE + " like " + "'%AMERICAN%'";
                break;
            case KOREAN: whereClause = RecipeEntry.COLUMN_CUISINE + " like " + "'%KOREAN%'";
                break;
            case JAPANESE: whereClause = RecipeEntry.COLUMN_CUISINE + " like " + "'%JAPANESE%'";
                break;
        }

        String sortOrder = RecipeEntry.COLUMN_NAME + " DESC";
        Cursor cursor = db.query(
                RecipeEntry.TABLE_NAME,
                projection,
                whereClause,
                null,
                null,
                null,
                sortOrder
        );

        List<RecipeContract.Recipe> recipes = new ArrayList<>();
        System.out.println(cursor.getCount() + "CURSOR COUNT");


        while (cursor.moveToNext()) {
            System.out.println(cursor);


            String name = cursor.getString(cursor.getColumnIndex(RecipeEntry.COLUMN_NAME));
            String dollars = cursor.getString(cursor.getColumnIndex(RecipeEntry.COLUMN_DOLLARS));
            String imageName = cursor.getString(cursor.getColumnIndex(RecipeEntry.COLUMN_PICTURE));
            String description = cursor.getString(cursor.getColumnIndex(RecipeEntry.COLUMN_DESCRIPTION));
            String cuisine = cursor.getString(cursor.getColumnIndex(RecipeEntry.COLUMN_CUISINE));

            recipes.add(new RecipeContract.Recipe(name, dollars, imageName, description, cuisine));
        }
        cursor.close();
        return recipes;
    }



    //note you need to use the contentValues method in order to bind the value to the column name, that way order doesn't matter
    public void insertRecipe(String name, String description, String dollars, String cuisine, String imageName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RecipeEntry.COLUMN_NAME, name);
        contentValues.put(RecipeEntry.COLUMN_DESCRIPTION, description);
        contentValues.put(RecipeEntry.COLUMN_DOLLARS, dollars);
        contentValues.put(RecipeEntry.COLUMN_CUISINE, cuisine);
        contentValues.put(RecipeEntry.COLUMN_PICTURE, imageName);

        db.insert(RecipeEntry.TABLE_NAME, null, contentValues);
    }

    public void insertSale(String id, String name, String price, String type, String quantity, String date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ItemContract.ItemEntry.COLUMN_ID, id);
        contentValues.put(ItemContract.ItemEntry.COLUMN_NAME, name);
        contentValues.put(ItemContract.ItemEntry.COLUMN_TYPE, type);

        contentValues.put(ItemContract.ItemEntry.COLUMN_PRICE, Double.parseDouble(price));
        contentValues.put(ItemContract.ItemEntry.COLUMN_DATE, date);
        contentValues.put(ItemContract.ItemEntry.COLUMN_QUANTITY, Integer.parseInt(quantity));
        db.insert(ItemContract.ItemEntry.TABLE_NAME, null, contentValues);
    }


    //see for delete() when the "Where" argument is empty then it deletes all rows since you're now limiting its "search"
    public void clearDb() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(ItemContract.ItemEntry.TABLE_NAME, null, null);
    }



}
