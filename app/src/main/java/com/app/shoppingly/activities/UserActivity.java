package com.app.shoppingly.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.app.shoppingly.fragments.AllProductsFragment;
import com.app.shoppingly.fragments.ContactUsFragment;
import com.app.shoppingly.fragments.MyAccountFragment;
import com.app.shoppingly.fragments.OrderResponseFragment;
import com.app.shoppingly.R;
import com.app.shoppingly.models.UserModel;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView userNumber , UserFullname;
    DatabaseReference databaseReference;
    public static String userId="", userName="", email="", address="", cardNumber="", token="", password="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("UsersData").child(userId);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        View view = navigationView.inflateHeaderView(R.layout.nav_header_user);

        Fragment selectedFragment = new AllProductsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,selectedFragment).commit();

        userNumber = (TextView) view.findViewById(R.id.tvNumber);
        UserFullname = (TextView) view.findViewById(R.id.tvFullname);

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();


            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        displaySelectedScreen(id);

        return true;
    }

    private void displaySelectedScreen(int itemId){
        Fragment fragment = null;

        switch(itemId){
            case R.id.nav_all_products:
                fragment = new AllProductsFragment();
                break;
            case R.id.nav_order_response:
                fragment = new OrderResponseFragment();
                break;
            case R.id.nav_my_account:
                fragment = new MyAccountFragment();
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), UserLoginActivity.class));
                finish();
                break;
            case R.id.nav_contact_us:
                fragment = new ContactUsFragment();
                break;
        }

        if(fragment!=null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    protected void onStart() {
        super.onStart();

        showProgressDialog();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel model = snapshot.getValue(UserModel.class);
                userName = model.getUserName();
                email = model.getEmail();
                address = model.getAddress();
                cardNumber = model.getCardNumber();
                token = model.getToken();
                password = model.getPassword();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){

                }
                hideProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideProgressDialog();
            }
        });
    }
}