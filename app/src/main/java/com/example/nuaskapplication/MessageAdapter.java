package com.example.nuaskapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatitem, parent, false);
        return new MyViewHolder(chatView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Message message = messageList.get(position);

        if (message.getSentBy().equals(Message.SENT_BY_ME)) {
            holder.leftChatView.setVisibility(View.GONE);
            holder.rightChatView.setVisibility(View.VISIBLE);
            holder.rightTextView.setText(message.getMessage());
        } else {
            holder.rightChatView.setVisibility(View.GONE);
            holder.leftChatView.setVisibility(View.VISIBLE);

            if (message.getImageResId() != 0) {
                holder.leftTextView.setVisibility(View.GONE);
                holder.leftImageView.setVisibility(View.VISIBLE);
                holder.leftImageView.setImageResource(message.getImageResId());
            } else {
                holder.leftImageView.setVisibility(View.GONE);
                holder.leftTextView.setVisibility(View.VISIBLE);
                holder.leftTextView.setText(message.getMessage());
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftChatView, rightChatView;
        TextView leftTextView, rightTextView;
        ImageView leftImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatView = itemView.findViewById(R.id.left_chat_layout);
            rightChatView = itemView.findViewById(R.id.right_chat_layout);
            leftTextView = itemView.findViewById(R.id.left_chat_text_view);
            rightTextView = itemView.findViewById(R.id.right_chat_text_view);
            leftImageView = itemView.findViewById(R.id.left_chat_image_view);
        }
    }
}