package com.osy.commerce.catalog.service;

import com.osy.commerce.catalog.dto.ProductDetailDto;
import com.osy.commerce.catalog.dto.ProductListDto;
import com.osy.commerce.catalog.dto.ProductSearchCond;
import com.osy.commerce.catalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Page<ProductListDto> getProductList(ProductSearchCond cond) {
        return productRepository.getProductList(cond);
    }

    public ProductDetailDto getProduct(Long id) {
        return productRepository.getProduct(id);
    }
}
