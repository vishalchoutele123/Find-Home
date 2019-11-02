package com.example.rent;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.text.TextUtils.isEmpty;
import static android.widget.Toast.LENGTH_SHORT;

public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth ;
    private DatabaseReference mDatabase ;
    private StorageReference mStorage ;
    private StorageReference mProfile_storage ;
    private String user_id;
    private static final int PICK_IMAGE = 1 ;
    Button register ;
    CircleImageView imageView_btn ;
    private Uri imageUri = null ;
    private String downloadurl;
    private EditText mUsername , mEmail , mMobno , mPassword ;

    private String username , email , mob ;

    private ProgressDialog mProgress ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mProgress = new ProgressDialog(this);

        mProgress.setMessage("Registration in progress ...");

        initialize() ;

        imageUri = null;

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mStorage = FirebaseStorage.getInstance().getReference();

        imageView_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select image"),PICK_IMAGE);

            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registeStart();

            }
        });

    }


    private void initialize()
    {
        mUsername = findViewById(R.id.username);
        mEmail = findViewById(R.id.email);
        mMobno = findViewById(R.id.mob_no);
        mPassword = findViewById(R.id.password);
        register = findViewById(R.id.btn_register);
        imageView_btn = findViewById(R.id.image_btn);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE)
        {
            imageUri=data.getData();
            imageView_btn.setImageURI(imageUri);
//            imageView_btn.setImageBitmap();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    private void registeStart() {

        Log.d("vishal","In register start");
        username = mUsername.getText().toString();
        email = mEmail.getText().toString();
        mob = mMobno.getText().toString();
        String password = mPassword.getText().toString();



            if (!isEmpty(username) && !isEmpty(email) && !isEmpty(mob) && !isEmpty(password) && imageUri != null) {

                if (mob.length() != 10){
                    Toast.makeText(Register.this, "Enter Proper Contact number" , LENGTH_SHORT).show();
                }
                else {
                    mProgress.show();

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull final Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information

                                        // adding values to database

                                        user_id = mAuth.getCurrentUser().getUid();

                                        mProfile_storage = mStorage.child("profile/" + user_id + ".jpg");

                                        mProfile_storage.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                                                mProfile_storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {

                                                        downloadurl = uri.toString();

                                                        Map<String, Object> usermap = new HashMap<>();


                                                        uploadData();

                                                    }
                                                });
                                            }

                                            private void uploadData() {

                                                Log.d("vishal", "in upload method");
                                                DatabaseReference current_user_db = mDatabase.child(user_id);
                                                current_user_db.child("name").setValue(username);
                                                current_user_db.child("profile").setValue(downloadurl);
                                                current_user_db.child("email").setValue(email);
                                                Log.d("vishal", "image url " + downloadurl);


                                            }


                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("vishal", "Not uploaded");
                                            }
                                        });

                                        Log.d("vishal", "createUserWithEmail:success");
                                        Toast.makeText(Register.this, "Authentication success.",
                                                LENGTH_SHORT).show();
                                        mProgress.dismiss();
                                        toHomeActivity();


//                            FirebaseUser user = mAuth.getCurrentUser();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("vishal", "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(Register.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        mProgress.dismiss();

                                    }

                                    // ...
                                }
                            });
                }
            } else {
                Toast.makeText(Register.this, "All Fields are required", LENGTH_SHORT).show();
            }


    }

    void toHomeActivity(){

        Intent home_intent = new Intent(Register.this,MainActivity.class);
        startActivity(home_intent);
        finish();
    }
}


