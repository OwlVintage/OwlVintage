package br.zestski.owlvintage.models.embed;

/**
 * Enumeration representing different types of embedded content.
 *
 * @author Zestski
 */
public enum EmbedType {
    /**
     * Represents an embedded image.
     */
    IMAGE,

    /**
     * Represents embedded audio content.
     */
    AUDIO,

    /**
     * Represents embedded video content.
     */
    VIDEO,

    /**
     * Represents an embedded file.
     */
    FILE,

    /**
     * Represents an unknown or unsupported type of embedded content.
     */
    UNKNOWN
}