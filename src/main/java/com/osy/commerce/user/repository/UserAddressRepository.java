package com.osy.commerce.user.repository;

import com.osy.commerce.user.domain.UserAddress;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long>, UserAddressRepositoryCustom {

    List<UserAddress> findAllByUserIdOrderByIsDefaultDescIdDesc(Long userId);

    Optional<UserAddress> findByIdAndUserId(Long id, Long userId);

    long countByUserIdAndIsDefaultTrue(Long userId);
}
