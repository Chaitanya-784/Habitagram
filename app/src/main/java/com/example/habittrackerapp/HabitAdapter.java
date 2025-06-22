package com.example.habittrackerapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {

    private List<HabitEntity> habitList;
    private OnHabitCheckedListener habitCheckedListener;
    private OnHabitClickListener habitClickListener;
    private OnHabitLongClickListener habitLongClickListener;
    private HabitDatabase db;
    private String username;

    public interface OnHabitCheckedListener {
        void onHabitChecked(HabitEntity habit, boolean isChecked);
    }

    public interface OnHabitClickListener {
        void onHabitClick(HabitEntity habit);
    }

    public interface OnHabitLongClickListener {
        void onHabitLongClick(HabitEntity habit);
    }

    public HabitAdapter(List<HabitEntity> habitList, OnHabitCheckedListener checkedListener, OnHabitLongClickListener longClickListener) {
        this.habitList = habitList != null ? habitList : new ArrayList<>();
        this.habitCheckedListener = checkedListener;
        this.habitLongClickListener = longClickListener;
    }

    public void setDatabase(HabitDatabase db, String username) {
        this.db = db;
        this.username = username;
    }

    public void setOnItemClickListener(OnHabitClickListener listener) {
        this.habitClickListener = listener;
    }

    public void updateList(List<HabitEntity> newList) {
        this.habitList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_habit, parent, false);
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        HabitEntity habit = habitList.get(position);
        holder.tvName.setText(habit.getName());
        holder.tvTimeXP.setText(habit.getTime());

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(habit.isCompleted());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            habit.setCompleted(isChecked);
            habitCheckedListener.onHabitChecked(habit, isChecked);

            if (isChecked && db != null && username != null && habit.getDate() != null) {
                Executors.newSingleThreadExecutor().execute(() -> {
                    boolean alreadyCompleted = db.habitCompletionDao()
                            .isHabitCompletedOnDate(habit.getId(), habit.getDate(), username);

                    if (!alreadyCompleted) {
                        HabitCompletionEntity completion = new HabitCompletionEntity(habit.getId(), habit.getDate(), username);
                        db.habitCompletionDao().insertCompletion(completion);
                    }
                });
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (habitClickListener != null) {
                habitClickListener.onHabitClick(habit);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (habitLongClickListener != null) {
                habitLongClickListener.onHabitLongClick(habit);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    public static class HabitViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTimeXP;
        CheckBox checkBox;

        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvHabitName);
            tvTimeXP = itemView.findViewById(R.id.tvHabitTime);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
}
