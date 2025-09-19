package com.osy.commerce.catalog.repository;

import com.osy.commerce.catalog.dto.ProductDetailDto;
import com.osy.commerce.catalog.dto.ProductListDto;
import com.osy.commerce.catalog.dto.ProductSearchCond;
import org.springframework.data.domain.Page;

public class ProductRepositoryImpl implements ProductRepositoryCustom {

    @Override
    public Page<ProductListDto> searchProductList(ProductSearchCond cond) {
        return null;
    }

    @Override
    public ProductDetailDto searchProduct(Long id) {
        return null;
    }
}
