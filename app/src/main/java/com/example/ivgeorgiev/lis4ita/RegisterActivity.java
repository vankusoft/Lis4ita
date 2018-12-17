package com.example.ivgeorgiev.lis4ita;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    EditText regNickNameET, regEmailET, regPasswordET;

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();

        mAuth=FirebaseAuth.getInstance();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Users");
    }

    private void init(){
        regNickNameET=findViewById(R.id.regNicknameET);
        regEmailET=findViewById(R.id.regEmailET);
        regPasswordET=findViewById(R.id.regPasswordET);
    }

    public void registerBtnOnClick(View view) {

        final String nickName=regNickNameET.getText().toString().trim();
        final String email=regEmailET.getText().toString().trim();
        final String password=regPasswordET.getText().toString().trim();

        if(!nickName.isEmpty() && !email.isEmpty() && !password.isEmpty()){

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){

                        String user_id = mAuth.getCurrentUser().getUid();

                        DatabaseReference current_user=databaseReference.child(user_id);
                        current_user.child("nick_name").setValue(nickName);

                        Toast.makeText(RegisterActivity.this, "Registration successfull!", Toast.LENGTH_SHORT).show();

                        Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                        startActivity(intent);

                    }else{
                        FirebaseAuthException e= (FirebaseAuthException) task.getException();
                        Toast.makeText(RegisterActivity.this,"Failed Registration: "+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }


            });

            
        }

    }
}
