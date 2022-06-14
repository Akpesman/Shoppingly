package com.app.shoppingly.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.shoppingly.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminLoginScreenActivity extends AppCompatActivity {

    Button btnSignIn;
    EditText edtKey;
    DatabaseReference databaseReference;
    String key="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        databaseReference = FirebaseDatabase.getInstance().getReference("AdminKey").child("key");

        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        edtKey = (EditText) findViewById(R.id.edtKey);


        loadCredentials();

        btnSignIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String str = edtKey.getText().toString();

                if(!isConnectionAvailable(AdminLoginScreenActivity.this)){
                    final Snackbar snackbar = Snackbar.make(v, "Check your network!", Snackbar.LENGTH_LONG);
                    snackbar.setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();
                    return;
                }

                if (TextUtils.isEmpty(str)) {
                    edtKey.setError("Enter number");
                    return;
                }
                if(str.equals(key)){
                    startActivity(new Intent(getApplicationContext(), AdminActivity.class));
                    finish();
                }else {
                    edtKey.setError("Incorrect Key");
                }
            }
        });
    }

    private void loadCredentials() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                key = (String) dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }});
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //This function checks mobile data connection or wifi is on or off..
    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }
}