package com.cherish.firebasepractice;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DealAdapter extends  RecyclerView.Adapter<DealAdapter.DealViewHolder> {
    private ArrayList<TravelDeal> travelDeal;
    private FirebaseDatabase mfirebaseDatabase;
    private DatabaseReference mdatabaseReference;
    private ChildEventListener mchildEventListener;
    private ImageView imageDeal;

    public  DealAdapter(ArrayList<TravelDeal>deals){
        this.travelDeal = deals;
//        FireBaseUtil.openFbReference("traveldeals");
        mfirebaseDatabase = FireBaseUtil.mfirebaseDatabase;
        mdatabaseReference = FireBaseUtil.mdatabaseReference;
        travelDeal = FireBaseUtil.mDeals;
        mchildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                TextView tvDeals = findViewById(R.id.tvDeals);
                TravelDeal deal = dataSnapshot.getValue(TravelDeal.class);
//                Log.d("DEAL",deal.getTitle());
                deal.setId(dataSnapshot.getKey());
                travelDeal.add(deal);
                notifyItemInserted(travelDeal.size()-1);

//                    tvDeals.setText(String.format("%s\n%s", tvDeals.getText(), travelDeal.getTitle()));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mdatabaseReference.addChildEventListener(mchildEventListener);
    }

    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.deal_item_layout,parent,false);
        return new DealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder holder, int position) {
        TravelDeal tvdeals = travelDeal.get(position);
        holder.bind(tvdeals);

    }

    @Override
    public int getItemCount() {
        return travelDeal.size();
    }

    public class DealViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title,description, price;

        public DealViewHolder(@NonNull View itemView) {
            super(itemView);
           title  = itemView.findViewById(R.id.tvTitle);
           description = itemView.findViewById(R.id.tvDescription);
           price = itemView.findViewById(R.id.tvPrice);
           imageDeal = itemView.findViewById(R.id.tvImage);
           itemView.setOnClickListener(this);
        }

        public void bind(TravelDeal deal){
            title.setText(deal.getTitle());
            description.setText(deal.getDescription());
            price.setText(deal.getPrice());
            showImage(deal.getImageUrl());

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Log.d("ITEM POSITION IS", String.valueOf(position));
            TravelDeal selectedDeal = travelDeal.get(position);
            Intent intent = new Intent(v.getContext(), MainActivity.class);
            intent.putExtra("Deal", selectedDeal);
            v.getContext().startActivity(intent);

        }

        public void showImage(String url ){
            if (url!=null && url.isEmpty() ==false){
                Picasso.get()
                        .load(url)
                        .resize(160,160)
                        .into(imageDeal);
            }

        }
    }
}
