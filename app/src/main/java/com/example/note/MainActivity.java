package com.example.note;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.note.Adapters.NotesListAdapters;
import com.example.note.Database.RoomDB;
import com.example.note.Models.Notes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener
{
    RecyclerView recyclerView;
    NotesListAdapters notesListAdapters;
    List<Notes> notes = new ArrayList<>();
    RoomDB database;
    FloatingActionButton fab_add;
    SearchView searchView;
    Notes selectedNote;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycle_home);
        fab_add = findViewById(R.id.fab_add);
        searchView = findViewById(R.id.search_home);

        database = RoomDB.getInstance(this);
        notes = database.mainDAO().getAll();

        updateRecycler(notes);

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, NotesTableActivity.class);
                startActivityForResult(i,101);

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    private void filter(String newText) {
        List<Notes> filtered_list = new ArrayList<>();
        for (Notes singleNote : notes) {
            if (singleNote.getTitle().toLowerCase().contains(newText.toLowerCase())
                    || singleNote.getNotes().toLowerCase().contains(newText.toLowerCase())) {
                filtered_list.add(singleNote);
            }
        }
        notesListAdapters.filterList(filtered_list);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==101) {
            if (resultCode == Activity.RESULT_OK) {
                Notes new_notes = (Notes) data.getSerializableExtra("note");
                database.mainDAO().insert(new_notes);
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                notesListAdapters.notifyDataSetChanged();
            }
        } else if (requestCode== 102) {
            if(resultCode == Activity.RESULT_OK)
            {
                Notes new_notes = (Notes) data.getSerializableExtra("note");
                database.mainDAO().update(new_notes.getID(), new_notes.getTitle(), new_notes.getNotes());
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                notesListAdapters.notifyDataSetChanged();
            }

        }
    }

    private void updateRecycler(List<Notes> notes) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        notesListAdapters = new NotesListAdapters(MainActivity.this, notes, notesClickListener);
        recyclerView.setAdapter(notesListAdapters);
    }
     private final  NotesClickListener notesClickListener = new NotesClickListener() {
         @Override
         public void onClick(Notes notes) {
             Intent i =new Intent(MainActivity.this, NotesTableActivity.class);
             i.putExtra("Old_Note", notes);
             startActivityForResult(i, 102);

         }

         @Override
         public void onLongClick(Notes notes, CardView cardView) {
             selectedNote = new Notes();
             selectedNote = notes;
             showPopup(cardView);
         }
     };

    private void showPopup(CardView cardView) {
        PopupMenu popupMenu = new PopupMenu(this,cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.pin:
                if(selectedNote.isPinned())
                {
                    database.mainDAO().pin(selectedNote.getID(), false);
                    Toast.makeText(MainActivity.this,"Unpinned!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    database.mainDAO().pin(selectedNote.getID(),true);
                    Toast.makeText(MainActivity.this,"Pinned!",Toast.LENGTH_SHORT).show();
                }
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                notesListAdapters.notifyDataSetChanged();
                return true;

            case R.id.delete:
                database.mainDAO().delete(selectedNote);
                notes.remove(selectedNote);
                notesListAdapters.notifyDataSetChanged();
                Toast.makeText(MainActivity.this,"Note Deleted!",Toast.LENGTH_SHORT).show();
                return  true;

            default:
                return false;

        }

    }
}
