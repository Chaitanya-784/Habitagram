package com.example.habittrackerapp;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PendingRequestsAdapter extends RecyclerView.Adapter<PendingRequestsAdapter.ViewHolder> {

    public interface OnActionClickListener {
        void onAccept(FriendEntity friend);
        void onReject(FriendEntity friend);
    }

    private final List<FriendEntity> pendingList;
    private final OnActionClickListener listener;

    public PendingRequestsAdapter(List<FriendEntity> pendingList, OnActionClickListener listener) {
        this.pendingList = pendingList;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername;
        Button btnAccept, btnReject;

        public ViewHolder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
        }

        public void bind(FriendEntity friend, OnActionClickListener listener) {
            tvUsername.setText(friend.getMyUsername()); // person who sent request
            btnAccept.setOnClickListener(v -> listener.onAccept(friend));
            btnReject.setOnClickListener(v -> listener.onReject(friend));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pending_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(pendingList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return pendingList.size();
    }
}