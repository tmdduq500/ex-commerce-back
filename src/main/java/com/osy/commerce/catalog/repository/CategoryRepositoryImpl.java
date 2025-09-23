package com.osy.commerce.catalog.repository;

import com.osy.commerce.catalog.dto.category.CategoryDetailDto;
import com.osy.commerce.catalog.dto.category.CategoryDto;
import com.osy.commerce.catalog.dto.category.CategoryListCond;
import com.osy.commerce.catalog.dto.category.QCategoryDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.osy.commerce.catalog.domain.QCategory.category;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CategoryDto> getCategoryList(CategoryListCond cond) {
        BooleanExpression parentFilter = (cond == null || cond.getParentSlug() == null)
                ? category.parent.isNull()
                : category.parent.slug.eq(cond.getParentSlug());

        return queryFactory
                .select(new QCategoryDto(
                        category.id, category.name, category.slug, category.depth
                ))
                .from(category)
                .where(parentFilter)
                .orderBy(category.name.asc())
                .fetch();
    }

    @Override
    public CategoryDetailDto getCategoryBySlug(String slug) {
        // base
        CategoryDto base = queryFactory
                .select(new QCategoryDto(
                        category.id, category.name, category.slug, category.depth
                ))
                .from(category)
                .where(category.slug.eq(slug))
                .fetchOne();
        if (base == null) return null;

        // parent
        CategoryDto parentDto = queryFactory
                .select(new QCategoryDto(
                        category.id, category.name, category.slug, category.depth
                ))
                .from(category)
                .where(category.id.eq(
                        queryFactory.select(category.parent.id)
                                .from(category)
                                .where(category.slug.eq(slug))
                ))
                .fetchOne();

        // children
        List<CategoryDto> children = queryFactory
                .select(new QCategoryDto(
                        category.id, category.name, category.slug, category.depth
                ))
                .from(category)
                .where(category.parent.slug.eq(slug))
                .orderBy(category.name.asc())
                .fetch();

        CategoryDetailDto dto = new CategoryDetailDto();
        dto.setId(base.getId());
        dto.setName(base.getName());
        dto.setSlug(base.getSlug());
        dto.setDepth(base.getDepth());
        dto.setParent(parentDto);
        dto.setChildren(children);
        return dto;
    }
}
