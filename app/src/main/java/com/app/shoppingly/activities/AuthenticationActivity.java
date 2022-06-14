package com.app.shoppingly.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.app.shoppingly.R;
import com.app.shoppingly.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AuthenticationActivity extends BaseActivity {
    EditText edtEmail, edtPassword, edtConfirmPassword;
    Button btnRegister, btnLogin;
    CheckBox checkbox;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public static String email, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mAuth = FirebaseAuth.getInstance();

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.editPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        checkbox = findViewById(R.id.checkbox);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkbox.isChecked()){
                    Toast.makeText(AuthenticationActivity.this, "Please accept privacy policy!", Toast.LENGTH_SHORT).show();
                    return;
                }
                email = edtEmail.getText().toString().trim();
                password = edtPassword.getText().toString().trim();

                if(email.isEmpty()){
                    edtEmail.setError("Required!");
                    edtEmail.requestFocus();
                    return;
                }
                if(password.isEmpty()){
                    edtPassword.setError("Required");
                    edtPassword.requestFocus();
                    return;
                }
                if(!password.equals(edtConfirmPassword.getText().toString().trim())){
                    edtConfirmPassword.setError("Password not matched!");
                    edtConfirmPassword.requestFocus();
                    return;
                }
                createAccount(email, password);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                } else {
                    Toast.makeText(getApplicationContext(), "user sign out", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void createAccount(final String email, final String password) {
        if (!validateForm()) {
            return;
        }
        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    sendVerification();
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
                hideProgressDialog();
            }
        });
    }

    private void sendVerification() {
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    saveData();
                    Toast.makeText(getApplicationContext(), "Verification email sent to " + firebaseUser.getEmail(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext() , VerificationActivity.class);
                    startActivity(intent);
                   // finish();
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void saveData() {
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String userId = firebaseUser.getUid();

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UsersData");
                    UserModel user = new UserModel(userId,email,password,"","","","");
                    databaseReference.child(userId).setValue(user);

                    Toast.makeText(getApplicationContext(), "Account created on email password and verification email sent to " + firebaseUser.getEmail(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext() , VerificationActivity.class);
                    startActivity(intent);

                }
            }
        });
    }

    private boolean validateForm() {
        if (TextUtils.isEmpty(edtEmail.getText().toString())) {
            edtEmail.setError("Required.");
            return false;
        } else if (TextUtils.isEmpty(edtPassword.getText().toString())) {
            edtPassword.setError("Required.");
            return false;
        }else {
            edtEmail.setError(null);
            edtPassword.setError(null);
            return true;
        }
    }

}