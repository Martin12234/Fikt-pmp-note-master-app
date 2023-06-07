package com.mlfikt.notemaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;

import android.view.KeyEvent;
import android.widget.Toast;

import butterknife.BindView;

public class MainActivity extends AppCompatActivity {



    FloatingActionButton addNoteBtn;
    RecyclerView recyclerView;
    ImageButton menuBtn;
    NoteAdapter noteAdapter;

    private static final int BACK_BUTTON_TIMEOUT = 2000; // Timeout in milliseconds
    private boolean backButtonPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addNoteBtn = findViewById(R.id.add_note_btn);
        recyclerView = findViewById(R.id.recyler_view);
        menuBtn = findViewById(R.id.menu_btn);

        addNoteBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, NoteDetailsActivity.class));
        });

        menuBtn.setOnClickListener(v -> showMenu());
        if (Utility.isInternetConnected(MainActivity.this)) {

            setupRecyclerView();


        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (backButtonPressedOnce) {
                super.onKeyDown(keyCode, event);
                return true;
            }

            this.backButtonPressedOnce = true;
            Toast.makeText(this, "Press back button again to exit", Toast.LENGTH_SHORT).show();

            new android.os.Handler().postDelayed(() -> backButtonPressedOnce = false, BACK_BUTTON_TIMEOUT);

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    void showMenu() {
        Resources resources = getResources();

        PopupMenu popupMenu = new PopupMenu(MainActivity.this, menuBtn);

        popupMenu.getMenu().add(resources.getString(R.string.logout));


        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals(resources.getString(R.string.logout))) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this, UserLoginActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });
    }

    void setupRecyclerView() {
        Query query = Utility.getCollectionReferenceForNotes().orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query,Note.class).build();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(options, this);
        recyclerView.setAdapter(noteAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Utility.isInternetConnected(MainActivity.this)) {
            noteAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Utility.isInternetConnected(MainActivity.this)) {
            noteAdapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Utility.isInternetConnected(MainActivity.this)) {
            noteAdapter.notifyDataSetChanged();
        }
    }
}