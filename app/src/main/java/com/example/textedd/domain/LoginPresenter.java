package com.example.textedd.domain;

import android.content.Context;

import com.example.textedd.shared.contracts.LoginContract;
import com.example.textedd.data.Repository;

public class LoginPresenter implements LoginContract.Presenter {
    private LoginContract.View view;
    private Repository userRepository;

    public LoginPresenter(LoginContract.View view) {
        this.view = view;
        userRepository = new Repository();
    }

    @Override
    public void login(Context context, String username, String password) {
        boolean lSuccess = userRepository.login(context, username, password);
        //Bundle bundle = setUserDir(context, username);
        if (lSuccess) {
            userRepository.getUserDirectory(context, username);
            view.onLoginSuccess();
        } else {
            view.onLoginFailed();
        }
    }

    @Override
    public void register(Context context, String username, String password) {
        boolean rSuccess = userRepository.register(context, username, password);
        if (rSuccess){
            login(context, username, password);
        }
        else {
            view.onRegistrationFailed();
        }
    }
}