-- V7__add_coupon_fields_to_orders.sql

ALTER TABLE orders
    ADD COLUMN coupon_id BIGINT NULL COMMENT '사용된 쿠폰 ID',
    ADD COLUMN discount_amount INT NOT NULL DEFAULT 0 COMMENT '할인 금액';

ALTER TABLE orders
    ADD CONSTRAINT fk_orders_coupon FOREIGN KEY (coupon_id)
        REFERENCES coupon(id)
        ON DELETE SET NULL;
