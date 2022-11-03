package com.test.kakaopaysecTest.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockListDto {
	//seq id
	private int id;
	
	//종목명 
	private String name;
	
	//종목코드 
	private int code;
	
	//날짜 
	private Date sdate;
	
	//거래가 
	private int price;
	
	//증감률 
	private double rate;
	
	//주제
	private String category;
}
