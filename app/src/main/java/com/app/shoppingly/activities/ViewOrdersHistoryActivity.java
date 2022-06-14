package com.app.shoppingly.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shoppingly.models.OrderModel;
import com.app.shoppingly.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewOrdersHistoryActivity extends BaseActivity {

    RecyclerView recyclerView;
    TextView textView, tv;
    DatabaseReference databaseReference;
    List<OrderModel> list;
    EventsListAdapter adapter;
    public static OrderModel model;
    EditText edtSearch;
    String str ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_orders);

        showProgressDialog();
        databaseReference = FirebaseDatabase.getInstance().getReference("Orders");
        list = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); ;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(gridLayoutManager);
        textView = findViewById(R.id.textView);
        tv = findViewById(R.id.tv);
        edtSearch = findViewById(R.id.edtSearch);

        tv.setText("Orders History");

        Toast.makeText(this, "true", Toast.LENGTH_SHORT).show();

        //Search bar code..
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }});

    }

    @Override
    protected void onStart() {
        super.onStart();

        str = "pending";

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                textView.setText("");
                recyclerView.setAdapter(null);
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    OrderModel model = snapshot.getValue(OrderModel.class);
                    if(!str.equals(model.getFlag())){
                        list.add(model);
                    }
                }
                if(list.size()>0){
                    Collections.reverse(list);
                    adapter = new EventsListAdapter(ViewOrdersHistoryActivity.this, list);
                    recyclerView.setAdapter(adapter);
                }else {
                    textView.setText("No Orders!");
                }
                hideProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideProgressDialog();
                Toast.makeText(getApplicationContext(), "Error : "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //This method apply searching in list..
    private void filter(String text) {
        //new array list that will hold the filtered data
        List<OrderModel> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (OrderModel s : list) {
            //if the existing elements contains the search input
            if (s.getItemName().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s);
            }
        }

        try {
            adapter.filterList(filterdNames);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.ImageViewHolder>{
        private Context mcontext ;
        private List<OrderModel> muploadList;

        public EventsListAdapter(Context context , List<OrderModel> uploadList ) {
            mcontext = context ;
            muploadList = uploadList ;
        }

        @Override
        public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mcontext).inflate(R.layout.orders_list_layout, parent , false);
            return (new ImageViewHolder(v));
        }

        @Override
        public void onBindViewHolder(final ImageViewHolder holder, @SuppressLint("RecyclerView") final int position) {

            final OrderModel order = muploadList.get(position);
            holder.tvCustomerName.setText("Buyer : "+order.getBuyerName());
            holder.tvItemName.setText("Item : "+order.getItemName());
            holder.tvQty.setText("Quantity : "+order.getQuantity());

            float price = Float.parseFloat(order.getItemPrice());
            int qty = Integer.parseInt(order.getQuantity());
            holder.tvTotal.setText("Total : "+price+" * "+qty+" = GBP"+price*qty);

            holder.tvOrderStatus.setVisibility(View.VISIBLE);
            holder.tvOrderStatus.setText("Order status: "+order.getFlag());

            holder.imgLoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    model = order;
                    startActivity(new Intent(getApplicationContext(), ViewUserLocationActivity.class));
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                    builder.setTitle("Confirmation");
                    builder.setMessage("Are you to delete this order record?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseReference.child(order.getId()).removeValue();
                            list.remove(position);
                            notifyDataSetChanged();

                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return muploadList.size();
        }

        public class ImageViewHolder extends RecyclerView.ViewHolder{
            public TextView tvCustomerName;
            public TextView tvItemName;
            public TextView tvQty;
            public TextView tvTotal;
            public TextView tvOrderStatus;
            public ImageView imgLoc;

            public ImageViewHolder(View itemView) {
                super(itemView);

                tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
                tvItemName = itemView.findViewById(R.id.tvItemName);
                tvQty = itemView.findViewById(R.id.tvQty);
                tvTotal = itemView.findViewById(R.id.tvTotal);
                imgLoc = itemView.findViewById(R.id.imgLoc);
                tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            }
        }

        public void filterList(List<OrderModel> searchList) {
            this.muploadList = searchList;
            notifyDataSetChanged();
        }
    }
}