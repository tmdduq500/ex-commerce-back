CREATE TABLE cart (
                      id         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'PK',
                      user_id    BIGINT NOT NULL COMMENT 'FK: users.id',
                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시각',
                      updated_at TIMESTAMP NULL COMMENT '수정 시각',
                      CONSTRAINT fk_cart_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) COMMENT='장바구니';
CREATE INDEX idx_cart_user ON cart(user_id);

CREATE TABLE cart_item (
                           id         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'PK',
                           cart_id    BIGINT NOT NULL COMMENT 'FK: cart.id',
                           product_id BIGINT NOT NULL COMMENT 'FK: product.id',
                           quantity   INT NOT NULL DEFAULT 1 COMMENT '수량',
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시각',
                           updated_at TIMESTAMP NULL COMMENT '수정 시각',
                           CONSTRAINT fk_cart_item_cart    FOREIGN KEY (cart_id) REFERENCES cart(id) ON DELETE CASCADE,
                           CONSTRAINT fk_cart_item_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
                           CONSTRAINT uq_cart_item UNIQUE (cart_id, product_id)
) COMMENT='장바구니 항목';
CREATE INDEX idx_cart_item_cart ON cart_item(cart_id);
CREATE INDEX idx_cart_item_product ON cart_item(product_id);
