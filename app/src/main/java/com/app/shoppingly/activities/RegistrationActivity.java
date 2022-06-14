package com.app.shoppingly.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.shoppingly.R;
import com.app.shoppingly.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    EditText edtName, edtAddress, edtCardNumber;
    Button btnRegister;
    DatabaseReference databaseReference;
    String name, address, cardNumber, token="";
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        databaseReference = FirebaseDatabase.getInstance().getReference("UsersData");
        mAuth = FirebaseAuth.getInstance();

        SharedPreferences e = getSharedPreferences("token",MODE_PRIVATE);
        token = e.getString("id","null");

        edtName = findViewById(R.id.edtName);
        edtAddress = findViewById(R.id.edtAddress);
        edtCardNumber = findViewById(R.id.edtCardNumber);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = edtName.getText().toString().trim();
                address = edtAddress.getText().toString().trim();
                cardNumber = edtCardNumber.getText().toString().trim();

                if(TextUtils.isEmpty(name)){
                   edtName.setError("Required!");
                   edtName.requestFocus();
                   return;
                }
                if(TextUtils.isEmpty(address)){
                    edtAddress.setError("Required!");
                    edtAddress.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(cardNumber)){
                    edtCardNumber.setError("Required!");
                    edtCardNumber.requestFocus();
                    return;
                }

                createAccount();
            }
        });
    }

    private void createAccount(){

        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        String userId = firebaseUser.getUid();
        UserModel user = new UserModel(userId, AuthenticationActivity.email,AuthenticationActivity.password,name,address,cardNumber,token);
        databaseReference.child(userId).setValue(user);

        Toast.makeText(this, "Your details saved", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), UserActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        finish();
    }
}
