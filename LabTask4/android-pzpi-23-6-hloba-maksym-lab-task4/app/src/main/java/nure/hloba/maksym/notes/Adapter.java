package nure.hloba.maksym.notes;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.zip.Inflater;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private List<Note> notes = new ArrayList<>();
    private Note selectedNote;
    private View.OnClickListener noteClickListener;

    public void setNoteClickListener(View.OnClickListener noteClickListener) {
        this.noteClickListener = noteClickListener;
    }

    public Note getSelectedNote() {
        return selectedNote;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note item = notes.get(position);
        holder.link(item);
        holder.itemView.setOnLongClickListener(view -> {
            selectedNote = item;
            return false;
        });
        holder.itemView.setOnClickListener(view -> {
            selectedNote = item;
            noteClickListener.onClick(view);
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnCreateContextMenuListener((contextMenu, view, contextMenuInfo) -> {
                MenuInflater menuInflater = new MenuInflater(view.getContext());
                menuInflater.inflate(R.menu.context_menu, contextMenu);
            });
        }

        public void link(Note note) {
            TextView title = itemView.findViewById(R.id.title);
            TextView description = itemView.findViewById(R.id.description);
            TextView dateOfCreation = itemView.findViewById(R.id.dateOfCreation);
            ImageView noteItemImageView = itemView.findViewById(R.id.noteItemImageView);
            ImageView noteImageView = itemView.findViewById(R.id.noteImageView);
            title.setText(note.getTitle());
            description.setText(note.getDescription());
            updateNoteItemImportanceImage(note, noteItemImageView);
            if(note.getTitle().toString().isBlank()) {
                title.setVisibility(View.GONE);
            }
            if(note.getDescription().toString().isBlank()) {
                description.setVisibility(View.GONE);
            }
            if (note.getImageUri() != null) {
                noteImageView.setImageURI(Uri.parse(note.getImageUri()));
            } else {
                noteImageView.setImageResource(R.drawable.image);
            }
            dateOfCreation.setText(new SimpleDateFormat("dd.MM.yyyy HH:mm").format(note.getDateOfCreation()));
        }
    }

    private static void updateNoteItemImportanceImage(Note note, ImageView noteItemImageView) {
        if(note.getImportance().equals(Importance.Great)) {
            noteItemImageView.setImageResource(R.drawable.warning_circle_red);
        } else if (note.getImportance().equals(Importance.Low)) {
            noteItemImageView.setImageResource(R.drawable.warning_circle_green);
        } else {
            noteItemImageView.setImageResource(R.drawable.warning_circle_yellow);
        }
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }
}


