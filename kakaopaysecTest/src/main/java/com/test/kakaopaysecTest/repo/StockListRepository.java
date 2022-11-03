package com.test.kakaopaysecTest.repo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.test.kakaopaysecTest.StockConstants;
import com.test.kakaopaysecTest.dto.StockListDto;
import com.test.kakaopaysecTest.entity.QStockHitEntity;
import com.test.kakaopaysecTest.entity.QStockInfoEntity;
import com.test.kakaopaysecTest.entity.QStockPriceEntity;
import com.test.kakaopaysecTest.entity.QStockTradeEntity;
import com.test.kakaopaysecTest.entity.StockHitEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class StockListRepository {
	private final JPAQueryFactory queryFactory;
	QStockHitEntity h1 = QStockHitEntity.stockHitEntity;
	QStockTradeEntity t1 = QStockTradeEntity.stockTradeEntity;
	QStockInfoEntity i1 = QStockInfoEntity.stockInfoEntity;
	QStockPriceEntity q1 = new QStockPriceEntity("q1");
	QStockPriceEntity q2 = new QStockPriceEntity("q2");
	
	
	
	/* 상위 5건 리스트 조회 */ 
	public List<StockListDto> getTop5List( Date yesterday, Date today, Pageable pageable, int limit){
		List<StockListDto> rs = new ArrayList<StockListDto>();
		long count = 0;
		String category = StockConstants.CT_HIT;
		
		if((pageable.getPageNumber()+1) * pageable.getPageSize() <= limit) {
			//"많이본" 리스트
			rs.addAll(getTopHitListResult(category, yesterday, today, pageable));
			
			//"많이오른" 리스트
			category = StockConstants.CT_INCREASE;
			rs.addAll(getTopIncreaseResult(category, yesterday, today, pageable));
			
			//"많이내린" 리스트
			category = StockConstants.CT_DECREASE;
			rs.addAll(getTopDecreaseResult(category, yesterday, today, pageable));
			
			
			//"거래량 많은" 리스트 
			category = StockConstants.CT_TRADE;
			rs.addAll(getTopTradeResult(category, yesterday, today, pageable));
		}
		
		
		return rs;
	}
	
	
	/* 많이 본 리스트 조회  */
	public Page<StockListDto> getTopHitList(String category, Date yesterday, Date today, Pageable pageable, int limit){
		List<StockListDto> rs = new ArrayList<>();
		if((pageable.getPageNumber()+1) * pageable.getPageSize() <= limit) rs = getTopHitListResult(category, yesterday, today, pageable);
		
		long count = getTopHitListCount(today);
		if(count > limit) count = limit;
		
		return new PageImpl<>(rs, pageable, count);
	}
	
	private List<StockListDto> getTopHitListResult(String category, Date yesterday, Date today, Pageable pageable){
		return		queryFactory.select(Projections.constructor(StockListDto.class, 
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
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();
		
		
		
	}
	
	private long getTopHitListCount(Date today) {
		return queryFactory.select(h1.id.count())
				.from(h1)
				.where(h1.hdate.eq(today))
				.orderBy(h1.hit.desc())
				.fetchOne();
	}
	
	
	/* 많이 오른 리스트 조회  */
	public Page<StockListDto> getTopIncreaseList(String category, Date yesterday, Date today, Pageable pageable, int limit){
		List<StockListDto> rs = new ArrayList<>();
		if((pageable.getPageNumber()+1) * pageable.getPageSize() <= limit) rs = getTopIncreaseResult(category, yesterday, today, pageable);
		
		long count = getTopIncreaseCount(yesterday,  today);
		if(count > limit) count = limit;
		return new PageImpl<>(rs, pageable, count);
	}
	
	
	private List<StockListDto> getTopIncreaseResult(String category, Date yesterday, Date today, Pageable pageable){
		return queryFactory
				.select(Projections.constructor(StockListDto.class
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
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();
	}
	
	private long getTopIncreaseCount(Date yesterday, Date today) {
		return queryFactory
				.select(q1.id.count())
				.from(q1)
				.leftJoin(q2)
				.on(q1.code.eq(q2.code).and(q2.pdate.eq(yesterday)))
				.fetchJoin()
				.where(q1.pdate.eq(today).and(q1.price.subtract(q2.price).gt(0)))
				.orderBy(q1.price.subtract(q2.price).divide(q2.price).multiply(100.00).doubleValue().desc())
				.fetchOne();
	}
	
	
	
	/* 많이 내린 리스트 조회  */
	public Page<StockListDto> getTopDecreaseList(String category,Date yesterday, Date today, Pageable pageable, int limit){
		List<StockListDto> rs = new ArrayList<>();
		if((pageable.getPageNumber()+1) * pageable.getPageSize() <= limit) rs = getTopDecreaseResult(category, yesterday, today, pageable);
		
		long count = getTopDecreaseCount(yesterday,  today);
		if(count > limit) count = limit;
		return new PageImpl<>(rs, pageable, count);
	}
	
	private List<StockListDto> getTopDecreaseResult(String category,Date yesterday, Date today, Pageable pageable){
		return queryFactory
		.select( Projections.constructor(StockListDto.class
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
		.offset(pageable.getOffset())
		.limit(pageable.getPageSize())
		.fetch();
	}
	
	private long getTopDecreaseCount(Date yesterday, Date today) {
		return queryFactory
				.select( q1.code.count())
				.from(q1)
				.leftJoin(q2)
				.on(q1.code.eq(q2.code).and(q2.pdate.eq(yesterday)))
				.fetchJoin()
				.where(q1.pdate.eq(today).and(q1.price.subtract(q2.price).lt(0)))
				.orderBy(q1.price.subtract(q2.price).divide(q2.price).multiply(100.00).doubleValue().asc())
				.fetchOne();
	}
	
	
	/* 거래량 많은 리스트 조회  */
	public Page<StockListDto> getTopTradeList(String category, Date yesterday, Date today, Pageable pageable, int limit){
		List<StockListDto> rs = new ArrayList<>();
		if((pageable.getPageNumber()+1) * pageable.getPageSize() <= limit) rs = getTopTradeResult(category, yesterday, today, pageable);
		
		
		long count = getTopTradeCount(yesterday,  today);
		if(count > limit) count = limit;
		return new PageImpl<>(rs, pageable, count);
	}
	
	private List<StockListDto> getTopTradeResult(String category, Date yesterday, Date today, Pageable pageable){
		return queryFactory
				.select(Projections.bean(StockListDto.class
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
						.offset(pageable.getOffset())
						.limit(pageable.getPageSize())
						.fetch();
	}
	
	private long getTopTradeCount(Date yesterday, Date today) {
		return queryFactory
				.select(t1.id.count())
				.from(t1)
				.leftJoin(q1)
				.on(t1.code.eq(q1.code).and(q1.pdate.eq(today)))
				.fetchJoin()
				.leftJoin(q2)
				.on(q1.code.eq(q2.code).and(q2.pdate.eq(yesterday)))
				.fetchJoin()
				.where(t1.tdate.eq(today))
				.groupBy(t1.code, t1.id, t1.price, q1.price.subtract(q2.price).divide(q2.price).multiply(100.00).doubleValue())
				.orderBy(t1.volume.sum().desc())
				.fetchOne();
	}
	
}

