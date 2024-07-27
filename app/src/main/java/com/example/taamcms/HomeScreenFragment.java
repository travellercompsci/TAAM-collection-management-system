package com.example.taamcms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private List<DisplayItem> itemList;

    private FirebaseDatabase db;
    private DatabaseReference itemsRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_screen_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        itemList = new ArrayList<>();
        itemAdapter = new DisplayItemAdapter(itemList);
        recyclerView.setAdapter(itemAdapter);

        db = FirebaseDatabase.getInstance("https://taam-collection-default-rtdb.firebaseio.com/");
        fetchItemsFromDatabase();

        Button buttonAdmin = view.findViewById(R.id.buttonAdmin);
        Button buttonView = view.findViewById(R.id.buttonView);
        Button buttonSearch = view.findViewById(R.id.buttonSearch);

        buttonAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Todo: add login verification.
                loadFragment(new AdminHomeScreenFragment());
            }
        });

        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<DisplayItem> viewItemList = new ArrayList<>();
                for (DisplayItem item : itemList) {
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

        return view;
    }

    private void fetchItemsFromDatabase() {
        itemsRef = db.getReference("Displays/");
        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DisplayItem item = snapshot.getValue(DisplayItem.class);
                    itemList.add(item);
                }
                itemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadStaticItems() {
        // Load static items from strings.xml or hardcoded values
        itemList.add(new DisplayItem(1, "test", "1", "Jade", "Ming", "this is d", "https://dictionary.cambridge.org/images/thumb/square_noun_002_35417.jpg?version=6.0.27"));
    }
}
