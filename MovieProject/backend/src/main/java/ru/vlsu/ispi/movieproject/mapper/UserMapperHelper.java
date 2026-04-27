package ru.vlsu.ispi.movieproject.mapper;

import ru.vlsu.ispi.movieproject.model.User;

public class UserMapperHelper {

    public static final String DELETED_USERNAME = "Удалённый пользователь";

    public static String mapUsername(User user) {
        if (user == null || user.getDeleted()) {
            return DELETED_USERNAME;
        }
        return user.getUsername();
    }

    public static String mapAvatar(User user) {
        if (user == null || user.getDeleted()) {
            return null;
        }
        return user.getAvatarUrl();
    }
}