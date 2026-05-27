package ru.vlsu.ispi.movieproject.service;

public interface CurrentUserService {
    Long getCurrentUserID();
    boolean hasRole(String role);
    boolean isAdmin();
}
