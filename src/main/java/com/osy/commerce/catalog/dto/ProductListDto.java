package com.osy.commerce.catalog.dto;

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

}
