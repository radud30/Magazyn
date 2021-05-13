package com.example.magazyn;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WorkerFirebaseDatabaseHelper {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceProducts, mReferenceWorker;
    private Query query,queryWorker;
    private List<Products> productsList = new ArrayList<>();
    private String creatorUid;

    public interface DataStatus{
        void DataIsLoaded(List<Products> products, List<String> keys);
    }

    public WorkerFirebaseDatabaseHelper(){
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceProducts = mDatabase.getReference("Products");
        mReferenceWorker = FirebaseDatabase.getInstance().getReference("Workers");
    }

    public void readProducts(final DataStatus dataStatus){

        mReferenceWorker = FirebaseDatabase.getInstance().getReference("Workers");
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        queryWorker = mReferenceWorker.child(currentUser);
        queryWorker.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    creatorUid = snapshot.child("creatorUid").getValue().toString();
                    //Log.d("MyTag", ""+creatorUid);
                    query = FirebaseDatabase.getInstance().getReference("Products").orderByChild("userUid").equalTo(creatorUid);

                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            productsList.clear();
                            List<String> keys = new ArrayList<>();
                            for(DataSnapshot keyNode: snapshot.getChildren()){
                                keys.add(keyNode.getKey());
                                Products products = keyNode.getValue(Products.class);
                                productsList.add(products);
                            }
                            dataStatus.DataIsLoaded(productsList, keys);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}
