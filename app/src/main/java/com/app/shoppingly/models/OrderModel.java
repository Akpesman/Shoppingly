package com.app.shoppingly.models;

public class OrderModel {
    private String id;
    private String itemId;
    private String itemName;
    private String itemPrice;
    private String buyerId;
    private String buyerName;
    private String buyerToken;
    private double buyerLati;
    private double buyerLongi;
    private String buyerAddress;
    private String quantity;
    private String flag;

    public OrderModel() { }

    public OrderModel(String id, String itemId, String itemName, String itemPrice, String buyerId,
                      String buyerName, String buyerToken, double buyerLati, double buyerLongi,
                      String buyerAddress, String quantity, String flag) {
        this.id = id;
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.buyerId = buyerId;
        this.buyerName = buyerName;
        this.buyerToken = buyerToken;
        this.buyerLati = buyerLati;
        this.buyerLongi = buyerLongi;
        this.buyerAddress = buyerAddress;
        this.quantity = quantity;
        this.flag = flag;
    }

    public String getId() {
        return id;
    }

    public String getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getBuyerToken() {
        return buyerToken;
    }

    public double getBuyerLati() {
        return buyerLati;
    }

    public double getBuyerLongi() {
        return buyerLongi;
    }

    public String getBuyerAddress() {
        return buyerAddress;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getFlag() {
        return flag;
    }
}
