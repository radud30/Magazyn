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

 public class FirebaseDatabaseHelper {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceProdukty;
    private List<Products> productsList = new ArrayList<>();
    private Query query;

    public interface DataStatus{
        void DataIsLoaded(List<Products> productsList, List<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }

    public FirebaseDatabaseHelper() {
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceProdukty = mDatabase.getReference("Products");

        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        query = FirebaseDatabase.getInstance().getReference("Products")
                .orderByChild("userUid")
                .equalTo(currentUser);
    }

    public void readProducts(final DataStatus dataStatus){

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productsList.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : snapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    Products products = keyNode.getValue(Products.class);
                    productsList.add(products);
                }
                dataStatus.DataIsLoaded(productsList,keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void addProduct(Products products, final DataStatus dataStatus){
        String key = mReferenceProdukty.push().getKey();
        mReferenceProdukty.child(key).setValue(products)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dataStatus.DataIsInserted();
                    }
                });
    }
    public void updateProduct(String key, Products products, final DataStatus dataStatus){
        mReferenceProdukty.child(key).setValue(products)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dataStatus.DataIsUpdated();
                    }
                });
    }
    public void deleteProducts(String key, final DataStatus dataStatus){
        mReferenceProdukty.child(key).setValue(null)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dataStatus.DataIsDeleted();
                    }
                });
    }
}
