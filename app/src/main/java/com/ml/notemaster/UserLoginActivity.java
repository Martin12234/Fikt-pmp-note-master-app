package com.ml.notemaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class UserLoginActivity extends AppCompatActivity {

    EditText emailEditTxt, passwordEditTxt;
    Button loginUserBtn;
    ProgressBar progressBar;
    TextView createUserBtnTxtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        emailEditTxt = findViewById(R.id.email_edit_text);
        passwordEditTxt = findViewById(R.id.password_edit_text);
        loginUserBtn = findViewById(R.id.login_user_btn);
        progressBar = findViewById(R.id.progress_bar);
        createUserBtnTxtView = findViewById(R.id.create_user_text_view_btn);


        loginUserBtn.setOnClickListener(v-> loginUser());
        createUserBtnTxtView.setOnClickListener(v->startActivity(new Intent(UserLoginActivity.this, UserRegistrationActivity.class)));
    }

    void loginUser() {
        String email = emailEditTxt.getText().toString();
        String password = passwordEditTxt.getText().toString();

        boolean isValidated = validateData(email, password);
        if(!isValidated) {
            return;
        }

        loginUserInFirebase(email,password);
    }

    void loginUserInFirebase(String email, String password) {
        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if(task.isSuccessful()) {
                    //success login
                    if(firebaseAuth.getCurrentUser().isEmailVerified()) {
                        //go to main activity
                        startActivity(new Intent(UserLoginActivity.this, MainActivity.class));
                    } else {
                        Utility.showToast(UserLoginActivity.this, "Email not verified, Please verify your email.");
                    }
                } else {
                    //failed login
                    Utility.showToast(UserLoginActivity.this, task.getException().getLocalizedMessage());

                }
            }
        });
    }

    void changeInProgress(boolean inProgress) {
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            loginUserBtn.setVisibility(View.GONE);
        }
        else {
            progressBar.setVisibility(View.GONE);
            loginUserBtn.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email, String password) {
        //validate data that are input by client
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditTxt.setError("Email is invalid");
            return false;
        }
        if(password.length()<6){
            passwordEditTxt.setError("Password length is invalid");
            return false;
        }
        return true;
    }
}