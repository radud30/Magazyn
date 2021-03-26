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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ProduktDetailsActivity extends AppCompatActivity {
    public static final int CAMERA_REQUEST_CODE = 10;
    private long mLastClickTime = 0;

    private EditText nazwaProduktu,iloscProduktu;
    public static EditText kodProduktuEdycja;
    private Button uaktualnijProdukt, usunProdukt;
    private ImageButton aparat;

    private String key, kod, nazwa, ilosc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produkt_details);

        key = getIntent().getStringExtra("key");
        kod = getIntent().getStringExtra("kod");
        nazwa = getIntent().getStringExtra("nazwa");
        ilosc = getIntent().getStringExtra("ilosc");

        nazwaProduktu = (EditText) findViewById(R.id.editTextTextPersonName_nazwa);
        nazwaProduktu.setText(nazwa);
        iloscProduktu = (EditText) findViewById(R.id.editTextNumber_ilosc);
        iloscProduktu.setText(ilosc);
        kodProduktuEdycja = (EditText) findViewById(R.id.editTextNumber_kod);
        kodProduktuEdycja.setText(kod);

        uaktualnijProdukt = (Button) findViewById(R.id.button_update);
        usunProdukt = (Button) findViewById(R.id.button_usun);
        aparat = (ImageButton) findViewById(R.id.imageButton2);

        uaktualnijProdukt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();

                String kod = kodProduktuEdycja.getText().toString().trim();
                String ilosc = iloscProduktu.getText().toString().trim();

                if(kod.isEmpty()){
                    kodProduktuEdycja.setError("Produkt musi posiadać kod");
                    kodProduktuEdycja.requestFocus();
                    return;
                }

                if(ilosc.isEmpty()){
                    iloscProduktu.setError("Musisz podać ilość");
                    iloscProduktu.requestFocus();
                    return;
                }

                int intIlosc = Integer.parseInt(ilosc);
                //String finalIlosc = String.valueOf(intIlosc);

                if(intIlosc == 0){
                    iloscProduktu.setError("Musisz podać liczbę większą od 0");
                    iloscProduktu.requestFocus();
                    return;
                }

                uaktualnijProd();
            }
        });

        usunProdukt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();

                AlertDialog.Builder alert = new AlertDialog.Builder(ProduktDetailsActivity.this);
                alert.setTitle("Magazyn");
                alert.setMessage("Czy na pewno chcesz usunąć produkt?");
                alert.setPositiveButton("TAK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        usunProd();
                    }
                });
                alert.setNegativeButton("NIE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert.show();

            }
        });


        aparat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();

                skanuj();
            }
        });
    }


    private void uaktualnijProd(){
        String obecnyuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String ilosc = iloscProduktu.getText().toString().trim();
        int intIlosc = Integer.parseInt(ilosc);
        String finalIlosc = String.valueOf(intIlosc);

        Produkty produkty = new Produkty();
        produkty.setKod(kodProduktuEdycja.getText().toString().trim());
        produkty.setProduktNazwa(nazwaProduktu.getText().toString().trim());
        produkty.setIlosc(finalIlosc);
        produkty.setUserId(obecnyuser);
        produkty.setKod_userid(obecnyuser + kodProduktuEdycja.getText().toString().trim());
        new FirebaseDatabaseHelper().updateProdukt(key, produkty, new FirebaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<Produkty> produktyList, List<String> keys) {

            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {
                Toast.makeText(ProduktDetailsActivity.this, "Pomyśle uaktualniono produkt",Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            @Override
            public void DataIsDeleted() {

            }
        });
    }
    private void usunProd(){
        new FirebaseDatabaseHelper().deleteProdukt(key, new FirebaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<Produkty> produktyList, List<String> keys) {

            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {
                Toast.makeText(ProduktDetailsActivity.this, "Pomyśle usunnięto produkt",Toast.LENGTH_SHORT).show();
                finish();
                return;
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
            startActivity(new Intent(this, EdytujSkanowanieActivity.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startActivity(new Intent(this, EdytujSkanowanieActivity.class));
            }else{
                Toast.makeText(this,"Skanowanie wymaga dostępu do kamery",Toast.LENGTH_SHORT).show();
            }
        }
    }

}