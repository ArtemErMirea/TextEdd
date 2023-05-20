package com.example.textedd;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TagsOfNote_Adapter extends RecyclerView.Adapter<TagsOfNote_Adapter.Holder>{
    private List<String> tag_list;
    protected final OnTagListener mOnTagListener;
    protected View view;

    public TagsOfNote_Adapter(List<String> tag_list, OnTagListener onTagListener) {
        this.tag_list = tag_list;
        this.mOnTagListener = onTagListener;
    }

    @NonNull
    @Override
    public TagsOfNote_Adapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tag, parent, false);
        return new Holder(view, mOnTagListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TagsOfNote_Adapter.Holder holder, final int position) {
        String tag = tag_list.get(position);
        if (tag != null) holder.textView.setText(tag);
        holder.itemView.setOnClickListener(itemView -> mOnTagListener.onTagClick(position));
        holder.itemView.setOnLongClickListener(view -> {
            mOnTagListener.onTagLongClick(position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return tag_list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView textView;
        protected View view;
        protected OnTagListener mOnTagListener;
        public Holder(@NonNull View itemView, OnTagListener onTagListener) {
            super(itemView);
            textView = itemView.findViewById(R.id.tag_name_tv);
            mOnTagListener = onTagListener;
            //itemView.setOnClickListener(View.mOnTagListener);
        }
        /*
        @Override
        public void onClick(View v) {
            mOnTagListener.onTagClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            return mOnTagListener.onTagLongClick(getAdapterPosition());
        }*/
    }
    public interface OnTagListener{
        void onTagClick(int position);
        boolean onTagLongClick(int position);
    }
}
