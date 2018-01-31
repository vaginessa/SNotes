package in.snotes.snotes.notes;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
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
import io.github.mthli.knife.KnifeText;

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


    private static final String TAG = "AddNotesFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAddNotesActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
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
        super.onViewCreated(view, savedInstanceState);
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

        Log.d(TAG, "title is " + title);
        Log.d(TAG, "Content is " + content);

        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(content)) {
            return;
        }


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
        Context context = getAddNotesActivity();

        Drawable normalDrawable = imageView.getDrawable();
        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);

        DrawableCompat.setTint(wrapDrawable, color);
        imageView.setImageDrawable(wrapDrawable);
    }

}
