package com.example.taamcms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

public class ReportScreenFragment extends LoaderFragment {
    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.report_screen_fragment, container, false);

        // Todo: handle the button inputs


        return view;
    }
}
