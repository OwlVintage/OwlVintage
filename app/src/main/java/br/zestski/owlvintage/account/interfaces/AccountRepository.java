package br.zestski.owlvintage.account.interfaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * Interface for managing user accounts.
 */
public interface AccountRepository {

    /**
     * Add an account.
     *
     * @param account The account to add
     */
    void addAccount(@NonNull UserAccount account);

    /**
     * Get all accounts.
     *
     * @return The list of accounts
     */
    @NonNull
    List<UserAccount> getAccounts();

    /**
     * Save accounts.
     *
     * @param accounts The list of accounts to save
     */
    void saveAccounts(@NonNull List<UserAccount> accounts);

    /**
     * Get the default account.
     *
     * @return The default account
     */
    @Nullable
    UserAccount getDefaultAccount();

    /**
     * Set the default account.
     *
     * @param account The account to set as default
     */
    void setDefaultAccount(@NonNull UserAccount account);

    /**
     * Remove an account.
     *
     * @param account The account to remove
     */
    void removeAccount(@NonNull UserAccount account);
}