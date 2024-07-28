package com.example.taamcms;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

public class RemoveScreenFragment extends LoaderFragment {
    public void removeItem(Object obj){
        // to be implemented
    }
    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.removefunctionscreen, container, false);
        // Todo: handle the button inputs
        Button generateYesButton = view.findViewById(R.id.button3);
        generateYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(v);
                loadFragment(new AdminHomeScreenFragment());
            }
        });
        Button generateCancelButton = view.findViewById(R.id.button2);
        generateCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new AdminHomeScreenFragment());
            }
        });
        return view;
    }
}