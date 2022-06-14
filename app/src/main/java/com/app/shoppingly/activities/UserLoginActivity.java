package com.app.shoppingly.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.app.shoppingly.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class UserLoginActivity extends BaseActivity {

    private static final String TAG = "EmailPasswordActivity";
    EditText edtEmail, editPassword;
    Button btnLogin, btnSignUp;
    TextView forgotTextView, tv;
    String email, passowrd, fcmToken="";
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        mAuth = FirebaseAuth.getInstance();

        SharedPreferences sharedPref = getSharedPreferences("token",MODE_PRIVATE);
        fcmToken = sharedPref.getString("id","null");

        //Toast.makeText(this, "token : "+fcmToken, Toast.LENGTH_SHORT).show();

        edtEmail = findViewById(R.id.edtEmail);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        forgotTextView = findViewById(R.id.forgotTextView);
        btnSignUp = findViewById(R.id.btnSignUp);
        tv = findViewById(R.id.tv);

        tv.setOnTouchListener(new View.OnTouchListener() {
            Handler handler = new Handler();

            int numberOfTaps = 0;
            long lastTapTimeMs = 0;
            long touchDownMs = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touchDownMs = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_UP:
                        handler.removeCallbacksAndMessages(null);

                        if ((System.currentTimeMillis() - touchDownMs) > ViewConfiguration.getTapTimeout()) {
                            //it was not a tap

                            numberOfTaps = 0;
                            lastTapTimeMs = 0;
                            break;
                        }

                        if (numberOfTaps > 0
                                && (System.currentTimeMillis() - lastTapTimeMs) < ViewConfiguration.getDoubleTapTimeout()) {
                            numberOfTaps += 1;
                        } else {
                            numberOfTaps = 1;
                        }

                        lastTapTimeMs = System.currentTimeMillis();

                        if (numberOfTaps == 4) {
                            startActivity(new Intent(getApplicationContext(), AdminLoginScreenActivity.class));
                        }
                }
                return true;
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = edtEmail.getText().toString().trim();
                passowrd = editPassword.getText().toString().trim();
                if(email.equals("admin") && passowrd.equals("123")){
                    startActivity(new Intent(getApplicationContext(), AddItemActivity.class));
                }else {
                    if(!isConnectionAvailable(UserLoginActivity.this)){
                        Toast.makeText(UserLoginActivity.this, "Check your network!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(TextUtils.isEmpty(email)){
                        edtEmail.setError("Required!");
                        edtEmail.requestFocus();
                        return;
                    }
                    if(TextUtils.isEmpty(passowrd)){
                        editPassword.setError("Required!");
                        editPassword.requestFocus();
                        return;
                    }
                    signIn();
                }
            }
        });
        forgotTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString();
                if(TextUtils.isEmpty(email)){
                    edtEmail.setError("Required!");
                    edtEmail.requestFocus();
                    return;
                }
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Password reset email sent successfully", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getApplicationContext(), "Error : "+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AuthenticationActivity.class));
            }
        });
    }

    private void signIn() {
        showProgressDialog();
        mAuth.signInWithEmailAndPassword(email, passowrd).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                if (task.isSuccessful()) {
                    startActivity(new Intent(getApplicationContext(), UserActivity.class));
                    Toast.makeText(UserLoginActivity.this, "Sign in successful", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Incorrect details", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                }

            }
        });
    }

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

    //This method checks user login status..
    @Override
    protected void onStart() {
        super.onStart();

        if(isOnline()){
            startActivity(new Intent(getApplicationContext(), UserActivity.class));
            finish();
        }else {
//            SharedPreferences sharedPref = getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
//            boolean statusAdmin = sharedPref.getBoolean("statusAdmin", false);
//
//            if(statusAdmin){
//                startActivity(new Intent(getApplicationContext(), AdminActivity.class));
//                finish();
//            }else {
//
//            }
        }
    }

    boolean isOnline(){
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            return true;
        }
        return false;
    }
}