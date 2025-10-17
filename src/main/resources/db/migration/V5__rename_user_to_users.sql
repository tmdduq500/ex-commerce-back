-- V5__rename_user_to_users.sql

-- 1. 외래키 제약 삭제
ALTER TABLE user_address DROP FOREIGN KEY fk_user_address_user;
ALTER TABLE orders DROP FOREIGN KEY fk_orders_user;
ALTER TABLE review DROP FOREIGN KEY fk_review_user;
ALTER TABLE review_like DROP FOREIGN KEY fk_review_like_user;
ALTER TABLE product_like DROP FOREIGN KEY fk_like_user;
ALTER TABLE wishlist_item DROP FOREIGN KEY fk_wish_user;
ALTER TABLE coupon_redemption DROP FOREIGN KEY fk_coupon_redemption_user;
ALTER TABLE user_roles DROP FOREIGN KEY fk_user_roles_user;

-- 2. 테이블명 변경
RENAME TABLE user TO users;

-- 3. 외래키 재생성
ALTER TABLE user_address
    ADD CONSTRAINT fk_user_address_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE orders
    ADD CONSTRAINT fk_orders_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT;

ALTER TABLE review
    ADD CONSTRAINT fk_review_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE review_like
    ADD CONSTRAINT fk_review_like_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE product_like
    ADD CONSTRAINT fk_like_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE wishlist_item
    ADD CONSTRAINT fk_wish_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE coupon_redemption
    ADD CONSTRAINT fk_coupon_redemption_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE user_roles
    ADD CONSTRAINT fk_user_roles_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
