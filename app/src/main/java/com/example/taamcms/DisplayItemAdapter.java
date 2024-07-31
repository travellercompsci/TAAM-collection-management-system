package com.example.taamcms;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DisplayItemAdapter extends RecyclerView.Adapter<DisplayItemAdapter.DisplayItemViewHolder> {
    private static List<DisplayItemCheckBox> itemList;
    private static String mode;

    private StorageReference storeRef;

    public DisplayItemAdapter(List<DisplayItemCheckBox> itemList) {
        this.itemList = itemList;
        mode = "default";
    }

    public DisplayItemAdapter(List<DisplayItemCheckBox> itemList, String mode) {
        this.itemList = itemList;
        this.mode = mode;
    }

    @NonNull
    @Override
    public DisplayItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (mode.equals("view")) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_display_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_item, parent, false);
        }
        return new DisplayItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DisplayItemViewHolder holder, int position) {
        DisplayItemCheckBox item = itemList.get(position);
        holder.textViewTitle.setText(item.item.getTitle());
        holder.textLotNum.setText(item.item.getLot());
        holder.textCategory.setText(item.item.getCategory());
        holder.textPeriod.setText(item.item.getPeriod());
        holder.textDescription.setText(item.item.getDescription());
        if (!mode.equals("view")) {
            holder.checkBox.setChecked(item.isSelected());
        }

        storeRef = FirebaseStorage.getInstance().getReference(item.item.getImage());
        try {
            File localFile = File.createTempFile("tempfile", ".jpg");
            storeRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    holder.displayImage.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(holder.displayImage.getContext(), "Failed to retrieve image", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class DisplayItemViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textLotNum, textCategory, textPeriod, textDescription;
        ImageView displayImage;
        CheckBox checkBox;

        public DisplayItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textLotNum = itemView.findViewById(R.id.textLotNum);
            textCategory = itemView.findViewById(R.id.textCategory);
            textPeriod = itemView.findViewById(R.id.textPeriod);
            textDescription = itemView.findViewById(R.id.textDescription);
            displayImage = itemView.findViewById(R.id.displayImage);
            if (!mode.equals("view")) {
                checkBox = itemView.findViewById(R.id.checkBox);

                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean isChecked = ((CheckBox) view).isChecked();
                        if (isChecked) {
                            itemList.get(getAdapterPosition()).setSelected(true);
                        } else {
                            itemList.get(getAdapterPosition()).setSelected(false);
                        }
                    }
                });
            }
        }
    }
}
