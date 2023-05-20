package com.example.textedd;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {

    private List<File> textFiles;
    private OnNoteListener mOnNoteListener;

    public NoteAdapter(List<File> textFiles, OnNoteListener onNoteListener) {
        this.textFiles = textFiles;
        this.mOnNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public NoteAdapter.NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteHolder(view, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.NoteHolder holder, int position) {
        File file = textFiles.get(position);
        holder.textView.setText(file.getName());
    }

    @Override
    public int getItemCount() {
        return textFiles.size();
    }


    public static class NoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textView;
        OnNoteListener mOnNoteListener;
        public NoteHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            textView = itemView.findViewById(R.id.note_name_text_view);
            mOnNoteListener = onNoteListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick: " + getAdapterPosition());
            mOnNoteListener.onNoteClick(getAdapterPosition());
        }
    }
    public interface OnNoteListener{
        void onNoteClick(int position);
    }
}
