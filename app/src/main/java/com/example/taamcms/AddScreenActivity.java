package com.example.taamcms;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddScreenActivity extends Fragment {
    private Button buttonAddItem;
    private EditText editTextLot, editTextName, editTextCategory, editTextPeriod, editTextDescription;
    private ImageView imageView_picture;

    private FirebaseDatabase db;
    private DatabaseReference itemsRef;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view1 = inflater.inflate(R.layout.add_screen, container, false);


        editTextName = view1.findViewById(R.id.editTextName);
        editTextLot = view1.findViewById(R.id.editTextLot);
        editTextCategory = view1.findViewById(R.id.editTextCategory);
        editTextPeriod = view1.findViewById(R.id.editTextPeriod);
        editTextDescription = view1.findViewById(R.id.editTextDescription);
        imageView_picture = view1.findViewById(R.id.imageView_picture);
        buttonAddItem = view1.findViewById(R.id.submit_button);

        db = FirebaseDatabase.getInstance("https://taam-collection-default-rtdb.firebaseio.com/");

        // Handle button click
        buttonAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

        return view1;

    }

    private void addItem() {
        String name = editTextName.getText().toString().trim();
        String lot = editTextLot.getText().toString().trim();
        String category = editTextCategory.getText().toString().trim();
        String period = editTextPeriod.getText().toString().trim();
        String image = imageView_picture.getTag().toString();
        String description = editTextDescription.getText().toString().trim();

        if (name.isEmpty() || lot.isEmpty() ||category.isEmpty() || period.isEmpty() || description.isEmpty() || image.isEmpty()) {
            Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        itemsRef = db.getReference("categories/" + category);
        String id = itemsRef.push().getKey();
        DisplayItem item = new DisplayItem(id, name, lot, category, period, description, image);

        itemsRef.child(id).setValue(item).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Item added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to add item", Toast.LENGTH_SHORT).show();
            }
        });
    }

}