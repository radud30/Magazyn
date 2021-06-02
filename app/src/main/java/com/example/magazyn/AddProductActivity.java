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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddProductActivity extends AppCompatActivity implements View.OnClickListener{
    public static final int CAMERA_REQUEST_CODE = 10;
    private long mLastClickTime = 0;

    private EditText editTextProductName, editTextProductQuantity;
    public static EditText editTextProductBarcode;
    private Button buttonAddProduct;
    private ImageButton imageButtonCamera, imageButtonDelLocation, imageButtonAddLocation, imageButtonShowDel;
    private DatabaseReference databaseRefProducts, databaseRefWorkers, databaseRefUsers, databaseRefActivity, databaseRefLoation, databaseRefDelLocation;
    private Query query, queryWorker, queryUser, queryLocation, queryDelLocation;
    private String workerFb, currentUser, creatorUid, userUidBarcode, userUidBarcodeWorker,userUidProdutName, userUidLocation, userEmailFb, workerEmailFb, selectedItem;
    private Products product;
    private Activity activity;
    private Location location;
    private AutoCompleteTextView autoCompleteLocation;
    private ArrayList<String> arrayList = new ArrayList<>();
    private Spinner spinner;
    boolean visable = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        databaseRefProducts = FirebaseDatabase.getInstance().getReference("Products");
        databaseRefWorkers = FirebaseDatabase.getInstance().getReference("Workers");
        databaseRefUsers = FirebaseDatabase.getInstance().getReference("Users");
        databaseRefActivity = FirebaseDatabase.getInstance().getReference("Activity");
        databaseRefLoation = FirebaseDatabase.getInstance().getReference("Location");
        databaseRefDelLocation = FirebaseDatabase.getInstance().getReference();

        editTextProductName = (EditText) findViewById(R.id.editTextTextPersonName_nazwapr);
        editTextProductQuantity = (EditText) findViewById(R.id.editTextNumber2_ilosc);
        editTextProductBarcode = (EditText) findViewById(R.id.editText_kodpr);
        autoCompleteLocation = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView_localization);

        buttonAddProduct = (Button) findViewById(R.id.button_dodajprod);
        buttonAddProduct.setOnClickListener(this);

        imageButtonCamera = (ImageButton) findViewById(R.id.imageButton_camera);
        imageButtonCamera.setOnClickListener(this);

        imageButtonDelLocation = (ImageButton) findViewById(R.id.imageButton_delLocation);
        imageButtonDelLocation.setOnClickListener(this);

        imageButtonAddLocation = (ImageButton) findViewById(R.id.imageButtonAddLocation);
        imageButtonAddLocation.setOnClickListener(this);

        imageButtonShowDel = (ImageButton) findViewById((R.id.imageButtonShow)) ;
        imageButtonShowDel.setOnClickListener(this);

        spinner = (Spinner) findViewById(R.id.spinnerLocation);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                    //Log.d("MyTag", userEmailFb);
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
            case  R.id.imageButtonAddLocation:
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();
                addLocation(0);
                break;
            case R.id.imageButton_delLocation:
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();
                delLocation();
                break;
            case R.id.imageButtonShow:
                visable = !visable;
                spinner.setVisibility(visable ? View.VISIBLE: View.GONE);
                imageButtonDelLocation.setVisibility(visable ? View.VISIBLE: View.GONE);
                break;
        }
    }

    private void addLocation(final int x){
        String textLocation = autoCompleteLocation.getText().toString().trim();
        if(workerFb != null && workerFb.equals("true")){
            queryLocation = databaseRefLoation.orderByChild("userUidLocation").equalTo(creatorUid+textLocation);
        }else{
            queryLocation = databaseRefLoation.orderByChild("userUidLocation").equalTo(currentUser+textLocation);
        }
        queryLocation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() || textLocation.equals("")){
                    if(x == 0){
                        Toast.makeText(AddProductActivity.this, "Taka lokalizacja już istnieje",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if(workerFb != null && workerFb.equals("true")){
                        location = new Location(creatorUid,textLocation,creatorUid+textLocation);
                    }else{
                        location = new Location(currentUser,textLocation,currentUser+textLocation);
                    }

                    String key = databaseRefLoation.push().getKey();
                    databaseRefLoation.child(key).setValue(location);
                    if(x == 0){
                        Toast.makeText(AddProductActivity.this, "Dodano lokalizację",Toast.LENGTH_SHORT).show();
                    }

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
                    Toast.makeText(AddProductActivity.this, "Usunięto lokalizację",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(AddProductActivity.this, "Taka lokalizacja nie istnieje",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addProduct(){
        String name = editTextProductName.getText().toString().trim();
        String quantity = editTextProductQuantity.getText().toString().trim();
        String barcode = editTextProductBarcode.getText().toString().trim();
        String textLocation = autoCompleteLocation.getText().toString().trim();

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
            userUidProdutName = creatorUid + name;
            userUidLocation = creatorUid + textLocation;
            query = databaseRefProducts.orderByChild("userUidBarcode").equalTo(userUidBarcodeWorker);
        }
        else{
            //reference = FirebaseDatabase.getInstance().getReference("Products");
            userUidBarcode = currentUser + barcode;
            userUidProdutName = currentUser + name;
            userUidLocation = currentUser + textLocation;
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
                        rejestrActivity();
                        addLocation(1);
                        clear();
                    }
                }
                else {
                    if(workerFb != null && workerFb.equals("true")){
                        product = new Products(barcode, creatorUid,name,finalQuantity,textLocation, userUidBarcodeWorker,userUidProdutName,userUidLocation);
                        rejestrActivity();
                        addLocation(1);
                        clear();
                    }
                    else{
                        product = new Products(barcode, currentUser,name,finalQuantity,textLocation, userUidBarcode,userUidProdutName,userUidLocation);
                        rejestrActivity();
                        addLocation(1);
                        clear();
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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void clear(){
        editTextProductBarcode.setText("");
        editTextProductName.setText("");
        editTextProductQuantity.setText("1");
        autoCompleteLocation.setText("");
    }

    private void rejestrActivity(){
        String barcode = editTextProductBarcode.getText().toString().trim();
        String quantity = editTextProductQuantity.getText().toString().trim();
        String noteActivity = "dodał na stan produkt o kodzie '" + barcode + "' w ilości " + quantity ;
        Date currentTime = Calendar.getInstance().getTime();
        if(workerFb != null && workerFb.equals("true")){
            activity = new Activity(noteActivity,creatorUid, currentTime+"", workerEmailFb);
        }else{
            activity = new Activity(noteActivity,currentUser, currentTime+"", userEmailFb);
        }

        String key = databaseRefActivity.push().getKey();
        databaseRefActivity.child(key).setValue(activity);
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
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddProductActivity.this, android.R.layout.simple_list_item_1 ,arrayList);
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
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddProductActivity.this, R.layout.support_simple_spinner_dropdown_item ,arrayList);
                spinner.setAdapter(arrayAdapter);
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



