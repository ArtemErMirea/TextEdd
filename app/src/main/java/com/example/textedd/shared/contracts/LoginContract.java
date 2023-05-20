package com.example.textedd.shared.contracts;

import android.content.Context;

public interface LoginContract {
    interface View {
        void onLoginSuccess();
        void onLoginFailed();
        void onRegistrationFailed();
    }
    interface Presenter {
        void login(Context context, String username, String password);
        void register(Context context, String username, String password);
    }
}
