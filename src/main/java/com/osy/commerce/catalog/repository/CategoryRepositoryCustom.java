package com.osy.commerce.catalog.repository;

import com.osy.commerce.catalog.dto.category.CategoryDetailDto;
import com.osy.commerce.catalog.dto.category.CategoryDto;
import com.osy.commerce.catalog.dto.category.CategoryListCond;
import com.osy.commerce.catalog.dto.product.ProductDetailDto;
import com.osy.commerce.catalog.dto.product.ProductListDto;
import com.osy.commerce.catalog.dto.product.ProductSearchCond;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryRepositoryCustom {
    List<CategoryDto> getCategoryList(CategoryListCond cond);
    CategoryDetailDto getCategoryBySlug(String slug);
}
