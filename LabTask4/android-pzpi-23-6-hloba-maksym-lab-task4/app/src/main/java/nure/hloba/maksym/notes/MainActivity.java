package nure.hloba.maksym.notes;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Adapter adapter = new Adapter();
    private List<Importance> activeFilters = new ArrayList<>();
    private String searchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        searchView.setOnSearchClickListener(view -> menu.findItem(R.id.add).setVisible(false));
        searchView.setOnCloseListener(() -> {
            menu.findItem(R.id.add).setVisible(true);
            update();
            return false;
        });
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                searchQuery = text;
                update();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add) {
            startActivity(new Intent(this, NotesActivity.class));
        } else if(item.getItemId() == R.id.filter_great) {
            toggleFilter(Importance.Great);
        } else if(item.getItemId() == R.id.filter_average) {
            toggleFilter(Importance.Average);
        } else if(item.getItemId() == R.id.filter_low) {
            toggleFilter(Importance.Low);
        } else if(item.getItemId() == R.id.filter_clear){
            activeFilters.clear();
            update();
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleFilter(Importance importance) {
        if (activeFilters.contains(importance)) {
            activeFilters.remove(importance);
        } else {
            activeFilters.add(importance);
        }
        update();
    }

    @Override
    protected void onStart() {
        super.onStart();
        RecyclerView list = findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
        registerForContextMenu(list);
        adapter.setNoteClickListener(view -> editNote(adapter.getSelectedNote().getId()));
        update();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Note note = adapter.getSelectedNote();
        if(item.getItemId() == R.id.edit) {
            editNote(note.getId());
        } else if(item.getItemId() == R.id.delete) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).setMessage(R.string.note_deletion_confirmation).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    NotesManager.deleteNote(note);
                    update();
                }
            }).setNegativeButton(R.string.no, null).create();
            alertDialog.show();
        }
        return true;
    }

    private void editNote(int noteId) {
        Intent intent = new Intent(this, NotesActivity.class);
        intent.putExtra("noteId", noteId);
        startActivity(intent);
    }

    private void update() {
        List<Note> filteredNotes = NotesManager.filterNotes(activeFilters, searchQuery);
        adapter.setNotes(filteredNotes);
    }
}