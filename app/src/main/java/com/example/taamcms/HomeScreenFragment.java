package com.example.taamcms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

    private FirebaseDatabase db;
    private DatabaseReference itemsRef;

    private View view;

    /**
     * If the home screen instance is that of an admin.
     * This is static so all home screens are synchronized when entering the screen.
     */
    private static boolean isAdmin;

    public HomeScreenFragment() {}

    public HomeScreenFragment(boolean isAdmin) {
        HomeScreenFragment.isAdmin = isAdmin;
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

        buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new HomeScreenFragment(false));
            }
        });

        buttonAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Todo: add login verification.
                loadFragment(new LoginScreenFragment());
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
                loadFragment(new ViewScreenFragment(viewItemList));
            }
        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Todo: change this to the search screen.
                loadFragment(new ReportScreenFragment());
            }
        });
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Todo: change this to the add screen.
                loadFragment(new AddCollectionActivity());
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Todo: change this to the add screen.
                loadFragment(new ReportScreenFragment());
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
                    itemList.add(item);
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
