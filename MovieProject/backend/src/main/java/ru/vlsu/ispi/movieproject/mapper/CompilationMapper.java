package ru.vlsu.ispi.movieproject.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.vlsu.ispi.movieproject.dto.compilation.CompilationDto;
import ru.vlsu.ispi.movieproject.dto.compilation.CreateCompilationRequest;
import ru.vlsu.ispi.movieproject.enums.FileDirectory;
import ru.vlsu.ispi.movieproject.model.Compilation;
import ru.vlsu.ispi.movieproject.projection.CompilationProjection;
import ru.vlsu.ispi.movieproject.service.FileStorageService;

import java.util.List;

@Component
@AllArgsConstructor
public class CompilationMapper {
    private final MovieMapper movieMapper;
    private final FileStorageService fileStorageService;

    public CompilationDto toDto(Compilation c, Long likesCount, boolean likedByUser) {
        return new CompilationDto(
                c.getId(),
                c.getTitle(),
                c.getDescription(),
                c.getAuthor().getId(),
                c.getAuthor().getUsername(),
                c.getIsPublic(),
                c.getCoverUrl(),
                likesCount,
                likedByUser,
                c.getMovies().stream().
                        map(movieMapper::toMovieDto)
                        .toList()
        );
    }

    public Compilation fromRequest(CreateCompilationRequest request) {
        Compilation c = new Compilation();
        c.setTitle(request.getTitle());
        c.setDescription(request.getDescription());
        c.setIsPublic(request.getIsPublic());

        MultipartFile cover = request.getCover();
        if (cover != null && !cover.isEmpty()) {
            c.setCoverUrl(fileStorageService.upload(cover, FileDirectory.COMPILATIONS.getFolder()));
        }
        else{
            c.setCoverUrl(null);
        }

        return c;
    }

    public CompilationDto fromProjection(CompilationProjection p) {
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
                List.of()
        );
    }
}
