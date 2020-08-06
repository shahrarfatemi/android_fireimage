package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URI;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView imageView;
    private Button chooseButton,saveButton,showButton;
    private EditText imageName;
    private ProgressBar progressBar;
    private Uri imageUri;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private StorageTask storageTask;
    private static final int IMAGE_REQUEST = 1;
    private static final int IMAGE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseReference = FirebaseDatabase.getInstance().getReference("Images");
        storageReference = FirebaseStorage.getInstance().getReference("Images");

        imageView = findViewById(R.id.imageId);
        chooseButton = findViewById(R.id.chooseButtonId);
        saveButton = findViewById(R.id.saveButtonId);
        showButton = findViewById(R.id.showButtonId);
        progressBar = findViewById(R.id.progressbarId);
        imageName = findViewById(R.id.fileNameId);

        chooseButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        showButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        progressBar.setVisibility(View.GONE);
        if(v.getId() == R.id.chooseButtonId){
            if(ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                chooseImage();
            }
            else{
                requestPermission();
            }

        }
        else if(v.getId() == R.id.saveButtonId){
            if(storageTask != null && storageTask.isInProgress()){
                progressBar.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext() , "Uploading!!", Toast.LENGTH_LONG).show();
            }
            else{
                saveImage();
                progressBar.setVisibility(View.GONE);
            }
        }
        else if(v.getId() == R.id.showButtonId){
            Intent intent = new Intent(MainActivity.this, ImageActivity.class);
            startActivity(intent);
        }
    }

    public void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this).
                    setTitle("Permission needed").
                    setMessage("bla bla bla").
                    setPositiveButton("okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                                    IMAGE_PERMISSION);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        }
        else{
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == IMAGE_PERMISSION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(MainActivity.this, "Granted", Toast.LENGTH_LONG).show();
                chooseImage();
            }
            else {
                Toast.makeText(MainActivity.this, "Not granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }

    public void saveImage(){
        final String fileName = imageName.getText().toString().trim();

        if(fileName.isEmpty()){
            imageName.setError("Enter a valid image name");
            imageName.requestFocus();
            return;
        }

        StorageReference reference = storageReference.child(System.currentTimeMillis()+"\\."+getFileExtension(imageUri));

        reference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Toast.makeText(getApplicationContext() , "Uploaded successfully", Toast.LENGTH_LONG).show();

                        Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();

                        while(!task.isSuccessful()){}

                        Uri uri = task.getResult();

                        //for storing a reference of the image to the database
                        ImageUpload imageUpload = new ImageUpload(fileName, uri.toString());

                        String key = databaseReference.push().getKey();
                        databaseReference.child(key).setValue(imageUpload);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        Toast.makeText(getApplicationContext() , "Could not upload because "+ exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

    }

    public String getFileExtension(Uri imageUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
//            Picasso.with(this).load(imageUri).into(imageView);
            Picasso.get().load(imageUri).into(imageView);
        }
    }
}
