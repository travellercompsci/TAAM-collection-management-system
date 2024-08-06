package com.example.taamcms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class SearchScreenFragment extends LoaderFragment {
    private EditText lotNumberEditText;
    private EditText nameEditText;
    private EditText categoryEditText;
    private EditText periodEditText;
    private EditText descriptionEditText;
    private boolean adminStatus;


    public SearchScreenFragment(boolean adminStatus) {
        this.adminStatus = adminStatus;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_screen_fragment, container, false);

        lotNumberEditText = view.findViewById(R.id.lot_number);
        nameEditText = view.findViewById(R.id.name);
        categoryEditText = view.findViewById(R.id.category);
        periodEditText = view.findViewById(R.id.period);
        descriptionEditText = view.findViewById(R.id.description);

        Button searchButton = view.findViewById(R.id.submit_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Extract search criteria from input fields
                String lotNumber = lotNumberEditText.getText().toString().trim();
                String name = nameEditText.getText().toString().trim();
                String category = categoryEditText.getText().toString().trim();
                String period = periodEditText.getText().toString().trim();
                String description = descriptionEditText.getText().toString().trim();

                // Load HomeScreenFragment
                loadFragment(new HomeScreenFragment(adminStatus, name, lotNumber, category, period, description));
            }
        });

        return view;
    }
}
