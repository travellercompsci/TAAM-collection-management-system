package com.example.taamcms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DisplayItemAdapter extends RecyclerView.Adapter<DisplayItemAdapter.DisplayItemViewHolder> {
    private List<DisplayItem> itemList;

    public DisplayItemAdapter(List<DisplayItem> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public DisplayItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_item, parent, false);
        return new DisplayItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DisplayItemViewHolder holder, int position) {
        DisplayItem item = itemList.get(position);
        holder.textViewTitle.setText(item.getTitle());
        holder.textLotNum.setText(item.getLot());
        holder.textCategory.setText(item.getCategory());
        holder.textPeriod.setText(item.getPeriod());
        holder.textDescription.setText(item.getDescription());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class DisplayItemViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textLotNum, textCategory, textPeriod, textDescription;

        public DisplayItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textLotNum = itemView.findViewById(R.id.textLotNum);
            textCategory = itemView.findViewById(R.id.textCategory);
            textPeriod = itemView.findViewById(R.id.textPeriod);
            textDescription = itemView.findViewById(R.id.textDescription);
        }
    }
}
