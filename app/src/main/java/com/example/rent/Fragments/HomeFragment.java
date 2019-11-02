package com.example.rent.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.rent.R;
import com.example.rent.SearchResult;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends android.support.v4.app.Fragment{


    private Spinner optionSpinner , citySpinner ;
    private Button btnSearch;

    private String option , city ;
    View view;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        optionSpinner = view.findViewById(R.id.option_spinner);
        citySpinner = view.findViewById(R.id.city_spinner);
        btnSearch = view.findViewById(R.id.btn_search);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                option = String.valueOf(optionSpinner.getSelectedItem());
                city = String.valueOf(citySpinner.getSelectedItem());

                Intent i = new Intent(container.getContext(), SearchResult.class);
                i.putExtra("option",option);
                i.putExtra("city",city);

                startActivity(i);

            }
        });


        return view;
    }

}
