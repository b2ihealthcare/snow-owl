/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.refset.automap;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithExpectedSize;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.google.common.io.Closeables;

/**
 * This class uses Apache POI library for parsing xls files.
 * Header and empty line omit is supported.
 * 
 */
public class XlsParser implements ITableParser {

	private File xlsFile;

	private List<String> header;
	private List<List<String>> content;
	
	private int maxWidth;
	
	private final boolean hasHeader;
	private final boolean skipEmptyRows;

	private FileInputStream fis;

	public XlsParser(File xlsFile, boolean hasHeader, boolean skipEmptyRows) {
		checkArgument(xlsFile != null, "XlsFile must be specified");
		this.xlsFile = xlsFile;
		this.header = newArrayList();
		this.content = newArrayList();
		this.maxWidth = -1;
		this.hasHeader = hasHeader;
		this.skipEmptyRows = skipEmptyRows;
	}
	
	@Override
	public void parse() throws SnowowlServiceException {
		parse(0);
	}
	
	public void parse(int sheetNumber) throws SnowowlServiceException {
		fis = null;
		try {
			fis = new FileInputStream(xlsFile);
			Workbook wb = WorkbookFactory.create(fis);
			Sheet sheet = wb.getSheetAt(sheetNumber);
			parse(sheet);
		} catch (final Exception e) {
			maxWidth = -1;
			throw new SnowowlServiceException(e);
		}
	}

	private void parse(Sheet sheet) throws SnowowlServiceException {

		int firstRowIndex = findFirstRow(sheet);

		if (firstRowIndex == -1) {
			return;
		}

		if (hasHeader) {
			header = collectRowValues(sheet.getRow(firstRowIndex));
			firstRowIndex++;
		} else {
			final Row firstRow = sheet.getRow(firstRowIndex);
			Cell first = firstRow.getCell(firstRow.getFirstCellNum());
			Cell second = firstRow.getCell(firstRow.getFirstCellNum() + 1);
			if (isNumeric(first) || isNumeric(second)) {
				header.add("ID");
			}
			if (isString(first) || isString(second)) {
				header.add("Label");
			}
		}

		for (int i = firstRowIndex; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);

			// totally empty row w/o any value
			if (row == null) {
				if (!skipEmptyRows) {
					content.add(Collections.<String>emptyList());
				}
				continue;
			}

			if (row.getLastCellNum() > maxWidth) {
				maxWidth = row.getLastCellNum();
			}

			List<String> rowValues = collectRowValues(row);
			
			if (rowValues.isEmpty()) {
				if (!skipEmptyRows) {
					content.add(Collections.<String>emptyList());
				}
				continue;
			}
			
			content.add(rowValues);
		}
	}
	
	private boolean isString(Cell cell) {
		return isCellType(cell, Cell.CELL_TYPE_STRING);
	}
	
	private boolean isNumeric(Cell cell) {
		return isCellType(cell, Cell.CELL_TYPE_NUMERIC);
	}
	
	private boolean isCellType(Cell cell, int type) {
		checkArgument(type >= 0, "type");
		return cell != null && type == cell.getCellType();
	}

	private List<String> collectRowValues(Row row) {

		List<String> list = newArrayListWithExpectedSize(row.getLastCellNum());
		
		boolean hasAnyCellWithValue = false;
		for (int i = 0; i < row.getLastCellNum(); i++) {
			String cellValue = getStringValue(row.getCell(i, Row.RETURN_BLANK_AS_NULL));
			hasAnyCellWithValue = !StringUtils.isEmpty(cellValue);
			list.add(cellValue);
		}
		
		return hasAnyCellWithValue ? list : Collections.<String>emptyList();
	}

	/**
	 * @param cell
	 * @return the textual representation of the cell or empty string if the cell is empty (null)
	 */
	private String getStringValue(Cell cell) {
		String value = ""; 
		if (cell != null) {
			switch (cell.getCellType()) {
		        case Cell.CELL_TYPE_STRING:
		            value = cell.getRichStringCellValue().getString();
		            break;
		        case Cell.CELL_TYPE_NUMERIC:
		            if (DateUtil.isCellDateFormatted(cell)) {
		            	value = cell.getDateCellValue().toString();
		            } else {
		            	value = Integer.toString((int) cell.getNumericCellValue());
		            }
		            break;
		        case Cell.CELL_TYPE_BOOLEAN:
		        	value = Boolean.toString(cell.getBooleanCellValue());
		            break;
		        case Cell.CELL_TYPE_FORMULA:
		        	value = cell.getCellFormula();
		            break;
			}
		}
		return value;
	}

	/**
	 * Returns the first logical row which contains a logical number or string.
	 * 
	 * @param sheet
	 * @return
	 */
	private int findFirstRow(Sheet sheet) {
		int i = -1;
		Iterator<Row> iterator = sheet.iterator();
		
		if (iterator == null || !iterator.hasNext()) {
			return -1;
		}
		
		int cellType = -1;
		do {
			Row row = iterator.next();
			if (row == null) {
				return -1;
			}
			short firstLogicalCell = row.getFirstCellNum();
			Cell cell = row.getCell(firstLogicalCell);
			if (cell != null) {
				cellType = cell.getCellType();
			}
			i++;
		} while (cellType != Cell.CELL_TYPE_STRING && cellType != Cell.CELL_TYPE_NUMERIC);

		return i;
	}
	
	private void print() {
		for (int i = 0; i < content.size(); i++) {
			System.out.print(i + ": ");
			
			for (String cell : content.get(i)) {
				System.out.print(cell + "\t");
			}
			System.out.println();
		}
	}

	
	/**
	 * Returns the parsed content. Key is the row number (starts from 0), value is the textual representation of each cell collected in a {@link List}.
	 * If the whole row is empty, the value must be an empty list (<code>Collections.emptyList()}</code>). If a cell is empty, empty string must be added to the list.
	 * 
	 * @return
	 */
	@Override
	public List<List<String>> getContent() {
		return content;
	}
	
	public int getMaxWidth() {
		return maxWidth;
	}

	@Override
	public List<String> getColumnHeader() {
		if (maxWidth == -1) {
			return Collections.emptyList();
		}
		return header;
	}
	
	public static void main(String[] args) {
		XlsParser parser = new XlsParser(new File("/home/bvizer/test2.xls"), true, false);
		try {
			parser.parse();
		} catch (SnowowlServiceException e) {
			e.printStackTrace();
		}
		parser.print();
	}

	@Override
	public void close() {
		Closeables.closeQuietly(fis);
	}
}