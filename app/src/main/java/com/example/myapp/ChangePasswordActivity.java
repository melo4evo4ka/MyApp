package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rengwuxian.materialedittext.MaterialEditText;

public class ChangePasswordActivity extends AppCompatActivity {
    private MaterialEditText oldPsw, newPsw, confirmPsw;
    private Button changePswBtn;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChangePasswordActivity.this,MainActivity.class));
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        oldPsw = findViewById(R.id.old_password);
        newPsw = findViewById(R.id.new_password);
        confirmPsw = findViewById(R.id.confirm_password);
        changePswBtn = findViewById(R.id.btn_change_psw);
        progressBar = findViewById(R.id.progressBar);

        changePswBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textOldPsw = oldPsw.getText().toString();
                String textNewPsw = newPsw.getText().toString();
                String textConfirmPsw = oldPsw.getText().toString();

                if (textOldPsw.isEmpty() || textNewPsw.isEmpty() || textConfirmPsw.isEmpty() ){
                    Toast.makeText(ChangePasswordActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else if(textNewPsw.length() > 6){
                    Toast.makeText(ChangePasswordActivity.this, "The new password length should be more than 6 character", Toast.LENGTH_SHORT).show();
                } else if(textConfirmPsw.equals(textNewPsw)){
                    Toast.makeText(ChangePasswordActivity.this, "Confirm password does not match password", Toast.LENGTH_SHORT).show();
               } else {
                    changePassword(textOldPsw,textNewPsw);
                    }
               }
        });


    }

    private void changePassword(String textOldPsw, final String textNewPsw) {
    progressBar.setVisibility(View.VISIBLE);
        AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(),textOldPsw);
        firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    firebaseUser.updatePassword(textNewPsw).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                firebaseAuth.signOut();
                                Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(ChangePasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ChangePasswordActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
