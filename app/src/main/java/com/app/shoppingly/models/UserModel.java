package com.app.shoppingly.models;

public class UserModel {
    private String userId;
    private String email;
    private String password;
    private String userName;
    private String address;
    private String cardNumber;
    private String token;

    public UserModel() { }

    public UserModel(String userId, String email, String password, String userName, String address, String cardNumber,String token) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.address = address;
        this.cardNumber = cardNumber;
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }

    public String getAddress() {
        return address;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getToken() {
        return token;
    }
}
