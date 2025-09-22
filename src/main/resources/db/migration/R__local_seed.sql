-- 카테고리
INSERT INTO category(name, slug, depth, created_at)
VALUES ('전자제품','electronics',0,NOW())
    ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 상품
INSERT INTO product(category_id, name, description, price, stock, status, created_at)
SELECT c.id, '샘플 폰', '샘플 설명', 399000, 50, 'ON', NOW()
FROM category c WHERE c.slug='electronics'
    ON DUPLICATE KEY UPDATE stock=50;

-- 이미지
INSERT INTO product_image(product_id, image_url, is_primary, sort_order, created_at)
SELECT p.id, 'https://picsum.photos/seed/phone/800/800', 1, 0, NOW()
FROM product p WHERE p.name='샘플 폰'
    ON DUPLICATE KEY UPDATE image_url=VALUES(image_url);
