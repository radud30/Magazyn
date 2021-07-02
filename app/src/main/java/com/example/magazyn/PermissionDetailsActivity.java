package com.example.magazyn;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class PermissionDetailsActivity extends AppCompatActivity {

    private TextView editTextEmail;
    private Switch aSwitchAdd, aSwitchStock, aSwitchCollect, aSwitchLocation;
    private Button buttonUpdate;

    private String key, email, switchAddStatus, swtichStockStatus, swtichCollectStatus, swtichLocationStatus, worker, creatorUid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_details);

        key = getIntent().getStringExtra("key");
        email = getIntent().getStringExtra("email");
        switchAddStatus = getIntent().getStringExtra("add");
        swtichStockStatus = getIntent().getStringExtra("stock");
        swtichCollectStatus = getIntent().getStringExtra("collect");
        swtichLocationStatus = getIntent().getStringExtra("location");
        worker = getIntent().getStringExtra("worker");
        creatorUid = getIntent().getStringExtra("creatorUid");

        editTextEmail = (TextView) findViewById(R.id.textViewEmailDetails);
        editTextEmail.setText(email);
        aSwitchAdd = (Switch) findViewById(R.id.switchAddDetails);
        if(switchAddStatus.equals("true")){
            aSwitchAdd.setChecked(true);
        }
        aSwitchStock = (Switch) findViewById(R.id.switchStockDetails);
        if(swtichStockStatus.equals("true")){
            aSwitchStock.setChecked(true);
        }
        aSwitchCollect= (Switch) findViewById(R.id.switchCollectDetails);
        if(swtichCollectStatus.equals("true")){
            aSwitchCollect.setChecked(true);
        }
        aSwitchLocation = (Switch) findViewById(R.id.switchLocationDetails);
        if(swtichLocationStatus.equals("true")){
            aSwitchLocation.setChecked(true);
        }

        buttonUpdate = (Button) findViewById(R.id.buttonPermissionDetails);

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Workers workers = new Workers();
                workers.setPermissionAdd(switchAddStatus);
                workers.setPermissionStockStatus(swtichStockStatus);
                workers.setPermissionCollect(swtichCollectStatus);
                workers.setPermissionLocation(swtichLocationStatus);
                workers.setEmail(email);
                workers.setCreatorUid(creatorUid);
                workers.setWorker(worker);


                new PermissionFirebase().updatePermission(key, workers, new PermissionFirebase.DataStatus() {
                    @Override
                    public void DataIsLoaded(List<Workers> workers, List<String> keys) {

                    }

                    @Override
                    public void DataIsUpdated() {
                        Toast.makeText(PermissionDetailsActivity.this, "Pomyśle zmieniono uprawnienia",Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                });
            }
        });

        aSwitchAdd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    switchAddStatus = "true";
                }
                else{
                    switchAddStatus = "false";
                }
                //Log.d("MyTag", switchAddStatusFb +"");
            }
        });

        aSwitchStock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    swtichStockStatus = "true";
                }
                else{
                    swtichStockStatus = "false";
                }
            }
        });

        aSwitchCollect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    swtichCollectStatus = "true";
                }
                else{
                    swtichCollectStatus = "false";
                }
            }
        });

        aSwitchLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    swtichLocationStatus = "true";
                }
                else{
                    swtichLocationStatus = "false";
                }
            }
        });
    }
}