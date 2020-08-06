package com.cherish.firebasepractice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.Resource;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
   public FirebaseDatabase mfirebaseDatabase;
    DatabaseReference mdatabaseReference;
    EditText title,description,price;
    TravelDeal deal;
    ImageView imageView;
    private static final int PICTURE_RESULT  = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        FireBaseUtil.openFbReference("traveldeals",MainActivity.this);
        mfirebaseDatabase = FireBaseUtil.mfirebaseDatabase;
        mdatabaseReference = FireBaseUtil.mdatabaseReference;
//        mfirebaseDatabase = FirebaseDatabase.getInstance();
//       mdatabaseReference =  mfirebaseDatabase.getReference().child("traveldeals");
       title = findViewById(R.id.title);
       description = findViewById(R.id.description);
       price = findViewById(R.id.price);
       imageView =findViewById(R.id.image);

        final Intent intent = getIntent();
        TravelDeal deals = (TravelDeal) intent.getSerializableExtra("Deal");
        if (deals==null){
            deals = new TravelDeal();
        }
        this.deal = deals;
        Log.d("DEALDETAILS", String.valueOf(deals));
        title.setText(deals.getTitle());
        description.setText(deals.getDescription());
        price.setText(deals.getPrice());
//        Log.i("IMAGE", deals.getImageUrl());
        showImage(deals.getImageUrl());


        Button btnImage = findViewById(R.id.btnImage);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(Intent.ACTION_GET_CONTENT);
                newIntent.setType("image/jpeg");
                newIntent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(newIntent.createChooser(newIntent,"insert picture"),PICTURE_RESULT);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu,menu);
        if (FireBaseUtil.isAdmin) {
            menu.findItem(R.id.delete).setVisible(true);
            menu.findItem(R.id.save).setVisible(true);
            enableEditText(true);
        } else {
            menu.findItem(R.id.delete).setVisible(false);
            menu.findItem(R.id.save).setVisible(false);
            enableEditText(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case (R.id.save):{
                saveDeal();
                Toast.makeText(this,"Deal Saved",Toast.LENGTH_LONG).show();
                clean();
                backToList();
                return  true;
            }
            case (R.id.delete):{
                deleteDeal();
                Toast.makeText(this,"Deal Deleted",Toast.LENGTH_LONG).show();
                backToList();
                return  true;
            }
            default:{
                return super.onOptionsItemSelected(item);
            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            final StorageReference ref = FireBaseUtil.mStorageRef.child(imageUri.getLastPathSegment());
            ref.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
//                        task.getResult().getStorage().getp
                    }
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            Uri downloadUri = task.getResult();
                            Log.i("URL",downloadUri.toString());
                            String pictureName = task.getResult().getPath();
                            deal.setImageUrl(downloadUri.toString());
                            Log.i("Picture Name",pictureName);
                            deal.setImageName(pictureName);

//                            task.getResult().getPath()

                            showImage(downloadUri.toString());

                        }else {

                        }
                }
            });
//            ref.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    String url = taskSnapshot.getUploadSessionUri().toString();
//                    deal.setImageUrl(url);
//                    Log.i("LOGIMAGE", url);
//                    showImage(url);
//                }
//            });
        }else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }


    }

    public void saveDeal(){
        deal.setTitle(title.getText().toString());
        deal.setDescription(description.getText().toString());
        deal.setPrice(price.getText().toString());
        if (deal.getId() == null) {
            mdatabaseReference.push().setValue(deal);
        }else {
            mdatabaseReference.child(deal.getId()).setValue(deal);
        }
    }

    public void deleteDeal(){
        if (deal == null){
            Toast.makeText(this,"Please save deal before deleting",Toast.LENGTH_LONG).show();
            return;

        }else {
            mdatabaseReference.child(deal.getId()).removeValue();
            if (deal.getImageName() !=null && deal.getImageName().isEmpty() == false){
                StorageReference ref = FireBaseUtil.mStorage.getReference().child(deal.getImageName());
                ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("DELETESUCCESSFULL", "DELETE SUCCESSFUL");

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("DELETE FAILURE", "DELETE FAILED");

                    }
                });
            }
        }
    }

    public void backToList(){
        Intent intent = new Intent(getApplicationContext(),ListActivity.class);
        startActivity(intent);
    }


    public  void clean(){
        title.setText("");
        description.setText("");
        price.setText("");
        title.requestFocus();
    }

    public  void enableEditText(boolean isEnabled){
        title.setEnabled(isEnabled);
        description.setEnabled(isEnabled);
        price.setEnabled(isEnabled);


    }

    private void showImage(String url){
        Log.i("ShowIm","ShowImage 1111");
        if ( url!=null && url.isEmpty()==false){
            Log.i("Show", "SHOW IMAGE 222");
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get()
                    .load(url)
                    .resize(width, width* 2/3)
                    .centerCrop()
                    .into(imageView);

        }

    }
}


