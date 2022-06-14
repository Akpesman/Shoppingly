package com.app.shoppingly.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.shoppingly.R;
import com.app.shoppingly.activities.ForgotPasswordActivity;
import com.app.shoppingly.activities.UserActivity;
import com.app.shoppingly.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyAccountFragment extends Fragment {
    private ProgressDialog mProgressDialog;
    Context context;
    View view;
    EditText edtName, edtCardNumber, edtAddress, edtEmail;
    ImageView imgEdit;
    Button btnUpdate,  btnUpdatePass;
    String userId, userName, userCard, userAddress, email, password, token;
    DatabaseReference databaseReference;
    int count = 0;

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_account, container, false);

        context = container.getContext();

        databaseReference = FirebaseDatabase.getInstance().getReference("UsersData");

        imgEdit = view.findViewById(R.id.imgEdit);
        edtName = view.findViewById(R.id.edtName);
        edtCardNumber = view.findViewById(R.id.edtCardNumber);
        edtAddress = view.findViewById(R.id.edtAddress);
        edtEmail = view.findViewById(R.id.edtEmail);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnUpdatePass = view.findViewById(R.id.btnUpdatePass);

        userId  = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userName = UserActivity.userName;
        userCard = UserActivity.cardNumber;
        userAddress = UserActivity.address;
        email = UserActivity.email;
        password = UserActivity.password;
        token = UserActivity.token;

        edtName.setText(userName);
        edtCardNumber.setText(userCard);
        edtAddress.setText(userAddress);
        edtEmail.setText(email);
        edtName.setEnabled(false);
        edtCardNumber.setEnabled(false);
        edtAddress.setEnabled(false);
        edtEmail.setEnabled(false);

        //Edit sign clicks code
        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                if(count%2 != 0) {
                    edtName.setEnabled(true);
                    edtName.requestFocus();
                    edtCardNumber.setEnabled(true);
                    edtAddress.setEnabled(true);
                }else {
                    edtName.setEnabled(false);
                    edtCardNumber.setEnabled(false);
                    edtAddress.setEnabled(false);
                }
            }
        });


        //Update button click code
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = edtName.getText().toString().trim();
                userCard = edtCardNumber.getText().toString().trim();
                userAddress = edtAddress.getText().toString().trim();

                //Validations to all data fileds..
                if(TextUtils.isEmpty(userName)){
                    edtName.setError("Required!");
                    edtName.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(userCard)){
                    edtCardNumber.setError("Required!");
                    edtCardNumber.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(userAddress)){
                    edtAddress.setError("Required!");
                    edtAddress.requestFocus();
                    return;
                }

                updateAccount();
            }
        });

        //Send update passowrd email code..
        btnUpdatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ForgotPasswordActivity.class));

            }
        });

        return view;
    }

    private void updateAccount() {
        final UserModel user = new UserModel(userId,email,password,userName,userAddress,userCard,token);
        databaseReference.child(userId).setValue(user);
        edtName.setText(userName);
        edtCardNumber.setText(userCard);
        edtAddress.setText(userAddress);
        edtName.setEnabled(false);
        edtCardNumber.setEnabled(false);
        edtAddress.setEnabled(false);

        Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show();

    }
}
