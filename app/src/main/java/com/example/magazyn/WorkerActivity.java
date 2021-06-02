package com.example.magazyn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class WorkerActivity extends AppCompatActivity implements View.OnClickListener{
    private long mLastClickTime = 0;
    private Button buttonAddWorker, buttonWorkerPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);

        buttonAddWorker = (Button) findViewById(R.id.button_dodajprac);
        buttonAddWorker.setOnClickListener(this);

        buttonWorkerPermission = (Button) findViewById(R.id.button_workerPermission);
        buttonWorkerPermission.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_dodajprac:
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();
                startActivity(new Intent(WorkerActivity.this, AddWorkerActivity.class));
                break;
            case R.id.button_workerPermission:
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();
                startActivity(new Intent(WorkerActivity.this, WorkerPermissionActivity.class));
        }
    }
}