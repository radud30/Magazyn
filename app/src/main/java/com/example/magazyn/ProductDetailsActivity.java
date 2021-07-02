package com.example.magazyn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity {
    public static final int CAMERA_REQUEST_CODE = 10;
    private static final int IMAGE_CAPTURE_CODE = 11;
    private long mLastClickTime = 0;

    private EditText editTextProductName, editTextProductQuantity;
    public static EditText editTextProductBarcode;
    private AutoCompleteTextView autoCompleteTextViewLocation;
    private Button buttonUpdate, buttonDelete;
    private ImageButton imageButtonCamera,imageButtonDel, imageButtonPhoto;
    private DatabaseReference databaseRefUsers, databaseRefActivity, databaseRefWorkers,databaseRefLoation, databaseRefProduct;
    private Query queryUser, queryWorker, queryLocation, queryProduct;
    private  Activity activity;
    private Location locat;
    private String key, barcode, name, quantity, location, currentUser, userEmailFb, workerFb, workerEmailFb, creatorUid, imageUrl;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ImageView imageView;
    private StorageReference reference;
    private Uri imageUri;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        key = getIntent().getStringExtra("key");
        barcode = getIntent().getStringExtra("barcode");
        name = getIntent().getStringExtra("name");
        quantity = getIntent().getStringExtra("quantity");
        location = getIntent().getStringExtra("location");

        editTextProductName = (EditText) findViewById(R.id.editTextTextPersonName_nazwa);
        editTextProductName.setText(name);
        editTextProductQuantity = (EditText) findViewById(R.id.editTextNumber_ilosc);
        editTextProductQuantity.setText(quantity);
        editTextProductBarcode = (EditText) findViewById(R.id.editTextNumber_kod);
        editTextProductBarcode.setText(barcode);
        autoCompleteTextViewLocation = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView_editLocation);
        autoCompleteTextViewLocation.setText(location);
        imageView = (ImageView) findViewById(R.id.imageView2);
        progressBar = (ProgressBar) findViewById(R.id.progressBarEdit);

        buttonUpdate = (Button) findViewById(R.id.button_update);
        buttonDelete = (Button) findViewById(R.id.button_usun);
        imageButtonCamera = (ImageButton) findViewById(R.id.imageButton2_camera);
        imageButtonDel = (ImageButton) findViewById(R.id.imageButtonDelPhoto);
        imageButtonPhoto = (ImageButton) findViewById(R.id.imageButtonCam);

        databaseRefUsers = FirebaseDatabase.getInstance().getReference("Users");
        databaseRefActivity = FirebaseDatabase.getInstance().getReference("Activity");
        databaseRefWorkers = FirebaseDatabase.getInstance().getReference("Workers");
        databaseRefLoation = FirebaseDatabase.getInstance().getReference("Location");
        databaseRefProduct = FirebaseDatabase.getInstance().getReference("Products");

        reference = FirebaseStorage.getInstance().getReference();


        queryProduct = databaseRefProduct.child(key);
        queryProduct.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    imageUrl = snapshot.child("imageUrl").getValue().toString();
                    if(!imageUrl.equals("")){
                        Picasso.get().load(imageUrl).fit().placeholder(R.drawable.progress_animation).into(imageView);
                    }
                    //Log.d("MyTag", imageUrl);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        queryUser = databaseRefUsers.child(currentUser);
        queryUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    userEmailFb = snapshot.child("email").getValue().toString();
                    showDataAutoComplete();
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
                    showDataAutoComplete();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        imageButtonPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();

                verifyPermission(1);
            }
        });

        imageButtonDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24);
                imageUrl = "";
                imageUri = null;
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 2);

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
                addLocation();
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

                verifyPermission(0);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2 && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }

        if(resultCode == RESULT_OK){
            imageView.setImageURI(imageUri);
        }
    }

    private String getFileExtension(Uri mUri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }


    private void updateProduct(){
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String quantity = editTextProductQuantity.getText().toString().trim();
        int intQuantity = Integer.parseInt(quantity);
        String finalQuantity = String.valueOf(intQuantity);

        if(imageUri != null){
            StorageReference fileRef = reference.child(System.currentTimeMillis()+"." + getFileExtension(imageUri));
            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageUrl = uri.toString();

                            Products products = new Products();
                            products.setBarcode(editTextProductBarcode.getText().toString().trim());
                            products.setProductName(editTextProductName.getText().toString().trim());
                            products.setQuantity(finalQuantity);
                            products.setLocation(autoCompleteTextViewLocation.getText().toString().trim());
                            products.setImageUrl(imageUrl);

                            if(workerFb != null && workerFb.equals("true")){
                                products.setUserUid(creatorUid);
                                products.setUserUidBarcode(creatorUid + editTextProductBarcode.getText().toString().trim());
                                products.setUserUidProductName(creatorUid + editTextProductName.getText().toString().trim());
                                products.setUserUidLocation(creatorUid + autoCompleteTextViewLocation.getText().toString().trim());
                            }
                            else {
                                products.setUserUid(currentUser);
                                products.setUserUidBarcode(currentUser + editTextProductBarcode.getText().toString().trim());
                                products.setUserUidProductName(currentUser + editTextProductName.getText().toString().trim());
                                products.setUserUidLocation(currentUser + autoCompleteTextViewLocation.getText().toString().trim());
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
                            progressBar.setVisibility(View.GONE);

                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    progressBar.setVisibility(View.VISIBLE);
                    buttonUpdate.setEnabled(false);
                    buttonDelete.setEnabled(false);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProductDetailsActivity.this,"Niepowodzenie przy dodawaniu zdjęcia",Toast.LENGTH_SHORT).show();
                }
            });
        }else{

            Products products = new Products();
            products.setBarcode(editTextProductBarcode.getText().toString().trim());
            products.setProductName(editTextProductName.getText().toString().trim());
            products.setQuantity(finalQuantity);
            products.setLocation(autoCompleteTextViewLocation.getText().toString().trim());
            products.setImageUrl(imageUrl);

            if(workerFb != null && workerFb.equals("true")){
                products.setUserUid(creatorUid);
                products.setUserUidBarcode(creatorUid + editTextProductBarcode.getText().toString().trim());
                products.setUserUidProductName(creatorUid + editTextProductName.getText().toString().trim());
                products.setUserUidLocation(creatorUid + autoCompleteTextViewLocation.getText().toString().trim());
            }
            else {
                products.setUserUid(currentUser);
                products.setUserUidBarcode(currentUser + editTextProductBarcode.getText().toString().trim());
                products.setUserUidProductName(currentUser + editTextProductName.getText().toString().trim());
                products.setUserUidLocation(currentUser + autoCompleteTextViewLocation.getText().toString().trim());
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
        String noteActivity = "usunął ze stanu produkt o kodzie '" + barcode +"' ";
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
        String noteActivity = "edytował produkt o kodzie '" + barcode+"' " + "nazwie '"+ name + "' ilości '" + quantity + "' na kod'" + editTextProductBarcode.getText().toString() +"' nazwa '" +editTextProductName.getText().toString() + "' ilość '" + editTextProductQuantity.getText().toString() + "'";
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

    private void addLocation(){
        String textLocation = autoCompleteTextViewLocation.getText().toString().trim();
        if(workerFb != null && workerFb.equals("true")){
            queryLocation = databaseRefLoation.orderByChild("userUidLocation").equalTo(creatorUid+textLocation);
        }else{
            queryLocation = databaseRefLoation.orderByChild("userUidLocation").equalTo(currentUser+textLocation);
        }
        queryLocation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() || textLocation.equals("")){

                }else{
                    if(workerFb != null && workerFb.equals("true")){
                        locat = new Location(creatorUid,textLocation,creatorUid+textLocation);
                    }else{
                        locat = new Location(currentUser,textLocation,currentUser+textLocation);
                    }

                    String key = databaseRefLoation.push().getKey();
                    databaseRefLoation.child(key).setValue(locat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showDataAutoComplete() {
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseRefLoation.addValueEventListener(new ValueEventListener() {
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
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ProductDetailsActivity.this, android.R.layout.simple_list_item_1 ,arrayList);
                autoCompleteTextViewLocation.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void verifyPermission(int where){
        String[] permissions = {Manifest.permission.CAMERA};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),permissions[0]) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,permissions,CAMERA_REQUEST_CODE);
        }else {
            if(where == 0){
                startActivity(new Intent(this, EditScanningActivity.class));
            }
            if(where == 1){
                try{
                    ContentValues values = new ContentValues();
                    imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
                }catch(Exception e){
                    Toast.makeText(this,"Ta wersja Android wymaga użycia zewnętrznego aparatu",Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startActivity(new Intent(this, EditScanningActivity.class));
            }else{
                Toast.makeText(this,"Skanowanie i robienie zdjęć wymaga dostępu do kamery",Toast.LENGTH_SHORT).show();
            }
        }
    }

}