package ru.vlsu.ispi.movieproject.projection;

public interface CompilationProjection {
    Long getId();
    String getTitle();
    String getDescription();
    Boolean getIsPublic();
    String getCoverUrl();
    Long getAuthorId();
    String getAuthorName();
    Long getLikesCount();
    Boolean getLikedByUser();
    Long getSubscribersCount();
    Boolean getIsSubscribed();
}
