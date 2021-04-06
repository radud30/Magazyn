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

import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity {
    public static final int CAMERA_REQUEST_CODE = 10;
    private long mLastClickTime = 0;

    private EditText editTextProductName, editTextProductQuantity;
    public static EditText editTextProductBarcode;
    private Button buttonUpdate, buttonDelete;
    private ImageButton imageButtonCamera;

    private String key, barcode, name, quantity;


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

                if(intQuantity == 0){
                    editTextProductQuantity.setError("Musisz podać liczbę większą od 0");
                    editTextProductQuantity.requestFocus();
                    return;
                }

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
        products.setUserUid(currentUser);
        products.setUserUidBarcode(currentUser + editTextProductBarcode.getText().toString().trim());
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