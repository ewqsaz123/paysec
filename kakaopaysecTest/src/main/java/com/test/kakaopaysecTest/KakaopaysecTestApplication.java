package com.test.kakaopaysecTest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;

import com.test.kakaopaysecTest.excel.ExcelPOIHelper;
import com.test.kakaopaysecTest.repo.StockInfoRepository;
import com.test.kakaopaysecTest.service.StockService;

import lombok.AllArgsConstructor;

@EnableCaching
@SpringBootApplication
@AllArgsConstructor
public class KakaopaysecTestApplication {

	
	public static void main(String[] args) {
		ConfigurableApplicationContext context =  SpringApplication.run(KakaopaysecTestApplication.class, args);
		
		//어플리케이션 로딩 시 데이터 셋팅 
		StockService service = context.getBean(StockService.class);
		final String filelocation = "./src/main/resources/files/data_stockinfo.xlsx";
		service.setInitialData(filelocation);
		
		
		
	}

}
