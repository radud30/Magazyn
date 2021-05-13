package com.example.magazyn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity {
    public static final int CAMERA_REQUEST_CODE = 10;
    private long mLastClickTime = 0;

    private EditText editTextProductName, editTextProductQuantity;
    public static EditText editTextProductBarcode;
    private Button buttonUpdate, buttonDelete;
    private ImageButton imageButtonCamera;
    private DatabaseReference databaseRefUsers, databaseRefActivity, databaseRefWorkers;
    private Query queryUser, queryWorker;
    private  Activity activity;
    private String key, barcode, name, quantity, currentUser, userEmailFb, workerFb, workerEmailFb, creatorUid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        key = getIntent().getStringExtra("key");
        barcode = getIntent().getStringExtra("barcode");
        name = getIntent().getStringExtra("name");
        quantity = getIntent().getStringExtra("quantity");

        editTextProductName = (EditText) findViewById(R.id.editTextTextPersonName_nazwa);
        editTextProductName.setText(name);
        editTextProductQuantity = (EditText) findViewById(R.id.editTextNumber_ilosc);
        editTextProductQuantity.setText(quantity);
        editTextProductBarcode = (EditText) findViewById(R.id.editTextNumber_kod);
        editTextProductBarcode.setText(barcode);

        buttonUpdate = (Button) findViewById(R.id.button_update);
        buttonDelete = (Button) findViewById(R.id.button_usun);
        imageButtonCamera = (ImageButton) findViewById(R.id.imageButton2_camera);

        databaseRefUsers = FirebaseDatabase.getInstance().getReference("Users");
        databaseRefActivity = FirebaseDatabase.getInstance().getReference("Activity");
        databaseRefWorkers = FirebaseDatabase.getInstance().getReference("Workers");

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        queryUser = databaseRefUsers.child(currentUser);
        queryUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    userEmailFb = snapshot.child("email").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        queryWorker = databaseRefWorkers.child(currentUser);
        queryWorker.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    workerFb = snapshot.child("worker").getValue().toString();
                    creatorUid = snapshot.child("creatorUid").getValue().toString();
                    workerEmailFb = snapshot.child("email").getValue().toString();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();

                String barcode = editTextProductBarcode.getText().toString().trim();
                String quantity = editTextProductQuantity.getText().toString().trim();

                if(barcode.isEmpty()){
                    editTextProductBarcode.setError("Produkt musi posiadać kod");
                    editTextProductBarcode.requestFocus();
                    return;
                }

                if(quantity.isEmpty()){
                    editTextProductQuantity.setError("Musisz podać ilość");
                    editTextProductQuantity.requestFocus();
                    return;
                }

                int intQuantity = Integer.parseInt(quantity);
                //String finalIlosc = String.valueOf(intQuantity);

                if(intQuantity < 0){
                    editTextProductQuantity.setError("Musisz podać liczbę nie mnijeszą niż 0");
                    editTextProductQuantity.requestFocus();
                    return;
                }

                rejestrActivityUpdate();
                updateProduct();

            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();

                AlertDialog.Builder alert = new AlertDialog.Builder(ProductDetailsActivity.this);
                alert.setTitle("Magazyn");
                alert.setMessage("Czy na pewno chcesz usunąć produkt?");
                alert.setPositiveButton("TAK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        rejestrActivityDelete();
                        deleteProduct();

                    }
                });
                alert.setNegativeButton("NIE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert.show();

            }
        });


        imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();

                scanning();
            }
        });
    }


    private void updateProduct(){
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String quantity = editTextProductQuantity.getText().toString().trim();
        int intQuantity = Integer.parseInt(quantity);
        String finalQuantity = String.valueOf(intQuantity);

        Products products = new Products();
        products.setBarcode(editTextProductBarcode.getText().toString().trim());
        products.setProductName(editTextProductName.getText().toString().trim());
        products.setQuantity(finalQuantity);
        if(workerFb != null && workerFb.equals("true")){
            products.setUserUid(creatorUid);
            products.setUserUidBarcode(creatorUid + editTextProductBarcode.getText().toString().trim());
        }
        else {
            products.setUserUid(currentUser);
            products.setUserUidBarcode(currentUser + editTextProductBarcode.getText().toString().trim());
        }


        new FirebaseDatabaseHelper().updateProduct(key, products, new FirebaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<Products> productsList, List<String> keys) {

            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {
                Toast.makeText(ProductDetailsActivity.this, "Pomyśle uaktualniono produkt",Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            @Override
            public void DataIsDeleted() {

            }
        });
    }
    private void deleteProduct(){
        new FirebaseDatabaseHelper().deleteProducts(key, new FirebaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<Products> productsList, List<String> keys) {

            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {
                Toast.makeText(ProductDetailsActivity.this, "Pomyśle usunnięto produkt",Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        });
    }

    private void rejestrActivityDelete(){
        String noteActivity = "usunął produkt o kodzie '" + barcode +"' ";
        Date currentTime = Calendar.getInstance().getTime();
        if(workerFb != null && workerFb.equals("true")){
            activity = new Activity(noteActivity,creatorUid, currentTime+"", workerEmailFb);
        }
        else{
            activity = new Activity(noteActivity,currentUser, currentTime+"", userEmailFb);
        }
        String key = databaseRefActivity.push().getKey();
        databaseRefActivity.child(key).setValue(activity);
    }

    private void rejestrActivityUpdate(){
        String noteActivity = "edytował produkt o kodzie '" + barcode+"' ";
        Date currentTime = Calendar.getInstance().getTime();
        if(workerFb != null && workerFb.equals("true")){
            activity = new Activity(noteActivity,creatorUid, currentTime+"", workerEmailFb);
        }
        else{
            activity = new Activity(noteActivity,currentUser, currentTime+"", userEmailFb);
        }

        String key = databaseRefActivity.push().getKey();
        databaseRefActivity.child(key).setValue(activity);
    }

    private void scanning(){
        verifyPermission();
    }

    private void verifyPermission(){
        String[] permissions = {Manifest.permission.CAMERA};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),permissions[0]) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,permissions,CAMERA_REQUEST_CODE);
        }else {
            startActivity(new Intent(this, EditScanningActivity.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startActivity(new Intent(this, EditScanningActivity.class));
            }else{
                Toast.makeText(this,"Skanowanie wymaga dostępu do kamery",Toast.LENGTH_SHORT).show();
            }
        }
    }

}