package com.example.taamcms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

public class LoginScreenView extends LoaderFragment {
    EditText username;
    EditText password;
    private LoginScreenPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_screen, container, false);
        // Todo: handle the button inputs
        Button generateLoginButton = view.findViewById(R.id.button);

        username = view.findViewById(R.id.loginScreenUsernameInput);
        password = view.findViewById(R.id.loginScreenPasswordInput);

        generateLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.handleLogin();
            }
        });

        return view;
    }
    public String getUsername(){
        return username.getText().toString();
    }

    public String getPassword(){
        return password.getText().toString();
    }

    public void showUsernameErr(String string){
        username.setError(string);
        username.requestFocus();
    }

    public void showPasswordErr(String string){
        password.setError(string);
        password.requestFocus();
    }


    public void loginSuccess(){
        loadFragment(new HomeScreenFragment(true));
    }

    public void showGeneralErr(String string){
        Toast.makeText(getContext(), "Database error: " + string, Toast.LENGTH_SHORT).show();
    }

}

