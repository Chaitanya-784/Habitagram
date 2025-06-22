package com.example.habittrackerapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    private final List<FriendEntity> friendList;
    private final Set<FriendEntity> selectedFriends = new HashSet<>();

    public FriendAdapter(List<FriendEntity> friendList) {
        this.friendList = friendList;
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView tvFriendName;
        CheckBox cbSelectFriend;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFriendName = itemView.findViewById(R.id.tvFriendName);
            cbSelectFriend = itemView.findViewById(R.id.cbSelectFriend);
        }
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        FriendEntity friend = friendList.get(position);
        holder.tvFriendName.setText(friend.getFriendUsername());
        holder.cbSelectFriend.setOnCheckedChangeListener(null);
        holder.cbSelectFriend.setChecked(selectedFriends.contains(friend));
        holder.cbSelectFriend.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedFriends.add(friend);
            } else {
                selectedFriends.remove(friend);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public List<FriendEntity> getSelectedFriends() {
        return new ArrayList<>(selectedFriends);
    }
}
