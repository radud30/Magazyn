package com.example.magazyn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class PracownikActivity extends AppCompatActivity implements View.OnClickListener{
    private long mLastClickTime = 0;
    private Button dodajPrac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pracownik);

        dodajPrac = (Button) findViewById(R.id.button_dodajprac);
        dodajPrac.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_dodajprac:
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();
                startActivity(new Intent(PracownikActivity.this, DodajPracownikaActivity.class));
                break;
        }
    }
}