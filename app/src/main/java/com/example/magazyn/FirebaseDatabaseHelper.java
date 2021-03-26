 package com.example.magazyn;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

 public class FirebaseDatabaseHelper {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceProdukty;
    private List<Produkty> produktyList = new ArrayList<>();
    private Query query;

    public interface DataStatus{
        void DataIsLoaded(List<Produkty> produktyList, List<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }

    public FirebaseDatabaseHelper() {
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceProdukty = mDatabase.getReference("Produkty");
        String obecnyuser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        query = FirebaseDatabase.getInstance().getReference("Produkty")
                .orderByChild("userId")
                .equalTo(obecnyuser);
    }

    public void readProdukty(final DataStatus dataStatus){
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                produktyList.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : snapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    Produkty produkty = keyNode.getValue(Produkty.class);
                    produktyList.add(produkty);
                }
                dataStatus.DataIsLoaded(produktyList,keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void addProdukt(Produkty produkty, final DataStatus dataStatus){
        String key = mReferenceProdukty.push().getKey();
        mReferenceProdukty.child(key).setValue(produkty)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dataStatus.DataIsInserted();
                    }
                });
    }
    public void updateProdukt(String key, Produkty produkty, final DataStatus dataStatus){
        mReferenceProdukty.child(key).setValue(produkty)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dataStatus.DataIsUpdated();
                    }
                });
    }
    public void deleteProdukt(String key, final DataStatus dataStatus){
        mReferenceProdukty.child(key).setValue(null)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dataStatus.DataIsDeleted();
                    }
                });
    }
}
