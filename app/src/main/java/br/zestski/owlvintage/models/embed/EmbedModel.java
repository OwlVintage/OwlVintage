package br.zestski.owlvintage.models.embed;

import com.google.gson.annotations.SerializedName;

/**
 * Represents an embed model containing information about embedded content.
 * @author Zestski
 */
@SuppressWarnings("unused")
public class EmbedModel {

    @SerializedName("type")
    private int type;

    @SerializedName("url")
    private String url;

    @SerializedName("width")
    private int width;

    @SerializedName("height")
    private int height;

    /**
     * Gets the type of the embedded content.
     *
     * @return The type of the embedded content.
     */
    public int getType() {
        return type;
    }

    /**
     * Gets the URL of the embedded content.
     *
     * @return The URL of the embedded content.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Gets the width of the embedded content.
     *
     * @return The width of the embedded content.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height of the embedded content.
     *
     * @return The height of the embedded content.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the embed type based on the type integer value.
     *
     * @return The embed type.
     */
    public EmbedType getEmbedType() {
        return switch (type) {
            case 0 -> EmbedType.IMAGE;
            case 1 -> EmbedType.VIDEO;
            case 2 -> EmbedType.AUDIO;
            case 3 -> EmbedType.FILE;
            default -> EmbedType.UNKNOWN;
        };
    }
}