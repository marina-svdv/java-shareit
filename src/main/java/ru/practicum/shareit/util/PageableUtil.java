package ru.practicum.shareit.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.InvalidPaginationParameterException;

public class PageableUtil {
    public static Pageable createPageable(int from, int size, Sort sort) {
        if (from < 0 || size <= 0) {
            throw new InvalidPaginationParameterException("Pagination parameters 'from' and 'size' must be non-negative" +
                    ", and 'size' must be greater than zero.");
        }
        int page = from / size;
        return PageRequest.of(page, size, sort);
    }
}
