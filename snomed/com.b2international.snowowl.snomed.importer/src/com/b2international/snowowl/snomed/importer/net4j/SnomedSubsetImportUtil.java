/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.importer.net4j;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.b2international.commons.VerhoeffCheck;
import com.b2international.snowowl.snomed.importer.net4j.SnomedSubsetImportConfiguration.SubsetEntry;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

/**
 * Provides utility methods for dsv imports. 
 * 
 */
public class SnomedSubsetImportUtil {

	/**
	 * Sets the properties (separator char, SNOMED CT concept ID column, line feed character) of the given subset entry.
	 * 
	 * @param entry the subset entry which contains informations for the process.
	 * @return <code>true</code> if the file contains concept IDs which can be imported, <code>false</code> otherwise.
	 */
	public boolean setProperties(SubsetEntry entry) throws InvalidFormatException, IOException {
		if ("xls".equals(entry.getExtension()) || "xlsx".equals(entry.getExtension())) {
			return processExcelFile(entry);
		} else {
			return processTextFile(entry);
		}
	}
	
	/**
	 * Updates properties where the value of the property is <code>null</code>.
	 * 
	 * @param entry
	 */
	public void updateNullProperties(final SubsetEntry entry) {
		if (null == entry.getEffectiveTime()) {
			entry.setEffectiveTime("");
		}
		if (null == entry.getNamespace()) {
			entry.setNamespace("");
		}
		if (null == entry.getFieldSeparator()) {
			entry.setFieldSeparator("");
		}
		if (null == entry.getQuoteCharacter()) {
			entry.setQuoteCharacter("");
		}
		if (null == entry.getLineFeedCharacter()) {
			entry.setLineFeedCharacter("nl");
		}
		if (null == entry.getSheetNumber()) {
			entry.setSheetNumber(-1);
		}
	}
	
	/*
	 * Collects the header, line feed and separator characters and the column where SNOMED CT ID can be found.
	 */
	private boolean processTextFile(final SubsetEntry entry) throws IOException {
		final FileInputStream inputStream = createFileInputStream(entry);
		final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		
		String line;
		int lineNumber = 0;
		boolean firstLine = true;
		boolean hasConceptId = false;
		
		while (null != (line = reader.readLine())) {
			if (firstLine) {
				setHeadings(line, entry);
				setDefaultLineFeedCharacter(entry);
				setDefaultSeparatorCharacter(line, entry);
				firstLine = false;
			}
			
			if (setSnomedCtColumnId(line, lineNumber, entry)) {
				hasConceptId = true;
				break;
			}
			
			lineNumber++;
		}
		
		reader.close();
		inputStream.close();
		
		return hasConceptId;
	}
	
	// Sets the headings for XLS subset and the column number where the SNOMED CT id can be found
	private boolean processExcelFile(final SubsetEntry entry) throws InvalidFormatException, IOException {
		
		final FileInputStream inputStream = createFileInputStream(entry);
		final Workbook workbook = WorkbookFactory.create(inputStream);
		final List<Integer> list = getSheetAndFirstRowNumber(workbook, workbook.getNumberOfSheets());
		
		if (null != list) {
			final int sheetNumber = list.get(0);
			final int firstRowNumber = list.get(1);
			final Sheet sheet = workbook.getSheetAt(sheetNumber);
			final List<String> row = collectRowValues(sheet.getRow(firstRowNumber));
			
			entry.setHeadings(row);
			entry.setSheetNumber(sheetNumber);
			
			if (entry.isHasHeader()) {
				Optional<String> match = FluentIterable.from(row).firstMatch(new Predicate<String>() {
					@Override public boolean apply(String input) {
						return input.contains("concept") && (input.contains("id") || input.contains("sctid"));
					}
				});
				entry.setIdColumnNumber(match.isPresent() ? row.indexOf(match.get()) : 0); // default to first?
			} else {
				for (int i = 0; i < row.size(); i++) {
					if (isConceptId(row.get(i).trim())) {
						entry.setIdColumnNumber(i);
					}
				}
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	// Returns with the right sheet number where SNOMED CT can be found and with the first row number 
	private List<Integer> getSheetAndFirstRowNumber(final Workbook workbook, final int numberOfSheets) {
		for (int i = 0; i < numberOfSheets; i++) {
			final Sheet sheet = workbook.getSheetAt(i);

			int firstRow = -1;
			
			for (int j = 0; j < sheet.getLastRowNum(); j++) {
				final List<String> row = collectRowValues(sheet.getRow(j));
				
				for (final String value : row) {
					if (!value.isEmpty() && -1 == firstRow) {
						firstRow = j;
					}
				}
				
				if (containsConceptId(row)) {
					return Lists.newArrayList(i, firstRow);
				}
			}
		}
		
		return null;
	}
	
	// Checks if the given row contains SNOMED CT id
	private boolean containsConceptId(final List<String> collectRowValues) {
		for (String rowValue : collectRowValues) {
			if (isConceptId(rowValue)) {
				return true;
			}
		}
		return false;
	}
	
	// Collects the row values
	private List<String> collectRowValues(final Row row) {
		List<String> list = Lists.newArrayList();
		for (int i = 0; i < row.getLastCellNum(); i++) {
			list.add(getStringValue(row.getCell(i, Row.CREATE_NULL_AS_BLANK)));
		}
		
		return list;
	}
	
	// Returns with the textual representation of the cell or empty string if the cell is empty (null)
	private String getStringValue(final Cell cell) {
		String value = ""; 

		//	empty cell
		if (cell == null) {
			return "";
		}

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

		return value;
	}
	
	// Sets the headers for a non XLS file
	private void setHeadings(final String firstRow, final SubsetEntry entry) throws IOException {
		final String [] headings;
		
		if (firstRow.contains("\t")) {
			headings = firstRow.split("\t");
		} else if (firstRow.contains(",")) {
			headings = firstRow.split(",");
		} else if (firstRow.contains(";")) {
			headings = firstRow.split(";");
		} else if (firstRow.contains("|")) {
			headings = firstRow.split("\\|");
		} else {
			headings = new String[] {firstRow};
		}
		
		if (!entry.getHeadings().isEmpty()) {
			entry.getHeadings().clear();
		}
		
		if (entry.isHasHeader()) {
			for (final String heading : headings) {
				entry.getHeadings().add(heading);
			}
		} else {
			for (int i = 0; i < headings.length; i++) {
				entry.getHeadings().add(String.format("%d.", i));
			}
		}
	}
	
	// Sets the default separator character
	private void setDefaultSeparatorCharacter(final String firstRow, final SubsetEntry entry) throws IOException {
		if (firstRow.contains("\t")) {
			entry.setFieldSeparator("\t");
		} else if (firstRow.contains(",")) {
			entry.setFieldSeparator(",");
		} else if (firstRow.contains(";")) {
			entry.setFieldSeparator(";");
		} else if (firstRow.contains("|")) {
			entry.setFieldSeparator("\\|");
		} else {
			entry.setFieldSeparator("");
		}
	}

	// Sets the column number where the SNOMED CT id can be found
	private boolean setSnomedCtColumnId(final String line, final int lineNumber, final SubsetEntry entry) {
		
		final List<String> idCandidates;
		
		if (StringUtils.isEmpty(entry.getFieldSeparator())) {
			idCandidates = Arrays.asList(new String[] {line});
		} else {
			idCandidates = Arrays.asList(line.split(entry.getFieldSeparator()));
		}
		
		for (final String idCandidate : idCandidates) {
			if (isConceptId(idCandidate)) {
				entry.setFirstConceptRowNumber(lineNumber);
				entry.setIdColumnNumber(idCandidates.indexOf(idCandidate));
				
				return true;
			}
		}

		return false;
	}
	
	// Sets the default separator character
	private void setDefaultLineFeedCharacter(final SubsetEntry entry) throws IOException {
		FileInputStream inputStream = createFileInputStream(entry);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		
		int i;
		boolean isNewLineFound = false;
		boolean containsCarriageReturn = false;

		while (!isNewLineFound && -1 != (i = reader.read())) {
			if (i == 13) {
				containsCarriageReturn = true;
				isNewLineFound = true;
			}
			if (i == 10) {
				isNewLineFound = true;
			}
		}
		
		if (containsCarriageReturn) {
			entry.setLineFeedCharacter("nlc");
		} else {
			entry.setLineFeedCharacter("nl");
		}
		
		reader.close();
		inputStream.close();
	}
	
	// Checks whether the given SNOMED CT id is a valid id
	private boolean isConceptId(final String sctId) {
		if (!Pattern.matches("^\\d*$", sctId) || sctId.length() < 6 || sctId.length() > 18) {
			return false;
		}
		if (!VerhoeffCheck.validateLastChecksumDigit(sctId)) {
			return false;
		}
		final String componentNatureId = sctId.substring(sctId.length()-2, sctId.length()-1);
		if ("1".equals(componentNatureId)) {
			return false;
		}
		if ("2".equals(componentNatureId)) {
			return false;
		}
		return true;
	}
	
	private FileInputStream createFileInputStream(final SubsetEntry entry) throws FileNotFoundException {
		return new FileInputStream(entry.getFileURL().getPath().replace("%20", " "));
	}
	
}