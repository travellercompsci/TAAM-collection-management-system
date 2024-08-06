package com.example.taamcms;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class AddScreenActivity extends LoaderFragment {
    private Button buttonAddItem;
    private EditText editTextLot, editTextName, editTextCategory, editTextPeriod, editTextDescription;
    private ImageView imageViewPicture;
    public Uri imageUri;

    private String currentImagePath;
    private FirebaseDatabase db;
    private DatabaseReference itemsRef;
    private FirebaseStorage storage;
    private StorageReference storeRef;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view1 = inflater.inflate(R.layout.add_screen, container, false);


        editTextName = view1.findViewById(R.id.editTextName);
        editTextLot = view1.findViewById(R.id.editTextLot);
        editTextCategory = view1.findViewById(R.id.editTextCategory);
        editTextPeriod = view1.findViewById(R.id.editTextPeriod);
        editTextDescription = view1.findViewById(R.id.editTextDescription);
        imageViewPicture = view1.findViewById(R.id.imageView_picture);
        buttonAddItem = view1.findViewById(R.id.submit);

        db = FirebaseDatabase.getInstance("https://taam-collection-default-rtdb.firebaseio.com/");

        storage = FirebaseStorage.getInstance();
        storeRef = storage.getReference();

        imageViewPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePic();
            }

        });

        // Handle button click
        buttonAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addItem()) {
                    loadFragment(new HomeScreenFragment(true));
                }
            }
        });

        return view1;

    }

    private void choosePic() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri = data.getData();
            imageViewPicture.setImageURI(imageUri);
            uploadPicture();
        }
    }

    private void uploadPicture() {
        final String randKey = UUID.randomUUID().toString();
        currentImagePath = "images/" + randKey;
        StorageReference mountainsRef = storeRef.child(currentImagePath);

        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setTitle("Uploading Image...");
        pd.show();

        mountainsRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Snackbar.make(getView(), "Image Uploaded.", Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        pd.dismiss();
                        Toast.makeText(getContext(), "Failed To Upload", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progPercent = (100.00 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        pd.setMessage("Percentage: " + (int) progPercent + "%");
                    }
                });
    }

    /**
     * @return If all the fields are filled correctly.
     */
    private boolean addItem() {
        String name = editTextName.getText().toString().trim();
        String lot = editTextLot.getText().toString().trim();
        String category = editTextCategory.getText().toString().trim();
        String period = editTextPeriod.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String image = currentImagePath;

        Log.d("AddItem", "Name: " + name);
        Log.d("AddItem", "Lot: " + lot);
        Log.d("AddItem", "Category: " + category);
        Log.d("AddItem", "Period: " + period);
        Log.d("AddItem", "Description: " + description);
        Log.d("AddItem", "Image: " + image);

        boolean containsError = false;
        if (name.isEmpty()) {
            editTextName.setError(getString(R.string.no_name_error));
            editTextName.requestFocus();
            containsError = true;
        } else {
            editTextName.setError(null);
        }

        if (lot.isEmpty()) {
            editTextLot.setError(getString(R.string.no_lot_number_error));
            editTextLot.requestFocus();
            containsError = true;
        } else {
            editTextLot.setError(null);
        }

        if (category.isEmpty()) {
            editTextCategory.setError(getString(R.string.no_category_error));
            editTextCategory.requestFocus();
            containsError = true;
        } else {
            editTextCategory.setError(null);
        }

        if (period.isEmpty()) {
            editTextPeriod.setError(getString(R.string.no_period_error));
            editTextPeriod.requestFocus();
            containsError = true;
        } else {
            editTextPeriod.setError(null);
        }

        if (description.isEmpty()) {
            editTextDescription.setError(getString(R.string.no_description_error));
            editTextDescription.requestFocus();
            containsError = true;
        } else {
            editTextDescription.setError(null);
        }

        if (image == null) {
            Toast.makeText(getContext(), "Please upload an image.", Toast.LENGTH_SHORT).show();
            containsError = true;
        }

        if (containsError) {
            return false;
        }

        itemsRef = db.getReference("Displays/");
        String id = itemsRef.push().getKey();
        DisplayItem item = new DisplayItem(id, name, lot, category, period, description, image);

        itemsRef.child(id).setValue(item).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Item added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to add item", Toast.LENGTH_SHORT).show();
            }
        });

        return true;
    }

}