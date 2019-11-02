package com.example.rent.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.rent.MainActivity;
import com.example.rent.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    Button logout_btn ;
    CircleImageView p_image;
    TextView p_username , p_email;
    private FirebaseAuth mAuth ;
    private FirebaseDatabase mDatabase ;
    private String userid ;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       final View view = inflater.inflate(R.layout.fragment_profile, container, false);

       mAuth = FirebaseAuth.getInstance();
       mDatabase = FirebaseDatabase.getInstance();
       logout_btn = view.findViewById(R.id.profile_logout);
       p_email = view.findViewById(R.id.profile_email);
       p_image = view.findViewById(R.id.profile_image);
       p_username = view.findViewById(R.id.profile_username);

       userid = mAuth.getCurrentUser().getUid();

       DatabaseReference current_user = mDatabase.getReference().child("Users");

       current_user.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               String uname = dataSnapshot.child("name").getValue().toString();
               String email = dataSnapshot.child("email").getValue().toString();
               String image = dataSnapshot.child("profile").getValue().toString();


               Glide.with(container.getContext()).load(image).into(p_image);
               p_username.setText(uname);
               p_email.setText(email);
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });


       logout_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               mAuth.signOut();
               Intent toLogin = new Intent(container.getContext() , MainActivity.class);
               startActivity(toLogin);

           }
       });

       return view ;

    }

}
