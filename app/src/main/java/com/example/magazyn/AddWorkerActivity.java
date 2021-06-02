package com.example.magazyn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class AddWorkerActivity extends AppCompatActivity implements View.OnClickListener {
    private long mLastClickTime = 0;
    private Button buttonAddWorker;
    private EditText editTextEmail, editTextPassword, editTextRepeatPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private String creatorUid, permission;
    private Spinner spinner;
    private Workers workers;
    //private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_worker);

        mAuth = FirebaseAuth.getInstance();
        creatorUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        buttonAddWorker = (Button) findViewById(R.id.button_zarejestrujPracownika);
        buttonAddWorker.setOnClickListener(this);

        editTextEmail = (EditText) findViewById(R.id.editTextTextEmailAddress_mailprac);
        editTextPassword = (EditText) findViewById(R.id.editTextTextPassword_haslo);
        editTextRepeatPassword = (EditText) findViewById(R.id.editTextTextPassword_powhaslo);

        progressBar = (ProgressBar) findViewById(R.id.progressBar4);
        spinner = (Spinner) findViewById(R.id.spinnerPermission);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinerPermissions, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                permission = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_zarejestrujPracownika:
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();
                registerWorker();
                break;
        }
    }

    private void registerWorker() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String repeatPassword = editTextRepeatPassword.getText().toString().trim();

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
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    if(permission.equals("Brak")){
                        workers = new Workers(email, creatorUid,"true", "false", "false", "false");
                    }
                    if(permission.equals("Zarządca")){
                        workers = new Workers(email, creatorUid,"true", "true", "true", "true");
                    }
                    if(permission.equals("Pracownik - Wykładanie")){
                        workers = new Workers(email, creatorUid,"true", "true", "false", "false");
                    }
                    if(permission.equals("Pracownik - Stan")){
                        workers = new Workers(email, creatorUid,"true", "false", "true", "false");
                    }
                    if(permission.equals("Pracownik - Zbieraj")){
                        workers = new Workers(email, creatorUid,"true", "false", "false", "true");
                    }


                    FirebaseDatabase.getInstance().getReference("Workers")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(workers).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(AddWorkerActivity.this, "Zarejestrowano konto pracownika", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                editTextEmail.setText("");
                                editTextPassword.setText("");
                                editTextRepeatPassword.setText("");

                                FirebaseAuth.getInstance().signOut();
                                Intent intToMain = new Intent(AddWorkerActivity.this, MainActivity.class);
                                intToMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intToMain);
                            }
                            else {
                                Toast.makeText(AddWorkerActivity.this, "Niepowodzenie w zakładaniu konta pracownika", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(AddWorkerActivity.this, "Niepowodzenie w zakładaniu konta pracownika", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}