package com.example.taamcms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LoginScreenFragment extends LoaderFragment {
    EditText username;
    EditText password;
    private FirebaseDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_screen, container, false);
        // Todo: handle the button inputs
        db = FirebaseDatabase.getInstance("https://taam-collection-default-rtdb.firebaseio.com/");
        Button generateLoginButton = view.findViewById(R.id.button);

        username = view.findViewById(R.id.loginScreenUsernameInput);
        password = view.findViewById(R.id.loginScreenPasswordInput);

        generateLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidUsername() && checkValidPassword()){
                    validUser();
                }
            }
        });

        return view;
    }
    public Boolean checkValidUsername(){
        String user = username.getText().toString();
        if (user.isEmpty()){
            username.setError("Provide username");
            return false;
        } else {
            username.setError(null);
            return true;
        }
    }

    public Boolean checkValidPassword(){
        String pass = password.getText().toString();
        if (pass.isEmpty()){
            password.setError("Provide a valid password");
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }

    public void validUser(){
        String name = username.getText().toString().trim();
        String passwordUser = password.getText().toString().trim();
        DatabaseReference ref = db.getReference("Users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean found = false;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userDatabase = userSnapshot.child("username").getValue(String.class);
                    String passwordDatabase = userSnapshot.child("password").getValue(String.class);

                    if(Objects.equals(userDatabase, name)) {
                        found = true;
                        if (Objects.equals(passwordDatabase, passwordUser)) {
                            username.setError(null);
                            password.setError(null);
                            loadFragment(new HomeScreenFragment(true));
                        } else {
                            password.setError("Wrong Password");
                            password.requestFocus();
                        }
                        break;
                    }

                }
                if(!found){
                    username.setError("Not valid username");
                    username.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

