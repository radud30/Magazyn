package com.example.magazyn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ZbierajActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    ZXingScannerView ScannerView;

    private DatabaseReference ref;
    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScannerView = new ZXingScannerView(this);
        setContentView(ScannerView);
    }

    @Override
    public void handleResult(Result result) {
        AlertDialog.Builder alert = new AlertDialog.Builder(ZbierajActivity.this);
        alert.setTitle("Czy zebrać produkt o kodzie:");
        String kodProduktu = result.getText();
        alert.setMessage(kodProduktu);
        alert.setPositiveButton("TAK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ref = FirebaseDatabase.getInstance().getReference();
                String obecnyuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String kod_userid = obecnyuser + kodProduktu;
                query = ref.child("Produkty").orderByChild("kod_userid").equalTo(kod_userid);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChildren()){
                            for(DataSnapshot kodSnapshot: snapshot.getChildren()){
                                //Log.d("MyTag", "" + kodSnapshot.child("ilosc").getValue());
                                String ilosc = kodSnapshot.child("ilosc").getValue().toString();
                                if(ilosc.equals("1")){
                                    kodSnapshot.getRef().removeValue();
                                }
                                else {
                                    int intIlosc = Integer.parseInt(ilosc);
                                    intIlosc--;
                                    kodSnapshot.getRef().child("ilosc").setValue(intIlosc+"");
                                }
                            }
                            Toast.makeText(ZbierajActivity.this, "Zebrano produkt",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(ZbierajActivity.this, "Nie ma w bazie produktu o takim kodzie",Toast.LENGTH_SHORT).show();
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ZbierajActivity.this, "Błąd",Toast.LENGTH_SHORT).show();
                    }
                });

                ScannerView.resumeCameraPreview(ZbierajActivity.this::handleResult);
            }
        });
        alert.setNegativeButton("NIE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ScannerView.resumeCameraPreview(ZbierajActivity.this::handleResult);
            }
        });
        alert.show();

    }

    @Override
    protected void onPause() {
        super.onPause();
        ScannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ScannerView.setResultHandler(this);
        ScannerView.startCamera();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch(keyCode){
            case KeyEvent.KEYCODE_VOLUME_UP:
                if(action == KeyEvent.ACTION_DOWN){
                    ScannerView.setFlash(true);
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if(action == KeyEvent.ACTION_DOWN){
                    ScannerView.setFlash(false);
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

}