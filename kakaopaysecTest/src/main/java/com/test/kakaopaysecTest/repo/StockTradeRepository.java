package com.test.kakaopaysecTest.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.test.kakaopaysecTest.entity.StockTradeEntity;

@Repository
public interface StockTradeRepository extends JpaRepository<StockTradeEntity, Integer> {
	public List<StockTradeEntity> findByTdate(Date today);
}
