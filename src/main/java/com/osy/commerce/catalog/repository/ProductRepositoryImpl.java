package com.osy.commerce.catalog.repository;

import com.osy.commerce.catalog.dto.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.osy.commerce.catalog.domain.QCategory.category;
import static com.osy.commerce.catalog.domain.QProduct.product;
import static com.osy.commerce.catalog.domain.QProductImage.productImage;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ProductListDto> getProductList(ProductSearchCond cond) {
        Pageable pageable = PageRequest.of(cond.getPage(), cond.getSize());

        var order = switch (cond.getSort() == null ? "" : cond.getSort()) {
            case "priceAsc" -> product.price.asc();
            case "priceDesc" -> product.price.desc();
            case "nameAsc" -> product.name.asc();
            case "nameDesc" -> product.name.desc();
            default -> product.createdAt.desc();
        };

        StringExpression thumbCase =
                new CaseBuilder()
                        .when(productImage.isPrimary.isTrue()).then(productImage.imageUrl)
                        .otherwise((String) null);

        var thumbnailExpr = Expressions.stringTemplate("max({0})", thumbCase);

        List<ProductListDto> content = queryFactory
                .select(new QProductListDto(
                        product.id, product.name, product.price, category.slug, category.name, thumbnailExpr
                ))
                .from(product)
                .leftJoin(product.category, category)
                .leftJoin(productImage).on(productImage.product.id.eq(product.id))
                .where(
                        likeKeyword(cond.getKeyword()),
                        eqStatus(cond.getStatus()),
                        eqCategorySlug(cond.getCategorySlug()),
                        gtePrice(cond.getMinPrice()),
                        ltePrice(cond.getMaxPrice())
                )
                .groupBy(product.id, product.name, product.price, category.slug, category.name)
                .orderBy(order)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.category, category)
                .where(
                        likeKeyword(cond.getKeyword()),
                        eqStatus(cond.getStatus()),
                        eqCategorySlug(cond.getCategorySlug()),
                        gtePrice(cond.getMinPrice()),
                        ltePrice(cond.getMaxPrice())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    @Override
    public ProductDetailDto getProduct(Long id) {
        var base = queryFactory
                .select(new QProductDetailBaseDto(
                        product.id, product.name, product.description, product.price, product.stock, product.status,
                        category.slug, category.name
                ))
                .from(product)
                .leftJoin(product.category, category)
                .where(product.id.eq(id))
                .fetchOne();

        if (base == null) return null;

        List<String> images = queryFactory
                .select(productImage.imageUrl)
                .from(productImage)
                .where(productImage.product.id.eq(id))
                .orderBy(productImage.isPrimary.desc(), productImage.sortOrder.asc())
                .fetch();

        return new ProductDetailDto(
                base.id, base.name, base.description, base.price, base.stock,
                base.status, base.categorySlug, base.categoryName, images
        );
    }

    private BooleanExpression likeKeyword(String kw) {
        if (kw == null || kw.isBlank()) return null;
        String like = "%" + kw.trim() + "%";
        return product.name.likeIgnoreCase(like);
    }

    private BooleanExpression eqStatus(String st) {
        return (st == null || st.isBlank()) ? null : product.status.eq(st);
    }

    private BooleanExpression eqCategorySlug(String slug) {
        return (slug == null || slug.isBlank()) ? null : product.category.slug.eq(slug);
    }

    private BooleanExpression gtePrice(Integer v) {
        return v == null ? null : product.price.goe(v);
    }

    private BooleanExpression ltePrice(Integer v) {
        return v == null ? null : product.price.loe(v);
    }
}
