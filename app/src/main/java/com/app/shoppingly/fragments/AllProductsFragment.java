package com.app.shoppingly.fragments;

import android.app.ProgressDialog;
import android.content.Context;
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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shoppingly.R;
import com.app.shoppingly.activities.ItemDetailsActivity;
import com.app.shoppingly.models.ItemModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AllProductsFragment extends Fragment {
    private ProgressDialog mProgressDialog;
    Context context;
    View view;
    RecyclerView recyclerView;
    TextView textView;
    DatabaseReference databaseReference;
    List<ItemModel> list;
    EditText edtSearch;
    PlantsListAdapter adapter;
    public static ItemModel model;

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_all_products, container, false);

        context = container.getContext();

        showProgressDialog();
        databaseReference = FirebaseDatabase.getInstance().getReference("ItemPosts");
        list = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); ;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,2);
        recyclerView.setLayoutManager(gridLayoutManager);

        textView = view.findViewById(R.id.textView);
        edtSearch = view.findViewById(R.id.edtSearch);

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

        return view;
    }


    //Get all the accepted events from firebase in this function and set them up in a list..
    @Override
    public void onStart() {
        super.onStart();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                recyclerView.setAdapter(null);
                textView.setText("");
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ItemModel model = snapshot.getValue(ItemModel.class);
                    list.add(model);
                }
                if(list.size()>0){
                    adapter = new PlantsListAdapter(context, list);
                    recyclerView.setAdapter(adapter);
                }else {
                    textView.setText("No items");
                }
                hideProgressDialog();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideProgressDialog();
                Toast.makeText(context, "Error : "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //This method apply searching in list..
    private void filter(String text) {
        //new array list that will hold the filtered data
        List<ItemModel> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (ItemModel s : list) {
            //if the existing elements contains the search input
            if (s.getTitle().toLowerCase().contains(text.toLowerCase())) {
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
    //Adapter class to set adpater to recyclerview of plants/fertilizers list..
    public static class PlantsListAdapter extends RecyclerView.Adapter<PlantsListAdapter.ImageViewHolder>{
        private Context mcontext ;
        private List<ItemModel> muploadList;

        public PlantsListAdapter(Context context , List<ItemModel> uploadList ) {
            mcontext = context ;
            muploadList = uploadList ;
        }

        @Override
        public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mcontext).inflate(R.layout.item_list_layout, parent , false);
            return (new ImageViewHolder(v));
        }

        @Override
        public void onBindViewHolder(final ImageViewHolder holder, final int position) {

            final ItemModel plantModel = muploadList.get(position);
            holder.tvPlantName.setText(plantModel.getTitle());
            holder.tvPrice.setText("Price : £"+plantModel.getPrice());
            Picasso.with(mcontext).load(plantModel.getPicUrl()).placeholder(R.drawable.holder).into(holder.imgPic);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    model = plantModel;
                    mcontext.startActivity(new Intent(mcontext, ItemDetailsActivity.class));
                }
            });

        }

        @Override
        public int getItemCount() {
            return muploadList.size();
        }

        public static  class ImageViewHolder extends RecyclerView.ViewHolder{
            public ImageView imgPic;
            public TextView tvPlantName;
            public TextView tvPrice;

            public ImageViewHolder(View itemView) {
                super(itemView);

                imgPic = itemView.findViewById(R.id.imgPic);
                tvPlantName = itemView.findViewById(R.id.tvPlantName);
                tvPrice = itemView.findViewById(R.id.tvPrice);

            }
        }

        public void filterList(List<ItemModel> searchList) {
            this.muploadList = searchList;
            notifyDataSetChanged();
        }
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
