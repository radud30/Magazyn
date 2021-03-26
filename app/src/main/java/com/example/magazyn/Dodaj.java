package com.example.magazyn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;

public class Dodaj extends AppCompatActivity implements View.OnClickListener{
    public static final int CAMERA_REQUEST_CODE = 10;
    private long mLastClickTime = 0;

    private EditText nazwaProduktu,iloscProduktu;
    public static EditText kodProduktu;
    private Button dodajProdukt;
    private ImageButton aparat;

    //private FirebaseUser user;
    //private DatabaseReference reference;
    private DatabaseReference databaseProdukty;
    private Query query;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj);

        databaseProdukty = FirebaseDatabase.getInstance().getReference("Produkty");

        nazwaProduktu = (EditText) findViewById(R.id.editTextTextPersonName2);
        iloscProduktu = (EditText) findViewById(R.id.editTextNumber2);
        kodProduktu = (EditText) findViewById(R.id.editTextNumber3);


        dodajProdukt = (Button) findViewById(R.id.button3);
        dodajProdukt.setOnClickListener(this);

        aparat = (ImageButton) findViewById(R.id.imageButton);
        aparat.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button3:
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();
                dodajProd();
                break;
            case R.id.imageButton:
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();
                skanuj();
                break;
        }
    }

    private void dodajProd(){
        String nazwa = nazwaProduktu.getText().toString().trim();

        String ilosc = iloscProduktu.getText().toString().trim();

        String obecnyuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String kod = kodProduktu.getText().toString().trim();

        if(kod.isEmpty()){
            kodProduktu.setError("Produkt musi posiadać kod");
            kodProduktu.requestFocus();
            return;
        }

        if(ilosc.isEmpty()){
            iloscProduktu.setError("Musisz podać ilość");
            iloscProduktu.requestFocus();
            return;
        }

        int intIlosc = Integer.parseInt(ilosc);
        String finalIlosc = String.valueOf(intIlosc);

        if(intIlosc == 0){
            iloscProduktu.setError("Musisz podać liczbę większą od 0");
            iloscProduktu.requestFocus();
            return;
        }

        //reference = FirebaseDatabase.getInstance().getReference("Produkty");
        query = databaseProdukty.orderByChild("kod").equalTo(kod);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot kodSnapshot: snapshot.getChildren()){
                        String iloscFb = kodSnapshot.child("ilosc").getValue().toString();
                        //Log.d("MyTag", "" + ilosc);
                        int intIloscFb = Integer.parseInt(iloscFb);
                        intIloscFb = intIloscFb + intIlosc;
                        kodSnapshot.getRef().child("ilosc").setValue(intIloscFb+"");
                        Toast.makeText(Dodaj.this, "Dodano "+ intIlosc +" produkt/ów o kodzie: " +"'" + kod + "'",Toast.LENGTH_SHORT).show();
                    }
                }
                else {

                    String kod_userid = obecnyuser + kod;
                    Produkty produkt = new Produkty(kod,obecnyuser,nazwa,finalIlosc,kod_userid);

                    new FirebaseDatabaseHelper().addProdukt(produkt, new FirebaseDatabaseHelper.DataStatus() {
                        @Override
                        public void DataIsLoaded(List<Produkty> produktyList, List<String> keys) {

                        }

                        @Override
                        public void DataIsInserted() {
                            Toast.makeText(Dodaj.this,"Nowy produkt dodano",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void DataIsUpdated() {

                        }

                        @Override
                        public void DataIsDeleted() {

                        }
                    });

                }
                kodProduktu.setText("");
                nazwaProduktu.setText("");
                iloscProduktu.setText("1");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void skanuj(){
        verifyPermission();
    }

    private void verifyPermission(){
        String[] permissions = {Manifest.permission.CAMERA};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),permissions[0]) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,permissions,CAMERA_REQUEST_CODE);
        }else {
            startActivity(new Intent(this, DodajSkanowanie.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startActivity(new Intent(this, DodajSkanowanie.class));
            }else{
                Toast.makeText(this,"Skanowanie wymaga dostępu do kamery",Toast.LENGTH_SHORT).show();
            }
        }
    }


}



