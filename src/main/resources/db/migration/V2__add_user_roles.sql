-- 1) user_roles 테이블 생성
CREATE TABLE user_roles
(
    user_id BIGINT      NOT NULL,
    role    VARCHAR(32) NOT NULL,
    PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id) REFERENCES user (id)
            ON DELETE CASCADE
);

-- 2) 인덱스(선택: role로 거를 일이 있다면)
CREATE INDEX idx_user_roles_role ON user_roles (role);

-- 3) 기존 user.role 값 백필 (null 제외)
INSERT INTO user_roles (user_id, role)
SELECT id, role
FROM user
WHERE role IS NOT NULL;
