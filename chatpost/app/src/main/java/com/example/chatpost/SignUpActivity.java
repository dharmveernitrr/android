package com.example.chatpost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.chatpost.Models.Users;
import com.example.chatpost.databinding.ActivitySignUpBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;

    FirebaseAuth auth;
    FirebaseDatabase database;

    ProgressDialog progressDialog;
    int RC_SIGN_IN = 44;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creating Account...");
        progressDialog.setMessage("We're creating your account..");

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        binding.tvAlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();

                if(binding.etEmail.getText() != null && binding.etEmail.getText().length() != 0) {
                    auth.createUserWithEmailAndPassword(binding.etEmail.getText().toString(),
                            binding.etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {

                                // now here we have to add the data of the user to the firebase..

                                Users users = new Users(binding.etUserName.getText().toString(), binding.etEmail.getText().toString(),
                                        binding.etPassword.getText().toString());
                                // now we have to set this to firebase...

                                String id = task.getResult().getUser().getUid();
                                database.getReference().child("users").child(id).setValue(users);

                                Intent intent = new Intent(SignUpActivity.this , SignInActivity.class);
                                startActivity(intent);
                                //Toast.makeText(SignUpActivity.this, "wohoo..!!", Toast.LENGTH_SHORT).show();
                                Toast.makeText(SignUpActivity.this, "signed up successfully", Toast.LENGTH_SHORT).show();
                                finish();

                            } else {
                                Log.v("fkldslasfkldsasfkld", "fksdnkdasnknsdasakfnasdlkfkldsanflkdnsf");
                                progressDialog.hide();
                                Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(SignUpActivity.this, "add proper email id and password", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        if(auth.getCurrentUser() != null)
        {
            finish();
        }
    }

}