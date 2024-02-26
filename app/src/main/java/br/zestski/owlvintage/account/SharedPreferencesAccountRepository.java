package br.zestski.owlvintage.account;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import br.zestski.owlvintage.account.interfaces.AccountRepository;
import br.zestski.owlvintage.account.interfaces.UserAccount;
import br.zestski.owlvintage.security.AppPreferences;

/**
 * Implementation of AccountRepository using SharedPreferences.
 *
 * @author Zestski
 */
public class SharedPreferencesAccountRepository implements AccountRepository {

    private static final String KEY_ACCOUNTS = "accounts";

    private final AppPreferences appPreferences;
    private final Gson gson;

    public SharedPreferencesAccountRepository(
            @NonNull AppPreferences appPreferences,
            @NonNull Gson gson
    ) {
        this.appPreferences = appPreferences;
        this.gson = gson;
    }

    @Override
    public void addAccount(@NonNull UserAccount account) {
        List<UserAccount> accounts = getAccounts();
        accounts.add(account);
        saveAccounts(accounts);
    }

    @NonNull
    @Override
    public List<UserAccount> getAccounts() {
        String json = appPreferences.getString(KEY_ACCOUNTS, "[]");
        return gson.fromJson(json, new TypeToken<List<UserAccount>>() {
        }.getType());
    }

    @Override
    public void saveAccounts(@NonNull List<UserAccount> accounts) {
        String json = gson.toJson(accounts);
        appPreferences.setString(KEY_ACCOUNTS, json);
    }


    @Nullable
    @Override
    public UserAccount getDefaultAccount() {
        UserAccount defaultAccount = null;
        var accounts = getAccounts();

        for (var account : accounts) {
            if (account.isDefault()) {
                defaultAccount = account;
                break;
            }
        }

        if (defaultAccount == null && !accounts.isEmpty()) {
            defaultAccount = accounts.get(0);
            defaultAccount.setDefault(true);
            saveAccounts(accounts);
        }

        return defaultAccount;
    }

    @Override
    public void setDefaultAccount(@NonNull UserAccount account) {
        var accounts = getAccounts();

        for (var acc : accounts) {
            acc.setDefault(acc.equals(account));
        }

        saveAccounts(accounts);
    }

    @Override
    public void removeAccount(@NonNull UserAccount account) {
        var accounts = getAccounts();

        accounts.remove(account);
        saveAccounts(accounts);
    }
}