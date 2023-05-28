package com.ml.notemaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
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

import java.util.regex.Pattern;

public class UserRegistrationActivity extends AppCompatActivity {

    EditText emailEditTxt, passwordEditTxt, confirmPasswordEditTxt;
    Button createUserBtn;
    ProgressBar progressBar;
    TextView loginBtnTxtView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        emailEditTxt = findViewById(R.id.email_edit_text);
        passwordEditTxt = findViewById(R.id.password_edit_text);
        confirmPasswordEditTxt = findViewById(R.id.confirm_password_edit_text);
        createUserBtn = findViewById(R.id.create_user_btn);
        progressBar = findViewById(R.id.progress_bar);
        loginBtnTxtView = findViewById(R.id.login_text_view_btn);


        createUserBtn.setOnClickListener(v->createUser());
        loginBtnTxtView.setOnClickListener(v->startActivity(new Intent(UserRegistrationActivity.this, UserLoginActivity.class)));


    }

    void createUser() {
        String email = emailEditTxt.getText().toString();
        String password = passwordEditTxt.getText().toString();
        String confirmPassword = confirmPasswordEditTxt.getText().toString();

        boolean isValidated = validateData(email, password, confirmPassword);
        if(!isValidated) {
            return;
        }

        createUserInFirebase(email,password);

    }

    void createUserInFirebase(String email, String password) {
        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(UserRegistrationActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Resources resources = getResources();
                changeInProgress(false);
                if(task.isSuccessful()){
                    //creating user is done
                    Utility.showToast(UserRegistrationActivity.this, resources.getString(R.string.succ_created_user));
                    firebaseAuth.getCurrentUser().sendEmailVerification();
                    firebaseAuth.signOut();
                    finish();
                }
                else {
                    //failure
                    Utility.showToast(UserRegistrationActivity.this, task.getException().getLocalizedMessage());
                }
            }
        });
    }

    void changeInProgress(boolean inProgress) {
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            createUserBtn.setVisibility(View.GONE);
        }
        else {
            progressBar.setVisibility(View.GONE);
            createUserBtn.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email, String password, String confirmPassword) {
        Resources resources = getResources();

        //validate data that are input by client
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditTxt.setError(resources.getString(R.string.invalid_email));
            return false;
        }
        if(password.length()<6){
            passwordEditTxt.setError(resources.getString(R.string.invalid_length_password));
            return false;
        }
        if(!password.equals(confirmPassword)) {
            confirmPasswordEditTxt.setError(resources.getString(R.string.password_not_matched));
            return false;
        }
        return true;
    }
}