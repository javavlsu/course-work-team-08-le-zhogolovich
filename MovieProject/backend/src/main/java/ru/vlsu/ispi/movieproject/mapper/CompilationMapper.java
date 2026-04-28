package ru.vlsu.ispi.movieproject.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.vlsu.ispi.movieproject.dto.compilation.CompilationDto;
import ru.vlsu.ispi.movieproject.dto.compilation.CreateCompilationRequest;
import ru.vlsu.ispi.movieproject.dto.movie.MovieDto;
import ru.vlsu.ispi.movieproject.model.Compilation;
import ru.vlsu.ispi.movieproject.projection.CompilationProjection;

import java.util.List;

@Component
@AllArgsConstructor
public class CompilationMapper {
    public Compilation fromRequest(CreateCompilationRequest request) {
        Compilation c = new Compilation();
        c.setTitle(request.getTitle());
        c.setDescription(request.getDescription());
        c.setIsPublic(request.getIsPublic());

        return c;
    }

    public CompilationDto fromView(CompilationProjection p, List<MovieDto> movies) {
        return new CompilationDto(
                p.getId(),
                p.getTitle(),
                p.getDescription(),
                p.getAuthorId(),
                p.getAuthorName(),
                p.getIsPublic(),
                p.getCoverUrl(),
                p.getLikesCount(),
                p.getLikedByUser(),
                p.getIsSubscribed(),
                p.getSubscribersCount(),
                movies
        );
    }
}
