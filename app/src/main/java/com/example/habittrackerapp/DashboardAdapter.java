package com.example.habittrackerapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.GroupedHabitViewHolder> {

    private List<HabitsByDate> groupedHabits;

    public DashboardAdapter(List<HabitsByDate> groupedHabits) {
        this.groupedHabits = groupedHabits;
    }

    @Override
    public GroupedHabitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grouped_habit, parent, false);
        return new GroupedHabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GroupedHabitViewHolder holder, int position) {
        HabitsByDate item = groupedHabits.get(position);
        holder.tvDate.setText("📅 " + item.getDate());

        StringBuilder sb = new StringBuilder();
        for (HabitEntity habit : item.getHabits()) {
            sb.append("• ")
                    .append(habit.getName())
                    .append(" at ")
                    .append(habit.getTime())
                    .append("\n");
        }

        holder.tvHabits.setText(sb.toString().trim()); // Trim to remove last \n
    }

    @Override
    public int getItemCount() {
        return groupedHabits.size();
    }

    public void updateList(List<HabitsByDate> newList) {
        groupedHabits = newList;
        notifyDataSetChanged();
    }

    static class GroupedHabitViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvHabits;

        GroupedHabitViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvHabits = itemView.findViewById(R.id.tvHabits);
        }
    }
}