package com.example.magazyn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ActivityLogActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        listView = (ListView) findViewById(R.id.listView);

        ArrayList<String> list = new ArrayList<>();
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
}