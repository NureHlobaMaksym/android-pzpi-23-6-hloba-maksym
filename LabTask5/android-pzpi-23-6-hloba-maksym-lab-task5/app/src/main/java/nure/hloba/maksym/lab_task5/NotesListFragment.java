package nure.hloba.maksym.lab_task5;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class NotesListFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notes_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView list = view.findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(requireContext()));
        list.setAdapter(adapter);
        registerForContextMenu(list);
        adapter.setNoteClickListener(view1 -> navigateToEditNoteFragment(adapter.getSelectedNote().getId()));
        setHasOptionsMenu(true);
    }

    private final Adapter adapter = new Adapter();
    private final List<Importance> activeFilters = new ArrayList<>();
    private String searchQuery = "";

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_activity_menu, menu);
        SearchManager searchManager = (SearchManager) requireContext().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));
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
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            navigateToSettingsFragment();
        } else if (item.getItemId() == R.id.add) {
            navigateToEditNoteFragment(0);
        } else if (item.getItemId() == R.id.filter_great) {
            toggleFilter(Importance.Great);
        } else if (item.getItemId() == R.id.filter_average) {
            toggleFilter(Importance.Average);
        } else if (item.getItemId() == R.id.filter_low) {
            toggleFilter(Importance.Low);
        } else if (item.getItemId() == R.id.filter_clear) {
            activeFilters.clear();
            update();
        }
        return super.onOptionsItemSelected(item);
    }

    private void navigateToSettingsFragment() {
        SettingsFragment fragment = new SettingsFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void navigateToEditNoteFragment(int noteId) {
        Bundle bundle = new Bundle();
        bundle.putInt("noteId", noteId);

        EditNoteFragment fragment = new EditNoteFragment();
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
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
    public void onStart() {
        super.onStart();
        update();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Note note = adapter.getSelectedNote();
        if (item.getItemId() == R.id.edit) {
            navigateToEditNoteFragment(note.getId());
        } else if (item.getItemId() == R.id.delete) {
            AlertDialog alertDialog = new AlertDialog.Builder(requireContext()).setMessage(R.string.note_deletion_confirmation).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
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

    private void update() {
        List<Note> filteredNotes = NotesManager.filterNotes(activeFilters, searchQuery);
        adapter.setNotes(filteredNotes);
    }
}