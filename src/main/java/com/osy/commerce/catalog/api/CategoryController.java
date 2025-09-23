package com.osy.commerce.catalog.api;

import com.osy.commerce.catalog.dto.category.CategoryDetailDto;
import com.osy.commerce.catalog.dto.category.CategoryDto;
import com.osy.commerce.catalog.dto.category.CategoryListCond;
import com.osy.commerce.catalog.service.CategoryService;
import com.osy.commerce.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public ApiResponse<List<CategoryDto>> getCategoryList(@RequestParam(required = false) String parentSlug) {
        return ApiResponse.ok(categoryService.getCategoryList(new CategoryListCond(parentSlug)));
    }

    @GetMapping("/categories/{slug}")
    public ApiResponse<CategoryDetailDto> getCategoryBySlug(@PathVariable String slug) {
        return ApiResponse.ok(categoryService.getCategoryBySlug(slug));
    }
}
