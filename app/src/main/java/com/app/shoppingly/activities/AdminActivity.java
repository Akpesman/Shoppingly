package com.app.shoppingly.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.app.shoppingly.R;

public class AdminActivity extends AppCompatActivity {

    Button btnAddItem, btnViewOrders, btnViewOrdersHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        btnAddItem = findViewById(R.id.btnAddItem);
        btnViewOrders = findViewById(R.id.btnViewOrders);
        btnViewOrdersHistory = findViewById(R.id.btnViewOrdersHistory);

        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddItemActivity.class);
                startActivity(intent);
            }
        });
        btnViewOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewOrdersActivity.class);
                startActivity(intent);
            }
        });
        btnViewOrdersHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewOrdersHistoryActivity.class);
                startActivity(intent);
            }
        });
    }
}