package in.snotes.snotes.auth;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import in.snotes.snotes.R;

public class LoginFragment extends Fragment {

    @BindView(R.id.edt_email)
    TextInputEditText edtEmail;
    @BindView(R.id.layout_email)
    TextInputLayout layoutEmail;
    @BindView(R.id.edt_password)
    TextInputEditText edtPassword;
    @BindView(R.id.layout_password)
    TextInputLayout layoutPassword;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.tv_forgot_password)
    TextView tvForgotPassword;
    Unbinder unbinder;

    private LoginListener mListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    @OnClick(R.id.btn_login)
    public void onBtnLoginClicked() {
        invalidateInputLayouts();

        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // validating edge cases

        if (TextUtils.isEmpty(email) || email.isEmpty()) {
            layoutEmail.setError("Email cannot be empty");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            layoutEmail.setError("Enter a valid Email ID");
            return;
        }

        if (TextUtils.isEmpty(password) || password.isEmpty()) {
            layoutPassword.setError("Password cannot be empty");
            return;
        }

        // if we reach here, email and password is valid and we can login the user
        mListener.loginUser(email, password);

    }

    private void invalidateInputLayouts() {
        layoutEmail.setError(null);
        layoutPassword.setError(null);
    }

    @OnClick(R.id.btn_register)
    public void onBtnRegisterClicked() {
        mListener.navToRegister();
    }

    @OnClick(R.id.tv_forgot_password)
    public void onTvForgotPasswordClicked() {
        mListener.forgotPassword();
    }

    public interface LoginListener {
        void loginUser(String email, String password);

        void navToRegister();

        void forgotPassword();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginListener) {
            mListener = (LoginListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement LoginListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
