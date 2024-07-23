package com.example.taamcms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

public class HomeScreenFragment extends LoaderFragment {
    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_screen_fragment, container, false);

        // Todo: do stuff

        Button buttonView = view.findViewById(R.id.buttonView);
        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Todo: change this to the view screen.
                loadFragment(new ReportScreenFragment());
            }
        });

        return view;
    }
}
