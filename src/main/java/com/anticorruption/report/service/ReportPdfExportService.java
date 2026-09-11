package com.anticorruption.report.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import com.anticorruption.report.dto.ReportResponse;

@Service
public class ReportPdfExportService {

	private static final float MARGIN = 30;
	
	private static final float ROW_HEIGHT = 18;
	
	private static final float HEADER_HEIGHT = 22;
	
	private static final String[] HEADERS = {
			"Report Number",
			"Demanded Amount",
			"Paid Amount",
			"State",
			"District",
			"Sub District",
			"Department",
			"Reason",
			"Reported Date",
			"Incident Date"
	};
	
	private static final float[] COLUMN_WIDTH = {
			90,70,65,65,80,80,70,80,90,90
	};
	
	public byte[] generatePdf(List<ReportResponse> reports) {
		
		try(PDDocument document = new PDDocument();
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			
			PDRectangle pageSize = PDRectangle.A4;
			PDRectangle landscape = new PDRectangle(
					pageSize.getHeight(),
					pageSize.getWidth()
					);
			
			PDPage page = new PDPage(landscape);
			
			document.addPage(page);
			
			int rowCount = 0;
			
			PDPageContentStream contentStream = null;
			
			try {
				contentStream = createPageContent(document, page);
				
				drawTitle(contentStream, landscape);
				
				float y = landscape.getHeight() -65;
				
				drawHeader(contentStream, y);
				y -= HEADER_HEIGHT;
				
				for(ReportResponse report : reports) {
					if(rowCount >= 38) {
						contentStream.close();
						page = new PDPage(landscape);
						document.addPage(page);
						contentStream = createPageContent(document, page);
						drawTitle(contentStream, landscape);
						y = landscape.getHeight() -65;
						drawHeader(contentStream,y);
						y -= HEADER_HEIGHT;
						rowCount = 0;
					}
					drawReportRow(contentStream,report,y);
					y -= ROW_HEIGHT;
					rowCount = 0;
				}
					
			} finally {
				if (contentStream != null) {
					contentStream.close();
				}
			}
			document.save(outputStream);
			return outputStream.toByteArray();
		} catch(IOException e) {
			throw new IllegalArgumentException("Failed to generate PDF file", e);
		}
		
	}
	
	private PDPageContentStream createPageContent(PDDocument document, PDPage page) throws IOException {
		return new PDPageContentStream(
				document,page,PDPageContentStream.AppendMode.APPEND,true);
	}
	
	private void drawTitle(
			PDPageContentStream contentStream,
			PDRectangle pageSize
			) throws IOException {
		PDType1Font boldFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
		contentStream.beginText();
		contentStream.setFont(boldFont, 14);
		contentStream.newLineAtOffset(MARGIN, pageSize.getHeight() -35);
		contentStream.showText("Corruption Report");
		contentStream.endText();
	}
	
	private void drawHeader(
			PDPageContentStream contentStream, float y) throws IOException {
		PDType1Font boldFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
		float x = MARGIN;
		for(int i = 0; i < HEADERS.length; i++) {
			drawCellBorder(
					contentStream,
					x,
					y-HEADER_HEIGHT,
					COLUMN_WIDTH[i],
					HEADER_HEIGHT
					);
			drawText(
					contentStream,
					HEADERS[i],
					x+3,
					y-15,
					boldFont,
					6.5f,
					COLUMN_WIDTH[i]
					);
			x += COLUMN_WIDTH[i];
		}
	}
	
	private void drawReportRow(PDPageContentStream contentStream, ReportResponse report, float y) throws IOException {
		PDType1Font normalFont = new PDType1Font(
				Standard14Fonts.FontName.HELVETICA_BOLD
				);
		String[] values = {
				safe(report.reportNumber()),
				report.demandedAmount() != null ? report.demandedAmount().toString() : "",
				report.paidAmount() != null ? report.paidAmount().toString() : "",
				safe(report.state()),
				safe(report.district()),
				safe(report.subDistrict()),
				safe(report.department()),
				safe(report.reason()),
				report.reportedDate() != null ? report.reportedDate().toString() : "",
				report.incidentDate() != null ? report.incidentDate().toString() : ""
		};
		
		float x = MARGIN;
		
		for(int i = 0; i < values.length; i++) {
			drawCellBorder(
					contentStream,
					x,
					y-ROW_HEIGHT,
					COLUMN_WIDTH[i],
					ROW_HEIGHT
					);
			drawText(
					contentStream,
					values[i],
					x+3,
					y-13,
					normalFont,
					6.5f,
					COLUMN_WIDTH[i]
					);
			x += COLUMN_WIDTH[i];
		}
	}
	
	private void drawCellBorder(PDPageContentStream contentStream,
			float x,
			float y,
     		float width,
			float height
			) throws IOException{
		contentStream.addRect(x, y, width, height);
		contentStream.stroke();
	}
	
	private void drawText(PDPageContentStream contentStream,
			String text,
			float x,
			float y,
			PDType1Font font,
			float fontSize,
			float maxWidth) throws IOException {
		String value = truncate(
				text,
				font,
				fontSize,
				maxWidth);
		contentStream.beginText();
		contentStream.setFont(font, fontSize);
		contentStream.newLineAtOffset(x, y);
		contentStream.showText(value);
		contentStream.endText();
	}
	
	private String truncate(String text, PDType1Font font, float fontSize,
			float maxWidth) throws IOException {
		if(text == null ) {
			return "";
		}
		if(font.getStringWidth(text)/1000*fontSize <= maxWidth) {
			return text;
		}
		
		String result = text;
		
		while(result.length() > 0 && font.getStringWidth(result + "...")/1000 * fontSize > maxWidth) {
			result = result.substring(0,result.length()-1);
		}
		return result + "...";
	}
	
	private String safe(String value) {
		return value != null ? value : "";
	}
	
}
