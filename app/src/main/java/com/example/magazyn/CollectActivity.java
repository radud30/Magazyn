package com.example.magazyn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import java.util.Calendar;
import java.util.Date;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class CollectActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    ZXingScannerView ScannerView;

    private DatabaseReference databaseRef, databaseReferenceWorker,databaseRefUsers,databaseRefActivity;
    private Query query, queryWorker, queryUser;
    private String workerFb, creatorUid,workerEmailFb,userEmailFb,productBarcode;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScannerView = new ZXingScannerView(this);
        setContentView(ScannerView);

        databaseReferenceWorker = FirebaseDatabase.getInstance().getReference("Workers");
        databaseRefUsers = FirebaseDatabase.getInstance().getReference("Users");
        databaseRefActivity = FirebaseDatabase.getInstance().getReference("Activity");
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        queryWorker = databaseReferenceWorker.child(currentUser);
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

        queryUser = databaseRefUsers.child(currentUser);
        queryUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    userEmailFb = snapshot.child("email").getValue().toString();
                    //Log.d("MyTag", userEmailFb);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void handleResult(Result result) {
        AlertDialog.Builder alert = new AlertDialog.Builder(CollectActivity.this);
        alert.setTitle("Czy zebrać produkt o kodzie:");
        productBarcode = result.getText();
        alert.setMessage(productBarcode);
        //kliknięcie poza alert dialog
        alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                ScannerView.resumeCameraPreview(CollectActivity.this::handleResult);
            }
        });
        alert.setPositiveButton("TAK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(workerFb != null && workerFb.equals("true")){
                    //Log.d("MyTag", isWorker);
                    //Log.d("MyTag", creator_uid);
                    databaseRef = FirebaseDatabase.getInstance().getReference();
                    String userUidBarcodeWorker = creatorUid + productBarcode;
                    query = databaseRef.child("Products").orderByChild("userUidBarcode").equalTo(userUidBarcodeWorker);
                }
                else{
                    databaseRef = FirebaseDatabase.getInstance().getReference();
                    String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    String userUidBarcode = currentUser + productBarcode;
                    query = databaseRef.child("Products").orderByChild("userUidBarcode").equalTo(userUidBarcode);

                }

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChildren()){
                            for(DataSnapshot barcodeSnapshot: snapshot.getChildren()){
                                //Log.d("MyTag", "" + barcodeSnapshot.child("quantity").getValue());
                                String quantity = barcodeSnapshot.child("quantity").getValue().toString();
                                if(quantity.equals("0")){
                                    Toast.makeText(CollectActivity.this, "Prouktu nie można zebrać ponieważ jego ilość to: 0",Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    int intQuantity = Integer.parseInt(quantity);
                                    intQuantity--;
                                    barcodeSnapshot.getRef().child("quantity").setValue(intQuantity+"");
                                    rejestrActivity();
                                    Toast.makeText(CollectActivity.this, "Zebrano produkt",Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                        else{
                            Toast.makeText(CollectActivity.this, "Nie ma w bazie produktu o takim kodzie",Toast.LENGTH_SHORT).show();
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CollectActivity.this, "Błąd",Toast.LENGTH_SHORT).show();
                    }
                });

                ScannerView.resumeCameraPreview(CollectActivity.this::handleResult);
            }
        });
        alert.setNegativeButton("NIE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ScannerView.resumeCameraPreview(CollectActivity.this::handleResult);
            }
        });
        alert.show();

    }

    private void rejestrActivity(){
        String noteActivity = "zebrał ze stanu produkt o kodzie '" + productBarcode + "' w ilości 1";
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Date currentTime = Calendar.getInstance().getTime();
        if(workerFb != null && workerFb.equals("true")){
            activity = new Activity(noteActivity,creatorUid, currentTime+"", workerEmailFb);
        }else{
            activity = new Activity(noteActivity,currentUser, currentTime+"", userEmailFb);
        }

        String key = databaseRefActivity.push().getKey();
        databaseRefActivity.child(key).setValue(activity);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ScannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ScannerView.setResultHandler(this);
        ScannerView.startCamera();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch(keyCode){
            case KeyEvent.KEYCODE_VOLUME_UP:
                if(action == KeyEvent.ACTION_DOWN){
                    ScannerView.setFlash(true);
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if(action == KeyEvent.ACTION_DOWN){
                    ScannerView.setFlash(false);
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

}