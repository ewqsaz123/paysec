package com.test.kakaopaysecTest.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.kakaopaysecTest.entity.StockHitEntity;

public interface StockHitRepository extends JpaRepository<StockHitEntity, Integer> {
	public List<StockHitEntity> findByHdate(Date today);
}
