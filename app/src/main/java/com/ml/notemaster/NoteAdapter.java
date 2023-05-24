package com.ml.notemaster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class NoteAdapter extends FirestoreRecyclerAdapter<Note, NoteAdapter.NoteViewHolder> {
    Context context;

    public NoteAdapter(@NonNull FirestoreRecyclerOptions<Note> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull Note note) {
        holder.titleTxtView.setText(note.getTitle());
        holder.contentTxtView.setText(note.getContent());
        holder.timestampTxtView.setText(Utility.timestampToString(note.getTimestamp()));
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_note_item, parent, false);
       return new NoteViewHolder(view);
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView titleTxtView, contentTxtView, timestampTxtView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxtView = itemView.findViewById(R.id.note_title_txt_view);
            contentTxtView = itemView.findViewById(R.id.note_content_txt_view);
            timestampTxtView = itemView.findViewById(R.id.note_timestamp_txt_view);
        }
    }
}
