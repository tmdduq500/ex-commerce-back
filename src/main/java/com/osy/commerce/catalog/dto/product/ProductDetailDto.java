package com.osy.commerce.catalog.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ProductDetailDto {
    private Long id;
    private String name;
    private String description;
    private Integer price;
    private Integer stock;
    private String status;
    private String categorySlug;
    private String categoryName;
    private List<String> imageUrls;
}
