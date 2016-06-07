package transcribe;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.*;

class RequestHandler {

	private int col, fRow, lRow, sheetNum;
	private String excelFile;
	private List<String> wordList;

	public RequestHandler(String excelFile, int col, int fRow, int lRow, int sheet) {
		this.col = col - 1;
		this.fRow = fRow - 1;
		this.lRow = lRow;
		this.excelFile = excelFile;
		this.sheetNum = sheet - 1;
		wordList = new ArrayList<String>(lRow - fRow);
	}

	//TODO: handle numerous different exceptions in TranscribeGUI
	public void findTranscription() throws Exception {
		
		extractWordList();
		List<String> transcriptionList = ServiceCaller.getTranscription(wordList);
		writeListToFile(transcriptionList);
	}

	/**
	 * Method for extraction of words list
	 * 
	 * @return List<String> List of words in an excel file
	 * @throws IOException
	 */
	public List<String> extractWordList() throws IllegalArgumentException, EncryptedDocumentException,
			InvalidFormatException, FileNotFoundException, IOException {

		Workbook workbook;
		if (excelFile.endsWith("xls"))
			workbook = new HSSFWorkbook();
		else
			workbook = new XSSFWorkbook();

		FileInputStream fileInput = new FileInputStream(excelFile);
		try {
			workbook = WorkbookFactory.create(fileInput);
		} catch (IllegalArgumentException | EncryptedDocumentException | InvalidFormatException
				| FileNotFoundException e) {
			workbook.close();
			throw e;
		} catch (IOException e) {
			workbook.close();
			throw e;
		}

		Sheet currentSheet = workbook.getSheetAt(sheetNum);

		Cell cell;

		// TODO: test different options
		for (int i = fRow; i <= lRow; ++i) {
			if (currentSheet.getRow(i) != null) {
				cell = currentSheet.getRow(i).getCell(col, Row.CREATE_NULL_AS_BLANK);
				if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK)
					wordList.add(cell.getStringCellValue());
				else
					wordList.add("");
			} else
				wordList.add("");
		}

		fileInput.close();
		workbook.close();
		return wordList;
	}

	void writeListToFile(List<String> transcriptionList) throws IOException {

		Workbook workbook;
		if (excelFile.endsWith("xls"))
			workbook = new HSSFWorkbook();
		else
			workbook = new XSSFWorkbook();

		FileInputStream fileInput = new FileInputStream(excelFile);
		try {
			workbook = WorkbookFactory.create(fileInput);
		} catch (IllegalArgumentException | EncryptedDocumentException | InvalidFormatException
				| FileNotFoundException e) {
			e.printStackTrace();
			workbook.close();
			return;
		} catch (IOException e) {
			workbook.close();
			throw e;
		}

		Sheet currentSheet = workbook.getSheetAt(sheetNum);

		Cell cell;
		Row row;
		int i = 0;
		// TODO: test different values for cells
		// TODO: write column should be user-defined
		for (String transcription : transcriptionList) {
			row = currentSheet.getRow(i);
			if(row == null)
				row = currentSheet.createRow(i);
			cell = row.getCell(col+1, Row.CREATE_NULL_AS_BLANK);
			if(cell == null)
				cell = row.createCell(col+1);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue(transcription);
			++i;
		}
		fileInput.close();
		
		FileOutputStream fileOutput = new FileOutputStream(excelFile);
		workbook.write(fileOutput);
		
		fileOutput.close();
		workbook.close();
	}

}
