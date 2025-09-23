package com.osy.commerce.catalog.dto.product;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductSearchCond {
    private String keyword;
    private String categorySlug;
    private String status;
    private Integer minPrice;
    private Integer maxPrice;

    private String sort;
    private int page = 0;
    private int size = 12;
}
