package br.zestski.owlvintage.models.status;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import br.zestski.owlvintage.models.embed.EmbedModel;
import br.zestski.owlvintage.models.user.UserModel;

/**
 * Represents a status model containing information about a user's status update.
 *
 * @author Zestski
 */
@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class StatusModel {

    @SerializedName("user")
    private UserModel userModel;

    @SerializedName("text")
    private String text;

    @SerializedName("id")
    private long id;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("source")
    private String source;

    @SerializedName("favorited")
    private boolean favorited;

    @SerializedName("reply")
    private UserModel replyUserModel;

    @SerializedName("embed")
    private EmbedModel embed;

    /**
     * Get the user associated with this status.
     *
     * @return The user model.
     */
    public UserModel getUser() {
        return userModel;
    }

    /**
     * Get the text content of this status.
     *
     * @return The text content.
     */
    public String getText() {
        return text;
    }

    /**
     * Get the unique identifier of this status.
     *
     * @return The status identifier.
     */
    public long getId() {
        return id;
    }

    /**
     * Get the formatted creation date of this status.
     *
     * @return The formatted creation date.
     */
    public String getCreatedAt() {
        var formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);

        try {
            var createdAtInstant = LocalDateTime.parse(this.createdAt, formatter)
                    .atOffset(ZoneOffset.UTC)
                    .toInstant();

            var createdAtZonedDateTime = ZonedDateTime.ofInstant(createdAtInstant, ZoneId.systemDefault());
            var now = ZonedDateTime.now();
            long diffSeconds = now.toEpochSecond() - createdAtZonedDateTime.toEpochSecond();

            if (diffSeconds < 60) {
                return diffSeconds + "s";
            } else if (diffSeconds < 3600) {
                long minutesAgo = diffSeconds / 60;
                return (minutesAgo > 1) ? minutesAgo + "m" : "1m";
            } else if (diffSeconds < 86400) {
                long hoursAgo = diffSeconds / 3600;
                return (hoursAgo > 1) ? hoursAgo + "h" : "1h";
            } else {
                var ldtNow = LocalDateTime.now();
                if (ldtNow.getYear() == createdAtZonedDateTime.getYear()) {
                    return createdAtZonedDateTime.format(DateTimeFormatter.ofPattern("hh:mm - dd MMM")).toLowerCase();
                } else {
                    return createdAtZonedDateTime.format(DateTimeFormatter.ofPattern("hh:mm - dd MMM yy")).toLowerCase();
                }
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * Get the source of this status.
     *
     * @return The status source.
     */
    public String getSource() {
        return source;
    }

    /**
     * Check if this status has been favorited.
     *
     * @return True if favorited, false otherwise.
     */
    public boolean isFavorited() {
        return favorited;
    }

    /**
     * Get the user being replied to in this status.
     *
     * @return The replied user model.
     */
    public UserModel getReplyUser() {
        return replyUserModel;
    }

    /**
     * Get the embedded media associated with this status.
     *
     * @return The embed model.
     */
    public EmbedModel getEmbed() {
        return embed;
    }
}