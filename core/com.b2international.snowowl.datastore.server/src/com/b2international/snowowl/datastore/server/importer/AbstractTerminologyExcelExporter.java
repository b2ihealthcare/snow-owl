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
package com.b2international.snowowl.datastore.server.importer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import com.b2international.commons.AlphaNumericComparator;
import com.b2international.snowowl.core.api.IBranchPath;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

/**
 * Abstract exporter to export terminology to an excel file.
 * 
 * @since Snow&nbsp;Owl 3.0
 * 
 * @param <T>
 *            the terminology component.
 * @param <M>
 *            the terminology component member/mapping/concept.
 */
public abstract class AbstractTerminologyExcelExporter<T, M> extends AbstractFilteredComponentsTerminologyExporter {

	// necessary for auto sizing column in the excel
	// basic java supports Sarif, Sans-serif, Monospaced, Dialog, DialogInput font styles,
	// with other font style the auto size of the sheet column doesn't work properly
	// XXX works on windows, other OS should be checked
	private static final String FONT_STYLE = "Sarif";
	
	private static final AlphaNumericComparator COMPARATOR = new AlphaNumericComparator();
	
	// The two fields below are non-static because the type parameter needs to be known
	private final Function<M, String> getLowerCaseMemberCodeFunction = input -> getMemberCode(input).toLowerCase(Locale.ENGLISH);
	private final Function<T, String> getLowerCaseComponentNameFunction = input -> getComponentName(input).toLowerCase(Locale.ENGLISH);

	private final Workbook workbook = new XSSFWorkbook();

	private final CellStyle centerBoldStyle;
	private final CellStyle wrapStyle;
	private final CellStyle defaultStyle;
	private final CellStyle hyperlinkStyle;

	private final CellStyle BOLD_STYLE;


	public AbstractTerminologyExcelExporter(final String userId, final IBranchPath branchPath, final Collection<String> componentIds) {
		super(userId, branchPath, componentIds);

		final Font headerFont = workbook.createFont();
		headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		headerFont.setFontName(FONT_STYLE);

		final Font defaultFont = workbook.createFont();
		defaultFont.setFontName(FONT_STYLE);

		final Font hyperlinkFont = workbook.createFont();
		hyperlinkFont.setUnderline(Font.U_SINGLE);
		hyperlinkFont.setColor(IndexedColors.BLUE.getIndex());

		centerBoldStyle = workbook.createCellStyle();
		centerBoldStyle.setAlignment(CellStyle.ALIGN_CENTER);
		centerBoldStyle.setFont(headerFont);

		BOLD_STYLE = workbook.createCellStyle();
		headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		BOLD_STYLE.setAlignment(CellStyle.ALIGN_LEFT);
		BOLD_STYLE.setFont(headerFont);

		// wrap needs to be enabled to accommodate multi-lines within a single cell
		wrapStyle = workbook.createCellStyle();
		wrapStyle.setWrapText(true);
		wrapStyle.setFont(defaultFont);

		defaultStyle = workbook.createCellStyle();
		defaultStyle.setFont(defaultFont);

		hyperlinkStyle = workbook.createCellStyle();
		hyperlinkStyle.setFont(hyperlinkFont);
	}

	@Override
	protected File exportTerminology(final String exportFilePath, final OMMonitor monitor) throws IOException {
		logExportActivity(MessageFormat.format("Exporting {0}s to Excel started. Server-side file: {1}", getTerminologyName(), exportFilePath));

		final File excelFile = new File(exportFilePath);
		final FileOutputStream outputStream = new FileOutputStream(excelFile);

		exportTerminologyComponents(monitor);

		getEditingContext().close();

		workbook.write(outputStream);
		outputStream.close();

		logExportActivity(MessageFormat.format("Finished exporting {0}s to Excel.", getTerminologyName()));

		return excelFile;
	}

	/**
	 * Gets the name of the given component.
	 * 
	 * @param component
	 *            the terminology specific component.
	 * @return the name of the component.
	 */
	protected abstract String getComponentName(final T component);

	/**
	 * Gets the ID of the given component.
	 * 
	 * @param component
	 *            the terminology specific component.
	 * @return the ID of the component.
	 */
	protected abstract String getComponentId(final T component);

	/**
	 * Gets the code of the given member.
	 * 
	 * @param member
	 *            the component member.
	 * @return the code of the member.
	 */
	protected abstract String getMemberCode(final M member);

	/**
	 * Sorts the given list that contains the terminology components.
	 * 
	 * @param components
	 *            the components to sort.
	 */
	protected void sortComponents(final List<T> components) {
		Collections.sort(components, Ordering.from(COMPARATOR).onResultOf(getLowerCaseComponentNameFunction));
	}

	/**
	 * Sorts the given list that contains the component members.
	 * 
	 * @param members
	 *            the members to sort.
	 */
	protected void sortMembers(final List<M> members) {
		Collections.sort(members, Ordering.from(COMPARATOR).onResultOf(getLowerCaseMemberCodeFunction));
	}

	/**
	 * Creates the index sheet based on the given sheet names.
	 * 
	 * @param sheetNames
	 */
	protected void createIndexSheet(final Collection<T> components) {

		final Sheet indexSheet = workbook.createSheet("INDEX");

		final List<T> filteredComponents = Lists.newArrayList(Iterables.filter(components, new Predicate<T>() {
			@Override
			public boolean apply(T input) {
				return isToExport(getComponentId(input));
			}
		}));

		final List<String> sheetNames = extractSheetNamesFromTerminologyComponents(filteredComponents);

		final Row firstRow = indexSheet.createRow(0);
		createCell(firstRow, getIndexSheetHeaderName(), BOLD_STYLE, 0);

		for (int i = 0; i < sheetNames.size(); i++) {

			final String sheetName = getFinalSheetName(i + 1, sheetNames.get(i));
			final Hyperlink hyperlink = workbook.getCreationHelper().createHyperlink(XSSFHyperlink.LINK_DOCUMENT);

			hyperlink.setLabel(sheetName);
			hyperlink.setAddress(String.format("'%s'!A1", sheetName));

			final Row row = indexSheet.createRow(i + 1);
			final Cell cell = row.createCell(0);

			cell.setCellValue(sheetName);
			cell.setCellStyle(hyperlinkStyle);
			cell.setHyperlink(hyperlink);

		}

		indexSheet.autoSizeColumn(0);

	}

	private String getIndexSheetHeaderName() {
		return String.format("%ss", getTerminologyName());
	}

	/**
	 * Creates a property row in the excel with the given property name and value.
	 * 
	 * @param sheet
	 *            the sheet where the property is created.
	 * @param rowNumber
	 *            the number of the row where the property is created.
	 * @param propertyName
	 *            the name of the property.
	 * @param propertyValue
	 *            the value of the property.
	 */
	protected void createProperty(final Sheet sheet, final int rowNumber, final String propertyName, final String propertyValue) {
		final Row row = sheet.createRow(rowNumber);
		Cell cell = row.createCell(0);
		cell.setCellValue(propertyName);
		cell.setCellStyle(BOLD_STYLE);

		cell = row.createCell(1);
		cell.setCellValue(propertyValue);
		cell.setCellStyle(defaultStyle);
	}

	/**
	 * Creates a metadata row in the excel with the given group name and keyword.
	 * 
	 * @param sheet
	 *            the sheet where the metadata is created.
	 * @param rowNum
	 *            the number of the row where the metadata is created.
	 * @param groupName
	 *            the name of the group.
	 * @param keyword
	 *            the name of the keyword.
	 */
	protected void createMetadata(Sheet sheet, int rowNum, String groupName, String keyword) {
		final Row row = sheet.createRow(rowNum);

		Cell cell = row.createCell(0);
		cell.setCellValue(groupName);
		cell.setCellStyle(defaultStyle);

		cell = row.createCell(1);
		cell.setCellValue(keyword);
		cell.setCellStyle(defaultStyle);
	}

	/**
	 * Creates a cell with the given string value.
	 * 
	 * @param row
	 *            the row where the cell is created.
	 * @param cellValue
	 *            the string value of the cell.
	 * @param cellStyle
	 *            the style of the cell.
	 * @param cellIndex
	 *            the index of the cell in the row.
	 */
	protected void createCell(final Row row, final String cellValue, final CellStyle cellStyle, final int cellIndex) {
		final Cell cell = row.createCell(cellIndex);
		cell.setCellValue(cellValue);
		cell.setCellStyle(cellStyle);
	}

	/**
	 * Creates a cell with the given boolean value.
	 * 
	 * @param row
	 *            the row where the cell is created.
	 * @param cellValue
	 *            the boolean value of the cell.
	 * @param cellStyle
	 *            the style of the cell.
	 * @param cellIndex
	 *            the index of the cell in the row.
	 */
	protected void createCell(final Row row, final boolean cellValue, final CellStyle cellStyle, final int cellIndex) {
		createCell(row, getStatusFromBoolean(cellValue), cellStyle, cellIndex);
	}

	/**
	 * Creates a cell with the given int value.
	 * 
	 * @param row
	 *            the row where the cell is created.
	 * @param cellValue
	 *            the int value of the cell.
	 * @param cellStyle
	 *            the style of the cell.
	 * @param cellIndex
	 *            the index of the cell in the row.
	 */
	protected void createCell(final Row row, final int cellValue, final CellStyle cellStyle, final int cellIndex) {
		final Cell cell = row.createCell(cellIndex);
		cell.setCellValue(cellValue);
		cell.setCellStyle(cellStyle);
	}

	protected String getFinalSheetName(final int index, final String componentName) {
		return ExcelUtilities.purgeTabname(String.format("%d. %s", index, componentName));
	}

	@Override
	protected String getFileExtension() {
		return "xlsx";
	}

	public Workbook getWorkbook() {
		return workbook;
	}

	public CellStyle getCenterBoldStyle() {
		return centerBoldStyle;
	}

	public CellStyle getWrapStyle() {
		return wrapStyle;
	}

	public CellStyle getDefaultStyle() {
		return defaultStyle;
	}

	public CellStyle getHyperlinkStyle() {
		return hyperlinkStyle;
	}

	/*
	 * Extracts the exported sheet names from the terminologies.
	 */
	private List<String> extractSheetNamesFromTerminologyComponents(final Collection<T> components) {

		return Lists.newArrayList(Iterables.transform(components, new Function<T, String>() {
			@Override
			public String apply(final T component) {
				return getComponentName(component);
			}
		}));

	}

	private String getStatusFromBoolean(boolean active) {
		return active ? "Active" : "Inactive";
	}

}