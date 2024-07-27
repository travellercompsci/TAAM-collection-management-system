package com.example.taamcms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ViewScreenFragment extends LoaderFragment {
    private RecyclerView recyclerView;
    private DisplayItemAdapter itemAdapter;
    private List<DisplayItem> itemList;

    public ViewScreenFragment(List<DisplayItem> itemList) {
        this.itemList = itemList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_screen_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        itemAdapter = new DisplayItemAdapter(itemList);
        recyclerView.setAdapter(itemAdapter);

        return view;
    }
}
