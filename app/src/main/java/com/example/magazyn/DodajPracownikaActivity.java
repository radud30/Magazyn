package com.example.magazyn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DodajPracownikaActivity extends AppCompatActivity implements View.OnClickListener {
    private long mLastClickTime = 0;
    private Button rejestrujPracownika;
    private EditText editTextEmail, editTextHaslo, editTextPowtorzHaslo;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    String creator_uid;
    //private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_pracownika);

        mAuth = FirebaseAuth.getInstance();
        creator_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        rejestrujPracownika = (Button) findViewById(R.id.button_zarejestrujPracownika);
        rejestrujPracownika.setOnClickListener(this);

        editTextEmail = (EditText) findViewById(R.id.editTextTextEmailAddress_mailprac);
        editTextHaslo = (EditText) findViewById(R.id.editTextTextPassword_haslo);
        editTextPowtorzHaslo = (EditText) findViewById(R.id.editTextTextPassword_powhaslo);

        progressBar = (ProgressBar) findViewById(R.id.progressBar4);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_zarejestrujPracownika:
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();
                registerPracownik();
                break;
        }
    }

    private void registerPracownik() {
        String email = editTextEmail.getText().toString().trim();
        String haslo = editTextHaslo.getText().toString().trim();
        String powtorzhaslo = editTextPowtorzHaslo.getText().toString().trim();

        if(email.isEmpty()){
            editTextEmail.setError("Email jest wymagany");
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Podaj poprawny email");
            editTextEmail.requestFocus();
            return;
        }
        if (haslo.isEmpty()){
            editTextHaslo.setError("Hasło jest wymagane");
            editTextHaslo.requestFocus();
            return;
        }
        if (haslo.length() < 6){
            editTextHaslo.setError("Hasło musi mieć co najmniej 6 zanków");
            editTextHaslo.requestFocus();
            return;
        }
        if (!haslo.equals(powtorzhaslo)){
            editTextPowtorzHaslo.setError("Hasła nie są identyczne");
            editTextPowtorzHaslo.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,haslo).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    boolean isworker = true;
                    Pracownik pracownik = new Pracownik(email,creator_uid,isworker);

                    FirebaseDatabase.getInstance().getReference("Pracownik")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(pracownik).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(DodajPracownikaActivity.this, "Zarejestrowano konto pracownika", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                editTextEmail.setText("");
                                editTextHaslo.setText("");
                                editTextPowtorzHaslo.setText("");

                                startActivity(new Intent(DodajPracownikaActivity.this, PracownikActivity.class));
                            }
                            else {
                                Toast.makeText(DodajPracownikaActivity.this, "Niepowodzenie w zakładaniu konta pracownika", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(DodajPracownikaActivity.this, "Niepowodzenie w zakładaniu konta pracownika", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}