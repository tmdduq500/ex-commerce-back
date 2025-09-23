package com.osy.commerce.catalog.dto.product;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductListDto {
    private Long id;
    private String name;
    private Integer price;
    private String categorySlug;
    private String categoryName;
    private String thumbnailUrl;

    @QueryProjection
    public ProductListDto(Long id, String name, Integer price,
                          String categorySlug, String categoryName, String thumbnailUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.categorySlug = categorySlug;
        this.categoryName = categoryName;
        this.thumbnailUrl = thumbnailUrl;
    }
}
