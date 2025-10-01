package com.osy.commerce.inventory.repository;

import com.osy.commerce.inventory.domain.StockTxn;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockTxnRepository extends JpaRepository<StockTxn, Long> {
}
