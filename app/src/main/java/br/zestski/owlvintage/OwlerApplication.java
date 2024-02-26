package br.zestski.owlvintage;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.android.material.color.DynamicColors;

import br.zestski.owlvintage.account.AccountManager;
import br.zestski.owlvintage.security.AppPreferences;

/**
 * Custom Application class for the Owler application.
 * Initializes application-wide resources and preferences.
 *
 * @author Zestski
 */
public class OwlerApplication extends Application {

    private static AppPreferences appPreferences;

    private static AccountManager accountManager;

    /**
     * Gets the account manager for the application.
     *
     * @return The account manager for the application.
     */
    public static AccountManager getAccountManager() {
        return accountManager;
    }

    /**
     * Sets a boolean value in the application preferences.
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     */
    @Deprecated
    public static void setSpBoolean(
            @NonNull String key,
            boolean value
    ) {
        appPreferences.setBoolean(key, value);
    }

    /**
     * Sets a string value in the application preferences.
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     */
    @Deprecated
    public static void setSpString(
            @NonNull String key,
            @NonNull String value
    ) {
        appPreferences.setString(key, value);
    }

    /**
     * Retrieves a boolean value from the application preferences.
     *
     * @param key The name of the preference to retrieve.
     * @return The preference value if it exists, or false if it doesn't.
     */
    @Deprecated
    public static boolean getSpBoolean(
            @NonNull String key
    ) {
        return appPreferences.getBoolean(key);
    }

    /**
     * Retrieves a string value from the application preferences.
     *
     * @param key The name of the preference to retrieve.
     * @return The preference value if it exists, or null if it doesn't.
     */
    @Deprecated
    public static String getSpString(
            @NonNull String key
    ) {
        return appPreferences.getString(key, null);
    }

    /**
     * Called when the application is starting.
     * Initializes application-wide resources and preferences.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        appPreferences = new AppPreferences(this);
        accountManager = new AccountManager(appPreferences);

        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}