package com.cherish.firebasepractice;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FireBaseUtil {
    public   static FirebaseDatabase mfirebaseDatabase;
    public static DatabaseReference mdatabaseReference;
    public static FireBaseUtil mfireBaseUtil;
    public  static FirebaseStorage mStorage;
    public  static StorageReference mStorageRef;
    public  static FirebaseAuth mfirebaseAuth;
    public  static  FirebaseAuth.AuthStateListener mauthStateListener;
    public static ArrayList<TravelDeal> mDeals;
   private static int RC_SIGN_IN = 123;
   private  static  ListActivity caller;
   public static boolean isAdmin;



    private FireBaseUtil(){
        //do not instantiate
    }

    public static void openFbReference(String ref, final ListActivity callerActivity){
        if (mfirebaseDatabase == null){
            mfireBaseUtil = new FireBaseUtil();
            mfirebaseDatabase = FirebaseDatabase.getInstance();
            mfirebaseAuth = FirebaseAuth.getInstance();
            caller = callerActivity;
            mauthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() == null) {
                        FireBaseUtil.Signin();
                    } else {
                        String userId = firebaseAuth.getUid();
                        checkAdmin(userId);
                    }
                    Toast.makeText(callerActivity.getBaseContext(), "Welcome Back", Toast.LENGTH_LONG).show();
                }
            };

            connectStorage();

        }
        mDeals = new ArrayList<>();

        mdatabaseReference = mfirebaseDatabase.getReference().child(ref);
    }


    private  static void  checkAdmin(String uid){
        isAdmin = false;
        DatabaseReference ref = mfirebaseDatabase.getReference().child("administrators")
                .child(uid);
        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                isAdmin = true;
                caller.showMenu();
                Log.i("ADMIN","you are an administrator");
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

        ref.addChildEventListener(listener);
    }


    private static void Signin(){
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
//                            new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());
//                            new AuthUI.IdpConfig.FacebookBuilder().build(),
//                            new AuthUI.IdpConfig.TwitterBuilder().build());


// Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);

    }

    public static  void attachListener(){
        mfirebaseAuth.addAuthStateListener(mauthStateListener);
    }

    public  static  void  detachListener(){
        mfirebaseAuth.removeAuthStateListener(mauthStateListener);
    }

    public static void connectStorage(){
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference().child("deals_pictures");
    }

}
