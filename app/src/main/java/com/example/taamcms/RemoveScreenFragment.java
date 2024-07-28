package com.example.taamcms;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;
import java.util.List;

public class RemoveScreenFragment extends AdminHomeScreenFragment {
    List <DisplayItem> itemstoberemoved;

    public RemoveScreenFragment(List<DisplayItem> itemstoberemoved){
        this.itemstoberemoved = itemstoberemoved;
    }
    public void removeItems(){
        for(DisplayItem item : itemstoberemoved){
            if(item.isSelected()){
                itemstoberemoved.remove(item);
            }
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.removefunctionscreen, container, false);
        Button generateYesButton = view.findViewById(R.id.button3);
        Button generateCancelButton = view.findViewById(R.id.button2);
        generateYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItems();
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