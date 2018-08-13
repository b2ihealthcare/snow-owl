/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.datastore.importer;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;

/**
 * Utilities class for dealing with Excel workbooks in Java.
 * Uses the apache POI library.
 * 
 *
 */
public final class ExcelUtilities {
	
	protected final static Logger LOGGER = Logger.getLogger(ExcelUtilities.class.getName());
	
	/**
	 * This is necessary to convert scientific format to regular number
	 * representing a SNOMED CT id.
	 * E.g. 1.3553412E08
	 */
	public static String convertToString(double doubleNumber) {
		
		if (doubleNumber>=1) {
			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(0);
			String number = nf.format(doubleNumber).replaceAll("\\W", "");
			return number;
		} else {
			BigDecimal bd = new BigDecimal(Double.toString(doubleNumber));
			return bd.setScale(0).toPlainString();
		}
	}
	
	/**
	 * Formats a double number to string without formatting the number.
	 * 
	 * @param doubleNumber
	 * @return
	 */
	public static String convertToStringWithoutFormat(final double doubleNumber) {
		if (doubleNumber>=1) {
			final NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(5);
			return nf.format(doubleNumber);
		} else {
			final BigDecimal bd = new BigDecimal(Double.toString(doubleNumber));
			return bd.setScale(0).toPlainString();
		}
	}
	
	/**
	 * Finds the first row that has a value in the first column
	 * @param sheet
	 * @return the index for the first row
	 */
	public static int findFirstRow(Sheet sheet) {
		int i = -1;
		Iterator<Row> iterator = sheet.iterator();
		int cellType = -1;
		do {
			Cell cell = iterator.next().getCell(0);
			cellType = cell.getCellType();
			i++;
		} while (cellType != Cell.CELL_TYPE_STRING && cellType != Cell.CELL_TYPE_NUMERIC && cellType != Cell.CELL_TYPE_FORMULA);

		return i;
	}
	
	/**
	 * Returns true if the cell (NOT the text inside) is set to bold.
	 * @param cell
	 * @return
	 */
	public static boolean isBold(Cell cell) {
		CellStyle style = cell.getCellStyle();
		Workbook workBook = cell.getSheet().getWorkbook();
		Font font = workBook.getFontAt(style.getFontIndex());
		XSSFFont xssfFont = (XSSFFont) font;
		return xssfFont.getBold();
	}
	
	/**
	 * Replaced invalid characters for tab names
	 * Invalid characters are: :,\,/,?,*,[,],_
	 * @param name
	 * @return
	 */
	public static String purgeTabname(String name) {
		// sheet name cannot be longer than 31 character
		if (name.length() > 31) {
			name = name.substring(0, 31);
		}
		
		return name.replaceAll(":|\\\\|/|\\?|\\*|\\[|\\(|\\]|\\)", "-");
	}

	
	/**
	 * Extracts the SNOMED CT id from the cell.
	 * @param cell
	 * @return content of the cell in string format
	 */
	public static String extractContentAsString(Cell cell) {
		return extractAsString(cell, true);
	}
	
	public static String extractVersionAsString(final Cell cell) {
		return extractAsString(cell, false);
	}
	
	private static String extractAsString(final Cell cell, final boolean formatNumber) {
		String value = "";
		
		if (cell == null) {
			return value;
		}
	
		FormulaEvaluator formulaEvaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
		
		int type = cell.getCellType();
		
		switch (type) {
		case Cell.CELL_TYPE_NUMERIC:
			if (formatNumber) {
				value = convertToString(cell.getNumericCellValue());
			} else{
				value = convertToStringWithoutFormat(cell.getNumericCellValue());
			}
			break;
		case Cell.CELL_TYPE_STRING:
			value = cell.getStringCellValue();
			break;
		case Cell.CELL_TYPE_FORMULA:
			CellValue cellValue = formulaEvaluator.evaluate(cell);
			value = cellValue.getStringValue(); //type should be checked
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			value = Boolean.toString(cell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_BLANK:
			//do nothing, sctId is null
			break;
		default:
			LOGGER.log(Level.SEVERE, "Unsupported cell type:" + type +
					" for cell: " + cell);
			break;
		}
		return null == value ? "" : value;
	}
	
}