package com.app.shoppingly.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.app.shoppingly.models.ItemModel;
import com.app.shoppingly.models.OrderModel;
import com.app.shoppingly.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentDetails extends BaseActivity {

    TextView txtId, txtStatus;
    DatabaseReference dbRef;
    String userId="";
    ItemModel itemModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        dbRef = FirebaseDatabase.getInstance().getReference("Orders");
        txtId = findViewById(R.id.txtId);
        txtStatus = findViewById(R.id.txtStatus);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        itemModel = ItemDetailsActivity.model;

        Intent intent = getIntent();
        try {
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("details"));
            showDetails(jsonObject.getJSONObject("response"));
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showDetails(JSONObject response) {
        try {
            txtId.setText(response.getString("id"));
//            txtStatus.setText(response.getString("state"));
            String resp = response.getString("state");
            if(resp.equals("approved")){

                String id = dbRef.push().getKey();
                OrderModel model = new OrderModel(id,itemModel.getId(),itemModel.getTitle(),itemModel.getPrice(),userId,UserActivity.userName,
                        UserActivity.token,ItemDetailsActivity.lati,ItemDetailsActivity.longi,UserActivity.address,
                        ItemDetailsActivity.quantity,"pending");

                dbRef.child(id).setValue(model);
                txtStatus.setText("Your order is placed successfully.");

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), UserActivity.class));
                        finish();
                    }
                },2000);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}