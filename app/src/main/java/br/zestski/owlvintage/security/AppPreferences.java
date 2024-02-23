package br.zestski.owlvintage.security;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

/**
 * Helper class to manage application preferences.
 *
 * @author Zestski
 */
public class AppPreferences {

    private final SharedPreferences sharedPreferences;

    /**
     * Constructs a new AppPreferences instance.
     *
     * @param context The application context.
     */
    public AppPreferences(
            @NonNull Context context
    ) {
        this.sharedPreferences = SecureSharedPreferencesHelper.initSecureSharedPreferences(context);
    }

    /**
     * Saves a boolean value to the preferences.
     *
     * @param key   The preference key.
     * @param value The boolean value to save.
     */
    public void setBoolean(
            @NonNull String key,
            boolean value
    ) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    /**
     * Saves a string value to the preferences.
     *
     * @param key   The preference key.
     * @param value The string value to save.
     */
    public void setString(
            @NonNull String key,
            @NonNull String value
    ) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    /**
     * Retrieves a boolean value from the preferences.
     *
     * @param key The preference key.
     * @return The boolean value, or false if the key is not found.
     */
    public boolean getBoolean(
            @NonNull String key
    ) {
        return sharedPreferences.getBoolean(key, false);
    }

    /**
     * Retrieves a string value from the preferences.
     *
     * @param key The preference key.
     * @return The string value, or null if the key is not found.
     */
    public String getString(
            @NonNull String key
    ) {
        return sharedPreferences.getString(key, null);
    }
}