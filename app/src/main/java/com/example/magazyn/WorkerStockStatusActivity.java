package com.example.magazyn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WorkerStockStatusActivity extends AppCompatActivity {


    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_stock_status);
        mRecyclerView = (RecyclerView) findViewById(R.id.racycleView_workerProducts);

        new WorkerFirebaseDatabaseHelper().readProducts(new WorkerFirebaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<Products> products, List<String> keys) {
                new RecycleViewConfig().setConfig(mRecyclerView, WorkerStockStatusActivity.this, products, keys);
            }
        });

    }

}