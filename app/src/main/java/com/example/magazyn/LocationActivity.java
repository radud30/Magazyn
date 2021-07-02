package com.example.magazyn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class LocationActivity extends AppCompatActivity {
    public static final int WRITE__REQUEST_CODE = 15;

    private ImageButton imageButtonDelLocation, imageButtonAddLocation, imageButtonTrush, imageButtonColor, imageButtonClear, imageButtonSave, imageButtonAdd, imageButtonChange;
    private AutoCompleteTextView autoCompleteLocation;
    private ArrayList<String> arrayList = new ArrayList<>();
    private Spinner spinner;
    private String currentUser ,selectedItem, workerFb, creatorUid, workerEmailFb, userEmailFb, uriSucces ="", mapImage;
    private DatabaseReference databaseRefWorkers, databaseRefDelLocation, databaseRefUsers, databaseRefLoation, databaseRef;
    private Query queryWorker, queryUser, queryLocation, queryDelLocation,queryUserMap;
    private Location location;
    private Uri imageUri;
    private ImageView imageView;
    private boolean enable = true;
    private StorageReference reference;

    private Display display;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        display = (Display) findViewById(R.id.drawing);
        display.setSetDrawingColorWhite();

        databaseRefWorkers = FirebaseDatabase.getInstance().getReference("Workers");
        databaseRefUsers = FirebaseDatabase.getInstance().getReference("Users");
        databaseRefLoation = FirebaseDatabase.getInstance().getReference("Location");
        databaseRefDelLocation = FirebaseDatabase.getInstance().getReference();
        reference = FirebaseStorage.getInstance().getReference();
        databaseRef = FirebaseDatabase.getInstance().getReference();

        imageView = (ImageView) findViewById(R.id.imageViewLocation);
        autoCompleteLocation = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        imageButtonAdd = (ImageButton) findViewById(R.id.imageButtonAdd);
        imageButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPhoto();
                enable = false;
                imageView.setVisibility(View.VISIBLE);
                buttonOff();
            }
        });

        imageButtonChange = (ImageButton) findViewById(R.id.imageButtonChange);
        imageButtonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enable = !enable;
                if(enable){
                    imageView.setVisibility(View.GONE);
                    buttonOn();
                }else {
                    imageView.setVisibility(View.VISIBLE);
                    buttonOff();
                }
            }
        });

        imageButtonDelLocation = (ImageButton) findViewById(R.id.imageButton_delLoc);
        imageButtonDelLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delLocation();
            }
        });
        imageButtonAddLocation = (ImageButton) findViewById(R.id.imageButtonAddLoc);
        imageButtonAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLocation();
            }
        });
        spinner = (Spinner) findViewById(R.id.spinnerLoc);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        imageButtonTrush = (ImageButton) findViewById(R.id.imageButtonTrush);
        imageButtonTrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                display.clear();
            }
        });

        imageButtonColor = (ImageButton) findViewById(R.id.imageButtonColor);
        imageButtonColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                display.setDrawingColorBlack();
            }
        });

        imageButtonClear = (ImageButton) findViewById(R.id.imageButtonClear);
        imageButtonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                display.setSetDrawingColorWhite();
            }
        });

        imageButtonSave = (ImageButton) findViewById(R.id.imageButtonSave);
        imageButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        queryWorker = databaseRefWorkers.child(currentUser);
        queryWorker.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    workerFb = snapshot.child("worker").getValue().toString();
                    creatorUid = snapshot.child("creatorUid").getValue().toString();
                    workerEmailFb = snapshot.child("email").getValue().toString();
                    showDataAutoComplete();
                    showDataSpinner();
                    queryUser = databaseRefUsers.child(creatorUid);
                    queryUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            mapImage = snapshot.child("mapImage").getValue().toString();
                            if(!mapImage.equals("")){
                                Picasso.get().load(mapImage).fit().placeholder(R.drawable.progress_animation).into(imageView);
                                imageView.setVisibility(View.VISIBLE);
                                enable = false;
                                buttonOff();
                            }else{
                                buttonOn();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        queryUser = databaseRefUsers.child(currentUser);
        queryUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    userEmailFb = snapshot.child("email").getValue().toString();
                    showDataAutoComplete();
                    showDataSpinner();
                    mapImage = snapshot.child("mapImage").getValue().toString();
                    if(!mapImage.equals("")){
                        Picasso.get().load(mapImage).fit().placeholder(R.drawable.progress_animation).into(imageView);
                        imageView.setVisibility(View.VISIBLE);
                        enable = false;
                        buttonOff();
                    }
                    else {
                        buttonOn();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void addToStorage(){
        if(imageUri != null){
            StorageReference fileRef = reference.child(System.currentTimeMillis()+"." + getFileExtension(imageUri));
            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if(workerFb != null && workerFb.equals("true")){
                                queryUserMap = databaseRef.child("Users").orderByChild("userUid").equalTo(creatorUid);
                            }else {
                                queryUserMap = databaseRef.child("Users").orderByChild("userUid").equalTo(currentUser);
                            }


                            queryUserMap.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    //Log.d("MyTag",snapshot+"");
                                    if(snapshot.exists()){
                                        for(DataSnapshot mapSnapshot: snapshot.getChildren()){
                                            uriSucces = uri.toString();
                                            mapSnapshot.getRef().child("mapImage").setValue(uriSucces);
                                            Toast.makeText(LocationActivity.this,"Upublikowano zdjęcie",Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });



                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LocationActivity.this,"Niepowodzenie przy dodawaniu zdjęcia",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String getFileExtension(Uri mUri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }

    private void buttonOff(){
        imageButtonTrush.setClickable(false);
        imageButtonColor.setClickable(false);
        imageButtonClear.setClickable(false);
        imageButtonAdd.setClickable(false);
        imageButtonSave.setClickable(false);
    }

    private void buttonOn(){
        imageButtonTrush.setClickable(true);
        imageButtonColor.setClickable(true);
        imageButtonClear.setClickable(true);
        imageButtonAdd.setClickable(true);
        imageButtonSave.setClickable(true);
    }

    private void addPhoto(){
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2 && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            addToStorage();
        }

        if(resultCode == RESULT_OK){
            imageView.setImageURI(imageUri);
        }
    }

    public void checkPermission()
    {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, WRITE__REQUEST_CODE);
        }
        else {
            display.saveImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == WRITE__REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }else{
                Toast.makeText(this,"Zapisanie pliku wymaga dostępu do pamięci",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDataAutoComplete() {
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseRefDelLocation.child("Location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for(DataSnapshot item: snapshot.getChildren()){
                    Location location = item.getValue(Location.class);
                    String userUid = location.getUserUid();
                    if(workerFb != null && workerFb.equals("true")){
                        if(userUid != null && userUid.equals(creatorUid)){
                            arrayList.add(location.getLocation());
                        }
                    }else{
                        if(userUid != null && userUid.equals(currentUser)){
                            arrayList.add(location.getLocation());
                        }
                    }

                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(LocationActivity.this, android.R.layout.simple_list_item_1 ,arrayList);
                autoCompleteLocation.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void showDataSpinner(){
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseRefDelLocation.child("Location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for(DataSnapshot item: snapshot.getChildren()){
                    Location location = item.getValue(Location.class);
                    String userUid = location.getUserUid();
                    if(workerFb != null && workerFb.equals("true")){
                        if(userUid != null && userUid.equals(creatorUid)){
                            arrayList.add(location.getLocation());
                        }
                    }else{
                        if(userUid != null && userUid.equals(currentUser)){
                            arrayList.add(location.getLocation());
                        }
                    }

                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(LocationActivity.this, R.layout.support_simple_spinner_dropdown_item ,arrayList);
                spinner.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addLocation(){
        String textLocation = autoCompleteLocation.getText().toString().trim();
        if(workerFb != null && workerFb.equals("true")){
            queryLocation = databaseRefLoation.orderByChild("userUidLocation").equalTo(creatorUid+textLocation);
        }else{
            queryLocation = databaseRefLoation.orderByChild("userUidLocation").equalTo(currentUser+textLocation);
        }
        queryLocation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(!textLocation.equals("")){
                        Toast.makeText(LocationActivity.this, "Taka lokalizacja już istnieje",Toast.LENGTH_SHORT).show();
                    }
                    else if(textLocation.equals("")){
                        Toast.makeText(LocationActivity.this, "Pole lokalizacja jest puste",Toast.LENGTH_SHORT).show();
                    }

                }else if(!textLocation.equals("")){
                    if(workerFb != null && workerFb.equals("true")){
                        location = new Location(creatorUid,textLocation,creatorUid+textLocation);
                    }else{
                        location = new Location(currentUser,textLocation,currentUser+textLocation);
                    }

                    String key = databaseRefLoation.push().getKey();
                    databaseRefLoation.child(key).setValue(location);
                    Toast.makeText(LocationActivity.this, "Dodano lokalizację",Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(LocationActivity.this, "Pole lokalizacja jest puste",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void delLocation(){
        String textLocation = selectedItem;
        if(workerFb != null && workerFb.equals("true")){
            queryDelLocation = databaseRefDelLocation.child("Location").orderByChild("userUidLocation").equalTo(creatorUid+textLocation);
        }else{
            queryDelLocation = databaseRefDelLocation.child("Location").orderByChild("userUidLocation").equalTo(currentUser+textLocation);
        }
        queryDelLocation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot locationSnapshot: snapshot.getChildren()){
                        locationSnapshot.getRef().removeValue();
                    }
                    Toast.makeText(LocationActivity.this, "Usunięto lokalizację",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(LocationActivity.this, "Taka lokalizacja nie istnieje",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}