ALTER TABLE carts
    ADD COLUMN applied_coupon_id BIGINT REFERENCES coupons(id);