package com.example.ivgeorgiev.lis4ita.activities.loginRegister;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ivgeorgiev.lis4ita.R;
import com.example.ivgeorgiev.lis4ita.activities.FetchUsers;
import com.example.ivgeorgiev.lis4ita.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText logEmail, loginPassword;

    FirebaseAuth mAuth;
    DatabaseReference database;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference().child("Users");

        init();

        //checkUserExistence();
    }

    private void init() {
        logEmail = findViewById(R.id.logEmail);
        loginPassword = findViewById(R.id.logPassword);
    }

    public void loginButtonOnClick(View view) {
        String email = logEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            dialog = ProgressDialog.show(LoginActivity.this, "Loading", "Please wait...", true);

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        checkUserExistence();
                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed! User and password do not match!",
                                Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            });
        } else {
            dialog.dismiss();
            Toast.makeText(LoginActivity.this, "Type e-mail and password!", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkUserExistence() {

        if (mAuth.getCurrentUser() != null) {
            final String user_id = mAuth.getCurrentUser().getUid();

            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(user_id)) {
                        dialog.dismiss();

                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                        Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(loginIntent);

                        database.removeEventListener(this);
                    }else{

                        dialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void loginRegisterButtonOnClick(View view) {
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(i);
    }
}
