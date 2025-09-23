package com.osy.commerce.catalog.dto.category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDetailDto {
    private Long id;
    private String name;
    private String slug;
    private int depth;
    private CategoryDto parent;
    private List<CategoryDto> children;
}