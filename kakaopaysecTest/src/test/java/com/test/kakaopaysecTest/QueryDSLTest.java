package com.test.kakaopaysecTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;
import static org.hamcrest.CoreMatchers.equalTo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.SQLExpressions;
import com.test.kakaopaysecTest.dto.StockListDto;
import com.test.kakaopaysecTest.entity.QStockHitEntity;
import com.test.kakaopaysecTest.entity.QStockInfoEntity;
import com.test.kakaopaysecTest.entity.QStockPriceEntity;
import com.test.kakaopaysecTest.entity.QStockTradeEntity;
import com.test.kakaopaysecTest.repo.StockInfoRepository;
import com.test.kakaopaysecTest.repo.StockListRepository;
import com.test.kakaopaysecTest.repo.StockPriceRepository;
import com.test.kakaopaysecTest.repo.StockTradeRepository;
import com.test.kakaopaysecTest.service.StockService;

import lombok.extern.slf4j.Slf4j;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Slf4j
@Transactional
public class QueryDSLTest { 
	@Autowired
	StockListRepository listRepository;
	
	@Autowired
	StockPriceRepository priceRepository;
	
	@Autowired
	EntityManager em;
	JPAQueryFactory query;
	
	
	@Autowired
	StockService service;
	
	
	Date today;
	Calendar c;
	Date yesterday;
	
	@BeforeAll
	static void prepareTest(@Autowired StockService service) {
		final String filelocation = "./src/main/resources/files/data_stockinfo.xlsx";
		service.setInitialData(filelocation);
	}
	
	@BeforeEach
	void initTest() {
		query = new JPAQueryFactory(em);
		
		today = new Date();
		c = Calendar.getInstance();
		c.setTime(today);
		c.add(Calendar.DATE, -1);
		yesterday= c.getTime();
	}
	@DisplayName("--------querydsl_테스트_상위5건_많이본------------")
	@Test
	public void test_hit_top5() {
		//given
		QStockPriceEntity q1 = new QStockPriceEntity("q1");
		QStockPriceEntity q2 = new QStockPriceEntity("q2");
		QStockInfoEntity i1 = QStockInfoEntity.stockInfoEntity;
		QStockTradeEntity t1 = QStockTradeEntity.stockTradeEntity;
		QStockHitEntity h1 = QStockHitEntity.stockHitEntity;
		String category  = StockConstants.CT_HIT;
		
		//when
		List<StockListDto> expt = query.select(Projections.constructor(StockListDto.class, 
												h1.id
												,i1.name
										        ,h1.code
										        ,h1.hdate.as("sdate")
										        ,q1.price
										        ,q1.price.subtract(q2.price).divide(q2.price).multiply(100.00).doubleValue().as("rate")
										        , Expressions.as(Expressions.constant(category), "category")))
										.from(h1)
										.leftJoin(i1)
										.on(h1.code.eq(i1.code))
										.fetchJoin()
										.leftJoin(q1)
										.on(h1.code.eq(q1.code).and(q1.pdate.eq(today)))
										.fetchJoin()
										.leftJoin(q2)
										.on(q1.code.eq(q2.code).and(q2.pdate.eq(yesterday)))
										.fetchJoin()
										.where(h1.hdate.eq(today))
										.orderBy(h1.hit.desc())
										.limit(5)
										.fetch();
		
		
		//then
		assertThat(expt).isNotNull().hasSize(5);
	}
	
	
	@DisplayName("--------querydsl_테스트_상위5건_많이오른------------")
	@Test
	public void test_increase_top5() {
		
		//given
		QStockPriceEntity q1 = new QStockPriceEntity("q1");
		QStockPriceEntity q2 = new QStockPriceEntity("q2");
		QStockInfoEntity i1 = QStockInfoEntity.stockInfoEntity;
		String category  = StockConstants.CT_INCREASE;
		
		//when
		List<StockListDto> expt = query.select(Projections.constructor(StockListDto.class
												,q1.id, i1.name, q1.code, q1.pdate.as("sdate")
												, q1.price
												, q1.price.subtract(q2.price).divide(q2.price).multiply(100.00).doubleValue().as("rate")
												, Expressions.as(Expressions.constant(category), "category")
												) )
										.from(q1)
										.leftJoin(q2)
										.on(q1.code.eq(q2.code).and(q2.pdate.eq(yesterday)))
										.fetchJoin()
										.leftJoin(i1)
										.on(q1.code.eq(i1.code))
										.fetchJoin()
										.where(q1.pdate.eq(today).and(q1.price.subtract(q2.price).gt(0)))
										.orderBy(q1.price.subtract(q2.price).divide(q2.price).multiply(100.00).doubleValue().desc())
										.limit(5)
										.fetch();
		
		
		//then
		assertThat(expt).isNotNull().hasSize(5);	
	}
	
	
	
	@DisplayName("--------querydsl_테스트_상위5건_많이 내린------------")
	@Test
	public void test_decrease_top5() {
		
		//given
		QStockPriceEntity q1 = new QStockPriceEntity("q1");
		QStockPriceEntity q2 = new QStockPriceEntity("q2");
		QStockInfoEntity i1 = QStockInfoEntity.stockInfoEntity;
		String category  = StockConstants.CT_DECREASE;
		
		//when
		List<StockListDto> expt = query.select( Projections.constructor(StockListDto.class
												, q1.id, i1.name, q1.code, q1.pdate.as("sdate")
												, q1.price
												, q1.price.subtract(q2.price).divide(q2.price).multiply(100.00).doubleValue().as("rate")
												, Expressions.as(Expressions.constant(category), "category")
												))
										.from(q1)
										.leftJoin(q2)
										.on(q1.code.eq(q2.code).and(q2.pdate.eq(yesterday)))
										.fetchJoin()
										.leftJoin(i1)
										.on(q1.code.eq(i1.code))
										.fetchJoin()
										.where(q1.pdate.eq(today).and(q1.price.subtract(q2.price).lt(0)))
										.orderBy(q1.price.subtract(q2.price).divide(q2.price).multiply(100.00).doubleValue().asc())
										.limit(5)
										.fetch();
		
		
		//then
		assertThat(expt).isNotNull().hasSize(5);	
	}
	
	

	@DisplayName("--------querydsl_테스트_상위5건_거래량 많은------------")
	@Test
	public void test_trade_top5() {
		
		//given
		QStockPriceEntity q1 = new QStockPriceEntity("q1");
		QStockPriceEntity q2 = new QStockPriceEntity("q2");
		QStockInfoEntity i1 = QStockInfoEntity.stockInfoEntity;
		QStockTradeEntity t1 = QStockTradeEntity.stockTradeEntity;
		String category  = StockConstants.CT_TRADE;
		
		//when
		List<StockListDto> expt = query.select(Projections.bean(StockListDto.class
												, t1.id , i1.name, t1.code,t1.tdate.as("sdate") 
												, t1.price
												,q1.price.subtract(q2.price).divide(q2.price).multiply(100.00).doubleValue().as("rate")
												, Expressions.as(Expressions.constant(category), "category")
												))
												.from(t1)
												.leftJoin(i1)
												.on(t1.code.eq(i1.code))
												.fetchJoin()
												.leftJoin(q1)
												.on(t1.code.eq(q1.code).and(q1.pdate.eq(today)))
												.fetchJoin()
												.leftJoin(q2)
												.on(q1.code.eq(q2.code).and(q2.pdate.eq(yesterday)))
												.fetchJoin()
												.where(t1.tdate.eq(today))
												.groupBy(t1.code, i1.name, t1.id, t1.price, q1.price.subtract(q2.price).divide(q2.price).multiply(100.00).doubleValue())
												.orderBy(t1.volume.sum().desc())
												.limit(5)
												.fetch();
								
		//then
		assertThat(expt).isNotNull().hasSize(5);	
	}
	
	@DisplayName("--------querydsl_테스트_상위5건_페이징------------")
	@Test
	public void test_hit_top5_paging() {
		//given
				QStockPriceEntity q1 = new QStockPriceEntity("q1");
				QStockPriceEntity q2 = new QStockPriceEntity("q2");
				QStockInfoEntity i1 = QStockInfoEntity.stockInfoEntity;
				QStockTradeEntity t1 = QStockTradeEntity.stockTradeEntity;
				String category  = StockConstants.CT_HIT;
				QStockHitEntity h1 = QStockHitEntity.stockHitEntity;
				
				
		log.info(query.select(h1.id.count())
		.from(h1)
		.where(h1.hdate.eq(today))
		.orderBy(h1.hit.desc())
		.fetchOne()+"");
	}
	
	@DisplayName("--------JPA_테스트_거래가 조회------------")
	@Test
	public void test_findByPdateEqualsOrderByCodeAscAndPdateAsc() {
		log.info(priceRepository.findByPdateEqualsOrPdateEqualsOrderByCodeAscPdateAsc(today, yesterday).toString());
	}

	
}
