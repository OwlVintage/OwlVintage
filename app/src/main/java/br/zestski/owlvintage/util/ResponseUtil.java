package br.zestski.owlvintage.util;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Utility class for handling Retrofit responses.
 *
 * @author Zestski
 */
public class ResponseUtil {

    /**
     * Parses the response and returns either the response body or an error object.
     *
     * @param response The Retrofit response object.
     * @param type     The type of the response body or error object.
     * @param <T>      The type of the response body or error object.
     * @return The response body if the response is successful, otherwise an error object.
     */
    public static <T> T responseFallback(
            @NonNull Response<T> response,
            @NonNull Type type
    ) {
        if (!response.isSuccessful()) {
            return parseErrorBody(response.errorBody(), type);
        }
        return response.body();
    }

    /**
     * Parses the error response body and returns an error object.
     *
     * @param errorBody The error response body.
     * @param type      The type of the error object.
     * @param <T>       The type of the error object.
     * @return The error object parsed from the error response body.
     */
    private static <T> T parseErrorBody(
            @Nullable ResponseBody errorBody,
            @NonNull Type type
    ) {
        try {
            if (errorBody != null) {
                var errorBodyString = errorBody.string();
                return new Gson().fromJson(errorBodyString, type);
            }
        } catch (IOException exception) {
            Log.e("ResponseUtil", "Error parsing error body", exception);
        }
        return null;
    }
}