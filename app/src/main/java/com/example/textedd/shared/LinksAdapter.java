package com.example.textedd.shared;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.textedd.R;

import java.util.List;

public class LinksAdapter extends RecyclerView.Adapter<LinksAdapter.Holder>{
    private List<String> links_list;
    protected final LinksAdapter.OnLinkListener mOnLinkListener;
    protected View view;

    public LinksAdapter(List<String> links_list, LinksAdapter.OnLinkListener mOnLinkListener) {
        this.links_list = links_list;
        this.mOnLinkListener = mOnLinkListener;
    }

    @NonNull
    @Override
    public LinksAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.link_item, parent, false);
        return new LinksAdapter.Holder(view, mOnLinkListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LinksAdapter.Holder holder, @SuppressLint("RecyclerView") int position) {
        String link= links_list.get(position);
        holder.textView.setText(link);
        holder.textView.setOnClickListener(v -> mOnLinkListener.onLinkClick(position));
        holder.textView.setOnLongClickListener(v -> {
            mOnLinkListener.onLinkLongClick(position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return links_list.size();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView textView;
        protected View view;
        protected LinksAdapter.OnLinkListener mOnLinkListener;
        public Holder(@NonNull View itemView, LinksAdapter.OnLinkListener onLinkListener) {
            super(itemView);
            textView = itemView.findViewById(R.id.link_item_tv);
            mOnLinkListener = onLinkListener;
            /*
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
             */
        }

        @Override
        public void onClick(View v) {
            mOnLinkListener.onLinkClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            return mOnLinkListener.onLinkLongClick(getAdapterPosition());
        }
    }
    public interface OnLinkListener{
        void onLinkClick(int position);
        boolean onLinkLongClick(int position);
    }
}


