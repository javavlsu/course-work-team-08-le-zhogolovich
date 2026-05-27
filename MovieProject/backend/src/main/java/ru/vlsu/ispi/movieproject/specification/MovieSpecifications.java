package ru.vlsu.ispi.movieproject.specification;

import jakarta.persistence.criteria.Expression;
import org.springframework.data.jpa.domain.Specification;
import ru.vlsu.ispi.movieproject.model.Movie;

public class MovieSpecifications {
    public static Specification<Movie> hasTag(Long tagId) {
        return (root, query, cb) -> {
            if (tagId == null) return null;
            query.distinct(true);

            return cb.equal(root.get("tags").get("id"), tagId);
        };
    }

    public static Specification<Movie> hasGenre(Long genreId) {
        return (root, query, cb) -> {
            if (genreId == null) return null;
            query.distinct(true);

            return cb.equal(root.get("genres").get("id"), genreId);
        };
    }

    public static Specification<Movie> hasYear(Integer year) {
        return (root, query, cb) -> {
            if (year == null) return null;

            return cb.equal(root.get("releaseYear"), year);
        };
    }

    public static Specification<Movie> hasCountry(Long countryId) {
        return (root, query, cb) -> {
            if (countryId == null) return null;
            query.distinct(true);

            return cb.equal(root.get("countries").get("id"), countryId);
        };
    }

    public static Specification<Movie> titleContains(String text) {
        return (root, query, cb) -> {
            if (text == null || text.isBlank()) return null;

            return cb.like(cb.lower(root.get("name")), "%" + text.toLowerCase() + "%");
        };
    }

    public static Specification<Movie> orderByName(String sortOrder) {
        return (root, query, cb) -> {
            Expression<String> expression = cb.coalesce(cb.nullif(root.get("name"), ""), root.get("originalName"));
            if ("asc".equalsIgnoreCase(sortOrder)) {
                query.orderBy(cb.asc(expression));
            }
            else query.orderBy(cb.desc(expression));

            return null;
        };
    }
}
