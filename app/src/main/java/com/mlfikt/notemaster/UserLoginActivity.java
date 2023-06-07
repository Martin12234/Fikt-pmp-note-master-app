package com.mlfikt.notemaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class UserLoginActivity extends AppCompatActivity {

    EditText emailEditTxt, passwordEditTxt;
    Button loginUserBtn, loginUserAnonymousBtn;
    ProgressBar progressBar;
    TextView createUserBtnTxtView;

    ImageView googleBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        emailEditTxt = findViewById(R.id.email_edit_text);
        passwordEditTxt = findViewById(R.id.password_edit_text);
        loginUserBtn = findViewById(R.id.login_user_btn);
        progressBar = findViewById(R.id.progress_bar);
        createUserBtnTxtView = findViewById(R.id.create_user_text_view_btn);
        googleBtn = findViewById(R.id.google_icon_btn);
        loginUserAnonymousBtn = findViewById(R.id.login_anonymous_btn);


        loginUserBtn.setOnClickListener(v-> loginUser());
        createUserBtnTxtView.setOnClickListener(v->startActivity(new Intent(UserLoginActivity.this, UserRegistrationActivity.class)));
        googleBtn.setOnClickListener(v->startActivity(new Intent(UserLoginActivity.this, GoogleSignInActivity.class)));
        loginUserAnonymousBtn.setOnClickListener(v->startActivity(new Intent(UserLoginActivity.this, AnonymousSignInActivity.class)));
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

        Resources resources = getResources();

        // Check for internet connectivity
        if (Utility.isInternetConnected(UserLoginActivity.this)) {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    changeInProgress(false);
                    if (task.isSuccessful()) {
                        // Success login
                        if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                            // Go to main activity
                            startActivity(new Intent(UserLoginActivity.this, MainActivity.class));
                        } else {
                            Utility.showToast(UserLoginActivity.this, resources.getString(R.string.verify_email_msg));
                        }
                    } else {
                        // Failed login
                        Utility.showToast(UserLoginActivity.this, resources.getString(R.string.wrong_user));
                    }
                }
            });
        } else {
            // No internet connection, try to log in using local database

            loginUserLocally(email, password, resources);
        }
    }


    void loginUserLocally(String email, String password, Resources resources) {
        changeInProgress(true);

        new AsyncTask<Void, Void, User>() {
            @Override
            protected User doInBackground(Void... voids) {
                try {
                    // Initialize the Room database
                    Log.d("UserLoginActivity", "Initializing the local database");
                    UserDatabase userDatabase = Room.databaseBuilder(
                            getApplicationContext(),
                            UserDatabase.class,
                            "user-database"
                    ).build();

                    // Check if the user exists in the local database
                    Log.d("UserLoginActivity", "Checking user in the local database");
                    UserDao userDao = userDatabase.userDao();
                    User user = userDao.getUserByEmailAndPassword(email, password);

                    return user;
                } catch (Exception e) {
                    // Handle any exceptions that occur during the database operation
                    Log.e("UserLoginActivity", "Error accessing the local database", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(User user) {
                super.onPostExecute(user);
                if (user != null) {
                    // User found in the local database
                    Log.d("UserLoginActivity", "User found in the local database");
                    startActivity(new Intent(UserLoginActivity.this, MainActivity.class));

                } else {
                    // User doesn't exist or wrong credentials, show a message or take appropriate action
                    Log.d("UserLoginActivity", "User not found in the local database");
                    Utility.showToast(UserLoginActivity.this, resources.getString(R.string.wrong_user));
                }
                changeInProgress(false);
            }
        }.execute();
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
        return true;
    }

}