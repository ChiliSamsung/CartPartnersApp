package com.charles.cartpartners_v1;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable{
    private String id;
    private String name;
    private String type;
    private double price;
    private int quantity;
    private String date;

    Item(String id, String name, String type, double price, int quantity, String date) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
        this.date = date;
    }


    /* Getters to return data */
    public String getTimelineFormat() {
        String expression = "";
        expression += name + " | Qty:" + quantity + " " + type + " " + price + " | " + date;
        return expression;
    }

    public double getTotalValue() {
        return quantity * price;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getName() {
        return name;
    }


    /* Required methods for Parcelable interface below. Allows instances of this class
    *  to "travel" from MetricsView to PieFragment, TimelineFragment, GraphFragment.*/
    public void writeToParcel(Parcel dest, int flags) {
        //order matters. Maintain the same order reading and writing
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeDouble(price);
        dest.writeInt(quantity);
        dest.writeString(date);
    }

    //constructor used for parcel
    public Item(Parcel parcel){
        //read and set saved values from parcel
        id = parcel.readString();
        name = parcel.readString();
        type = parcel.readString();
        price = parcel.readDouble();
        quantity = parcel.readInt();
        date = parcel.readString();
    }

    //creator - used when un-parceling our parcel (creating the object)
    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>(){

        @Override
        public Item createFromParcel(Parcel parcel) {
            return new Item(parcel);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    //return hashcode of object
    public int describeContents() {
        return hashCode();
    }

}
