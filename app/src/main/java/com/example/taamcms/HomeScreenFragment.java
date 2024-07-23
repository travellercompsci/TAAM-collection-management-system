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
import java.util.ArrayList;
import java.util.List;

public class HomeScreenFragment extends LoaderFragment {
    private RecyclerView recyclerView;
    private DisplayItemAdapter itemAdapter;
    private List<DisplayItem> itemList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_screen_fragment, container, false);

        Button buttonAdmin = view.findViewById(R.id.buttonAdmin);
        Button buttonView = view.findViewById(R.id.buttonView);
        Button buttonSearch = view.findViewById(R.id.buttonSearch);

        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Todo: change this to the view screen.
                loadFragment(new ReportScreenFragment());
            }
        });

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        itemList = new ArrayList<>();
        loadStaticItems();
        itemAdapter = new DisplayItemAdapter(itemList);
        recyclerView.setAdapter(itemAdapter);

        return view;
    }

    private void loadStaticItems() {
        // Load static items from strings.xml or hardcoded values
        itemList.add(new DisplayItem(1, "test", "1", "Jade", "Ming", "this is d", "https://dictionary.cambridge.org/images/thumb/square_noun_002_35417.jpg?version=6.0.27"));
    }
}
