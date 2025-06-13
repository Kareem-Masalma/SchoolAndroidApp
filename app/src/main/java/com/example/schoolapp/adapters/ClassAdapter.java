package com.example.schoolapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.R;
import com.example.schoolapp.models.SchoolClass;

import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {

    private final Context context;
    private final List<SchoolClass> classList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(SchoolClass selectedClass);
    }

    public ClassAdapter(Context context, List<SchoolClass> classList, OnItemClickListener listener) {
        this.context = context;
        this.classList = classList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        return new ClassViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        SchoolClass model = classList.get(position);
        holder.tvName.setText(model.getClassName());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(model));
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
        }
    }
}
