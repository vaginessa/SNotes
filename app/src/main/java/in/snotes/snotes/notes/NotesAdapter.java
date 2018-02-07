package in.snotes.snotes.notes;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.snotes.snotes.R;
import in.snotes.snotes.utils.Utils;
import in.snotes.snotes.model.Note;


public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Note> notesList;

    private NotesListener mNotesListener;

    public NotesAdapter(Context context, NotesListener notesListener) {
        this.context = context;
        this.notesList = new ArrayList<>();
        mNotesListener = notesListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Note note = notesList.get(position);
        holder.bind(note);
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public void setNotes(ArrayList<Note> notes) {
        this.notesList = notes;
        notifyDataSetChanged();
    }

    public interface NotesListener {
        void onNoteClicked(Note note);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_note_title)
        TextView tvNoteTitle;
        @BindView(R.id.tv_note_content)
        TextView tvNoteContent;
        @BindView(R.id.img_locked)
        ImageView imgLocked;
        @BindView(R.id.img_starred)
        ImageView imgStarred;
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.layout_note)
        CardView layoutNote;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(view -> mNotesListener.onNoteClicked(notesList.get(getAdapterPosition())));
        }

        public void bind(Note note) {

            String title = note.getTitle();
            String content = note.getContent();

            if (TextUtils.isEmpty(title)) {
                tvNoteTitle.setVisibility(View.GONE);
            } else {
                tvNoteTitle.setText(title);
            }

            if (TextUtils.isEmpty(content)) {
                tvNoteContent.setVisibility(View.GONE);
            } else {
                if (note.isLocked()) {
                    String protectedContent = "******************************************************";
                    tvNoteContent.setText(protectedContent);
                } else {
                    tvNoteContent.setText(Html.fromHtml(content));
                }

            }

            layoutNote.setCardBackgroundColor(note.getColorOfNote());

            if (!note.isLocked()) {
                imgLocked.setVisibility(View.GONE);
            } else {
                imgLocked.setVisibility(View.VISIBLE);
            }

            if (!note.isStarred()) {
                imgStarred.setVisibility(View.GONE);
            } else {
                imgStarred.setVisibility(View.VISIBLE);
            }

            String date = Utils.getDate(note.getCreatedTimestamp());
            tvDate.setText(date);

        }
    }

}
