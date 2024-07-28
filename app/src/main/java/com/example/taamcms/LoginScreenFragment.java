package com.example.taamcms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

public class LoginScreenFragment extends LoaderFragment {
    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.adminscreen, container, false);
        // Todo: handle the button inputs
        Button generateLoginButton = view.findViewById(R.id.button);

        EditText editText = view.findViewById(R.id.editTextTextEmailAddress2);
        String userinput = editText.getText().toString();

        EditText editText2 = view.findViewById(R.id.editTextTextEmailAddress);
        String userinput2 = editText.getText().toString();

        generateLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loadFragment(new ScreenFragment());
            }
        });

        return view;
    }
}
