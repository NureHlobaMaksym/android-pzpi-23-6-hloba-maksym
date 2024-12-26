package nure.hloba.maksym.lab_task5;

import android.content.Context;

import java.util.List;
import java.util.stream.Collectors;

public class NotesManager {
    private static DBHelper dbHelper;

    public static void initialize(Context context) {
        dbHelper = new DBHelper(context);
    }

    public static void addNote(Note note) {
        dbHelper.addNote(note);
    }

    public static void editNote(Note note) {
        dbHelper.updateNote(note);
    }

    public static void deleteNote(Note note) {
        dbHelper.deleteNote(note.getId());
    }

    public static Note getNote(int id) {
        return dbHelper.getNoteById(id);
    }

    public static List<Note> filterNotes(List<Importance> importanceLevels, String searchText) {
        List<Note> allNotes = dbHelper.getAllNotes();
        return allNotes.stream()
                .filter(note -> (importanceLevels.isEmpty() || importanceLevels.contains(note.getImportance())) &&
                        (searchText.isEmpty() ||
                                note.getTitle().toLowerCase().contains(searchText.toLowerCase()) ||
                                note.getDescription().toLowerCase().contains(searchText.toLowerCase())))
                .collect(Collectors.toList());
    }
}
