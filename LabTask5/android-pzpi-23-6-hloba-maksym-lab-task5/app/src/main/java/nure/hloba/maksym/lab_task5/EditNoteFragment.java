package nure.hloba.maksym.lab_task5;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditNoteFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_note, container, false);
    }

    private static final String EMPTY_DATE_PLACEHOLDER = "XX.XX.XXXX XX:XX";
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private boolean isDateOfAppointmentChanged = false;
    private Importance noteImportance = Importance.Low;
    private ImageView importanceImageView;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView noteImageView;
    private String imageUri;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageButton ok = view.findViewById(R.id.ok);
        ImageButton backArrow = view.findViewById(R.id.backArrow);
        TextView dateAndTime = view.findViewById(R.id.dateAndTime);
        dateAndTime.setText(EMPTY_DATE_PLACEHOLDER);
        EditText editTextTitle = view.findViewById(R.id.title);
        EditText editTextDescription = view.findViewById(R.id.textField);
        noteImageView = view.findViewById(R.id.noteImageView);
        importanceImageView = view.findViewById(R.id.importanceImageView);
        int noteId = getArguments() != null ? getArguments().getInt("noteId", 0) : 0;
        ok.setOnClickListener(v -> {
            if (editTextTitle.getText().toString().isBlank() && editTextDescription.getText().toString().isBlank()) {
                AlertDialog alertDialog = new AlertDialog.Builder(requireContext()).setMessage(R.string.non_written_title_and_description_error).setPositiveButton(R.string.ok, null).create();
                alertDialog.show();
                return;
            }
            if (dateAndTime.getText().toString().equals(EMPTY_DATE_PLACEHOLDER) || !dateAndTime.getText().toString().contains(" ")) {
                AlertDialog alertDialog = new AlertDialog.Builder(requireContext()).setMessage(R.string.non_written_date_and_time_error).setPositiveButton(R.string.ok, null).create();
                alertDialog.show();
                return;
            }
            try {
                Date appointmentDate = simpleDateFormat.parse(dateAndTime.getText().toString());
                if (new Date().after(appointmentDate) && isDateOfAppointmentChanged) {
                    AlertDialog alertDialog = new AlertDialog.Builder(requireContext()).setMessage(R.string.date_expired_error).setPositiveButton(R.string.ok, null).create();
                    alertDialog.show();
                    return;
                }
                if (noteId == 0) {
                    Note note = new Note(editTextTitle.getText().toString(), editTextDescription.getText().toString(), new Date(), appointmentDate, noteImportance, imageUri);
                    NotesManager.addNote(note);
                } else {
                    Note note = new Note(editTextTitle.getText().toString(), editTextDescription.getText().toString(), new Date(), noteId, appointmentDate, noteImportance, imageUri);
                    NotesManager.editNote(note);
                }
                getParentFragmentManager().popBackStack();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
        dateAndTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            int minute = calendar.get(Calendar.MINUTE);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                    dateAndTime.setText(dateAndTime.getText() + " " + addZeroesToDateOrTime(hour) + ":" + addZeroesToDateOrTime(minute));
                }
            }, hour, minute, true);
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (datePicker, year1, month1, day1) -> {
                isDateOfAppointmentChanged = true;
                dateAndTime.setText(addZeroesToDateOrTime(day1) + "." + addZeroesToDateOrTime(month1 + 1) + "." + year1);
                timePickerDialog.show();
            }, year, month, day);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
        });
        backArrow.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });
        if (noteId != 0) {
            Note note = NotesManager.getNote(noteId);
            editTextTitle.setText(note.getTitle());
            editTextDescription.setText(note.getDescription());
            noteImportance = note.getImportance();
            updateImportanceImage();
            if (note.getImageUri() != null) {
                noteImageView.setImageURI(Uri.parse(note.getImageUri()));
                imageUri = note.getImageUri();
            }
            dateAndTime.setText(simpleDateFormat.format(note.getDateOfAppointment()));
        }
        importanceImageView.setOnClickListener(view1 -> {
            if (noteImportance.equals(Importance.Great)) {
                noteImportance = Importance.Low;
            } else if (noteImportance.equals(Importance.Low)) {
                noteImportance = Importance.Average;
            } else {
                noteImportance = Importance.Great;
            }
            updateImportanceImage();
        });
        noteImageView.setOnClickListener(v -> openGallery());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        ContentResolver resolver = requireActivity().getContentResolver();
        InputStream inputStream = resolver.openInputStream(sourceUri);

        File targetDir = new File(requireContext().getFilesDir(), "images");
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("imageUri", imageUri);
        TextView dateAndTime = getView().findViewById(R.id.dateAndTime);
        outState.putString("dateAndTime", dateAndTime.getText().toString());
        outState.putString("noteImportance", noteImportance.name());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            imageUri = savedInstanceState.getString("imageUri");
            if (imageUri != null) {
                noteImageView.setImageURI(Uri.parse(imageUri));
            }

            String savedDateAndTime = savedInstanceState.getString("dateAndTime", EMPTY_DATE_PLACEHOLDER);
            TextView dateAndTime = getView().findViewById(R.id.dateAndTime);
            dateAndTime.setText(savedDateAndTime);

            String savedImportance = savedInstanceState.getString("noteImportance", Importance.Low.name());
            noteImportance = Importance.valueOf(savedImportance);
            updateImportanceImage();
        }
    }

    private void updateImportanceImage() {
        if (noteImportance.equals(Importance.Great)) {
            importanceImageView.setImageResource(R.drawable.warning_circle_red);
        } else if (noteImportance.equals(Importance.Low)) {
            importanceImageView.setImageResource(R.drawable.warning_circle_green);
        } else {
            importanceImageView.setImageResource(R.drawable.warning_circle_yellow);
        }
    }

    private String addZeroesToDateOrTime(int value) {
        if (value < 10) {
            return "0" + value;
        } else {
            return Integer.toString(value);
        }
    }
}