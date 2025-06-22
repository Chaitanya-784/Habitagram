package com.example.habittrackerapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.ViewHolder> {

    private List<UserEntity> userList;
    private OnFriendAddListener listener;

    public interface OnFriendAddListener {
        void onAddFriend(UserEntity user);
    }

    public UserSearchAdapter(List<UserEntity> userList, OnFriendAddListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    public void updateList(List<UserEntity> newList) {
        userList = newList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername;
        ImageView ivProfile, ivAdd;

        public ViewHolder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivProfile = itemView.findViewById(R.id.ivProfilePic);
            ivAdd = itemView.findViewById(R.id.ivAddFriend);
        }

        public void bind(UserEntity user) {
            tvUsername.setText(user.getUsername());
            // Load profile image if available
            ivAdd.setOnClickListener(v -> listener.onAddFriend(user));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_search, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(userList.get(position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}

