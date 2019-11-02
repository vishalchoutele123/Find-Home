package com.example.rent.Fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.rent.R;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.app.Activity.RESULT_OK;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.text.TextUtils.isEmpty;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostPropertyFragment extends Fragment {


    public PostPropertyFragment() {
        // Required empty public constructor
    }

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUser;
    private DatabaseReference mDatabaseProperty;
    private StorageReference mStorage;
    private String post_key;
    Button btn_post ;
    EditText post_type , post_price , post_city , post_options , post_contact , post_address;
    RadioGroup radioGroupOption , radioGroupType;
    RadioButton radioButtonoption , radioButtonType;
    ImageView imageView1 ;
    String user_id;
    private Uri imageUri1= null;
    String downloadurl ;

    String option ;
    Spinner city_post_spinner;

    private ProgressDialog mProgress ;


    View view ;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.fragment_post_property, container, false);

         mProgress = new ProgressDialog(getActivity());
         mProgress.setTitle("Posting Property ...");


         mAuth = FirebaseAuth.getInstance();
         mDatabase = FirebaseDatabase.getInstance().getReference().child("Property");
         mStorage = FirebaseStorage.getInstance().getReference();
         mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users");
         mDatabaseProperty=FirebaseDatabase.getInstance().getReference().child("MyProperty");


        user_id = mAuth.getCurrentUser().getUid();

        final DatabaseReference myProperty = mDatabaseProperty.child(user_id);


        btn_post= view.findViewById(R.id.post);
         post_price=view.findViewById(R.id.post_price);
         city_post_spinner= view.findViewById(R.id.city_post_spinner);
         imageView1 = view.findViewById(R.id.post_image1);
         post_address = view.findViewById(R.id.post_address);
         post_contact = view.findViewById(R.id.post_contact);

         radioGroupOption = view.findViewById(R.id.radio_option);
         radioGroupType = view.findViewById(R.id.radio_type);

         imageView1.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 Intent cameraIntent = new Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                 cameraIntent.setType("image/*");
                 if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                     startActivityForResult(cameraIntent, 1000);
                 }
             }
         });


         btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                uploadToDatabase();

                int selectedIdOption=  radioGroupOption.getCheckedRadioButtonId();
                radioButtonoption= view.findViewById(selectedIdOption);
//                int selectedIdType=;
                radioButtonType= view.findViewById(radioGroupType.getCheckedRadioButtonId());

                Log.d("vishal" , "post  value");

                Log.d("vishal" , "post "+radioButtonoption.getText());


                final String post = radioButtonoption.getText().toString();
                final String price = post_price.getText().toString();
                final String City = String.valueOf(city_post_spinner.getSelectedItem());
                final String type = radioButtonType.getText().toString();
                final String contact = post_contact.getText().toString();
                final String address = post_address.getText().toString();

                if (!isEmpty(post) && !isEmpty(price) && !isEmpty(City) && !isEmpty(type) && !isEmpty(contact) && !isEmpty(address) && imageUri1 !=null ) {

                    mProgress.show();

                    if (contact.length()!= 10){
                        Toast.makeText(container.getContext() , "Enter Proper Contact" , Toast.LENGTH_SHORT).show();
                    }
                    else {
                        final StorageReference mProperty_storage = mStorage.child("property/" + post + "/" + imageUri1.getLastPathSegment() + ".jpg");
                        mProperty_storage.putFile(imageUri1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                mProperty_storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        downloadurl = uri.toString();

                                        final DatabaseReference current_property_db = mDatabase.child(post).child(City);

                                        mDatabaseUser.child(user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                DatabaseReference newPost = current_property_db.push();
                                                post_key = newPost.getKey().toString();
                                                newPost.child("price").setValue(price);
                                                newPost.child("type").setValue(type);
                                                newPost.child("image").setValue(downloadurl);
                                                newPost.child("uid").setValue(user_id);
                                                newPost.child("contact").setValue(contact);
                                                newPost.child("address").setValue(address);
                                                newPost.child("option").setValue(post);
                                                newPost.child("username").setValue(dataSnapshot.child("name").getValue());

                                                radioGroupOption.clearCheck();
                                                post_price.setText("");
                                                radioGroupType.clearCheck();
                                                post_address.setText("");
                                                post_contact.setText("");
                                                imageView1.setImageResource(R.drawable.ic_photo_size_select_actual_black_24dp);

                                                DatabaseReference newProperty = myProperty.child(post_key);
                                                newProperty.child("price").setValue(price);
                                                newProperty.child("type").setValue(type);
                                                newProperty.child("city").setValue(City);
                                                newProperty.child("image").setValue(downloadurl);
                                                newProperty.child("PropertyPostKey").setValue(post_key);
                                                newProperty.child("option").setValue(post);

                                                mProgress.dismiss();

                                                Toast.makeText(container.getContext(), "Property posted Successfully:", Toast.LENGTH_SHORT).show();


                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }

                                        });


                                    }
                                });


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(container.getContext(), "Property Not posted Successfully:", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(container.getContext() , "All Fields are required " , Toast.LENGTH_SHORT).show();
                }

            }

         });
    return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if(requestCode == 1000){
                imageUri1 = data.getData();
                imageView1.setImageURI(imageUri1);
            }

        }
    }
}
