package com.osy.commerce.catalog.service;

import com.osy.commerce.catalog.dto.category.CategoryDetailDto;
import com.osy.commerce.catalog.dto.category.CategoryDto;
import com.osy.commerce.catalog.dto.category.CategoryListCond;
import com.osy.commerce.catalog.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryDto> getCategoryList(CategoryListCond cond) {
        return categoryRepository.getCategoryList(cond);
    }

    public CategoryDetailDto getCategoryBySlug(String slug) {
        return categoryRepository.getCategoryBySlug(slug);
    }
}
