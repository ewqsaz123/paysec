package com.test.kakaopaysecTest.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.test.kakaopaysecTest.entity.StockInfoEntity;

@Repository
public interface StockInfoRepository extends JpaRepository<StockInfoEntity, Integer> {
//	
//	public List<StockInfoEntity> saveAll(List<StockInfoEntity> list);
//	public List<StockInfoEntity> findAll();
}
