package com.example.taamcms;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import java.util.List;

public class RemoveScreenFragment extends AdminHomeScreenFragment {
    List <DisplayItem> itemstoberemoved;

    private DatabaseReference itemsRef;
    private FirebaseDatabase db;

    public RemoveScreenFragment(List<DisplayItem> itemstoberemoved){
        this.itemstoberemoved = itemstoberemoved;
    }
    public void removeItem(int id) {
        itemsRef = db.getReference("Displays/");
        itemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean itemFound = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DisplayItem item = snapshot.getValue(DisplayItem.class);
                    if (item != null && item.getId() == id) {
                        snapshot.getRef().removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Item deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Failed to delete item", Toast.LENGTH_SHORT).show();
                            }
                        });
                        itemFound = true;
                        break;
                    }
                }
                if (!itemFound) {
                    Toast.makeText(getContext(), "Item not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        db = FirebaseDatabase.getInstance("https://taam-collection-default-rtdb.firebaseio.com/");

        View view = inflater.inflate(R.layout.removefunctionscreen, container, false);
        Button generateYesButton = view.findViewById(R.id.button3);
        Button generateCancelButton = view.findViewById(R.id.button2);
        generateYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (DisplayItem item : itemstoberemoved) {
                    removeItem(item.getId());
                }
                loadFragment(new AdminHomeScreenFragment());
            }
        });
        generateCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new AdminHomeScreenFragment());
            }
        });
        return view;
    }
}