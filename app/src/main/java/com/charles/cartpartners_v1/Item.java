package com.charles.cartpartners_v1;

public class Item {
    private String id;
    private String name;
    private String type;
    private double price;
    private int quantity;
    private String date;

    public Item(String id, String name, String type, double price, int quantity, String date) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
        this.date = date;
    }

    public String getDate() {
        return date;
    }





}
