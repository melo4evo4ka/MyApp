package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class StartActivity extends AppCompatActivity {
    private TextView userName;
    private CircleImageView circleImageView;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private ImageRecyclerAdapter imageRecyclerAdapter;
    private DatabaseReference databaseReference;
    private List<ImageList> imagesList;
    private static final int IMAGE_REQUEST = 1;
    private StorageTask storageTask;
    private Uri imageUri;
    private StorageReference storageReference;
    private UsersData usersData;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imagesList = new ArrayList<>();
        userName = findViewById(R.id.username);
        circleImageView = findViewById(R.id.profileImage);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        storageReference = FirebaseStorage.getInstance().getReference("profile_images");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersData = dataSnapshot.getValue(UsersData.class);
                assert usersData!=null;
                userName.setText(usersData.getUsername());
                if(usersData.getImageURL().equals("default")){
                    circleImageView.setImageResource(R.drawable.ic_launcher_background);
                } else {
                    Glide.with(getApplicationContext()).load(usersData.getImageURL()).into(circleImageView);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(StartActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    circleImageView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
            builder.setCancelable(true);
            View mView = LayoutInflater.from(StartActivity.this).inflate(R.layout.select_image_layout,null);
            RecyclerView recyclerView = mView.findViewById(R.id.recycleView);
            collectOldImages();
            recyclerView.setLayoutManager(new GridLayoutManager(StartActivity.this,3));
            recyclerView.setHasFixedSize(true);

            imageRecyclerAdapter = new ImageRecyclerAdapter(imagesList,StartActivity.this);
            recyclerView.setAdapter(imageRecyclerAdapter);
            imageRecyclerAdapter.notifyDataSetChanged();

            final Button openImage = mView.findViewById(R.id.openImages);
            openImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openImg();
                }
            });
            builder.setView(mView);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    });

    }

    private void openImg() {
        Intent intent = new Intent();
        intent.setType("image/'");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();

            if (storageTask != null && storageTask.isInProgress()) {
                Toast.makeText(this, "Ulpoading is in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImg();
            }
        }
    }

    private void uploadImg() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading Image");
        progressDialog.show();
        if (imageUri != null){
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            assert bitmap != null;
            bitmap.compress(Bitmap.CompressFormat.JPEG,25,byteArrayOutputStream);
            byte[] imageFileToByte = byteArrayOutputStream.toByteArray();
            final StorageReference imageReference = storageReference.child(usersData.getUsername()+System.currentTimeMillis()+".jpg");
            storageTask = imageReference.putBytes(imageFileToByte);
            storageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return imageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String sDownloadUri = downloadUri.toString();
                        Map<String,Object> hashMap = new HashMap<>();
                        hashMap.put("imageUrl",sDownloadUri);
                        databaseReference.updateChildren(hashMap);
                        final DatabaseReference profileImagesReference = FirebaseDatabase.getInstance().getReference("profile_images").child(firebaseUser.getUid());
                        profileImagesReference.push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    progressDialog.dismiss();
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(StartActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(StartActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(StartActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drawer_menu,menu);
    return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (mToggle.onOptionsItemSelected(item)){
            return true;
        }

        if (id == R.id.ddd1){
            startActivity(new Intent(StartActivity.this,ChangePasswordActivity.class));
        } else if (id == R.id.ddd5){
            firebaseAuth.signOut();
            startActivity(new Intent(StartActivity.this,MainActivity.class));
            finish();
        }
        return true;
    }

    private void collectOldImages() {
        DatabaseReference imageListReference = FirebaseDatabase.getInstance().getReference("profile_images").
                child(firebaseUser.getUid());
        imageListReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            imagesList.clear();
            for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                imagesList.add(snapshot.getValue(ImageList.class));
            }
            imageRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            Toast.makeText(StartActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

}
