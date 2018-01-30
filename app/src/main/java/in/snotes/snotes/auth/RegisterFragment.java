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

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import in.snotes.snotes.R;

public class RegisterFragment extends Fragment {

    @BindView(R.id.edt_name)
    TextInputEditText edtName;
    @BindView(R.id.layout_name)
    TextInputLayout layoutName;
    @BindView(R.id.edt_email)
    TextInputEditText edtEmail;
    @BindView(R.id.layout_email)
    TextInputLayout layoutEmail;
    @BindView(R.id.edt_password)
    TextInputEditText edtPassword;
    @BindView(R.id.layout_password)
    TextInputLayout layoutPassword;
    @BindView(R.id.edt_confirm_password)
    TextInputEditText edtConfirmPassword;
    @BindView(R.id.layout_confirm_password)
    TextInputLayout layoutConfirmPassword;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.tv_registered_already)
    TextView tvRegisteredAlready;
    Unbinder unbinder;

    private RegisterListener mListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btn_register)
    public void onBtnRegisterClicked() {
        invalidateLayouts();

        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        // handle edge cases
        if (isEmpty(name)) {
            layoutName.setError("Name cannot be empty");
            return;
        }

        if (isEmpty(email)) {
            layoutEmail.setError("Email cannot be empty");
            return;
        }

        if (!(Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            layoutEmail.setError("Enter a valid email address");
            return;
        }

        if (isEmpty(password)) {
            layoutPassword.setError("Password cannot be empty");
            return;
        }

        if (isEmpty(confirmPassword)) {
            layoutConfirmPassword.setError("Confirm Password cannot be empty");
            return;
        }

        if (!Objects.equals(password, confirmPassword)) {
            layoutPassword.setError("Passwords should match");
            layoutConfirmPassword.setError("Passwords should match");
            return;
        }

        // if we have reached here, we can register the user. All the edge cases have been handled
        mListener.registerUser(name, email, password);
    }

    private void invalidateLayouts() {
        layoutName.setError(null);
        layoutEmail.setError(null);
        layoutPassword.setError(null);
        layoutConfirmPassword.setError(null);
    }

    private boolean isEmpty(String whatever) {
        return (TextUtils.isEmpty(whatever) || whatever.isEmpty());
    }

    @OnClick(R.id.tv_registered_already)
    public void onTvRegisteredAlreadyClicked() {
        mListener.userIsAlreadyRegistered();
    }

    public interface RegisterListener {
        void registerUser(String name, String email, String password);

        void userIsAlreadyRegistered();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RegisterListener) {
            mListener = (RegisterListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement RegisterListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
