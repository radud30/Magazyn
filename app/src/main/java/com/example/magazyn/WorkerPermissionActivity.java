package com.example.magazyn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class WorkerPermissionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceWorker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_permission);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerviewPermission);
        new PermissionFirebase().readWorkers(new PermissionFirebase.DataStatus() {
            @Override
            public void DataIsLoaded(List<Workers> workers, List<String> keys) {
                new PermissionRecycleView().setConfig(recyclerView, WorkerPermissionActivity.this, workers, keys);
            }

            @Override
            public void DataIsUpdated() {

            }
        });

    }
}