package com.example.beanbrew.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.beanbrew.R;
import com.example.beanbrew.models.Coffee;
import java.util.List;

public class CoffeeAdapter extends RecyclerView.Adapter<CoffeeAdapter.CoffeeViewHolder> {

    private List<Coffee> coffeeList;
    private OnItemClickListener listener;
    private boolean isDealsMode = false; // New flag for deals

    public interface OnItemClickListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Set deals mode (hides edit/delete buttons)
    public void setDealsMode(boolean isDealsMode) {
        this.isDealsMode = isDealsMode;
    }

    public static class CoffeeViewHolder extends RecyclerView.ViewHolder {
        public TextView coffeeName;
        public TextView coffeePrice;
        public TextView coffeeCategory;
        public TextView coffeeDescription;
        public ImageView editIcon;
        public ImageView deleteIcon;

        public CoffeeViewHolder(@NonNull View itemView, final OnItemClickListener listener, boolean isDealsMode) {
            super(itemView);
            coffeeName = itemView.findViewById(R.id.coffeeName);
            coffeePrice = itemView.findViewById(R.id.coffeePrice);
            coffeeCategory = itemView.findViewById(R.id.coffeeCategory);
            coffeeDescription = itemView.findViewById(R.id.coffeeDescription);
            editIcon = itemView.findViewById(R.id.coffeeEdit);
            deleteIcon = itemView.findViewById(R.id.coffeeDelete);

            // Hide edit/delete for deals
            if (isDealsMode) {
                editIcon.setVisibility(View.GONE);
                deleteIcon.setVisibility(View.GONE);
            } else {
                editIcon.setOnClickListener(v -> {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onEditClick(position);
                        }
                    }
                });

                deleteIcon.setOnClickListener(v -> {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                });
            }
        }
    }

    public CoffeeAdapter(List<Coffee> coffeeList) {
        this.coffeeList = coffeeList;
    }

    @NonNull
    @Override
    public CoffeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cofee_item, parent, false);
        return new CoffeeViewHolder(view, listener, isDealsMode);
    }

    @Override
    public void onBindViewHolder(@NonNull CoffeeViewHolder holder, int position) {
        Coffee coffee = coffeeList.get(position);
        holder.coffeeName.setText(coffee.getName());
        holder.coffeePrice.setText(String.format("$%.2f", coffee.getPrice()));
        holder.coffeeCategory.setText(coffee.getCategory());
        holder.coffeeDescription.setText(coffee.getDescription());

        // Different styling for deals
        if (isDealsMode) {
            holder.coffeeName.setTextColor(0xFF4CAF50); // Green for deals
            if (coffee.getName().contains("✨")) {
                holder.coffeeName.setTextColor(0xFFFF9800); // Orange for special deals
            }
        } else {
            holder.coffeeName.setTextColor(0xFFFF69B4); // Pink for user coffee
        }
    }

    @Override
    public int getItemCount() {
        return coffeeList.size();
    }

    public void setCoffeeList(List<Coffee> coffeeList) {
        this.coffeeList = coffeeList;
        notifyDataSetChanged();
    }

    public List<Coffee> getCoffeeList() {
        return coffeeList;
    }
}