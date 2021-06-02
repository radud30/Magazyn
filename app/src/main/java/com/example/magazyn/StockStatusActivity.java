package com.example.magazyn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.List;

public class StockStatusActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ImageButton imageButton;
    private EditText searchEditText;
    private Spinner spinner;
    private String search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_status);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_produkty);
        new FirebaseDatabaseHelper().readProducts("","",new FirebaseDatabaseHelper.DataStatus() {
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

        searchEditText = (EditText) findViewById(R.id.editTextSearch);

        spinner = (Spinner) findViewById(R.id.spinner_search);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinerSearch, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                search = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        imageButton = (ImageButton) findViewById(R.id.imageButton_search);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FirebaseDatabaseHelper().readProducts(searchEditText.getText().toString().trim(),search,new FirebaseDatabaseHelper.DataStatus() {
                    @Override
                    public void DataIsLoaded(List<Products> productsList, List<String> keys) {
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
        });
    }
}