package nure.hloba.maksym.notes;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NotesActivity extends AppCompatActivity {
    private static final String EMPTY_DATE_PLACEHOLDER = "XX.XX.XXXX XX:XX";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private boolean isDateOfAppointmentChanged = false;
    private Importance noteImportance = Importance.Low;
    private ImageView importanceImageView;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView noteImageView;
    private String imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ImageButton ok = findViewById(R.id.ok);
        ImageButton backArrow = findViewById(R.id.backArrow);
        TextView dateAndTime = findViewById(R.id.dateAndTime);
        dateAndTime.setText(EMPTY_DATE_PLACEHOLDER);
        EditText editTextTitle = findViewById(R.id.title);
        EditText editTextDescription = findViewById(R.id.textField);
        noteImageView = findViewById(R.id.noteImageView);
        importanceImageView = findViewById(R.id.importanceImageView);
        int noteId = getIntent().getIntExtra("noteId", 0);
        ok.setOnClickListener(view -> {
            if(editTextTitle.getText().toString().isBlank() && editTextDescription.getText().toString().isBlank()) {
                AlertDialog alertDialog = new AlertDialog.Builder(this).setMessage(R.string.non_written_title_and_description_error).setPositiveButton(R.string.ok, null).create();
                alertDialog.show();
                return;
            }
            if(dateAndTime.getText().toString().equals(EMPTY_DATE_PLACEHOLDER) || !dateAndTime.getText().toString().contains(" ")) {
                AlertDialog alertDialog = new AlertDialog.Builder(this).setMessage(R.string.non_written_date_and_time_error).setPositiveButton(R.string.ok, null).create();
                alertDialog.show();
                return;
            }
            try {
                Date appointmentDate = simpleDateFormat.parse(dateAndTime.getText().toString());
                if(new Date().after(appointmentDate) && isDateOfAppointmentChanged) {
                    AlertDialog alertDialog = new AlertDialog.Builder(this).setMessage(R.string.date_expired_error).setPositiveButton(R.string.ok, null).create();
                    alertDialog.show();
                    return;
                }
                if(noteId == 0) {
                    Note note = new Note(editTextTitle.getText().toString(), editTextDescription.getText().toString(), new Date(), appointmentDate, noteImportance, imageUri);
                    NotesManager.addNote(note);
                } else {
                    Note note = new Note(editTextTitle.getText().toString(), editTextDescription.getText().toString(), new Date(), noteId, appointmentDate, noteImportance, imageUri);
                    NotesManager.editNote(note);
                }
                onBackPressed();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
        dateAndTime.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            int minute = calendar.get(Calendar.MINUTE);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            TimePickerDialog timePickerDialog = new TimePickerDialog(NotesActivity.this, (timePicker, hour1, minute1) -> dateAndTime.setText(dateAndTime.getText() + " " + addZeroesToDateOrTime(hour1) + ":" + addZeroesToDateOrTime(minute1)), hour, minute, true);
            DatePickerDialog datePickerDialog = new DatePickerDialog(NotesActivity.this, (datePicker, year1, month1, day1) -> {
                isDateOfAppointmentChanged = true;
                dateAndTime.setText(addZeroesToDateOrTime(day1) + "." + addZeroesToDateOrTime(month1 + 1) + "." + year1);
                timePickerDialog.show();
            }, year, month, day);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
        });
        backArrow.setOnClickListener(view -> {
            onBackPressed();
        });
        if(noteId != 0) {
            Note note = NotesManager.getNote(noteId);
            editTextTitle.setText(note.getTitle());
            editTextDescription.setText(note.getDescription());
            noteImportance = note.getImportance();
            updateImportanceImage();
            if(note.getImageUri() != null) {
                noteImageView.setImageURI(Uri.parse(note.getImageUri()));
                imageUri = note.getImageUri();
            }
            dateAndTime.setText(simpleDateFormat.format(note.getDateOfAppointment()));
        }
        importanceImageView.setOnClickListener(view -> {
            if(noteImportance.equals(Importance.Great)) {
                noteImportance = Importance.Low;
            } else if (noteImportance.equals(Importance.Low)) {
                noteImportance = Importance.Average;
            } else {
                noteImportance = Importance.Great;
            }
            updateImportanceImage();
        });
        noteImageView.setOnClickListener(view -> openGallery());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                Uri copiedImageUri = copyImageToInternalStorage(uri);
                imageUri = copyImageToInternalStorage(uri).toString();
                if (copiedImageUri != null) {
                    noteImageView.setImageURI(copiedImageUri);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Uri copyImageToInternalStorage(Uri sourceUri) throws Exception {
        ContentResolver resolver = getContentResolver();
        InputStream inputStream = resolver.openInputStream(sourceUri);

        File targetDir = new File(getFilesDir(), "images");
        if (!targetDir.exists()) targetDir.mkdirs();

        String fileName = "note_image_" + System.currentTimeMillis() + ".jpg";
        File targetFile = new File(targetDir, fileName);

        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }

        inputStream.close();

        return Uri.fromFile(targetFile);
    }

    private void updateImportanceImage() {
        if(noteImportance.equals(Importance.Great)) {
            importanceImageView.setImageResource(R.drawable.warning_circle_red);
        } else if (noteImportance.equals(Importance.Low)) {
            importanceImageView.setImageResource(R.drawable.warning_circle_green);
        } else {
            importanceImageView.setImageResource(R.drawable.warning_circle_yellow);
        }
    }

    private String addZeroesToDateOrTime(int value) {
        if(value < 10) {
            return "0" + value;
        } else {
            return Integer.toString(value);
        }
    }
}