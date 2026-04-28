package ru.vlsu.ispi.movieproject.projection;

import java.time.LocalDateTime;

public interface ReviewProjection {
    Long getId();
    Long getMovieId();
    String getAuthorName();
    String getAuthorAvatar();
    String getTitle();
    String getContent();
    String getStatus();
    LocalDateTime getCreatedAt();
    int getLikesCount();
    boolean getIsLikedByCurrentUser();
    String getMovieCover();
}