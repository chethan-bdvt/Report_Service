package com.anticorruption.report.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.anticorruption.report.dto.ReportResponse;

@Service
public class ReportExcelExportService {

	public byte[] generateExcel(List<ReportResponse> responses) {
		try (Workbook workbook = new XSSFWorkbook();
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			
			Sheet sheet = workbook.createSheet("Reports");
			
			CreationHelper creationHelper = workbook.getCreationHelper();
			
			CellStyle dateStyle = workbook.createCellStyle();
			dateStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd-MM-yyyy"));
			
			Row header = sheet.createRow(0);
			
			header.createCell(0).setCellValue("Report Number");
			header.createCell(1).setCellValue("Demanded Amount");
			header.createCell(2).setCellValue("Paid Amount");
			header.createCell(3).setCellValue("State");
			header.createCell(4).setCellValue("District");
			header.createCell(5).setCellValue("SubDistrict");
			header.createCell(6).setCellValue("Departmnet");
			header.createCell(7).setCellValue("Reason");
			header.createCell(8).setCellValue("Reported Date");
			header.createCell(9).setCellValue("Incident Date");
			
			int rowNumber = 1;
			
			for(ReportResponse response:responses) {
				Row row = sheet.createRow(rowNumber++);
				row.createCell(0).setCellValue(response.reportNumber() != null ? response.reportNumber() : "");
				row.createCell(1).setCellValue(response.demandedAmount().doubleValue());
				row.createCell(2).setCellValue(response.paidAmount().doubleValue());
				row.createCell(3).setCellValue(response.state() != null ? response.state() : "");
				row.createCell(4).setCellValue(response.district() != null ? response.district() : "");
				row.createCell(5).setCellValue(response.subDistrict() != null ? response.subDistrict() : "");
				row.createCell(6).setCellValue(response.department() != null ? response.department() : "");
				row.createCell(7).setCellValue(response.reason() != null ? response.reason() : "");
				if(response.reportedDate() != null) {
					row.createCell(8).setCellValue(response.reportedDate().toString());
					row.getCell(8).setCellStyle(dateStyle);
				}
				if(response.reportedDate() != null) {
					row.createCell(9).setCellValue(response.incidentDate().toString());
					row.getCell(9).setCellStyle(dateStyle);
				}
			}
			for(int i = 0; i<10; i++) {
				sheet.autoSizeColumn(i);
			}
			workbook.write(outputStream);
			return outputStream.toByteArray();
					
		} catch(IOException e) {
			throw new IllegalArgumentException("Failed to generate Excel file");
		}
	}
}
