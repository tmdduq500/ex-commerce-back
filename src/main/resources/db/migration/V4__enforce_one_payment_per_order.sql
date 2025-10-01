-- 1) 기존 중복 결제 정리: 같은 order_id에 여러 결제가 있으면 가장 최신(id 최대)만 남기고 삭제
DELETE p
FROM payment p
         JOIN payment q
              ON p.order_id = q.order_id AND p.id < q.id;

-- 2) order_id에 유니크 제약 추가 (이미 있으면 스킵)
ALTER TABLE payment
    ADD UNIQUE KEY uq_payment_order_id (order_id);
