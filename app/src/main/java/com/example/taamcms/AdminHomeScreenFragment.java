package com.example.taamcms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

public class AdminHomeScreenFragment extends LoaderFragment {
    private RecyclerView recyclerView;
    private DisplayItemAdapter itemAdapter;
    private List<DisplayItem> itemList;

    private FirebaseDatabase db;
    private DatabaseReference itemsRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_home_screen_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        itemList = new ArrayList<>();
        itemAdapter = new DisplayItemAdapter(itemList);
        recyclerView.setAdapter(itemAdapter);

        db = FirebaseDatabase.getInstance("https://taam-collection-default-rtdb.firebaseio.com/");
        fetchItemsFromDatabase();

        Button buttonLogOut = view.findViewById(R.id.buttonBack);
        Button buttonView = view.findViewById(R.id.buttonView);
        Button buttonSearch = view.findViewById(R.id.buttonSearch);
        Button buttonAdd = view.findViewById(R.id.buttonAdd);
        Button buttonRemove = view.findViewById(R.id.buttonRemove);
        Button buttonReport = view.findViewById(R.id.buttonReport);

        buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new HomeScreenFragment());
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

            }
        });

        buttonReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
}
