package com.example.magazyn;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PermissionFirebase {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceWorker;
    private List<Workers> workers = new ArrayList<>();
    private Query query;

    public interface DataStatus{
        void DataIsLoaded(List<Workers> workers, List<String> keys);
        void DataIsUpdated();
    }
    public PermissionFirebase() {
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceWorker = mDatabase.getReference("Workers");

        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        query = FirebaseDatabase.getInstance().getReference("Workers").orderByChild("creatorUid").equalTo(currentUser);
    }

    public void readWorkers(final DataStatus dataStatus){
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                workers.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : snapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    Workers worker = keyNode.getValue(Workers.class);
                    workers.add(worker);
                }
                dataStatus.DataIsLoaded(workers,keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updatePermission(String key, Workers workers, final DataStatus dataStatus){
        mReferenceWorker.child(key).setValue(workers).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dataStatus.DataIsUpdated();
            }
        });
    }
}
