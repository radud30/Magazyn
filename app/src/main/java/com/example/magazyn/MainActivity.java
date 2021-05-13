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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private long mLastClickTime = 0;

    private TextView textViewRegister, textViewForgotPassword;
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private DatabaseReference mReferenceWorker;
    private Query queryWorker;
    private String workerFb, permissionAddFb, permissionStockStatusFb, permissionCollectFb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewRegister = (TextView) findViewById(R.id.textView_rej);
        textViewRegister.setOnClickListener(this);

        buttonLogin = (Button) findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(this);

        editTextEmail = (EditText) findViewById(R.id.editTextTextEmailAddress2_mail);
        editTextPassword = (EditText) findViewById(R.id.editTextTextPassword2_pass);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        textViewForgotPassword = (TextView) findViewById(R.id.textView_forgotpass);
        textViewForgotPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textView_rej:
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();
                startActivity(new Intent(this, RegisterUserActivity.class));
                break;
            case R.id.button_login:
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();
                userLogin();
                break;
            case R.id.textView_forgotpass:
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;
        }
    }

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

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

        if(password.isEmpty()){
            editTextPassword.setError("Podaj hasło");
            editTextPassword.requestFocus();
            return;
        }
        if (password.length() < 6){
            editTextPassword.setError("Hasło jest za krótkie");
            editTextPassword.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user.isEmailVerified()){

                        mReferenceWorker = FirebaseDatabase.getInstance().getReference("Workers");
                        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        queryWorker = mReferenceWorker.child(currentUser);
                        queryWorker.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    workerFb = snapshot.child("worker").getValue().toString();
                                    permissionAddFb = snapshot.child("permissionAdd").getValue().toString();
                                    permissionStockStatusFb = snapshot.child("permissionStockStatus").getValue().toString();
                                    permissionCollectFb = snapshot.child("permissionCollect").getValue().toString();
                                    Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                                    intent.putExtra("EXTRA_WORKER_FB", workerFb);
                                    intent.putExtra("EXTRA_PERMISSION_ADD_FB", permissionAddFb);
                                    intent.putExtra("EXTRA_PERMISSION_STOCK_FB", permissionStockStatusFb);
                                    intent.putExtra("EXTRA_PERMISSION_COLLECT_FB",permissionCollectFb);
                                    startActivity(intent);
                                }
                                else {
                                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        editTextEmail.setText("");
                        editTextPassword.setText("");
                        progressBar.setVisibility(View.GONE);
                    }
                    else{
                        user.sendEmailVerification();
                        editTextEmail.setText("");
                        editTextPassword.setText("");
                        Toast.makeText(MainActivity.this, "Sprawdz swój email aby zweryfikować konto!", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }


                }
                else {
                    Toast.makeText(MainActivity.this, "Niepoprawne dane logowania", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}