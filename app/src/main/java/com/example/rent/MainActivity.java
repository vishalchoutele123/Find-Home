package com.example.rent;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import static android.text.TextUtils.isEmpty;

public class MainActivity extends AppCompatActivity {

    Button login_btn , signup_btn ;
    private EditText Email , Password ;
    private ProgressDialog mProgress ;


    //    private DatabaseReference mDatabase ;
    private FirebaseAuth mAuth ;
    private FirebaseAuth.AuthStateListener mAuthListner ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgress = new ProgressDialog(this);

        mProgress.setMessage("Login in progress ...");


        Email = findViewById(R.id.login_email);
        Password = findViewById(R.id.login_password);
        login_btn = findViewById(R.id.btn_login);
        signup_btn = findViewById(R.id.sign_up);


        mAuth = FirebaseAuth.getInstance();

        //checking user already logged in or not

        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null)
                {
                    Intent homeIntent = new Intent(MainActivity.this , Home.class);
                    startActivity(homeIntent);
                    finish();

                }

            }
        };



        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startLogin();

            }
        });

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this , Register.class);
                startActivity(intent);

            }
        });
    }


    private void startLogin(){

        String email =Email.getText().toString() ;
        String pwd = Password.getText().toString();

        if (!isEmpty(email) && !isEmpty(email) ) {

            mProgress.show();


            mAuth.signInWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("vishal", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(MainActivity.this, "" + user,
                                        Toast.LENGTH_SHORT).show();
                                mProgress.dismiss();
                                logintoHome();


                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("vishal", "signInWithEmail:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                mProgress.dismiss();

                            }

                            // ...
                        }

                    });
        }else {
            Toast.makeText(MainActivity.this, " username or password is empty" , Toast.LENGTH_SHORT).show();
        }

    }

    private void logintoHome(){

        Intent i = new Intent(MainActivity.this , Home.class);

        startActivity(i);
        finish();


    }


    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListner);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }
}
