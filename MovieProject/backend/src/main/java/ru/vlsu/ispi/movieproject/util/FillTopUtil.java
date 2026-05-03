package ru.vlsu.ispi.movieproject.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class FillTopUtil {
    public static <T, ID> List<T> fillTop(
            List<T> top,
            int limit,
            Function<List<ID>, List<T>> fallbackFetcher,
            Function<T, ID> idFetcher
    ) {
        if (top == null) top = new ArrayList<>();
        if (top.size() >= limit) return top;

        List<ID> excludeIds = top.stream().map(idFetcher).filter(Objects::nonNull).toList();

        List<ID> safeExcludeIds = excludeIds.isEmpty() ? null : excludeIds;

        List<T> additional = fallbackFetcher.apply(safeExcludeIds);

        if (additional != null && !additional.isEmpty()) {
            Set<ID> existingIds = new HashSet<>(excludeIds);

            for (T item : additional) {
                ID id = idFetcher.apply(item);
                if (id != null && !existingIds.contains(id)) {
                    top.add(item);
                    existingIds.add(id);

                    if (top.size() >= limit) break;
                }
            }
        }

        return top;
    }
}
