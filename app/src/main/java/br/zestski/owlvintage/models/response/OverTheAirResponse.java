package br.zestski.owlvintage.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Represents the response received from the Over-The-Air (OTA) update service.
 *
 * @author Zestski
 */
@SuppressWarnings("unused")
public class OverTheAirResponse {

    @SerializedName("error")
    private String error;

    @SerializedName("message")
    private String message;

    @SerializedName("versionCode")
    private int versionCode;

    @SerializedName("versionCodename")
    private String versionCodename;

    @SerializedName("downloadUrl")
    private String downloadUrl;

    @SerializedName("changelog")
    private String changelog;

    /**
     * Get the error message from the Over-The-Air response.
     *
     * @return The error message.
     */
    public String getError() {
        return error;
    }

    /**
     * Get the general message from the Over-The-Air response.
     *
     * @return The general message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get the version code of the update from the Over-The-Air response.
     *
     * @return The version code of the update.
     */
    public int getVersionCode() {
        return versionCode;
    }

    /**
     * Get the version codename of the update from the Over-The-Air response.
     *
     * @return The version codename of the update.
     */
    public String getVersionCodename() {
        return versionCodename;
    }

    /**
     * Get the download URL of the update from the Over-The-Air response.
     *
     * @return The download URL of the update.
     */
    public String getDownloadUrl() {
        return downloadUrl;
    }

    /**
     * Get the changelog of the update from the Over-The-Air response.
     *
     * @return The changelog of the update.
     */
    public String getChangelog() {
        return changelog;
    }
}