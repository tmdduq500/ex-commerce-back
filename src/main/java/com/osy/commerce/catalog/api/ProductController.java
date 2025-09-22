package com.osy.commerce.catalog.api;

import com.osy.commerce.catalog.dto.ProductDetailDto;
import com.osy.commerce.catalog.dto.ProductListDto;
import com.osy.commerce.catalog.dto.ProductSearchCond;
import com.osy.commerce.catalog.service.ProductService;
import com.osy.commerce.global.response.ApiResponse;
import com.osy.commerce.global.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/products")
    public ApiResponse<PageResponse<ProductListDto>> list(ProductSearchCond cond) {
        Page<ProductListDto> page = productService.getProductList(cond);
        return ApiResponse.ok(PageResponse.from(page));
    }

    @GetMapping("/products/{id}")
    public ApiResponse<ProductDetailDto> detail(@PathVariable Long id) {
        return ApiResponse.ok(productService.getProduct(id));
    }
}
