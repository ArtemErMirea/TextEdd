package com.example.textedd.presentation.frags;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.textedd.LoginContract;
import com.example.textedd.R;

import com.example.textedd.domain.LoginPresenter;

public class LoginFragment extends Fragment implements LoginContract.View {
    private static final String TAG = "LoginFragment";

    private EditText usernameEditText;
    private EditText passwordEditText;
    Context context;
    View view;



    private LoginContract.Presenter presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        usernameEditText = view.findViewById(R.id.username);
        passwordEditText = view.findViewById(R.id.password);
        Button loginButton = view.findViewById(R.id.login);
        Button registerButton = view.findViewById(R.id.register);
        presenter = new LoginPresenter(this);

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            presenter.login(context, username, password);
            Log.d(TAG, "LoginButton Was Pressed");
        });

        registerButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            presenter.register(context, username, password);
            Log.d(TAG, "RegisterButton Was Pressed");
        });
        context = requireActivity().getApplicationContext();

    }

    @Override
    public void onLoginSuccess() {
        // Navigate to main screen
        NavHostFragment.findNavController(this).
                navigate(R.id.action_loginFragment_to_catalogFragment);
    }

    @Override
    public void onLoginFailed() {
        Toast.makeText(requireContext(), "Неверный пароль", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onRegistrationFailed(){
        Toast.makeText(requireContext(), "Регистрация не удалась. \n" +
                "Измените имя пользователя или пароль", Toast.LENGTH_SHORT).show();
    }
}