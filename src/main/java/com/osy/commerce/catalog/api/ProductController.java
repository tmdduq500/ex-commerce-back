package com.osy.commerce.catalog.api;

import com.osy.commerce.catalog.dto.product.ProductListDto;
import com.osy.commerce.catalog.dto.product.ProductSearchCond;
import com.osy.commerce.catalog.service.ProductService;
import com.osy.commerce.global.response.ApiResponse;
import com.osy.commerce.global.response.PageResponse;
import com.osy.commerce.global.response.Responses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse> getProductList(ProductSearchCond cond) {
        Page<ProductListDto> page = productService.getProductList(cond);
        return Responses.ok(PageResponse.from(page));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponse> getProduct(@PathVariable Long id) {
        return Responses.ok(productService.getProduct(id));
    }

}
