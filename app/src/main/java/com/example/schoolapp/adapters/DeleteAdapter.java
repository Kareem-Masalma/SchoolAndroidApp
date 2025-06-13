package com.example.schoolapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.R;
import com.example.schoolapp.models.Student;


import java.util.List;
import com.example.schoolapp.models.Subject;
import com.example.schoolapp.models.Teacher;


public class DeleteAdapter<T> extends RecyclerView.Adapter<DeleteAdapter.ViewHolder> {

    private final Context context;
    private final List<T> itemList;
    private final OnDeleteClickListener listener;

    public interface OnDeleteClickListener {
        void onDeleteClicked(int id);
    }

    public DeleteAdapter(Context context,List<T> itemList, OnDeleteClickListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_delete_card, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        T item = itemList.get(position);
        int id = -1;
        String name = "";

        if (item instanceof Student) {
            Student s = (Student) item;
            id = s.getUser_id();
            name = s.getFirstName() + " " + s.getLastName();
        } else if (item instanceof Teacher) {
            Teacher t = (Teacher) item;
            id = t.getUser_id();
            name = t.getFirstName() + " " + t.getLastName();
        } else if (item instanceof Subject) {
            Subject s = (Subject) item;
            id = s.getSubjectId();
            name = s.getTitle();
        }

        int finalId = id;
        holder.itemIdTV.setText(" " + finalId);
        holder.itemNameTV.setText(name);


        holder.itemView.setOnClickListener(v -> listener.onDeleteClicked(finalId));
        holder.deleteBT.setOnClickListener(v -> listener.onDeleteClicked(finalId));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemIdTV, itemNameTV;
        Button deleteBT;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemIdTV = itemView.findViewById(R.id.itemIdTV);
            itemNameTV = itemView.findViewById(R.id.itemNameTV);
            deleteBT = itemView.findViewById(R.id.deleteBT);

        }
    }

}

