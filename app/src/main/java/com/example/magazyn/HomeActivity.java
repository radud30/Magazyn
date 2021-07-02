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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int CAMERA_REQUEST_CODE = 10;
    private long mLastClickTime = 0;
    private Button buttonLogout, buttonAdd, buttonStockStatus, buttonCollect, buttonWorker, buttonActivity, buttonLocation;
    private String workerExtra, permissionAddExtra, permissionStockStatusExtra, permissionCollectExtra, permissionLocationExtra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        workerExtra = getIntent().getStringExtra("EXTRA_WORKER_FB");
        permissionAddExtra = getIntent().getStringExtra("EXTRA_PERMISSION_ADD_FB");
        permissionStockStatusExtra = getIntent().getStringExtra("EXTRA_PERMISSION_STOCK_FB");
        permissionCollectExtra = getIntent().getStringExtra("EXTRA_PERMISSION_COLLECT_FB");
        permissionLocationExtra = getIntent().getStringExtra("EXTRA_PERMISSION_LOCATION_FB");
        Log.d("MyTag", permissionLocationExtra +"");

        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(this);

        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(this);
        if(permissionAddExtra != null && permissionAddExtra.equals("false")){
            buttonAdd.setEnabled(false);
        }

        buttonStockStatus = (Button) findViewById(R.id.buttonStockStatus);
        buttonStockStatus.setOnClickListener(this);
        if(permissionStockStatusExtra != null && permissionStockStatusExtra.equals("false")){
            buttonStockStatus.setEnabled(false);
        }

        buttonCollect = (Button) findViewById(R.id.buttonCollect);
        buttonCollect.setOnClickListener(this);
        if(permissionCollectExtra != null && permissionCollectExtra.equals("false")){
            buttonCollect.setEnabled(false);
        }

        buttonLocation = (Button) findViewById(R.id.buttonLocation);
        buttonLocation.setOnClickListener(this);
        if(permissionLocationExtra != null && permissionLocationExtra.equals("false")){
            buttonLocation.setEnabled(false);
        }

        buttonWorker = (Button) findViewById(R.id.buttonWorker);
        buttonWorker.setOnClickListener(this);
        if(workerExtra != null && workerExtra.equals("true")){
            buttonWorker.setVisibility(View.GONE);
        }

        buttonActivity = (Button) findViewById(R.id.buttonActivity);
        buttonActivity.setOnClickListener(this);
        if(workerExtra != null && workerExtra.equals("true")){
            buttonActivity.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonAdd:
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();
                startActivity(new Intent(HomeActivity.this, AddProductActivity.class));
                break;
            case R.id.buttonStockStatus:
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();
                if(workerExtra != null && workerExtra.equals("true")){
                    startActivity(new Intent(HomeActivity.this, WorkerStockStatusActivity.class));
                }
                else {
                    startActivity(new Intent(HomeActivity.this, StockStatusActivity.class));
                }
                break;
            case R.id.buttonCollect:
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();
                verifyPermission();
            case R.id.buttonWorker:
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();
                startActivity(new Intent(HomeActivity.this, WorkerActivity.class));
                break;
            case R.id.buttonActivity:
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();
                startActivity(new Intent(HomeActivity.this, ActivityLogActivity.class));
                break;
            case R.id.buttonLocation:
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();
                startActivity(new Intent(HomeActivity.this, LocationActivity.class));
                break;
            case R.id.buttonLogout:
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();
                FirebaseAuth.getInstance().signOut();
                Intent intToMain = new Intent(HomeActivity.this, MainActivity.class);
                // Zabezpieczenie przed kliknięceim wstecz po wylogowaniu
                intToMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intToMain);
                finish();
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