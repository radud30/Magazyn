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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUserActivity extends AppCompatActivity implements View.OnClickListener{
    private long mLastClickTime = 0;

    private Button registerUser;
    private EditText editTextImie, editTextWiek, editTextEmail, editTextHaslo, editTextPowtorzHaslo;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        registerUser = (Button) findViewById(R.id.button_zarejestruj);
        registerUser.setOnClickListener(this);

        editTextImie = (EditText) findViewById(R.id.editTextTextPersonName_name);
        editTextWiek = (EditText) findViewById(R.id.editTextNumber_age);
        editTextEmail = (EditText) findViewById(R.id.editTextTextEmailAddress2_mail);
        editTextHaslo = (EditText) findViewById(R.id.editTextTextPassword2_pass);
        editTextPowtorzHaslo = (EditText) findViewById(R.id.editTextTextPassword_re);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_zarejestruj:
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String haslo = editTextHaslo.getText().toString().trim();
        String imie = editTextImie.getText().toString().trim();
        String wiek = editTextWiek.getText().toString().trim();
        String powtorzhaslo = editTextPowtorzHaslo.getText().toString().trim();

        if (imie.isEmpty()){
            editTextImie.setError("Imię jest wymagane");
            editTextImie.requestFocus();
            return;
        }

        if (wiek.isEmpty()){
            editTextWiek.setError("Wiek jest wymagany");
            editTextWiek.requestFocus();
            return;
        }
        if (email.isEmpty()){
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
        mAuth.createUserWithEmailAndPassword(email,haslo)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            User user = new User(imie, wiek, email);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){
                                        Toast.makeText(RegisterUserActivity.this, "Zarejestrowano użytkownika", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        editTextEmail.setText("");
                                        editTextHaslo.setText("");
                                        editTextImie.setText("");
                                        editTextPowtorzHaslo.setText("");
                                        editTextWiek.setText("");

                                        startActivity(new Intent(RegisterUserActivity.this, MainActivity.class));
                                    }
                                    else{
                                        Toast.makeText(RegisterUserActivity.this, "Niepowodzenie w zakładaniu konto", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                        else{
                            Toast.makeText(RegisterUserActivity.this, "Niepowodzenie w zakładaniu konto", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}