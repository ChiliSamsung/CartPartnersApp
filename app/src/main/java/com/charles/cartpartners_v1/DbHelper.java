package com.charles.cartpartners_v1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DbHelper extends SQLiteOpenHelper {

    private static String listingsTableName = "LISTINGS_TABLE";
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

    private static final String SQL_CREATE_LISTINGS_TABLE = "CREATE TABLE " + listingsTableName + " (" +
            ItemContract.ItemEntry.COLUMN_ID + " TEXT," +
            ItemContract.ItemEntry.COLUMN_NAME + " TEXT," +
            ItemContract.ItemEntry.COLUMN_TYPE + " TEXT," +
            ItemContract.ItemEntry.COLUMN_PRICE + " TEXT," +
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
        db.execSQL(SQL_CREATE_LISTINGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_TABLE);
        onCreate(db);
    }

    //TODO: for online mode, only pull data if it's been at least 5 minutes since last pull that way user can't spam the database a ton

    //fetches all sales data going "x" days back
    ArrayList<Parcelable> fetchSalesData(int x, String month) {
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

        //setup the where clause based on month
        String selectStatement = "";
        Calendar now = Calendar.getInstance();
        String currentYear = "" + now.get(Calendar.YEAR);
        switch(month) {
            case "January": selectStatement = ItemContract.ItemEntry.COLUMN_DATE + " BETWEEN " + "'" + currentYear + "-01-01" + "'" + " AND " + "'" + currentYear + "-01-31" + "'";
                break;
            case "February": selectStatement = ItemContract.ItemEntry.COLUMN_DATE + " BETWEEN " + "'" + currentYear + "-02-01" + "'" + " AND " + "'" + currentYear + "-02-31" + "'";
                break;
            case "March": selectStatement = ItemContract.ItemEntry.COLUMN_DATE + " BETWEEN " + "'" + currentYear + "-03-01" + "'" + " AND " + "'" + currentYear + "-03-31" + "'";
                break;
            case "April": selectStatement = ItemContract.ItemEntry.COLUMN_DATE + " BETWEEN " + "'" + currentYear + "-04-01" + "'" + " AND " + "'" + currentYear + "-04-30" + "'";
                break;
            case "May": selectStatement = ItemContract.ItemEntry.COLUMN_DATE + " BETWEEN " + "'" + currentYear + "-05-01" + "'" + " AND " + "'" + currentYear + "-05-31" + "'";
                break;
            case "June": selectStatement = ItemContract.ItemEntry.COLUMN_DATE + " BETWEEN " + "'" + currentYear + "-06-01" + "'" +  " AND " + "'" + currentYear + "-06-30" + "'";
                break;
            case "July": selectStatement = ItemContract.ItemEntry.COLUMN_DATE + " BETWEEN " + "'" + currentYear + "-07-01" + "'" + " AND " + "'" + currentYear + "-07-31" + "'";
                break;
            case "August": selectStatement = ItemContract.ItemEntry.COLUMN_DATE + " BETWEEN " + "'" + currentYear + "-08-01" + "'" + " AND " + "'" + currentYear + "-08-31" + "'";
                break;
            case "September": selectStatement = ItemContract.ItemEntry.COLUMN_DATE + " BETWEEN " + "'" + currentYear + "-09-01" + "'" + " AND " + "'" + currentYear + "-09-30" + "'";
                break;
            case "October": selectStatement = ItemContract.ItemEntry.COLUMN_DATE + " BETWEEN " + "'" + currentYear + "-10-01" + "'" + " AND " + "'" + currentYear + "-10-31" + "'";
                break;
            case "November": selectStatement = ItemContract.ItemEntry.COLUMN_DATE + " BETWEEN " + "'" + currentYear + "-11-01" + "'" + " AND " + "'" + currentYear + "-11-30" + "'";
                break;
            case "December": selectStatement = ItemContract.ItemEntry.COLUMN_DATE + " BETWEEN " + "'" + currentYear + "-12-01" + "'" + " AND " + "'" + currentYear + "-12-31" + "'";
                break;
        }

        //sept and april fail, likely cause there is only one of them

        Cursor cursor = null;
        //if the month is N/A then just do Days Back
        if (month.equals("N/A")) {
            String sortOrder = ItemContract.ItemEntry.COLUMN_DATE + " DESC";
            cursor = db.query(
                    ItemContract.ItemEntry.TABLE_NAME,
                    projection,
                    ItemContract.ItemEntry.COLUMN_DATE + " BETWEEN " + daysBackHelper(x, startDateString) + " AND " + startDateString,
                    null,
                    null,
                    null,
                    sortOrder
            );
        } else {
            String sortOrder = ItemContract.ItemEntry.COLUMN_DATE + " DESC";
            cursor = db.query(
                    ItemContract.ItemEntry.TABLE_NAME,
                    projection,
                    selectStatement,
                    null,
                    null,
                    null,
                    sortOrder
            );
        }

        ArrayList<Parcelable> sales = new ArrayList<>();

        //the Cursor is like an iterator and you can go down all the results of the query one at a time
        //also note the COLUMN names are what you use to access the data stored there. So it's the column index and the column name that do it
        while(cursor.moveToNext()) {
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

    private String daysBackHelper(int x, String date) {
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

    ArrayList<Item> fetchItemListings() {
        SQLiteDatabase db = getReadableDatabase();

        //this decides which columns to select. Must be a string[] format
        String[] projection = {
                ItemContract.ItemEntry.COLUMN_ID,
                ItemContract.ItemEntry.COLUMN_NAME,
                ItemContract.ItemEntry.COLUMN_TYPE,
                ItemContract.ItemEntry.COLUMN_PRICE,
                ItemContract.ItemEntry.COLUMN_QUANTITY,
        };

        /*
            argument 1: which table to look at
            argument 2: what which columns to select
            argument 3: the where-clause, determines how to filter
            argument 7: how to sort the elements in the Cursor
            db.query() returns a Cursor object to iterate down the results of the query
        */
        String sortOrder = ItemContract.ItemEntry.COLUMN_NAME + " DESC";
        Cursor cursor = db.query(
                listingsTableName,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );

        ArrayList<Item> sales = new ArrayList<>();

        //the Cursor is like an iterator and you can go down all the results of the query one at a time
        //also note the COLUMN names are what you use to access the data stored there. So it's the column index and the column name that do it
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_NAME));
            double price = cursor.getDouble(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_PRICE));
            String type = cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_TYPE));
            int quantity = cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_QUANTITY));
//            String date = cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_DATE));
            sales.add(new Item(id, name, type, price, quantity, ""));
        }

        cursor.close();
        return sales;
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

    public void insertItemListing(String id, String name, String price, String type, String quantity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ItemContract.ItemEntry.COLUMN_ID, id);
        contentValues.put(ItemContract.ItemEntry.COLUMN_NAME, name);
        contentValues.put(ItemContract.ItemEntry.COLUMN_TYPE, type);

        contentValues.put(ItemContract.ItemEntry.COLUMN_PRICE, Double.parseDouble(price));
        contentValues.put(ItemContract.ItemEntry.COLUMN_QUANTITY, Integer.parseInt(quantity));
        db.insert(listingsTableName, null, contentValues);
    }

    //see for delete() when the "Where" argument is empty then it deletes all rows since you're now limiting its "search"
    public void clearDb() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(ItemContract.ItemEntry.TABLE_NAME, null, null);
        db.delete(listingsTableName, null, null);
    }

}
