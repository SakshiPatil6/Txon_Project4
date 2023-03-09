package com.example.note;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.note.Models.Notes;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotesTableActivity extends AppCompatActivity {

    EditText editText_title, editText_notes;
    ImageView imageView_save;
    Notes notes;
    boolean isOldNote = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_table);

        imageView_save = findViewById(R.id.save);
        editText_title= findViewById(R.id.edit_title);
        editText_notes =findViewById(R.id.notes);

        notes = new Notes();
        try {
            notes= (Notes) getIntent().getSerializableExtra("Old_Note");
            editText_title.setText(notes.getTitle());
            editText_notes.setText(notes.getNotes());
            isOldNote = true;
        }catch (Exception e){
            e.printStackTrace();
        }

        imageView_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title= editText_title.getText().toString();
                String description= editText_notes.getText().toString();

                if(description.isEmpty())
                {
                    Toast.makeText(NotesTableActivity.this, "Please add some notes", Toast.LENGTH_SHORT).show();
                    return;
                }

                SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
                Date date = new Date();

                if(!isOldNote)
                {
                    notes = new Notes();
                }

                notes.setTitle(title);
                notes.setNotes(description);
                notes.setDate(formatter.format(date));

                Intent i=new Intent();
                i.putExtra("note",notes);
                setResult(Activity.RESULT_OK,i);
                finish();
            }
        });

    }
}