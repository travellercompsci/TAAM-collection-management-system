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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_screen_fragment, container, false);

        lotNumberEditText = view.findViewById(R.id.lot_number);
        nameEditText = view.findViewById(R.id.name);
        categoryEditText = view.findViewById(R.id.category);
        periodEditText = view.findViewById(R.id.period);
        descriptionEditText = view.findViewById(R.id.description);

        if (getArguments() != null) {
            adminStatus = getArguments().getBoolean("adminStatus", false);
        }

        Button searchButton = view.findViewById(R.id.submit_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });

        return view;
    }

    private void performSearch() {
        // Extract search criteria from input fields
        String lotNumber = lotNumberEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();
        String category = categoryEditText.getText().toString().trim();
        String period = periodEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        // Perform search logic
        List<DisplayItemCheckBox> filteredItems = filterItems(lotNumber, name, category, period, description);

        if (filteredItems.isEmpty()) {
            Toast.makeText(getContext(), "No items found", Toast.LENGTH_SHORT).show();
        } else {
            // Pass the filtered results to HomeScreenFragment
            HomeScreenFragment homeScreenFragment = new HomeScreenFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("filteredItems", new ArrayList<>(filteredItems));
            bundle.putBoolean("adminStatus", adminStatus); // Pass the admin status to HomeScreenFragment
            homeScreenFragment.setArguments(bundle);

            // Load the HomeScreenFragment
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, homeScreenFragment)
                    .addToBackStack(null) // Add this transaction to the back stack
                    .commit();
        }
    }

    private List<DisplayItemCheckBox> filterItems(String lotNumber, String name, String category, String period, String description) {
        // Mock data, replace this with actual data fetching and filtering logic
        List<DisplayItemCheckBox> allItems = fetchAllItems();  // This should fetch all items from the database or other source
        List<DisplayItemCheckBox> filteredItems = new ArrayList<>();

        for (DisplayItemCheckBox item : allItems) {
            if ((lotNumber.isEmpty() || item.item.getLot().contains(lotNumber)) &&
                    (name.isEmpty() || item.item.getTitle().contains(name)) &&
                    (category.isEmpty() || item.item.getCategory().contains(category)) &&
                    (period.isEmpty() || item.item.getPeriod().contains(period)) &&
                    (description.isEmpty() || item.item.getDescription().contains(description))) {
                filteredItems.add(item);
            }
        }

        return filteredItems;
    }

    private List<DisplayItemCheckBox> fetchAllItems() {
        // Fetch all items from the database or other source
        return new ArrayList<>(); // Replace with actual data fetching logic
    }
}
