package com.test.kakaopaysecTest.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.test.kakaopaysecTest.entity.StockPriceEntity;



@Repository
public interface StockPriceRepository extends JpaRepository<StockPriceEntity, Integer> {
	
	
	
	public List<StockPriceEntity> findTop1ByCodeAndPdateEqualsOrderByPdateDesc(int code, Date today);
	
	public List<StockPriceEntity> findByPdateEqualsOrPdateEqualsOrderByCodeAscPdateAsc(Date today, Date yesterday);
	
	
}
