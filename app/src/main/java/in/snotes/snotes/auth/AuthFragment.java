package in.snotes.snotes.auth;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import in.snotes.snotes.R;

public class AuthFragment extends Fragment {

    Unbinder unbinder;

    private AuthListener mListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_auth, container, false);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btn_login)
    public void onBtnLoginClicked() {
        mListener.onLoginClicked();
    }

    @OnClick(R.id.btn_register)
    public void onBtnRegisterClicked() {
        mListener.onRegisterClicked();
    }

    public interface AuthListener{
        void onLoginClicked();
        void onRegisterClicked();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AuthListener){
            mListener = (AuthListener) context;
        }else{
            throw new ClassCastException(context.toString()+" must implement AuthListener");
        }
    }
}
