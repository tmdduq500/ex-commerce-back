package com.osy.commerce.catalog.repository;

import com.osy.commerce.catalog.dto.product.ProductDetailDto;
import com.osy.commerce.catalog.dto.product.ProductListDto;
import com.osy.commerce.catalog.dto.product.ProductSearchCond;
import org.springframework.data.domain.Page;

public interface ProductRepositoryCustom {
    Page<ProductListDto> getProductList(ProductSearchCond cond);
    ProductDetailDto getProduct(Long id);
}
