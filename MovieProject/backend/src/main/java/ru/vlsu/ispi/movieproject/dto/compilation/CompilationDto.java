package ru.vlsu.ispi.movieproject.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.vlsu.ispi.movieproject.dto.movie.MovieDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {
    private Long id;
    private String title;
    private String description;
    private Long authorId;
    private String authorName;
    private Boolean isPublic;
    private String coverUrl;
    private Long likesCount;
    private Boolean likedByCurrentUser;
    private Boolean isSubscribed;
    private Long subscribersCount;
    private List<MovieDto> movies;
}
