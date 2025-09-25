package com.osy.commerce.catalog.api;

import com.osy.commerce.catalog.dto.category.CategoryDto;
import com.osy.commerce.catalog.dto.category.CategoryListCond;
import com.osy.commerce.catalog.service.CategoryService;
import com.osy.commerce.global.response.ApiResponse;
import com.osy.commerce.global.response.Responses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse> getCategoryList(@RequestParam(required = false) String parentSlug) {
        List<CategoryDto> list = categoryService.getCategoryList(new CategoryListCond(parentSlug));
        return Responses.ok(list);
    }

    @GetMapping("/categories/{slug}")
    public ResponseEntity<ApiResponse> getCategoryBySlug(@PathVariable String slug) {
        return Responses.ok(categoryService.getCategoryBySlug(slug));
    }
}
