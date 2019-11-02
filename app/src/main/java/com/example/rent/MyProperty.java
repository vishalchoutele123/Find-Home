package com.example.rent;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.rent.Fragments.MyPropertyFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyProperty extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth ;
    private String user_id;

    private RecyclerView mPropertyList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_property);


        mPropertyList = findViewById(R.id.property_list);
        mPropertyList.setHasFixedSize(true);
        mPropertyList.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();

        user_id = mAuth.getCurrentUser().getUid();

        Log.d("vishal" , user_id);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("MyProperty");


//        FirebaseRecyclerAdapter<Property , MyProperty.PropertyViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Property, MyProperty.PropertyViewHolder>(
//
//                Property.class ,
//                R.layout.post_row ,
//                MyProperty.PropertyViewHolder.class,
//                mDatabase.child(user_id)
//
//        ) {
//            @Override
//            protected void populateViewHolder(MyProperty.PropertyViewHolder viewHolder, Property model, int position) {
//
//                viewHolder.setPropertyType(model.getPropertyType());
//                viewHolder.setPropertyPrice(model.getPropertyPrice());
//            }
//        };
//
//
//        mPropertyList.setAdapter(firebaseRecyclerAdapter);

    }


    @Override
    protected void onStart() {
        super.onStart();

       FirebaseRecyclerAdapter<Property , PropertyViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Property, PropertyViewHolder>(

               Property.class,
               R.layout.post_row,
               PropertyViewHolder.class,
               mDatabase


       ) {
           @Override
           protected void populateViewHolder(PropertyViewHolder viewHolder, Property model, int position) {

               viewHolder.setType(model.getType());
               viewHolder.setPrice(model.getPrice());

           }
       };

       mPropertyList.setAdapter(firebaseRecyclerAdapter);

    }


    public static class PropertyViewHolder extends RecyclerView.ViewHolder {

        View mView ;

        public PropertyViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setType(String propertyType){

            TextView property_type = mView.findViewById(R.id.property_type);
            property_type.setText("Rs. "+propertyType);
        }

        public void setPrice(String propertyPrice){

            TextView property_price = mView.findViewById(R.id.property_price);
            property_price.setText(propertyPrice);
        }
    }
}


