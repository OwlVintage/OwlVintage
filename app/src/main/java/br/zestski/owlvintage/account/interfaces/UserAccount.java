package br.zestski.owlvintage.account.interfaces;

import androidx.annotation.NonNull;

/**
 * Interface for user account data.
 *
 * @author Zestski
 */
public interface UserAccount {

    /**
     * Get the username of the account.
     *
     * @return The username
     */
    @NonNull
    String getUsername();

    /**
     * Get the email of the account.
     *
     * @return The email
     */
    @NonNull
    String getEmail();

    /**
     * Get the password of the account.
     *
     * @return The password
     */
    @NonNull
    String getPassword();

    /**
     * Get the encrypted auth of the account.
     *
     * @return The encrypted auth using Base64
     */
    @NonNull
    String getEncryptedAuth();

    /**
     * Check if the account is set as default.
     *
     * @return True if the account is default, false otherwise
     */
    boolean isDefault();

    /**
     * Set the account as default or not.
     *
     * @param isDefault True to set as default, false otherwise
     */
    void setDefault(boolean isDefault);
}