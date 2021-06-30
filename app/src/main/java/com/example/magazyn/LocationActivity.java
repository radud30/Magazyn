package com.example.magazyn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LocationActivity extends AppCompatActivity {

    private ImageButton imageButtonDelLocation, imageButtonAddLocation, imageButtonDelPath;
    private AutoCompleteTextView autoCompleteLocation;
    private ArrayList<String> arrayList = new ArrayList<>();
    private Spinner spinner;
    private String currentUser ,selectedItem, workerFb, creatorUid, workerEmailFb, userEmailFb;
    private DatabaseReference databaseRefWorkers, databaseRefDelLocation, databaseRefUsers, databaseRefLoation;
    private Query queryWorker, queryUser, queryLocation, queryDelLocation;
    private Location location;

    private Display display;

    public static Path path = new Path();
    public static Paint paint_brush = new Paint();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        display = (Display) findViewById(R.id.drawing);

        databaseRefWorkers = FirebaseDatabase.getInstance().getReference("Workers");
        databaseRefUsers = FirebaseDatabase.getInstance().getReference("Users");
        databaseRefLoation = FirebaseDatabase.getInstance().getReference("Location");
        databaseRefDelLocation = FirebaseDatabase.getInstance().getReference();

        autoCompleteLocation = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
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

        imageButtonDelPath = (ImageButton) findViewById(R.id.imageButtonDelPath);
        imageButtonDelPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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