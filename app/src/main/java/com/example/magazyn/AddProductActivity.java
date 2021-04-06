package com.example.magazyn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
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

import java.util.List;

public class AddProductActivity extends AppCompatActivity implements View.OnClickListener{
    public static final int CAMERA_REQUEST_CODE = 10;
    private long mLastClickTime = 0;

    private EditText editTextProductName, editTextProductQuantity;
    public static EditText editTextProductBarcode;
    private Button buttonAddProduct;
    private ImageButton imageButtonCamera;
    private DatabaseReference databaseRefProducts, databaseRefWorkers;
    private Query query, queryWorker;
    private String workerFb, currentUser, creatorUid, userUidBarcode, userUidBarcodeWorker;
    private Products product;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        databaseRefProducts = FirebaseDatabase.getInstance().getReference("Products");
        databaseRefWorkers = FirebaseDatabase.getInstance().getReference("Workers");

        editTextProductName = (EditText) findViewById(R.id.editTextTextPersonName_nazwapr);
        editTextProductQuantity = (EditText) findViewById(R.id.editTextNumber2_ilosc);
        editTextProductBarcode = (EditText) findViewById(R.id.editText_kodpr);


        buttonAddProduct = (Button) findViewById(R.id.button_dodajprod);
        buttonAddProduct.setOnClickListener(this);

        imageButtonCamera = (ImageButton) findViewById(R.id.imageButton_camera);
        imageButtonCamera.setOnClickListener(this);

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        queryWorker = databaseRefWorkers.child(currentUser);
        queryWorker.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //Log.d("MyTag", snapshot.toString());
                    workerFb = snapshot.child("worker").getValue().toString();
                    creatorUid = snapshot.child("creatorUid").getValue().toString();
                    //Log.d("MyTag", worker);
                    //Log.d("MyTag", creatorUid);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_dodajprod:
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();
                addProduct();
                break;
            case R.id.imageButton_camera:
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();
                scanning();
                break;
        }
    }

    private void addProduct(){
        String name = editTextProductName.getText().toString().trim();
        String quantity = editTextProductQuantity.getText().toString().trim();
        String barcode = editTextProductBarcode.getText().toString().trim();

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
        String finalQuantity = String.valueOf(intQuantity);

        if(intQuantity == 0){
            editTextProductQuantity.setError("Musisz podać liczbę większą od 0");
            editTextProductQuantity.requestFocus();
            return;
        }

        if(workerFb != null && workerFb.equals("true")){
            //Log.d("MyTag", worker);
            userUidBarcodeWorker = creatorUid + barcode;
            query = databaseRefProducts.orderByChild("userUidBarcode").equalTo(userUidBarcodeWorker);
        }
        else{
            //reference = FirebaseDatabase.getInstance().getReference("Products");
            userUidBarcode = currentUser + barcode;
            query = databaseRefProducts.orderByChild("userUidBarcode").equalTo(userUidBarcode);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot barcodeSnapshot: snapshot.getChildren()){
                        String quantityFb = barcodeSnapshot.child("quantity").getValue().toString();
                        //Log.d("MyTag", "" + quantity);
                        int intQuantityFb = Integer.parseInt(quantityFb);
                        intQuantityFb = intQuantityFb + intQuantity;
                        barcodeSnapshot.getRef().child("quantity").setValue(intQuantityFb+"");
                        Toast.makeText(AddProductActivity.this, "Dodano "+ intQuantity +" produkt/ów o kodzie: " +"'" + barcode + "'",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    if(workerFb != null && workerFb.equals("true")){
                        product = new Products(barcode, creatorUid,name,finalQuantity, userUidBarcodeWorker);
                    }
                    else{
                        product = new Products(barcode, currentUser,name,finalQuantity, userUidBarcode);
                    }


                    new FirebaseDatabaseHelper().addProduct(product, new FirebaseDatabaseHelper.DataStatus() {
                        @Override
                        public void DataIsLoaded(List<Products> productsList, List<String> keys) {

                        }

                        @Override
                        public void DataIsInserted() {
                            Toast.makeText(AddProductActivity.this,"Nowy produkt dodano",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void DataIsUpdated() {

                        }

                        @Override
                        public void DataIsDeleted() {

                        }
                    });

                }
                editTextProductBarcode.setText("");
                editTextProductName.setText("");
                editTextProductQuantity.setText("1");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void scanning(){
        verifyPermission();
    }

    private void verifyPermission(){
        String[] permissions = {Manifest.permission.CAMERA};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),permissions[0]) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,permissions,CAMERA_REQUEST_CODE);
        }else {
            startActivity(new Intent(this, AddScanningActivity.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startActivity(new Intent(this, AddScanningActivity.class));
            }else{
                Toast.makeText(this,"Skanowanie wymaga dostępu do kamery",Toast.LENGTH_SHORT).show();
            }
        }
    }


}



