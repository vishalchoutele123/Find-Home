package com.example.rent.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.rent.Property;
import com.example.rent.R;
import com.example.rent.SingleProperty;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyPropertyFragment extends Fragment {

    View view;

    private DatabaseReference mDatabase ;
    private FirebaseAuth mAuth ;
    private String user_id;
    private String option , city , post_key , child_key;
    public MyPropertyFragment() {
        // Required empty public constructor
    }

    private RecyclerView mPropertyList ;



    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_my_property, container, false);

        mAuth=FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("MyProperty").child(user_id);


        mPropertyList = view.findViewById(R.id.property_list);
        mPropertyList.setHasFixedSize(true);
        mPropertyList.setLayoutManager(new LinearLayoutManager(container.getContext()));

//        user_id = mAuth.getCurrentUser().getUid();

        DatabaseReference myProperty_db = mDatabase.child(user_id);



        FirebaseRecyclerAdapter<Property , PropertyViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Property, PropertyViewHolder>(

                Property.class ,
                R.layout.post_row ,
                PropertyViewHolder.class,
                mDatabase

        ) {
            @Override
            protected void populateViewHolder(PropertyViewHolder viewHolder, Property model, final int position) {

                viewHolder.setType(model.getType());
                viewHolder.setPrice(model.getPrice());
                viewHolder.setImage(container.getContext() , model.getImage());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        child_key = getRef(position).getKey();

                        mDatabase.child(child_key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                city = dataSnapshot.child("city").getValue().toString();
                                option = dataSnapshot.child("option").getValue().toString();
                                post_key = dataSnapshot.child("PropertyPostKey").getValue().toString();


                                Intent intent = new Intent(container.getContext() , SingleProperty.class);
                                intent.putExtra("option" , option);
                                intent.putExtra("city",city);
                                intent.putExtra("post_key" , post_key);
                                startActivity(intent);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                });

            }
        };

        mPropertyList.setAdapter(firebaseRecyclerAdapter);

        return view;
    }

    public static class PropertyViewHolder extends RecyclerView.ViewHolder {

        View mView ;

        public PropertyViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setType(String propertyType){

            TextView property_type = mView.findViewById(R.id.property_type);
            property_type.setText(propertyType);
        }

        public void setPrice(String propertyPrice){

            TextView property_price = mView.findViewById(R.id.property_price);
            property_price.setText("Rs. "+propertyPrice);
        }

        public void setImage(Context context, String propertyImage){
            ImageView property_image = mView.findViewById(R.id.property_image);
            Glide.with(context).load(propertyImage).into(property_image);
        }


    }

}

