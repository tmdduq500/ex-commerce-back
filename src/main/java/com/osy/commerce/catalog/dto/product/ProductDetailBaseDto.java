package com.osy.commerce.catalog.dto.product;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDetailBaseDto {
    public final Long id;
    public final String name;
    public final String description;
    public final Integer price;
    public final Integer stock;
    public final String status;
    public final String categorySlug;
    public final String categoryName;

    @QueryProjection
    public ProductDetailBaseDto(Long id, String name, String description, Integer price,
                                Integer stock, String status, String categorySlug, String categoryName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.status = status;
        this.categorySlug = categorySlug;
        this.categoryName = categoryName;
    }
}
