package com.test.kakaopaysecTest.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.test.kakaopaysecTest.ErrorMessage;
import com.test.kakaopaysecTest.StockConstants;
import com.test.kakaopaysecTest.dto.StockListDto;
import com.test.kakaopaysecTest.entity.StockHitEntity;
import com.test.kakaopaysecTest.entity.StockInfoEntity;
import com.test.kakaopaysecTest.entity.StockPriceEntity;
import com.test.kakaopaysecTest.entity.StockTradeEntity;
import com.test.kakaopaysecTest.excel.ExcelPOIHelper;
import com.test.kakaopaysecTest.repo.StockHitRepository;
import com.test.kakaopaysecTest.repo.StockInfoRepository;
import com.test.kakaopaysecTest.repo.StockListRepository;
import com.test.kakaopaysecTest.repo.StockPriceRepository;
import com.test.kakaopaysecTest.repo.StockTradeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class StockService {
	
	private final StockInfoRepository infoRepository;
	private final StockPriceRepository priceRepository;
	private final StockTradeRepository tradeRepository;
	private final StockHitRepository hitRepository;
	private final ExcelPOIHelper excelPOIHelper;
	private final StockListRepository listRepository;
	
	/* 종목 정보 파일 읽어서 데이터 저장, 랜덤 데이터 생성 후 저장 */
	public void setInitialData(String filelocation) {
		try {
			/* stock_info 테이블에 샘플데이터 저장 */
			infoRepository.saveAll(excelPOIHelper.readFile(filelocation));
			
			Date today = new Date();
			Calendar c = Calendar.getInstance();
			c.setTime(today);
			c.add(Calendar.DATE, -1);
			Date yesterday= c.getTime();

			/* stock_price 테이블에 거래가 랜덤데이터 저장 */
			makeRandomPriceData(today, yesterday);
			
			/* stock_trade 테이블에 거래량 랜덤데이터 저장 */
			makeRandomTradeData(today);
			
			/* stock_hit 테이블에 조회수 랜덤데이터 저장 */
			makeRandomHitDate(today);
			
		}catch(FileNotFoundException e) {
			log.info(ErrorMessage.ERROR_003);
		}catch(IOException e) {
			log.info(ErrorMessage.ERROR_002);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* 일일 거래가 랜덤값 생성 메소드 */
	public List<StockPriceEntity> makeRandomPriceData(Date today,Date yesterday)  {
		List<StockInfoEntity> list = infoRepository.findAll();
		List<StockPriceEntity> pList = new ArrayList<StockPriceEntity>();
		Random r = new Random();

		for(StockInfoEntity e : list) {
			
			
			/* 어제 거래가 */
			StockPriceEntity y_priceEntity = new StockPriceEntity();
			y_priceEntity.setCode(e.getCode());
			y_priceEntity.setPdate(yesterday);
			int y_price = r.nextInt(1000000)+1;
			y_priceEntity.setPrice(y_price);
			pList.add(y_priceEntity);
			
			/* 오늘 거래가. 어제보다 30%이상을 넘지 않게 조정 */
			StockPriceEntity t_priceEntity = new StockPriceEntity();
			t_priceEntity.setCode(e.getCode());
			t_priceEntity.setPdate(today);
			double rate = r.nextDouble(30.00);					//증감률. 30이하로 제한 
			double pn = (r.nextInt(100)%2)==0? 1 : -1;			//증감여부. 난수 생성 후 짝수/홀수 여부로 판단 
			int t_price = y_price + (int)(y_price * (rate/100) * pn);
			
			t_priceEntity.setPrice(t_price);
			pList.add(t_priceEntity);
		}
		
		return priceRepository.saveAll(pList);
		
	}
	
	//일일 거래량 랜덤값 생성 메소드
	public List<StockTradeEntity> makeRandomTradeData(Date today) {
		List<StockInfoEntity> list = infoRepository.findAll(); 
		List<StockTradeEntity> tList = new ArrayList<StockTradeEntity>();
		Random r = new Random();
		
		for(StockInfoEntity e: list) {
			List<StockPriceEntity> pList = priceRepository.findTop1ByCodeAndPdateEqualsOrderByPdateDesc(e.getCode(), today);
			
			int i = r.nextInt(50)+1;
			for(;i>0;i--)
			{
				StockTradeEntity tradeEntity = new StockTradeEntity();
				
				tradeEntity.setCode(e.getCode());
				tradeEntity.setTdate(today);
				
				if(!pList.isEmpty()) tradeEntity.setPrice(pList.get(0).getPrice());
				
				tradeEntity.setVolume(r.nextInt(1000000)+1);
				tList.add(tradeEntity);
			}
		}
		return tradeRepository.saveAll(tList);
	}

	//일일 조회수 랜덤값 생성 메소드 
	public List<StockHitEntity> makeRandomHitDate(Date today) {
		List<StockInfoEntity> list = infoRepository.findAll(); 
		List<StockHitEntity> hList = new ArrayList<StockHitEntity>();
		Random r = new Random();
		
		for(StockInfoEntity e: list) {
		
			int hit = r.nextInt(10000)+1;
			StockHitEntity he = new StockHitEntity();
			he.setCode(e.getCode());
			he.setHdate(today);
			he.setHit(hit);
			hList.add(he);
			
		}
		return hitRepository.saveAll(hList);
	}
	
	
	/* 주제별 상위5건 조회 API */
	@Cacheable(cacheNames="top", value = "top", key = "#pageable")
	public List<StockListDto> getTop5List(Pageable pageable) {
		List<StockListDto> list = new ArrayList<StockListDto>();
		
		Date today = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(today);
		c.add(Calendar.DATE, -1);
		Date yesterday= c.getTime();

		return listRepository.getTop5List(yesterday, today, pageable, 5);
	}
	
	
	/* 주제별 상위 100건 조회 API _많이 본  */ 
	@Cacheable(cacheNames="hit", value = "hit", key = "#pageable")
	public Page<StockListDto> getTop100HitList(Pageable pageable) {
		Date today = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(today);
		c.add(Calendar.DATE, -1);
		Date yesterday= c.getTime();
		
		return listRepository.getTopHitList(StockConstants.CT_HIT, yesterday, today, pageable, 100);
	}
	
	/* 주제별 상위 100건 조회 API _많이 오른  */ 
	@Cacheable(cacheNames="increase", value = "increase", key = "#pageable")
	public Page<StockListDto> getTop100IncreaseList(Pageable pageable) {
		Date today = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(today);
		c.add(Calendar.DATE, -1);
		Date yesterday= c.getTime();
		
		return listRepository.getTopIncreaseList(StockConstants.CT_INCREASE, yesterday, today, pageable, 100);
	}
	
	/* 주제별 상위 100건 조회 API _많이 내린  */ 
	@Cacheable(cacheNames="decrease", value = "decrease", key = "#pageable")
	public Page<StockListDto> getTop100DecreaseList(Pageable pageable) {
		Date today = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(today);
		c.add(Calendar.DATE, -1);
		Date yesterday= c.getTime();
		
		return listRepository.getTopDecreaseList(StockConstants.CT_DECREASE, yesterday, today, pageable, 100);
	}
	
	/* 주제별 상위 100건 조회 API _거래량 많은  */ 
	@Cacheable(cacheNames="trade", value = "trade", key = "#pageable")
	public Page<StockListDto> getTop100TradeList(Pageable pageable) {
		Date today = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(today);
		c.add(Calendar.DATE, -1);
		Date yesterday= c.getTime();
		
		return listRepository.getTopTradeList(StockConstants.CT_TRADE, yesterday, today, pageable, 100);
	}
	
	
	/* 순위 랜덤 변경 API */
	@Caching(evict = {
			@CacheEvict(cacheNames = "top", allEntries = true)
			,@CacheEvict(cacheNames = "hit", allEntries = true)
			,@CacheEvict(cacheNames = "increase", allEntries = true)
			,@CacheEvict(cacheNames = "decrease", allEntries = true)
			,@CacheEvict(cacheNames = "trade", allEntries = true)
	})
	public void updateRandomData() {
		
		Date today = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(today);
		c.add(Calendar.DATE, -1);
		Date yesterday= c.getTime();
		
		updateRandomPriceData(today, yesterday);
		updateRandomTradeData(today);
		updateRandomHitData(today);
		
	}
	
	/* 순위 랜덤 변경_거래가 */
	public void updateRandomPriceData(Date today, Date yesterday) {

		List<StockPriceEntity> pList = priceRepository.findByPdateEqualsOrPdateEqualsOrderByCodeAscPdateAsc(today, yesterday);	//오늘 거래가 데이터 가져오기
		List<StockPriceEntity> upList = new ArrayList<>();
		
		Random r = new Random();

		int y_code = 0;
		StringBuilder y_date = new StringBuilder();
		int y_price = 0;
		for(StockPriceEntity e : pList) {
			StockPriceEntity tmp = new StockPriceEntity();
			
			if(y_code != e.getCode() || y_date.equals(new StringBuilder(e.getPdate().toString()))) {	//어제 거래가 변경
				//비교 데이터 저장 
				y_code = e.getCode();							
				y_date = new StringBuilder(e.getPdate().toString());
				y_price = r.nextInt(1000000)+1;
				
				e.setPrice(y_price);
			}else {																						//오늘 거래가 변경

				double rate = r.nextDouble(30.00);					//증감률. 30이하로 제한 
				double pn = (r.nextInt(100)%2)==0? 1 : -1;			//증감여부. 난수 생성 후 짝수/홀수 여부로 판단 
				int t_price = y_price + (int)(y_price * (rate/100) * pn);
				
				e.setPrice(t_price);
			}
			upList.add(tmp);
		}
		
		priceRepository.saveAll(pList);
	}
	
	/* 순위 랜덤 변경_거래량 */
	public void updateRandomTradeData(Date today) {
		List<StockTradeEntity> tList = tradeRepository.findByTdate(today);
		
		Random r = new Random();
		
		for(StockTradeEntity e: tList) {
			List<StockPriceEntity> pList = priceRepository.findTop1ByCodeAndPdateEqualsOrderByPdateDesc(e.getCode(), today);
			
			e.setVolume(r.nextInt(1000000)+1);
			if(!pList.isEmpty()) e.setPrice(pList.get(0).getPrice());
		}

		tradeRepository.saveAll(tList);
	}
	
	/* 순위 랜덤 변경_조회수 */
	public void updateRandomHitData(Date today) {
		List<StockHitEntity> hList = hitRepository.findByHdate(today);
		Random r = new Random();
		
		for(StockHitEntity e: hList) {
		
			e.setHit(r.nextInt(10000)+1);
			
		}
		hitRepository.saveAll(hList);
	}
	
}
