package com.example.rent;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.rent.Fragments.MyPropertyFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class SingleProperty extends AppCompatActivity {

    TextView p_type , p_price , p_city , p_Oname , p_contact , p_address ;
    ImageView p_image;
    ImageButton mLikebtn ;
    Button remove ;

    private String option , city , post_key ;
    private String mImage , mCity , mName , mType , mPrice , mContact , mAddress ;
    private DatabaseReference mDatabase , mLike_db ,check_like_db;
    private FirebaseAuth mAuth ;
    boolean mLike = false;
    private String user_id  ,u_id ;

    private ProgressDialog mProgress ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_property);

        mProgress = new ProgressDialog(SingleProperty.this);
        mProgress.setTitle("Removing Property ...");


        p_address = findViewById(R.id.p_address);
        p_city = findViewById(R.id.p_city);
        p_contact = findViewById(R.id.p_contact);
        p_Oname = findViewById(R.id.p_name);
        p_price = findViewById(R.id.p_price);
        p_type = findViewById(R.id.p_type);
        p_image = findViewById(R.id.p_image);
        mLikebtn = findViewById(R.id.p_like);
        remove = findViewById(R.id.remove_post);

        city = getIntent().getStringExtra("city");
        option = getIntent().getStringExtra("option");
        post_key = getIntent().getStringExtra("post_key");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Property").child(option).child(city);

        mLike_db = FirebaseDatabase.getInstance().getReference().child("Like");

        mAuth =FirebaseAuth.getInstance();

        user_id = mAuth.getCurrentUser().getUid();

        Log.d("vishal" , "city " +city);
        Log.d("vishal", "option "+ option );
        Log.d("vishal","post Key "+post_key);

        DatabaseReference db = mDatabase.child(post_key);

        check_like_db = FirebaseDatabase.getInstance().getReference().child("Like").child(user_id);

        check_like_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(post_key)){

                    mLikebtn.setImageResource(R.drawable.like_btn);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                mCity = city;
                mImage = dataSnapshot.child("image").getValue().toString();
                mName =  dataSnapshot.child("username").getValue().toString();
                mType =  dataSnapshot.child("type").getValue().toString();
                mPrice = dataSnapshot.child("price").getValue().toString();
                mContact = dataSnapshot.child("contact").getValue().toString();
                mAddress = dataSnapshot.child("address").getValue().toString();
                u_id = dataSnapshot.child("uid").getValue().toString();

                if (mImage!=null && mName!=null && mType!=null && mPrice!=null && mContact!=null && mAddress!=null){

                    if (u_id.equals(user_id)){
                        remove.setVisibility(VISIBLE);
                        mLikebtn.setVisibility(INVISIBLE);
                    }else {
                        remove.setVisibility(INVISIBLE);
                        mLikebtn.setVisibility(VISIBLE);
                    }

                    p_Oname.setText(mName);
                    p_type.setText(mType);
                    p_price.setText("Rs. "+mPrice);
                    p_city.setText(mCity);
                    p_contact.setText(mContact);
                    p_address.setText(mAddress);

                    Glide.with(getApplicationContext()).load(mImage).into(p_image);

                }
                else {
                    getSupportFragmentManager().beginTransaction()
                            .add(android.R.id.content, new MyPropertyFragment ()).commit();

                    Intent i = new Intent(SingleProperty.this,MyPropertyFragment.class);
                    startActivity(i);
                    finish();

            }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mLikebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mLike =true;

                mLike_db.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (mLike){

                            if (dataSnapshot.child(user_id).hasChild(post_key)){

                                mLike_db.child(user_id).child(post_key).removeValue();

                                mLike = false;

                                mLikebtn.setImageResource(R.drawable.dislike_btn);

                            }else {

                                mLikebtn.setImageResource(R.drawable.like_btn);


                                mLike_db.child(user_id).child(post_key).child("city").setValue(mCity);
                                mLike_db.child(user_id).child(post_key).child("type").setValue(mType);
                                mLike_db.child(user_id).child(post_key).child("price").setValue(mPrice);
                                mLike_db.child(user_id).child(post_key).child("name").setValue(mName);
                                mLike_db.child(user_id).child(post_key).child("image").setValue(mImage);
                                mLike_db.child(user_id).child(post_key).child("option").setValue(option);


                                mLike = false;
                            }




                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }
        });


        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgress.show();
                removeLike();


//  DatabaseReference mProperty_db = FirebaseDatabase.getInstance().getReference().child("MyProperty").child(user_id).child(post_key);
//                    mProperty_db.removeValue();
//
//


            }

            private void removeLike() {

                mLike_db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot item_snapshot:dataSnapshot.getChildren()) {


                        if (item_snapshot.hasChild(post_key)){

                            Log.d("item id ",item_snapshot.child(post_key).child("city").getValue().toString());
                            Log.d("item desc",item_snapshot.child(post_key).child("price").getValue().toString());

                            item_snapshot.child(post_key).getRef().removeValue();



                        }else
                        {
                            Log.d("vishal" , "nothing to remove");
                        }



                    }
                    DatabaseReference mProperty_db = FirebaseDatabase.getInstance().getReference().child("MyProperty").child(user_id).child(post_key);
                    mProperty_db.removeValue();

                    mDatabase.child(post_key).removeValue();


                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            }


        });

    }

}
