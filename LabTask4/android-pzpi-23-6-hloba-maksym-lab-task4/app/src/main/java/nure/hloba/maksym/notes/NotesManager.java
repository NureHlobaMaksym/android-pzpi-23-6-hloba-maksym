package nure.hloba.maksym.notes;

import android.media.Image;

import java.util.ArrayList;
import java.util.List;

public class NotesManager {
    private static List<Note> notes = new ArrayList<>();

    public static void addNote(Note note) {
        notes.add(note);
    }

    public static void editNote(Note note) {
        Note oldNote = getNote(note.getId());
        int oldNodeIndex = notes.indexOf(oldNote);
        oldNote.setDateOfAppointment(note.getDateOfAppointment());
        oldNote.setTitle(note.getTitle());
        oldNote.setDescription(note.getDescription());
        oldNote.setImportance(note.getImportance());
        oldNote.setImageUri(note.getImageUri());
        notes.set(oldNodeIndex, oldNote);
    }

    public static List<Note> filterNotes(List<Importance> importanceLevels, String searchText) {
        List<Note> result = new ArrayList<>();
        for (Note note : notes) {
            boolean matchesImportance = importanceLevels.isEmpty() || importanceLevels.contains(note.getImportance());
            boolean matchesSearch = searchText.isEmpty() ||
                    note.getTitle().toLowerCase().contains(searchText.toLowerCase()) ||
                    note.getDescription().toLowerCase().contains(searchText.toLowerCase());
            if (matchesImportance && matchesSearch) {
                result.add(note);
            }
        }
        return result;
    }

    public static void deleteNote(Note note) {
        notes.remove(note);
    }

    public static Note getNote(int id) {
        for(int i = 0; i < notes.size(); i++) {
            if(notes.get(i).getId() == id) {
                return notes.get(i);
            }
        }
        return null;
    }
}
