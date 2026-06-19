package com.networx.networx.utils;

import com.networx.networx.dto.responses.PageResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PaginationUtils {

    public static <T> PageResponseDTO<T> getPageResponse(Page<T> pageData) {

        return new PageResponseDTO<>(
                pageData.getContent(),
                pageData.getTotalElements(),
                pageData.getTotalPages(),
                pageData.getNumber() + 1,
                pageData.getSize(),
                pageData.hasNext()
        );
    }
    public static Pageable createPageable(Integer page, Integer size) {
//        int pageNumber = (page == null || page < 1) ? 0 : page - 1;
//        int pageSize = (size == null || size < 1) ? 10 : size;
        return PageRequest.of(page-1, size);
    }
}
