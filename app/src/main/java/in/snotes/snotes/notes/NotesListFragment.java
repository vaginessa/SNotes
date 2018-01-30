package in.snotes.snotes.notes;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import in.snotes.snotes.R;

public class NotesListFragment extends Fragment {

    public interface NotesListFragmentListener {
        void onFabClicked();
    }

    @BindView(R.id.rv_notes_list)
    RecyclerView rvNotesList;
    Unbinder unbinder;

    private NotesListFragmentListener mListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notes_list, container, false);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.fab_add_notes)
    public void onFabClicked() {
        mListener.onFabClicked();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NotesListFragmentListener) {
            mListener = (NotesListFragmentListener) context;
        } else {
            throw new ClassCastException(context.toString() + " should implement NotesListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
