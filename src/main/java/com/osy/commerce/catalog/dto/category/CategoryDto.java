package com.osy.commerce.catalog.dto.category;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDto {
    private Long id;
    private String name;
    private String slug;
    private int depth;

    @QueryProjection
    public CategoryDto(Long id, String name, String slug, int depth) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.depth = depth;
    }
}