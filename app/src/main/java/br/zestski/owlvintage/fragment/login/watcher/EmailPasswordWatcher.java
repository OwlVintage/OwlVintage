package br.zestski.owlvintage.fragment.login.watcher;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;

import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

/**
 * A TextWatcher implementation to validate email and password fields and enable/disable a button accordingly.
 *
 * @author Zestski
 */
public class EmailPasswordWatcher implements TextWatcher {

    private final TextInputLayout tilEtEmail;
    private final TextInputLayout tilEtPassword;
    private final ExtendedFloatingActionButton extendedFloatingActionButton;

    /**
     * Constructs an EmailPasswordWatcher with the specified TextInputLayouts and ExtendedFloatingActionButton.
     *
     * @param tilEtEmail                   The TextInputLayout for the email field.
     * @param tilEtPassword                The TextInputLayout for the password field.
     * @param extendedFloatingActionButton The ExtendedFloatingActionButton to enable/disable.
     */
    public EmailPasswordWatcher(
            @NonNull TextInputLayout tilEtEmail,
            @NonNull TextInputLayout tilEtPassword,
            @NonNull ExtendedFloatingActionButton extendedFloatingActionButton
    ) {
        this.tilEtEmail = tilEtEmail;
        this.tilEtPassword = tilEtPassword;
        this.extendedFloatingActionButton = extendedFloatingActionButton;
    }

    @Override
    public void beforeTextChanged(
            @NonNull CharSequence s,
            int start,
            int count,
            int after
    ) {
    }

    @Override
    public void onTextChanged(
            @NonNull CharSequence s,
            int start,
            int before,
            int count
    ) {
    }

    @Override
    public void afterTextChanged(
            @NonNull Editable s
    ) {
        final var email = Objects.requireNonNull(tilEtEmail.getEditText()).getText().toString().trim();
        final var password = Objects.requireNonNull(tilEtPassword.getEditText()).getText().toString().trim();

        final var isEmailValid = isValidEmail(email);
        final var isPasswordValid = isPasswordValid(password);

        extendedFloatingActionButton.setEnabled(isEmailValid && isPasswordValid);

        if (isEmailValid && isPasswordValid) {
            extendedFloatingActionButton.extend();
        } else {
            extendedFloatingActionButton.shrink();
        }
    }

    /**
     * Validates an email address.
     *
     * @param email The email address to validate.
     * @return true if the email address is valid, false otherwise.
     */
    private boolean isValidEmail(
            @NonNull String email
    ) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Validates a password.
     *
     * @param password The password to validate.
     * @return true if the password is valid, false otherwise.
     */
    private boolean isPasswordValid(
            @NonNull String password
    ) {
        return password.length() >= 6;
    }
}