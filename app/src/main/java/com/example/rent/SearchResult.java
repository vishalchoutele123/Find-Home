package com.example.rent;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.rent.Fragments.MyPropertyFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SearchResult extends AppCompatActivity {

    private String option , city ;
    private DatabaseReference mDatabase  ;
    private FirebaseAuth mAuth ;

    private RecyclerView mSearchList ;

    private DatabaseReference mShortlist_db ;

    String user_id , post_key;

    String propertyImage , propertyType , propertyPrice ;

    TextView property_type;


    private Boolean like = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        option = getIntent().getStringExtra("option");
        city = getIntent().getStringExtra("city");
        if(option.equals("Buy")){
            option = "Sell";
        }

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Property");

        mShortlist_db = FirebaseDatabase.getInstance().getReference().child("Likes").child(user_id);


        mSearchList = findViewById(R.id.search_list);
        mSearchList.setHasFixedSize(true);
        mSearchList.setLayoutManager(new LinearLayoutManager(this));



        FirebaseRecyclerAdapter<Property , SearchViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Property, SearchViewHolder>(

                Property.class ,
                R.layout.post_row ,
                SearchViewHolder.class,
                mDatabase.child(option).child(city)

        ) {
            @Override
            protected void populateViewHolder(SearchViewHolder viewHolder, Property model, final int position) {



                viewHolder.setType(model.getType());
                viewHolder.setPrice(model.getPrice());
                viewHolder.setImage(getApplicationContext(),model.getImage());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        post_key = getRef(position).getKey();

                        Intent intent = new Intent(SearchResult.this , SingleProperty.class);
                        intent.putExtra("post_key",post_key);
                        intent.putExtra("city",city);
                        intent.putExtra("option",option);
                        startActivity(intent);

                    }
                });

            }
        };


        mSearchList.setAdapter(firebaseRecyclerAdapter);



    }


    public static class SearchViewHolder extends RecyclerView.ViewHolder {

        View mView ;

        ImageButton mLike_btn ;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

//            mLike_btn = mView.findViewById(R.id.like_btn);
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
