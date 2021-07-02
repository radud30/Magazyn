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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUserActivity extends AppCompatActivity implements View.OnClickListener{
    private long mLastClickTime = 0;

    private Button ButtonRegisterUser;
    private EditText editTextName, editTextAge, editTextEmail, editTextPassword, editTextRepeatPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        ButtonRegisterUser = (Button) findViewById(R.id.button_zarejestruj);
        ButtonRegisterUser.setOnClickListener(this);

        editTextName = (EditText) findViewById(R.id.editTextTextPersonName_name);
        editTextAge = (EditText) findViewById(R.id.editTextNumber_age);
        editTextEmail = (EditText) findViewById(R.id.editTextTextEmailAddress2_mail);
        editTextPassword = (EditText) findViewById(R.id.editTextTextPassword2_pass);
        editTextRepeatPassword = (EditText) findViewById(R.id.editTextTextPassword_re);

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
        String password = editTextPassword.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        String age = editTextAge.getText().toString().trim();
        String repeatPassword = editTextRepeatPassword.getText().toString().trim();

        if (name.isEmpty()){
            editTextName.setError("Imię jest wymagane");
            editTextName.requestFocus();
            return;
        }

        if (age.isEmpty()){
            editTextAge.setError("Wiek jest wymagany");
            editTextAge.requestFocus();
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
        if (password.isEmpty()){
            editTextPassword.setError("Hasło jest wymagane");
            editTextPassword.requestFocus();
            return;
        }
        if (password.length() < 6){
            editTextPassword.setError("Hasło musi mieć co najmniej 6 zanków");
            editTextPassword.requestFocus();
            return;
        }
        if (!password.equals(repeatPassword)){
            editTextRepeatPassword.setError("Hasła nie są identyczne");
            editTextRepeatPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            User user = new User(name, age, email,FirebaseAuth.getInstance().getCurrentUser().getUid(),"");

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){
                                        Toast.makeText(RegisterUserActivity.this, "Zarejestrowano użytkownika", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        editTextEmail.setText("");
                                        editTextPassword.setText("");
                                        editTextName.setText("");
                                        editTextRepeatPassword.setText("");
                                        editTextAge.setText("");

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