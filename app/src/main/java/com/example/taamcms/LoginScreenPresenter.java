package com.example.taamcms;

public class LoginScreenPresenter {

    LoginScreenView view;
    LoginScreenModule model;

    public LoginScreenPresenter(LoginScreenView view, LoginScreenModule model) {
        this.view = view;
        this.model = model;
    }

    public void handleLogin(String username, String password) {
        if (username.equals("")) {
            view.showUsernameErr(username);
        }
        else if (password.equals("")){
            view.showPasswordErr(password);
        }
        else {
            model.isValidCredentials(username, password, new CredentialValidationCallback() {
                @Override
                public void isSuccessful() {
                    view.loginSuccess();

                }

                @Override
                public void invalidUsername() {
                    view.showUsernameErr("Invalid username");
                }

                @Override
                public void invalidPassword() {
                    view.showPasswordErr("Invalid password");
                }

                @Override
                public void databaseError() {
                    view.showGeneralErr("Database error occurred");
                }
            });
        }
    }
}
