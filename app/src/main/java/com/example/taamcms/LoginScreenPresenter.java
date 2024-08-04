package com.example.taamcms;

public class LoginScreenPresenter {

    LoginScreenView view;
    LoginScreenModule model;

    public LoginScreenPresenter(LoginScreenView view, LoginScreenModule model) {
        this.view = view;
        this.model = model;
    }

    public void handleLogin() {
        String username = view.getUsername();
        String password = view.getPassword();

        if (username.equals("")) {
            view.showUsernameErr("Please enter a username.");
        }
        else if (password.equals("")){
            view.showPasswordErr("Please enter a password.");
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
                public void databaseError(String message) {
                    view.showGeneralErr("A database error occurred: " + message);
                }
            });
        }
    }
}
