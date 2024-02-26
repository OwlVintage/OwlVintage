package br.zestski.owlvintage.util;

import android.util.Base64;

import androidx.annotation.NonNull;

public class B64Util {
    public static String encodeBase64(
            @NonNull String input
    ) {
        var bytes = input.getBytes();
        var encodedBytes = Base64.encode(bytes, Base64.NO_WRAP);
        return new String(encodedBytes);
    }
}