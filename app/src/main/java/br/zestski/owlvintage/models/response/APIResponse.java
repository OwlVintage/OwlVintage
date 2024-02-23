package br.zestski.owlvintage.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a generic API response.
 *
 * @author Zestski
 */
@SuppressWarnings("unused")
public class APIResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("success")
    private String success;

    @SerializedName("error")
    private String error;

    /**
     * Get the message from the API response.
     *
     * @return The message from the API response.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get the success status from the API response.
     *
     * @return The success status from the API response.
     */
    public String getSuccess() {
        return success;
    }

    /**
     * Get the error message from the API response.
     *
     * @return The error message from the API response.
     */
    public String getError() {
        return error;
    }
}