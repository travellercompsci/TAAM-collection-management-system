package com.example.taamcms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeScreenFragment extends LoaderFragment {
    private RecyclerView recyclerView;
    private DisplayItemAdapter itemAdapter;
    private List<DisplayItemCheckBox> itemList;
    private DisplayItem searchParameters;

    private FirebaseDatabase db;
    private DatabaseReference itemsRef;

    private View view;

    /**
     * If the home screen instance is that of an admin.
     * This is static so all home screens are synchronized when entering the screen.
     */
    private static boolean isAdmin;

    public HomeScreenFragment(boolean isAdmin) {
        HomeScreenFragment.isAdmin = isAdmin;
    }

    public HomeScreenFragment(boolean isAdmin, String title, String lot, String category, String period, String description) {
        HomeScreenFragment.isAdmin = isAdmin;
        searchParameters = new DisplayItem("", title, lot, category, period, description, "");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_screen_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        itemList = new ArrayList<>();
        itemAdapter = new DisplayItemAdapter(itemList);
        recyclerView.setAdapter(itemAdapter);

        db = FirebaseDatabase.getInstance("https://taam-collection-default-rtdb.firebaseio.com/");
        fetchItemsFromDatabase();

        Button buttonLogOut = view.findViewById(R.id.buttonLogOut);
        Button buttonAdmin = view.findViewById(R.id.buttonAdmin);

        Button buttonView = view.findViewById(R.id.buttonView);
        Button buttonSearch = view.findViewById(R.id.buttonSearch);
        Button buttonAdd = view.findViewById(R.id.buttonAdd);
        Button buttonRemove = view.findViewById(R.id.buttonRemove);
        Button buttonReport = view.findViewById(R.id.buttonReport);

        view.findViewById(R.id.noResultsMsg).setVisibility(View.GONE);
        if (searchParameters != null) buttonSearch.setText("Show All");

        buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new HomeScreenFragment(false));
            }
        });

        buttonAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new LoginScreenView());
            }
        });

        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<DisplayItemCheckBox> viewItemList = new ArrayList<>();
                for (DisplayItemCheckBox item : itemList) {
                    if (item.isSelected()) {
                        viewItemList.add(item);
                    }
                }

                if (viewItemList.isEmpty()) {
                    Toast.makeText(getContext(), "Please select items before trying to view them.", Toast.LENGTH_SHORT).show();
                    return;
                }

                loadFragment(new ViewScreenFragment(viewItemList, isAdmin));
            }
        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchParameters == null) {
                    loadFragment(new SearchScreenFragment(isAdmin));
                } else {
                    loadFragment(new HomeScreenFragment(isAdmin));
                }
            }
        });
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new AddScreenActivity());
            }
        });

        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<DisplayItem> removeItemList = new ArrayList<>();
                for (DisplayItemCheckBox item : itemList) {
                    if (item.isSelected()) {
                        removeItemList.add(item.item);
                    }
                }

                if (removeItemList.isEmpty()) {
                    Toast.makeText(getContext(), "Please select items before trying to remove them.", Toast.LENGTH_SHORT).show();
                    return;
                }

                loadFragment(new RemoveScreenFragment(removeItemList));
            }
        });

        buttonReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ReportScreenFragment());
            }
        });

        // Show/hide admin content
        setAdminStatus(isAdmin);

        return view;
    }

    private void fetchItemsFromDatabase() {
        itemsRef = db.getReference("Displays/");
        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DisplayItemCheckBox item = new DisplayItemCheckBox(snapshot.getValue(DisplayItem.class));
                    if (searchParameters == null ||
                            (searchParameters.getLot().toLowerCase().isEmpty() || item.item.getLot().equals(searchParameters.getLot().toLowerCase())) &&
                            (searchParameters.getTitle().toLowerCase().isEmpty() || item.item.getTitle().contains(searchParameters.getTitle().toLowerCase())) &&
                            (searchParameters.getCategory().toLowerCase().isEmpty() || item.item.getCategory().contains(searchParameters.getCategory().toLowerCase())) &&
                            (searchParameters.getPeriod().toLowerCase().isEmpty() || item.item.getPeriod().contains(searchParameters.getPeriod().toLowerCase())) &&
                            (searchParameters.getDescription().toLowerCase().isEmpty() || item.item.getDescription().contains(searchParameters.getDescription().toLowerCase())))
                        itemList.add(item);
                }

                if (searchParameters != null && itemList.isEmpty()) {
                    view.findViewById(R.id.noResultsMsg).setVisibility(View.VISIBLE);
                }
                itemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Sets if this home screen should display the admin controls or that of the user.
     * @param isAdmin if this screen should be an admin screen.
     */
    public void setAdminStatus(boolean isAdmin) {
        HomeScreenFragment.isAdmin = isAdmin;

        // The screen hasn't loaded yet, or did not load properly.
        if (view == null) {
            return;
        }

        LinearLayout adminButtonContainer = view.findViewById(R.id.homeScreenAdminButtonContainer);
        Button adminButton = view.findViewById(R.id.buttonAdmin);
        Button logOutButton = view.findViewById(R.id.buttonLogOut);
        TextView adminLoggedInDisplay = view.findViewById(R.id.adminLogInTextDisplay);

        final int adminControlsVisibility;
        // Visibility for controls exclusive to non-admins.
        final int userControlsVisibility;

        if (isAdmin) {
            adminControlsVisibility = View.VISIBLE;
            userControlsVisibility = View.GONE;
        } else {
            adminControlsVisibility = View.GONE;
            userControlsVisibility = View.VISIBLE;
        }

        // Show/hide the user/admin buttons and containers
        adminButtonContainer.setVisibility(adminControlsVisibility);
        logOutButton.setVisibility(adminControlsVisibility);
        adminLoggedInDisplay.setVisibility(adminControlsVisibility);

        adminButton.setVisibility(userControlsVisibility);
    }

    /**
     * @return if this home screen is an admin instance.
     */
    public boolean getAdminStatus() {
        return isAdmin;
    }
}
