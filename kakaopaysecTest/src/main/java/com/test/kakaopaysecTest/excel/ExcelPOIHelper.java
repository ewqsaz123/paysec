package com.test.kakaopaysecTest.excel;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.impl.CTBdoContentRunImpl;
import org.springframework.stereotype.Component;

import com.test.kakaopaysecTest.entity.StockInfoEntity;

@Component
public class ExcelPOIHelper {

	/* 샘플데이터 DB 적재 */
	public List<StockInfoEntity> readFile(String fileLocation) throws Exception{
		//파일 읽기
		File file = new File(fileLocation);
		FileInputStream f = new FileInputStream(file);
		Workbook workbook =  new XSSFWorkbook(f);
		Sheet sheet = workbook.getSheetAt(0);
		
		List<StockInfoEntity> list = new ArrayList<StockInfoEntity>();
		
		for(Row row : sheet) {
			if(row.getRowNum() == 0) continue;
			if(row == null) break;
			StockInfoEntity e = new StockInfoEntity();
			
			for(Cell cell: row) {
				if(cell == null) break;
				/* data 타입 확인 후 엔티티클래스에 넣기 */
				switch(cell.getCellType()) {
					case STRING:
						e.setName(cell.getRichStringCellValue().getString());
						break;
					case NUMERIC:
						e.setCode((int)cell.getNumericCellValue());
						break;
				}
			}
			list.add(e);
		}
		
		
		return list;
	}
}




