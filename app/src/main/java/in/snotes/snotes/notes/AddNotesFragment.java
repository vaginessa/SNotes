package in.snotes.snotes.notes;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import in.snotes.snotes.R;
import in.snotes.snotes.model.Note;
import io.github.mthli.knife.KnifeText;
import timber.log.Timber;

public class AddNotesFragment extends Fragment {

    @BindView(R.id.bold)
    ImageButton bold;
    @BindView(R.id.italic)
    ImageButton italic;
    @BindView(R.id.underline)
    ImageButton underline;
    @BindView(R.id.strikethrough)
    ImageButton strikethrough;
    @BindView(R.id.bullet)
    ImageButton bullet;
    @BindView(R.id.quote)
    ImageButton quote;
    @BindView(R.id.link)
    ImageButton link;
    @BindView(R.id.clear)
    ImageButton clear;
    @BindView(R.id.tools)
    HorizontalScrollView tools;
    @BindView(R.id.title_notes_add)
    EditText titleNotesAdd;
    @BindView(R.id.edt_content)
    KnifeText knife;
    Unbinder unbinder;

    private AddNotesListener mListener;

    private static final String TAG = "AddNotesFragment";

    public static final String ACTION_NEW_NOTE = "action-new-note";
    public static final String ACTION_EDIT_NOTE = "action-edit-note";

    private int defaultColor = -16777216;

    private static String CURRENT_ACTION;

    public static AddNotesFragment getInstance(String action, Note note) {
        AddNotesFragment addNotesFragment = new AddNotesFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("note", note);
        bundle.putString("action", action);
        addNotesFragment.setArguments(bundle);
        return addNotesFragment;
    }

    public static AddNotesFragment getInstance(String action) {
        AddNotesFragment addNotesFragment = new AddNotesFragment();

        Bundle args = new Bundle();
        args.putString("action", action);
        addNotesFragment.setArguments(args);

        return addNotesFragment;
    }

    public static AddNotesFragment getInstance(String action, String content) {
        AddNotesFragment addNotesFragment = new AddNotesFragment();

        Bundle args = new Bundle();
        args.putString("content", content);
        args.putString("action", action);
        addNotesFragment.setArguments(args);

        return addNotesFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notes_add, container, false);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args == null) {
            return;
        }

        String action = args.getString("action");

        CURRENT_ACTION = action;

        if (ACTION_EDIT_NOTE.equals(action)) {
            Note note = args.getParcelable("note");
            if (note == null) {
                Timber.e("Error. There is no note on edit");
                return;
            }
            setTheNote(note);
            changeColor(note.getColorOfNote());
        } else if (ACTION_NEW_NOTE.equals(action)) {
            changeColor(defaultColor);
        } else if (Intent.ACTION_SEND.equals(action)) {
            String content = args.getString("content");
            setContentOnRecieve(content);
        }

    }

    // this is used when there is an intent incoming from other apps
    private void setContentOnRecieve(String content) {
        knife.fromHtml(content);
    }

    private void setTheNote(Note note) {

        titleNotesAdd.setText(note.getTitle());
        knife.fromHtml(note.getContent());
        titleNotesAdd.setTextColor(note.getColorOfNote());

    }

    public AddNotesActivity getAddNotesActivity() {
        return (AddNotesActivity) getActivity();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    public void saveToDatabase() {
        String title = titleNotesAdd.getText().toString().trim();
        String content = knife.toHtml().trim();

        Timber.d("title is %s", title);
        Timber.d("Content is %s", content);

        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(content)) {
            return;
        }

        mListener.onSaveToDatabase(title, content);
    }

    public void saveToDatabaseOnEdit() {
        String title = titleNotesAdd.getText().toString().trim();
        String content = knife.toHtml().trim();

        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(content)) {
            mListener.deleteNoteFromDb();
        } else {
            mListener.updateNote(title, content);
        }
    }

    public interface AddNotesListener {
        void onSaveToDatabase(String title, String content);

        void deleteNoteFromDb();

        void updateNote(String title, String content);
    }


    @OnClick({R.id.bold, R.id.italic, R.id.underline, R.id.strikethrough, R.id.bullet, R.id.quote, R.id.link, R.id.clear, R.id.undo, R.id.redo})
    public void onFormatClicked(View view) {
        switch (view.getId()) {
            case R.id.bold:
                knife.bold(!knife.contains(KnifeText.FORMAT_BOLD));
                break;
            case R.id.italic:
                knife.italic(!knife.contains(KnifeText.FORMAT_ITALIC));
                break;
            case R.id.underline:
                knife.underline(!knife.contains(KnifeText.FORMAT_UNDERLINED));
                break;
            case R.id.strikethrough:
                knife.strikethrough(!knife.contains(KnifeText.FORMAT_STRIKETHROUGH));
                break;
            case R.id.bullet:
                knife.bullet(!knife.contains(KnifeText.FORMAT_BULLET));
                break;
            case R.id.quote:
                knife.quote(!knife.contains(KnifeText.FORMAT_QUOTE));
                break;
            case R.id.link:
                showLinkDialog();
                break;
            case R.id.clear:
                knife.clearFormats();
                break;
            case R.id.undo:
                knife.undo();
                break;
            case R.id.redo:
                knife.redo();
                break;
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AddNotesListener) {
            mListener = (AddNotesListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement AddNotesListener");
        }
    }

    private void showLinkDialog() {
        final int start = knife.getSelectionStart();
        final int end = knife.getSelectionEnd();
        new MaterialDialog.Builder(getAddNotesActivity())
                .title("Enter a link")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("www.google.com", "", (dialog, input) -> {
                    // Do something
                    if (TextUtils.isEmpty(input.toString())) {
                        return;
                    }
                    knife.link(input.toString(), start, end);
                }).show();
    }

    public void changeColor(int color) {

        ImageView[] views = {
                getAddNotesActivity().findViewById(R.id.undo),
                getAddNotesActivity().findViewById(R.id.redo),
                getAddNotesActivity().findViewById(R.id.bold),
                getAddNotesActivity().findViewById(R.id.italic),
                getAddNotesActivity().findViewById(R.id.underline),
                getAddNotesActivity().findViewById(R.id.strikethrough),
                getAddNotesActivity().findViewById(R.id.bullet),
                getAddNotesActivity().findViewById(R.id.quote),
                getAddNotesActivity().findViewById(R.id.link),
                getAddNotesActivity().findViewById(R.id.clear)
        };

        for (ImageView view : views) {
            tintIcon(view, color);
        }

        EditText edt_title = getAddNotesActivity().findViewById(R.id.title_notes_add);
        edt_title.setTextColor(color);
        edt_title.setHintTextColor(color);

    }

    public void tintIcon(ImageView imageView, int color) {

        Drawable normalDrawable = imageView.getDrawable();
        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);

        DrawableCompat.setTint(wrapDrawable, color);
        imageView.setImageDrawable(wrapDrawable);
    }

}
