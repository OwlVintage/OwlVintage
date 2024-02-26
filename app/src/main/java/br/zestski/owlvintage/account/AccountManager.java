package br.zestski.owlvintage.account;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import br.zestski.owlvintage.account.adapter.UserAccountTypeAdapter;
import br.zestski.owlvintage.account.interfaces.AccountRepository;
import br.zestski.owlvintage.account.interfaces.UserAccount;
import br.zestski.owlvintage.security.AppPreferences;

/**
 * Account manager class to manage user accounts.
 *
 * @author Zestski
 */
public class AccountManager {

    private final AccountRepository accountRepository;

    private final AppPreferences appPreferences;

    private final Gson gson;

    public AccountManager(
            @NonNull AppPreferences appPreferences
    ) {
        gson = new GsonBuilder()
                .registerTypeAdapter(UserAccount.class, new UserAccountTypeAdapter())
                .create();

        this.appPreferences = appPreferences;
        this.accountRepository = new SharedPreferencesAccountRepository(appPreferences, gson);
    }

    public void addAccount(@NonNull UserAccount account) {
        accountRepository.addAccount(account);
    }

    @NonNull
    public List<UserAccount> getAccounts() {
        var json = appPreferences.getString("accounts", "[]");
        var type = new TypeToken<List<UserAccount>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public void saveAccounts(@NonNull List<UserAccount> accounts) {
        accountRepository.saveAccounts(accounts);
    }

    @Nullable
    public UserAccount getDefaultAccount() {
        return accountRepository.getDefaultAccount();
    }

    public void setDefaultAccount(@NonNull UserAccount account) {
        accountRepository.setDefaultAccount(account);
    }

    public void removeAccount(@NonNull UserAccount account) {
        accountRepository.removeAccount(account);
    }
}