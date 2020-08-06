package com.cherish.firebasepractice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionValues;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
   private ArrayList<TravelDeal>travelDeal;
   private FirebaseDatabase mfirebaseDatabase;
   private DatabaseReference mdatabaseReference;
   private ChildEventListener mchildEventListener;
    RecyclerView recyclerView;
//   private DealAdapter dealAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
//        FireBaseUtil.openFbReference("traveldeals",this);
//         recyclerView = (RecyclerView) findViewById(R.id.tvDeals);
//       final  DealAdapter  dealAdapter = new DealAdapter(travelDeal);
//         recyclerView.setAdapter(dealAdapter);
//        LinearLayoutManager dealLinearLayoutManger = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
//        recyclerView.setLayoutManager(dealLinearLayoutManger);

//
//        mfirebaseDatabase = FireBaseUtil.mfirebaseDatabase;
//        mdatabaseReference = FireBaseUtil.mdatabaseReference;
//        mfirebaseDatabase = FirebaseDatabase.getInstance();
//        mdatabaseReference = mfirebaseDatabase.getReference().child("traveldeals");
//        mchildEventListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                TextView tvDeals = findViewById(R.id.tvDeals);
//                TravelDeal travelDeal = dataSnapshot.getValue(TravelDeal.class);
//                    tvDeals.setText(String.format("%s\n%s", tvDeals.getText(), travelDeal.getTitle()));
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };
//        mdatabaseReference.addChildEventListener(mchildEventListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_activity_menu,menu);

        MenuItem insertMenu = menu.findItem(R.id.insertMenu);

        if (FireBaseUtil.isAdmin){
            insertMenu.setVisible(true);
        }else {
            insertMenu.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.insertMenu :{
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.logoutMenu:{
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.i("LOGOUT","USER LOG-OUT");
                                FireBaseUtil.attachListener();
                            }
                        });
                FireBaseUtil.detachListener();
                return true;

            }
            default:{
                return super.onOptionsItemSelected(item);
            }
        }


    }

    public void showMenu(){
        invalidateOptionsMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        FireBaseUtil.detachListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FireBaseUtil.openFbReference("traveldeals",this);
        recyclerView = (RecyclerView) findViewById(R.id.tvDeals);
        final  DealAdapter  dealAdapter = new DealAdapter(travelDeal);
        recyclerView.setAdapter(dealAdapter);
        LinearLayoutManager dealLinearLayoutManger = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(dealLinearLayoutManger);
        FireBaseUtil.attachListener();
    }
}
