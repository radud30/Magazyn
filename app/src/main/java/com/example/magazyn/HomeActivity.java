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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int CAMERA_REQUEST_CODE = 10;
    private long mLastClickTime = 0;
    private Button buttonLogout, buttonAdd, buttonStockStatus, buttonCollect, buttonWorker;
    private DatabaseReference mReferenceWorker;
    private Query queryWorker;
    private String workerFb, creatorUid;

//    FirebaseAuth mFirebaseAuth;
//    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        buttonLogout = (Button) findViewById(R.id.button_logout);
        buttonLogout.setOnClickListener(this);

        buttonAdd = (Button) findViewById(R.id.button_dodaj);
        buttonAdd.setOnClickListener(this);

        buttonStockStatus = (Button) findViewById(R.id.button_sprawdz);
        buttonStockStatus.setOnClickListener(this);

        buttonCollect = (Button) findViewById(R.id.button_zbieraj);
        buttonCollect.setOnClickListener(this);

        buttonWorker = (Button) findViewById(R.id.button_pracownik);
        buttonWorker.setOnClickListener(this);

        mReferenceWorker = FirebaseDatabase.getInstance().getReference("Workers");
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        queryWorker = mReferenceWorker.child(currentUser);
        queryWorker.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    workerFb = snapshot.child("worker").getValue().toString();
                    creatorUid = snapshot.child("creatorUid").getValue().toString();
                    //Log.d("MyTag", isWorker);
                    //Log.d("MyTag", creator_uid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_logout:
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();
                FirebaseAuth.getInstance().signOut();
                Intent intToMain = new Intent(HomeActivity.this, MainActivity.class);
                // Zabezpieczenie przed kliknięceim wstecz po wylogowaniu
                intToMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intToMain);
                finish();
                break;
            case R.id.button_dodaj:
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();
                startActivity(new Intent(HomeActivity.this, AddProductActivity.class));
                break;
            case R.id.button_sprawdz:
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();
                if(workerFb != null && workerFb.equals("true")){
                    //Log.d("MyTag", "tu przehcodzimy do stanu praownika");
                }
                else {
                    startActivity(new Intent(HomeActivity.this, StockStatusActivity.class));
                }

                break;
            case R.id.button_zbieraj:
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();
                verifyPermission();
            case R.id.button_pracownik:
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();
                startActivity(new Intent(HomeActivity.this, WorkerActivity.class));
                break;
        }
    }

    private void verifyPermission(){
        String[] permissions = {Manifest.permission.CAMERA};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),permissions[0]) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,permissions,CAMERA_REQUEST_CODE);
        }else {
            startActivity(new Intent(HomeActivity.this, CollectActivity.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startActivity(new Intent(HomeActivity.this, CollectActivity.class));
            }else{
                Toast.makeText(this,"Skanowanie wymaga dostępu do kamery",Toast.LENGTH_SHORT).show();
            }
        }
    }
}