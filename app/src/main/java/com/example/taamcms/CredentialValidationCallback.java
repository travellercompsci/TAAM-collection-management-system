package com.example.taamcms;

/**
 * Callbacks for the login validation. Exactly one method will be called.
 */
public interface CredentialValidationCallback {
    /**
     * If username and password are both valid.
     */
    void isSuccessful();

    /**
     * Called if the username is invalid.
     */
    void invalidUsername();

    /**
     * Called if the username is correct but the password is not.
     */
    void invalidPassword();

    /**
     * Called if a database error occurs.
     */
    void databaseError(String errorMessage);
}
