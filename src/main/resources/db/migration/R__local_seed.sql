-- ===========================
-- Repeatable seed: categories & sample products (idempotent)
-- ===========================

-- 0) Root category: electronics
INSERT INTO category(name, slug, depth, created_at)
SELECT '전자제품','electronics',0,NOW()
WHERE NOT EXISTS (SELECT 1 FROM category WHERE slug='electronics');

UPDATE category
SET name='전자제품', depth=0
WHERE slug='electronics';

-- Capture parent id (ensure single row usage even if duplicates exist)
SET @EID := (SELECT id FROM category WHERE slug='electronics' ORDER BY id LIMIT 1);

-- 1) Children: mobile, laptop
INSERT INTO category(parent_id, name, slug, depth, created_at)
SELECT @EID, '모바일', 'mobile', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM category WHERE slug='mobile');

UPDATE category
SET parent_id=@EID, name='모바일', depth=1
WHERE slug='mobile';

INSERT INTO category(parent_id, name, slug, depth, created_at)
SELECT @EID, '노트북', 'laptop', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM category WHERE slug='laptop');

UPDATE category
SET parent_id=@EID, name='노트북', depth=1
WHERE slug='laptop';

-- 2) Grandchild under mobile: android
SET @MID := (SELECT id FROM category WHERE slug='mobile' ORDER BY id LIMIT 1);

INSERT INTO category(parent_id, name, slug, depth, created_at)
SELECT @MID, '안드로이드', 'android', 2, NOW()
WHERE NOT EXISTS (SELECT 1 FROM category WHERE slug='android');

UPDATE category
SET parent_id=@MID, name='안드로이드', depth=2
WHERE slug='android';

-- 3) Sample product under 'mobile'
-- Use SKU as a natural unique key (V1 has UNIQUE on sku).
SET @MOBILE_ID := (SELECT id FROM category WHERE slug='mobile' ORDER BY id LIMIT 1);

INSERT INTO product(category_id, name, description, price, stock, sku, status, created_at)
SELECT @MOBILE_ID,
       '샘플 폰',
       '샘플 설명',
       399000,
       50,
       'SKU-SAMPLE-PHONE',
       'ON',
       NOW()
WHERE NOT EXISTS (SELECT 1 FROM product WHERE sku='SKU-SAMPLE-PHONE');

-- Keep fields updated if row already exists
UPDATE product
SET category_id=@MOBILE_ID,
    name='샘플 폰',
    description='샘플 설명',
    price=399000,
    stock=50,
    status='ON'
WHERE sku='SKU-SAMPLE-PHONE';

-- 4) Primary image (sort_order = 0) for the sample product
SET @PID := (SELECT id FROM product WHERE sku='SKU-SAMPLE-PHONE' ORDER BY id LIMIT 1);

INSERT INTO product_image(product_id, image_url, is_primary, sort_order, created_at)
SELECT @PID, 'https://picsum.photos/seed/phone/800/800', 1, 0, NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM product_image WHERE product_id=@PID AND sort_order=0
);

UPDATE product_image
SET image_url='https://picsum.photos/seed/phone/800/800', is_primary=1
WHERE product_id=@PID AND sort_order=0;
