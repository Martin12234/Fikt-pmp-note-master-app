package com.ml.notemaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;


public class NoteDetailsActivity extends AppCompatActivity {

    EditText titleEditTxt, contentEditTxt;
    ImageButton createNoteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        titleEditTxt = findViewById(R.id.notes_title_text);
        contentEditTxt = findViewById(R.id.notes_content_text);
        createNoteBtn = findViewById(R.id.create_note_btn);

        createNoteBtn.setOnClickListener(v-> createNote());
    }

    void createNote() {
        String noteTitle = titleEditTxt.getText().toString();
        String noteContent = contentEditTxt.getText().toString();

        if(noteTitle == null || noteTitle.isEmpty()) {
            titleEditTxt.setError("Title is required");
            return;
        }

        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());

        createNoteToFireBase(note);

    }

    void createNoteToFireBase(Note note) {
        DocumentReference documentReference;
        //create document for our note
        documentReference = Utility.getCollectionReferenceForNotes().document();
        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    //note is created
                    Utility.showToast(NoteDetailsActivity.this,"Note created successfully");
                    finish();
                }
                else {
                    //note is not created
                    Utility.showToast(NoteDetailsActivity.this,"Failed while creating note");
                }
            }
        });
    }
}