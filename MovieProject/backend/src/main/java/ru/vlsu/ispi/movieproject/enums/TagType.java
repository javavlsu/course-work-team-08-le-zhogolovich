package ru.vlsu.ispi.movieproject.enums;

public enum TagType {
    THEME,
    TROPE,
    STYLE,
    MOOD,
    CHARACTER;

    public static TagType fromString(String s) {
        try{
            return TagType.valueOf(s.toUpperCase());
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("Неправильный тип тега: " + s);
        }
    }
}
