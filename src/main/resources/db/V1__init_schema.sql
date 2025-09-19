/* ===========================
   USERS & AUTH
   =========================== */
CREATE TABLE users (
                       id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'PK',
                       email         VARCHAR(255) NOT NULL UNIQUE COMMENT '로그인/식별용 이메일(유니크)',
                       password      VARCHAR(255) NULL COMMENT 'BCrypt 해시(소셜 계정은 NULL 가능)',
                       role          VARCHAR(32)  NOT NULL DEFAULT 'ROLE_USER' COMMENT '권한: ROLE_USER | ROLE_ADMIN 등',
                       provider      VARCHAR(32)  NULL COMMENT '인증 제공자: local | google | github ...',
                       provider_id   VARCHAR(255) NULL COMMENT '소셜 계정 식별자(서브 키)',
                       status        VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE' COMMENT '계정 상태: ACTIVE | BLOCKED | DELETED ...',
                       last_login_at TIMESTAMP NULL COMMENT '마지막 로그인 시간',
                       created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시각',
                       updated_at    TIMESTAMP NULL COMMENT '수정 시각'
) COMMENT='사용자 계정';

CREATE TABLE user_address (
                              id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'PK',
                              user_id     BIGINT NOT NULL COMMENT 'FK: users.id',
                              recipient   VARCHAR(100) NOT NULL COMMENT '수령인 이름',
                              phone       VARCHAR(30)  NULL COMMENT '연락처',
                              zipcode     VARCHAR(20)  NULL COMMENT '우편번호',
                              address1    VARCHAR(255) NOT NULL COMMENT '기본 주소',
                              address2    VARCHAR(255) NULL COMMENT '상세 주소',
                              city        VARCHAR(100) NULL COMMENT '도시',
                              state       VARCHAR(100) NULL COMMENT '주/도',
                              country     VARCHAR(2)   NULL DEFAULT 'KR' COMMENT '국가 코드(ISO-3166-1 alpha-2)',
                              is_default  TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '기본 배송지 여부(1/0)',
                              created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시각',
                              updated_at  TIMESTAMP NULL COMMENT '수정 시각',
                              CONSTRAINT fk_user_address_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) COMMENT='사용자 배송지';
CREATE INDEX idx_user_address_user ON user_address(user_id);
CREATE INDEX idx_user_address_default ON user_address(user_id, is_default);

/* ===========================
   CATALOG
   =========================== */
CREATE TABLE category (
                          id         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'PK',
                          parent_id  BIGINT NULL COMMENT '부모 카테고리 FK(루트는 NULL)',
                          name       VARCHAR(120) NOT NULL COMMENT '카테고리명',
                          slug       VARCHAR(160) NOT NULL UNIQUE COMMENT 'URL/고유 식별 슬러그(유니크)',
                          depth      INT NOT NULL DEFAULT 0 COMMENT '트리 깊이(루트=0)',
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시각',
                          updated_at TIMESTAMP NULL COMMENT '수정 시각',
                          CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES category(id) ON DELETE SET NULL
) COMMENT='상품 카테고리';
CREATE INDEX idx_category_parent ON category(parent_id);

CREATE TABLE product (
                         id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'PK',
                         category_id  BIGINT NULL COMMENT 'FK: category.id',
                         name         VARCHAR(255) NOT NULL COMMENT '상품명',
                         description  TEXT NULL COMMENT '상세 설명',
                         price        INT  NOT NULL COMMENT '정가/판매가(원화 정수)',
                         stock        INT  NOT NULL DEFAULT 0 COMMENT '재고 수량',
                         sku          VARCHAR(100) NULL UNIQUE COMMENT '재고 관리용 SKU(유니크)',
                         status       VARCHAR(32)  NOT NULL DEFAULT 'ON' COMMENT '상태: ON | OFF | HIDDEN ...',
                         created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시각',
                         updated_at   TIMESTAMP NULL COMMENT '수정 시각',
                         CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL
) COMMENT='상품';
CREATE INDEX idx_product_category ON product(category_id);
CREATE INDEX idx_product_status ON product(status);
CREATE INDEX idx_product_name ON product(name);

CREATE TABLE product_image (
                               id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'PK',
                               product_id  BIGINT NOT NULL COMMENT 'FK: product.id',
                               image_url   VARCHAR(500) NOT NULL COMMENT '이미지 URL(S3/CloudFront 등)',
                               is_primary  TINYINT(1) NOT NULL DEFAULT 0 COMMENT '대표 이미지 여부(1/0)',
                               sort_order  INT NOT NULL DEFAULT 0 COMMENT '노출 순서(오름차순)',
                               created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시각',
                               CONSTRAINT fk_product_image_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
) COMMENT='상품 이미지';
CREATE INDEX idx_product_image_prod ON product_image(product_id, sort_order);

CREATE TABLE product_like (
                              user_id     BIGINT NOT NULL COMMENT 'FK: users.id',
                              product_id  BIGINT NOT NULL COMMENT 'FK: product.id',
                              created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '좋아요 시각',
                              PRIMARY KEY (user_id, product_id),
                              CONSTRAINT fk_like_user    FOREIGN KEY (user_id)    REFERENCES users(id)    ON DELETE CASCADE,
                              CONSTRAINT fk_like_product FOREIGN KEY (product_id)  REFERENCES product(id)  ON DELETE CASCADE
) COMMENT='상품 좋아요(찜과 구분)';

CREATE TABLE wishlist_item (
                               user_id     BIGINT NOT NULL COMMENT 'FK: users.id',
                               product_id  BIGINT NOT NULL COMMENT 'FK: product.id',
                               created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '추가 시각',
                               PRIMARY KEY (user_id, product_id),
                               CONSTRAINT fk_wish_user    FOREIGN KEY (user_id)    REFERENCES users(id)    ON DELETE CASCADE,
                               CONSTRAINT fk_wish_product FOREIGN KEY (product_id)  REFERENCES product(id)  ON DELETE CASCADE
) COMMENT='위시리스트';

/* ===========================
   REVIEWS
   =========================== */
CREATE TABLE review (
                        id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'PK',
                        user_id     BIGINT NOT NULL COMMENT 'FK: users.id',
                        product_id  BIGINT NOT NULL COMMENT 'FK: product.id',
                        rating      TINYINT NOT NULL COMMENT '평점 1~5(앱 검증)',
                        title       VARCHAR(255) NULL COMMENT '리뷰 제목',
                        content     TEXT NULL COMMENT '리뷰 본문',
                        like_count  INT NOT NULL DEFAULT 0 COMMENT '도움돼요 카운트',
                        created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '작성 시각',
                        updated_at  TIMESTAMP NULL COMMENT '수정 시각',
                        CONSTRAINT fk_review_user    FOREIGN KEY (user_id)    REFERENCES users(id)    ON DELETE CASCADE,
                        CONSTRAINT fk_review_product FOREIGN KEY (product_id)  REFERENCES product(id)  ON DELETE CASCADE,
                        CONSTRAINT uq_review_user_product UNIQUE (user_id, product_id)
) COMMENT='상품 리뷰(유저당 상품별 1건 정책)';
CREATE INDEX idx_review_prod ON review(product_id);

CREATE TABLE review_like (
                             user_id    BIGINT NOT NULL COMMENT 'FK: users.id',
                             review_id  BIGINT NOT NULL COMMENT 'FK: review.id',
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '좋아요 시각',
                             PRIMARY KEY (user_id, review_id),
                             CONSTRAINT fk_review_like_user   FOREIGN KEY (user_id)   REFERENCES users(id)   ON DELETE CASCADE,
                             CONSTRAINT fk_review_like_review FOREIGN KEY (review_id) REFERENCES review(id)  ON DELETE CASCADE
) COMMENT='리뷰 좋아요';

/* ===========================
   ORDERING
   =========================== */
CREATE TABLE orders (
                        id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'PK',
                        order_number  VARCHAR(50) NOT NULL UNIQUE COMMENT '표시/조회용 주문번호(유니크)',
                        user_id       BIGINT NOT NULL COMMENT 'FK: users.id',
                        status        VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '주문 상태: PENDING|PAID|PREPARING|SHIPPED|DELIVERED|CANCELLED|REFUNDED',
                        total_amount  INT NOT NULL COMMENT '최종 결제 금액(원화 정수)',
                        currency      VARCHAR(3) NOT NULL DEFAULT 'KRW' COMMENT '통화 코드(ISO-4217)',
                        note          VARCHAR(500) NULL COMMENT '구매자 메모/요청사항',
                        ordered_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '주문 시각',
                        updated_at    TIMESTAMP NULL COMMENT '상태 변경 시각',
                        CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
) COMMENT='주문';
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);

CREATE TABLE order_item (
                            id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'PK',
                            order_id      BIGINT NOT NULL COMMENT 'FK: orders.id',
                            product_id    BIGINT NULL COMMENT 'FK: product.id(상품 삭제 시 NULL)',
                            product_name  VARCHAR(255) NOT NULL COMMENT '상품명 스냅샷',
                            unit_price    INT NOT NULL COMMENT '단가 스냅샷(원)',
                            quantity      INT NOT NULL COMMENT '수량',
                            line_amount   INT NOT NULL COMMENT '라인 합계(단가*수량)',
                            created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시각',
                            CONSTRAINT fk_order_item_order   FOREIGN KEY (order_id)   REFERENCES orders(id)   ON DELETE CASCADE,
                            CONSTRAINT fk_order_item_product FOREIGN KEY (product_id) REFERENCES product(id)  ON DELETE SET NULL
) COMMENT='주문 항목(스냅샷)';
CREATE INDEX idx_order_item_order ON order_item(order_id);

CREATE TABLE order_address (
                               order_id    BIGINT PRIMARY KEY COMMENT 'FK=PK: orders.id',
                               recipient   VARCHAR(100) NOT NULL COMMENT '수령인',
                               phone       VARCHAR(30)  NULL COMMENT '연락처',
                               zipcode     VARCHAR(20)  NULL COMMENT '우편번호',
                               address1    VARCHAR(255) NOT NULL COMMENT '기본 주소',
                               address2    VARCHAR(255) NULL COMMENT '상세 주소',
                               city        VARCHAR(100) NULL COMMENT '도시',
                               state       VARCHAR(100) NULL COMMENT '주/도',
                               country     VARCHAR(2)   NULL DEFAULT 'KR' COMMENT '국가 코드',
                               CONSTRAINT fk_order_address_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
) COMMENT='주문 시점 배송지 스냅샷';

/* ===========================
   PAYMENTS & SHIPMENTS
   =========================== */
CREATE TABLE payment (
                         id             BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'PK',
                         order_id       BIGINT NOT NULL COMMENT 'FK: orders.id',
                         status         VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '결제 상태: PENDING|PAID|FAILED|CANCELLED|REFUNDED',
                         method         VARCHAR(32) NULL COMMENT '결제 수단: CARD|VBANK|TRANSFER|KAKAOPAY ...',
                         provider       VARCHAR(64) NULL COMMENT 'PG/게이트웨이 제공자',
                         merchant_uid   VARCHAR(100) NULL COMMENT '상점 주문번호(내부)',
                         pg_tid         VARCHAR(100) NULL COMMENT 'PG 거래번호(외부)',
                         amount         INT NOT NULL COMMENT '결제 금액(원)',
                         approved_at    TIMESTAMP NULL COMMENT '승인 시각',
                         failed_reason  VARCHAR(500) NULL COMMENT '실패 사유',
                         created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시각',
                         updated_at     TIMESTAMP NULL COMMENT '수정 시각',
                         CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
) COMMENT='결제';
CREATE INDEX idx_payment_order ON payment(order_id);
CREATE INDEX idx_payment_status ON payment(status);

CREATE TABLE shipment (
                          id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'PK',
                          order_id      BIGINT NOT NULL COMMENT 'FK: orders.id',
                          status        VARCHAR(32) NOT NULL DEFAULT 'READY' COMMENT '배송 상태: READY|SHIPPED|DELIVERED|RETURNED|CANCELLED',
                          carrier       VARCHAR(64) NULL COMMENT '택배사',
                          tracking_no   VARCHAR(100) NULL COMMENT '운송장 번호',
                          shipped_at    TIMESTAMP NULL COMMENT '발송 시각',
                          delivered_at  TIMESTAMP NULL COMMENT '배송 완료 시각',
                          created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시각',
                          updated_at    TIMESTAMP NULL COMMENT '수정 시각',
                          CONSTRAINT fk_shipment_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
) COMMENT='배송';
CREATE INDEX idx_shipment_order ON shipment(order_id);
CREATE INDEX idx_shipment_tracking ON shipment(tracking_no);

/* ===========================
   PROMOTION
   =========================== */
CREATE TABLE coupon (
                        id             BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'PK',
                        code           VARCHAR(50)  NOT NULL UNIQUE COMMENT '쿠폰 코드(유니크)',
                        name           VARCHAR(120) NOT NULL COMMENT '쿠폰명',
                        description    VARCHAR(500) NULL COMMENT '설명',
                        discount_type  VARCHAR(16)  NOT NULL COMMENT '할인 타입: RATE(%) | AMOUNT(원)',
                        discount_value INT NOT NULL COMMENT '할인 값: RATE면 %, AMOUNT면 원',
                        min_order_amt  INT NULL DEFAULT 0 COMMENT '최소 주문 금액',
                        max_discount   INT NULL COMMENT '정률 할인 상한(원)',
                        starts_at      TIMESTAMP NULL COMMENT '유효 시작',
                        ends_at        TIMESTAMP NULL COMMENT '유효 종료',
                        issued_qty     INT NULL COMMENT '발행 수량 제한(NULL=무제한)',
                        created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시각'
) COMMENT='쿠폰';

CREATE TABLE coupon_redemption (
                                   id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'PK',
                                   coupon_id   BIGINT NOT NULL COMMENT 'FK: coupon.id',
                                   user_id     BIGINT NOT NULL COMMENT 'FK: users.id',
                                   order_id    BIGINT NULL COMMENT 'FK: orders.id(적용 시)',
                                   redeemed_at TIMESTAMP NULL COMMENT '사용 시각',
                                   status      VARCHAR(16) NOT NULL DEFAULT 'ISSUED' COMMENT '상태: ISSUED|REDEEMED|CANCELLED|EXPIRED',
                                   CONSTRAINT fk_coupon_redemption_coupon FOREIGN KEY (coupon_id) REFERENCES coupon(id) ON DELETE CASCADE,
                                   CONSTRAINT fk_coupon_redemption_user   FOREIGN KEY (user_id)   REFERENCES users(id)  ON DELETE CASCADE,
                                   CONSTRAINT fk_coupon_redemption_order  FOREIGN KEY (order_id)  REFERENCES orders(id) ON DELETE SET NULL
) COMMENT='쿠폰 발급/사용 이력';
CREATE UNIQUE INDEX uq_coupon_user_once ON coupon_redemption(coupon_id, user_id);

/* ===========================
   INVENTORY AUDIT
   =========================== */
CREATE TABLE stock_txn (
                           id             BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'PK',
                           product_id     BIGINT NOT NULL COMMENT 'FK: product.id',
                           order_item_id  BIGINT NULL COMMENT 'FK: order_item.id(주문 기반 변동일 경우)',
                           delta          INT NOT NULL COMMENT '변동 수량(+입고 / -차감)',
                           reason         VARCHAR(32) NOT NULL COMMENT '사유: ORDER|CANCEL|MANUAL|ADJUST ...',
                           memo           VARCHAR(255) NULL COMMENT '비고/메모',
                           created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '기록 시각',
                           CONSTRAINT fk_stock_txn_product    FOREIGN KEY (product_id)    REFERENCES product(id)    ON DELETE CASCADE,
                           CONSTRAINT fk_stock_txn_order_item FOREIGN KEY (order_item_id) REFERENCES order_item(id) ON DELETE SET NULL
) COMMENT='재고 변경 트랜잭션 로그';
CREATE INDEX idx_stock_txn_product ON stock_txn(product_id);
