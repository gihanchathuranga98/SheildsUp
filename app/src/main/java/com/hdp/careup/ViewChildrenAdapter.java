package com.hdp.careup;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ViewChildrenAdapter extends RecyclerView.Adapter<ViewChildrenAdapter.ChildViewHolder> {

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ChildViewHolder extends RecyclerView.ViewHolder {
        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
