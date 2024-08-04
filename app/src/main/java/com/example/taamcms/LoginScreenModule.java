package com.example.taamcms;
import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginScreenModule {
    private static final String USER_DATABASE_PATH = "Users";

    private final FirebaseDatabase db;

    LoginScreenModule() {
        db = FirebaseDatabase.getInstance("https://taam-collection-default-rtdb.firebaseio.com/");
    }

    /**
     * Determines if the username/password is valid.
     * @param callback see `CredentialValidationCallback` for details on what methods will be invoked based on different situations.
     */
    public void isValidCredentials(String username, String password, CredentialValidationCallback callback) {
        DatabaseReference ref = db.getReference(USER_DATABASE_PATH);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean found = false;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userDatabase = userSnapshot.child("username").getValue(String.class);
                    String passwordDatabase = userSnapshot.child("password").getValue(String.class);

                    if(Objects.equals(userDatabase, username)) {
                        found = true;
                        if (Objects.equals(passwordDatabase, password)) {
                            callback.isSuccessful();
                            return;
                        } else {
                            callback.invalidPassword();
                        }
                        break;
                    }

                }
                if(!found){
                    callback.invalidUsername();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.databaseError(databaseError.toString());
            }
        });
    }
}
