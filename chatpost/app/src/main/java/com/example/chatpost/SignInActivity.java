package com.example.chatpost;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.chatpost.Models.Users;
import com.example.chatpost.databinding.ActivityMainBinding;
import com.example.chatpost.databinding.ActivitySignInBinding;
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

public class SignInActivity extends AppCompatActivity {


    ActivitySignInBinding binding;

    private GoogleSignInClient mGoogleSignInClient;

    int RC_SIGN_IN = 44;

    FirebaseAuth auth;
    ProgressDialog progressDialog;

    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Toast.makeText(this, "on create is called..", Toast.LENGTH_SHORT).show();




        binding.tvNotHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this , SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        if(auth.getCurrentUser() != null)
        {
            // open the main activity..
            Intent intent = new Intent(SignInActivity.this , MainActivity.class);
            startActivity(intent);
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Login Account...");
        progressDialog.setMessage("Signing to your account..");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        binding.btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                // here we have to take the data from the intent and have to store in database and
                // have to open the main activity..

                if(binding.etEmail.getText().toString().isEmpty()){
                    progressDialog.dismiss();
                    binding.etEmail.setError("email cannot be empty.");
                    return;
                }

                if(binding.etPassword.getText().toString().isEmpty()){
                    progressDialog.dismiss();
                    binding.etPassword.setError("enter your correct password.");
                    return;
                }

                if (binding.etEmail.getText() != null && binding.etEmail.getText().length() != 0) {
                    auth.signInWithEmailAndPassword(binding.etEmail.getText().toString(), binding.etPassword.getText().toString()).
                            addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()) {

                                        Toast.makeText(SignInActivity.this, "User signed in..", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(SignInActivity.this, "Id or password is incorrect..", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                } else
                {
                    progressDialog.dismiss();
                    Toast.makeText(SignInActivity.this, "add proper email id and password..", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();

                            Users users = new Users();
                            users.setUserId(user.getUid());
                            users.setUserName(user.getDisplayName());
                            users.setProfilePicture(user.getPhotoUrl().toString());

                            database.getReference().child("users").child(user.getUid()).setValue(users);

                            // here user will have to sign in..

                            // now call the new Intent..
                            Intent intent = new Intent(SignInActivity.this , MainActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                        }

                    }
                });
        if(auth.getCurrentUser() != null)
        {
            //Toast.makeText(this, "this function get called..", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SignInActivity.this , MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Toast.makeText(this, "on start called..", Toast.LENGTH_SHORT).show();
        if(auth.getCurrentUser() != null)
        {
            finish();
        }
    }
}