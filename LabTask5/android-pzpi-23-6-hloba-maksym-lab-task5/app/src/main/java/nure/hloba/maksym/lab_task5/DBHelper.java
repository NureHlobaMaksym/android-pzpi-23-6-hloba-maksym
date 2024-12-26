package nure.hloba.maksym.lab_task5;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NOTES = "notes";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_DATE_CREATION = "date_creation";
    private static final String COLUMN_DATE_APPOINTMENT = "date_appointment";
    private static final String COLUMN_IMPORTANCE = "importance";
    private static final String COLUMN_IMAGE_URI = "image_uri";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NOTES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_DATE_CREATION + " INTEGER, " +
                COLUMN_DATE_APPOINTMENT + " INTEGER, " +
                COLUMN_IMPORTANCE + " TEXT, " +
                COLUMN_IMAGE_URI + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    public void addNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, note.getTitle());
        values.put(COLUMN_DESCRIPTION, note.getDescription());
        values.put(COLUMN_DATE_CREATION, note.getDateOfCreation().getTime());
        values.put(COLUMN_DATE_APPOINTMENT, note.getDateOfAppointment().getTime());
        values.put(COLUMN_IMPORTANCE, note.getImportance().name());
        values.put(COLUMN_IMAGE_URI, note.getImageUri());
        db.insert(TABLE_NOTES, null, values);
        db.close();
    }

    public void updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, note.getTitle());
        values.put(COLUMN_DESCRIPTION, note.getDescription());
        values.put(COLUMN_DATE_CREATION, note.getDateOfCreation().getTime());
        values.put(COLUMN_DATE_APPOINTMENT, note.getDateOfAppointment().getTime());
        values.put(COLUMN_IMPORTANCE, note.getImportance().name());
        values.put(COLUMN_IMAGE_URI, note.getImageUri());

        db.update(TABLE_NOTES, values, COLUMN_ID + "=?", new String[]{String.valueOf(note.getId())});
        db.close();
    }

    public void deleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public Note getNoteById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTES, null, COLUMN_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Note note = cursorToNote(cursor);
            cursor.close();
            return note;
        }
        return null;
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTES, null, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                notes.add(cursorToNote(cursor));
            }
            cursor.close();
        }
        return notes;
    }

    private Note cursorToNote(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
        long dateCreation = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE_CREATION));
        long dateAppointment = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE_APPOINTMENT));
        Importance importance = Importance.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMPORTANCE)));
        String imageUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI));
        return new Note(title, description, new Date(dateCreation), id, new Date(dateAppointment), importance, imageUri);
    }
}
