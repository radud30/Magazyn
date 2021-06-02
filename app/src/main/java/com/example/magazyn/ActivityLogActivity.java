package com.example.magazyn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ActivityLogActivity extends AppCompatActivity {

    private ListView listView;
    private Spinner spinner;
    private DatabaseReference databaseReferenceWorker;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayList<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        databaseReferenceWorker = FirebaseDatabase.getInstance().getReference();

        listView = (ListView) findViewById(R.id.listView);
        spinner = (Spinner) findViewById(R.id.spinnerWorker);

        //showListAll();
        showDataSpiner();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                //Log.d("MyTag", selectedItem+"");
                if(selectedItem !=null && selectedItem.equals("Wszyscy")){
                    showListAll();
                }
                else {
                    showListWorker(selectedItem);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void showListWorker(String worker){
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Activity");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot snapshotActivity: snapshot.getChildren()){
                    Activity activity = snapshotActivity.getValue(Activity.class);
                    String userUid = activity.getUserUid();
                    String email = activity.getWhoAddedEmail();
                    if(userUid != null && userUid.equals(currentUser) && email.equals(worker)){
                        String txt = activity.getDate() + " \n "+ activity.getWhoAddedEmail() + " \n " + activity.getActivity() + "\n  ";
                        list.add(0,txt);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showListAll(){
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Activity");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot snapshotActivity: snapshot.getChildren()){
                    Activity activity = snapshotActivity.getValue(Activity.class);
                    String userUid = activity.getUserUid();
                    if(userUid != null && userUid.equals(currentUser)){
                        String txt = activity.getDate() + " \n "+ activity.getWhoAddedEmail() + " \n " + activity.getActivity() + "\n  ";
                        list.add(0,txt);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showDataSpiner() {
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReferenceWorker.child("Workers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                arrayList.add("Wszyscy");
                for(DataSnapshot item: snapshot.getChildren()){
                    Workers workers = item.getValue(Workers.class);
                    String creatorUid = workers.getCreatorUid();
                    if(creatorUid != null && creatorUid.equals(currentUser)){
                        arrayList.add(workers.getEmail());
                    }
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ActivityLogActivity.this, R.layout.support_simple_spinner_dropdown_item,arrayList);
                spinner.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}