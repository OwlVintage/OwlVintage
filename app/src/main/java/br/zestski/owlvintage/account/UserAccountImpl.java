package br.zestski.owlvintage.account;

import static br.zestski.owlvintage.util.B64Util.encodeBase64;

import android.util.Log;

import androidx.annotation.NonNull;

import br.zestski.owlvintage.BuildConfig;
import br.zestski.owlvintage.account.interfaces.UserAccount;

/**
 * Implementation of UserAccount interface to store user data.
 *
 * @author Zestski
 */
public class UserAccountImpl implements UserAccount {

    private final String username, email, password;
    private boolean isDefault;

    public UserAccountImpl(@NonNull String username, @NonNull String email, @NonNull String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.isDefault = false;

        if (BuildConfig.DEBUG)
            Log.i(getClass().getSimpleName(), toString());
    }

    @NonNull
    @Override
    public String getUsername() {
        return username;
    }

    @NonNull
    @Override
    public String getEmail() {
        return email;
    }

    @NonNull
    @Override
    public String getPassword() {
        return password;
    }

    @NonNull
    @Override
    public String getEncryptedAuth() {
        return String.format("Basic %s",
                encodeBase64(String.format("%s:%s", email, password)));
    }

    @Override
    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("UserAccountImpl{username=%s, email=%s, password=%s, isDefault=%s}",
                username, email, password, isDefault);
    }
}