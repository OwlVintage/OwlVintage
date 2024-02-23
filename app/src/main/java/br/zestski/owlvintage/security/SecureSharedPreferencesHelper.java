package br.zestski.owlvintage.security;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Helper class to create a secure SharedPreferences instance.
 *
 * @author Zestski
 */
@SuppressLint("HardwareIds")
public class SecureSharedPreferencesHelper {

    /**
     * Initializes a secure instance of SharedPreferences.
     * <p>
     * This method creates an EncryptedSharedPreferences instance with a master key
     * generated based on the Android device's unique ID.
     *
     * @param context The context used to access the SharedPreferences.
     * @return A secure SharedPreferences instance, or null if initialization fails.
     */
    public static SharedPreferences initSecureSharedPreferences(
            @NonNull Context context
    ) {
        try {
            final var keyGenParameterSpec = new KeyGenParameterSpec.Builder(
                    MasterKey.DEFAULT_MASTER_KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE)
                    .build();

            final var masterKey = new MasterKey.Builder(context)
                    .setKeyGenParameterSpec(keyGenParameterSpec)
                    .build();

            return EncryptedSharedPreferences.create(
                    context,
                    Settings.Secure.getString(
                            context.getContentResolver(),
                            Settings.Secure.ANDROID_ID
                    ),
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        } catch (GeneralSecurityException | IOException exception) {
            Log.e("SecureSharedPreferencesHelper", "Error initializing EncryptedSharedPreferences", exception);
            return null;
        }
    }
}