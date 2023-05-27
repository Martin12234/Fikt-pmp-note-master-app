package com.ml.notemaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;


public class NoteDetailsActivity extends AppCompatActivity {

    EditText titleEditTxt, contentEditTxt;
    ImageButton createNoteBtn;
    TextView  pageTitleTxtView, deleteNoteBtn;
    String title, content, docId;
    boolean isEditing = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        titleEditTxt = findViewById(R.id.notes_title_text);
        contentEditTxt = findViewById(R.id.notes_content_text);
        createNoteBtn = findViewById(R.id.create_note_btn);
        pageTitleTxtView = findViewById(R.id.page_title);
        deleteNoteBtn = findViewById(R.id.delete_note_btn);

        //receive data
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        if(docId != null && !docId.isEmpty()) {
            isEditing = true;
            pageTitleTxtView.setText("Edit note");
            deleteNoteBtn.setVisibility(View.VISIBLE);
        }

        titleEditTxt.setText(title);
        contentEditTxt.setText(content);


        createNoteBtn.setOnClickListener(v-> createNote());

        deleteNoteBtn.setOnClickListener(v-> deleteNoteFromFirebase());
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

        if(isEditing) {
            //update note
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        } else {
            //create document for our new note
            documentReference = Utility.getCollectionReferenceForNotes().document();
        }

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

    void deleteNoteFromFirebase() {
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForNotes().document(docId);


        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    //note is deleted
                    Utility.showToast(NoteDetailsActivity.this,"Note deleted successfully");
                    finish();
                }
                else {
                    //note is not created
                    Utility.showToast(NoteDetailsActivity.this,"Failed while deleting note");
                }
            }
        });
    }
}