package br.zestski.owlvintage.models.user;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a user model containing information about a user.
 *
 * @author Zestski
 */
@SuppressWarnings("unused")
public class UserModel {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("screen_name")
    private String screenName;

    @SerializedName("protected")
    private boolean isProtected;

    @SerializedName("profile_image_url")
    private String profileImageUrl;

    /**
     * Get the unique identifier of the user.
     *
     * @return The user identifier.
     */
    public String getId() {
        return id;
    }

    /**
     * Get the name of the user.
     *
     * @return The user name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the screen name of the user.
     *
     * @return The user screen name.
     */
    public String getScreenName() {
        return screenName;
    }

    /**
     * Check if the user is protected.
     *
     * @return True if protected, false otherwise.
     */
    public boolean isProtected() {
        return isProtected;
    }

    /**
     * Get the profile image URL of the user.
     *
     * @return The profile image URL.
     */
    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}