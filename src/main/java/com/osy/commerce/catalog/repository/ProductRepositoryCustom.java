package com.osy.commerce.catalog.repository;

import com.osy.commerce.catalog.dto.ProductDetailDto;
import com.osy.commerce.catalog.dto.ProductListDto;
import com.osy.commerce.catalog.dto.ProductSearchCond;
import org.springframework.data.domain.Page;

public interface ProductRepositoryCustom {
    Page<ProductListDto> searchProductList(ProductSearchCond cond);
    ProductDetailDto searchProduct(Long id);
}
