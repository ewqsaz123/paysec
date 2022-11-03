package com.test.kakaopaysecTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.test.kakaopaysecTest.dto.StockListDto;
import com.test.kakaopaysecTest.entity.StockHitEntity;
import com.test.kakaopaysecTest.entity.StockPriceEntity;
import com.test.kakaopaysecTest.entity.StockTradeEntity;
import com.test.kakaopaysecTest.excel.ExcelPOIHelper;
import com.test.kakaopaysecTest.repo.StockHitRepository;
import com.test.kakaopaysecTest.repo.StockInfoRepository;
import com.test.kakaopaysecTest.repo.StockListRepository;
import com.test.kakaopaysecTest.repo.StockPriceRepository;
import com.test.kakaopaysecTest.repo.StockTradeRepository;
import com.test.kakaopaysecTest.service.StockService;

@ExtendWith(MockitoExtension.class)
//@SpringBootTest
public class StockServiceTest {
	
	@Mock
	private StockInfoRepository infoRepository;
	@Mock
	private StockPriceRepository priceRepository;
	@Mock
	private StockTradeRepository tradeRepository;
	@Mock
	private StockHitRepository hitRepository;
	@Mock
	private StockListRepository listRepository;
	

	@InjectMocks
	private StockService service;
	
	private ExcelPOIHelper excelPOIHelper = new ExcelPOIHelper();
	
	@DisplayName("*** 엑셀 파일 테스트_예외처리 ***")
	@Test
	void testExcelData() {
		//given
		String filelocation = "";
		
		//then
		/* null exception */
		Assertions.assertThrows(NullPointerException.class, ()->{
			excelPOIHelper.readFile(null);
		});
		/* file not found exception */
		Assertions.assertThrows(FileNotFoundException.class, ()->{
			excelPOIHelper.readFile(filelocation);
		});	
	}
	
	@DisplayName("*** 서비스 단위 테스트_[상위 5건] 리스트 조회_ ***")
	@Test
	void testTop5() {
		//given
		Pageable pageable = PageRequest.of(0, 100);
		
		List<StockListDto> list = new ArrayList<StockListDto>();
		list.add(mock(StockListDto.class));
		given(listRepository.getTop5List(any(), any(), any(), anyInt())).willReturn(list);
		
		//when
		List<StockListDto> result = service.getTop5List(pageable);
		
		//then
		Assertions.assertEquals(list.size(), result.size());
		Assertions.assertEquals(list, result);
	}
	
	@DisplayName("*** 서비스 단위 테스트_[많이 본] 리스트 조회_ ***")
	@Test
	void testTop100Hit() {
		//given
		Pageable pageable = PageRequest.of(0, 100);
		
		List<StockListDto> list = new ArrayList<StockListDto>();
		list.add(mock(StockListDto.class));
		Page<StockListDto> sp = new PageImpl<StockListDto>(list);
		given(listRepository.getTopHitList(any(), any(), any(), any(), anyInt())).willReturn(sp);
		
		//when
		Page<StockListDto> result = service.getTop100HitList(pageable);
		
		//then
		Assertions.assertEquals(list.size(), result.getContent().size());
		Assertions.assertEquals(sp, result);
	}

	@DisplayName("*** 서비스 단위 테스트_[많이 오른] 리스트 조회_ ***")
	@Test
	void testTop100Increase() {
		//given
		Pageable pageable = PageRequest.of(0, 100);
		
		List<StockListDto> list = new ArrayList<StockListDto>();
		list.add(mock(StockListDto.class));
		Page<StockListDto> sp = new PageImpl<StockListDto>(list);
		given(listRepository.getTopIncreaseList(any(), any(), any(), any(), anyInt())).willReturn(sp);
		
		//when
		Page<StockListDto> result = service.getTop100IncreaseList(pageable);
		
		//then
		Assertions.assertEquals(list.size(), result.getContent().size());
		Assertions.assertEquals(sp, result);
	}
	
	@DisplayName("*** 서비스 단위 테스트_[많이 내린] 리스트 조회_ ***")
	@Test
	void testTop100Decrease() {
		//given
		Pageable pageable = PageRequest.of(0, 100);
		
		List<StockListDto> list = new ArrayList<StockListDto>();
		list.add(mock(StockListDto.class));
		Page<StockListDto> sp = new PageImpl<StockListDto>(list);
		given(listRepository.getTopDecreaseList(any(), any(), any(), any(), anyInt())).willReturn(sp);
		
		//when
		Page<StockListDto> result = service.getTop100DecreaseList(pageable);
		
		//then
		Assertions.assertEquals(list.size(), result.getContent().size());
		Assertions.assertEquals(sp, result);
	}
	
	@DisplayName("*** 서비스 단위 테스트_[거래량 많은] 리스트 조회_ ***")
	@Test
	void testTop100Trade() {
		//given
		Pageable pageable = PageRequest.of(0, 100);
		
		List<StockListDto> list = new ArrayList<StockListDto>();
		list.add(mock(StockListDto.class));
		Page<StockListDto> sp = new PageImpl<StockListDto>(list);
		given(listRepository.getTopTradeList(any(), any(), any(), any(), anyInt())).willReturn(sp);
		
		//when
		Page<StockListDto> result = service.getTop100TradeList(pageable);
		
		//then
		Assertions.assertEquals(list.size(), result.getContent().size());
		Assertions.assertEquals(sp, result);
	}
	
	@DisplayName("*** 서비스 단위 테스트_[일일 거래가] 랜덤값 생성 ***")
	@Test
	void testRandomPrice() {

		//given
		Date today = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(today);
		c.add(Calendar.DATE, -1);
		Date yesterday= c.getTime();
		
		List<StockPriceEntity> list = new ArrayList<>();
		list.add(mock(StockPriceEntity.class));
		given(priceRepository.saveAll(any())).willReturn(list);
		
		//when
		List<StockPriceEntity> result = service.makeRandomPriceData(today, yesterday);
		
		//then
		Assertions.assertEquals(list.size(), result.size());
		Assertions.assertEquals(list, result);
		
	}
	
	@DisplayName("*** 서비스 단위 테스트_[일일 거래량] 랜덤값 생성 ***")
	@Test
	void testRandomTrade() {
		//given
		Date today = new Date();
		List<StockTradeEntity> list = new ArrayList<>();
		list.add(mock(StockTradeEntity.class));
		given(tradeRepository.saveAll(any())).willReturn(list);
		
		//when
		List<StockTradeEntity> result = service.makeRandomTradeData(today);
		
		//then
		Assertions.assertEquals(list.size(), result.size());
		Assertions.assertEquals(list, result);
		
	}
	
	@DisplayName("*** 서비스 단위 테스트_[일일 조회수] 랜덤값 생성 ***")
	@Test
	void testRandomHit() {
		//given
		Date today = new Date();
		List<StockHitEntity> list = new ArrayList<>();
		list.add(mock(StockHitEntity.class));
		given(hitRepository.saveAll(any())).willReturn(list);
		
		//when
		List<StockHitEntity> result = service.makeRandomHitDate(today);
		
		//then
		Assertions.assertEquals(list.size(), result.size());
		Assertions.assertEquals(list, result);
		
	}
	
	
	
}
