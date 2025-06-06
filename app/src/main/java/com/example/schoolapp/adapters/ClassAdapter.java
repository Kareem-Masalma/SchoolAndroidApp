package com.example.schoolapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.models.SchoolClass;

import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {

    private final List<SchoolClass> ClassList;
    private final OnClassClickListener listener;

    public ClassAdapter(List<SchoolClass> ClassList, OnClassClickListener listener) {
        this.ClassList = ClassList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        SchoolClass c = ClassList.get(position);
        holder.nameText.setText(c.getClassName());

        holder.itemView.setOnClickListener(v -> listener.onClassClick(c));
    }

    @Override
    public int getItemCount() {
        return ClassList.size();
    }

    static class ClassViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(android.R.id.text1);
        }
    }

    public interface OnClassClickListener {
        void onClassClick(SchoolClass c);
    }
}
