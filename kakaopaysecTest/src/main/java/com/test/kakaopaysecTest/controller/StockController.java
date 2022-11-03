package com.test.kakaopaysecTest.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.test.kakaopaysecTest.dto.StockListDto;
import com.test.kakaopaysecTest.service.StockService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class StockController {
	private final StockService service;
	
	
	/* 모든 주제별 상위5건 조회 API  */
	@RequestMapping(value="/top5", method = RequestMethod.GET)
	public ResponseEntity<List<StockListDto>> getTop5List(@PageableDefault(size = 5) Pageable pageable) {
		return new ResponseEntity<List<StockListDto>>(service.getTop5List(pageable), HttpStatus.OK);
	}
	
	/* 상위 100건 조회 API _많이 본 */
	@RequestMapping(value="/top100/hit", method = RequestMethod.GET)
	public ResponseEntity<Page<StockListDto>> getTop100HitList(@PageableDefault(size=20) Pageable pageable){
			return new ResponseEntity<Page<StockListDto>>(service.getTop100HitList(pageable), HttpStatus.OK);
	}
	
	/* 상위 100건 조회 API _많이 오른 */
	@RequestMapping(value="/top100/increase", method = RequestMethod.GET)
	public ResponseEntity<Page<StockListDto>> getTop100IncreaseList(@PageableDefault(size=20) Pageable pageable){
			return new ResponseEntity<Page<StockListDto>>(service.getTop100IncreaseList(pageable), HttpStatus.OK);
	}
	
	/* 상위 100건 조회 API _많이 내린 */
	@RequestMapping(value="/top100/decrease", method = RequestMethod.GET)
	public ResponseEntity<Page<StockListDto>> getTop100DecreaseList(@PageableDefault(size=20) Pageable pageable){
			return new ResponseEntity<Page<StockListDto>>(service.getTop100DecreaseList(pageable), HttpStatus.OK);
	}
	
	/* 상위 100건 조회 API _거래량 많은 */
	@RequestMapping(value="/top100/trade", method = RequestMethod.GET)
	public ResponseEntity<Page<StockListDto>> getTop100TradeList(@PageableDefault(size=20) Pageable pageable){
			return new ResponseEntity<Page<StockListDto>>(service.getTop100TradeList(pageable), HttpStatus.OK);
	}
	
	/* 전체 순위 랜덤 변경 API */
	@RequestMapping(value="/changeRank", method = RequestMethod.POST)
	public ResponseEntity<Void> updateRandomData(){
		service.updateRandomData();
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
