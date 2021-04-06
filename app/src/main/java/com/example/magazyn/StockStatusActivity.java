package com.example.magazyn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import java.util.List;

public class StockStatusActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_status);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_produkty);
        new FirebaseDatabaseHelper().readProducts(new FirebaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<Products> productsList, List<String> keys) {
                findViewById(R.id.progressBar3).setVisibility(View.GONE);
                new RecycleViewConfig().setConfig(mRecyclerView, StockStatusActivity.this, productsList,keys);
            }

            @Override
            public void DataIsInserted() {

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