package com.example.rent.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.rent.Property;
import com.example.rent.R;
import com.example.rent.SearchResult;
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
public class ShortListFragment extends android.support.v4.app.Fragment {

    View view ;

    private String option , city ;
    private DatabaseReference mDatabase  ;
    private FirebaseAuth mAuth ;

    private RecyclerView mShortList ;

    private DatabaseReference mShortlist_db ;

    String user_id , post_key;

    String propertyImage , propertyType , propertyPrice ;

    TextView property_type;


    public ShortListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.fragment_short_list, container, false);

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
//        mDatabase = FirebaseDatabase.getInstance().getReference().child("Property");



        mShortlist_db = FirebaseDatabase.getInstance().getReference().child("Like");


        mShortList = view.findViewById(R.id.short_list);
        mShortList.setHasFixedSize(true);
        mShortList.setLayoutManager(new LinearLayoutManager(container.getContext()));


        FirebaseRecyclerAdapter<Property, ShortListFragment.ShortListViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Property, ShortListFragment.ShortListViewHolder>(

                Property.class ,
                R.layout.post_row ,
                ShortListFragment.ShortListViewHolder.class,
                mShortlist_db.child(user_id)
        ) {
            @Override
            protected void populateViewHolder(ShortListFragment.ShortListViewHolder viewHolder, Property model, final int position) {


                viewHolder.setType(model.getType());
                viewHolder.setPrice(model.getPrice());
                viewHolder.setImage(container.getContext() , model.getImage());


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        post_key = getRef(position).getKey();



                        DatabaseReference intentRef = FirebaseDatabase.getInstance().getReference().child("Like").child(user_id).child(post_key);

                        intentRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                city = dataSnapshot.child("city").getValue().toString();
                                option = dataSnapshot.child("option").getValue().toString();

                                Intent intent = new Intent(container.getContext() , SingleProperty.class);

                                intent.putExtra("post_key",post_key);
                                intent.putExtra("city",city);
                                intent.putExtra("option",option);
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


        mShortList.setAdapter(firebaseRecyclerAdapter);

        return view ;
    }

    public static class ShortListViewHolder extends RecyclerView.ViewHolder {

        View mView ;

        public ShortListViewHolder(@NonNull View itemView) {
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
