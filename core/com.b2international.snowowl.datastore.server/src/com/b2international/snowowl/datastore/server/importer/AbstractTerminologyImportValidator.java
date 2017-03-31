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
package com.b2international.snowowl.datastore.server.importer;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.eclipse.emf.cdo.CDOObject;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.datastore.importer.TerminologyImportType;
import com.b2international.snowowl.datastore.importer.TerminologyImportValidationDefect;
import com.b2international.snowowl.datastore.importer.TerminologyImportValidationDefect.DefectType;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * Abstract validator class for terminology sheet validation during the import.
 * 
 * @since Snow&nbsp;Owl 3.0.1
 * 
 * @param <T>
 *            the terminology for the validation process.
 */
public abstract class AbstractTerminologyImportValidator<T extends CDOObject> {

	private final TerminologyImportType importType;
	private final TerminologyImportExcelParser excelParser;
	private final Map<String, TerminologyImportValidationDefect> defects = Maps.newHashMap();
	private final Map<String, T> databaseComponents;
	
	/**
	 * Keys are the properties for instance, 'OID' or 'short name'
	 * Values are the actual values for the properties such as '2.16.840.1.113883.6.96' or 'SNOMEDCT'
	 * And the values for the multimap are the sheet names where the property with the actual value has occurred.
	 */
	private final Map<String, Multimap<String, String>> uniqueAttributes = Maps.newHashMap();

	public AbstractTerminologyImportValidator(final TerminologyImportType importType, final TerminologyImportExcelParser excelParser, final Map<String, T> databaseComponents) {
		this.importType = importType;
		this.excelParser = excelParser;
		this.databaseComponents = databaseComponents;
	}

	/**
	 * Validates the sheets from the imported excel file.
	 * 
	 * @return a collection of the validation defects.
	 */
	public Collection<TerminologyImportValidationDefect> validate() {

		for (final String sheetName : excelParser.getProperties().keySet()) {

			validateProperties(sheetName, excelParser.getProperties().get(sheetName));
			validateMetadata(sheetName, excelParser.getMetadata().get(sheetName));
			validateMembers(sheetName, excelParser.getMembers().get(sheetName));

		}
		
		checkDuplication();

		return getDefects();
	}

	/**
	 * Validates the properties from the sheet.
	 * 
	 * @param sheetName
	 *            the name of the sheet.
	 * @param properties
	 *            the properties from the sheet.
	 */
	protected abstract void validateProperties(final String sheetName, final List<String> properties);

	/**
	 * Validates the members from the sheet.
	 * 
	 * @param sheetName
	 *            the name of the sheet.
	 * @param members
	 *            the members from the sheet.
	 */
	protected abstract void validateMembers(final String sheetName, final Set<Row> members);

	/**
	 * Returns true if rows contain duplicate values in the given column.
	 * 
	 * @param the name of the sheet being validated
	 * @param rows to check for duplicates
	 * @param uniqueColumn the index of the unique value column
	 * @return true if duplicates found
	 */
	protected void validateDuplicateFields(final String sheetName, final Set<Row> rows, final int uniqueColumn) {
		Set<String> fieldSet = FluentIterable.from(rows).transform(new Function<Row, String>() {
			@Override
			public String apply(Row row) {
				return ExcelUtilities.extractContentAsString(row.getCell(uniqueColumn));
			}
		}).toSet();
		
		if (rows.size() > fieldSet.size()) {
			addDefect(sheetName, DefectType.DUPLICATE, String.format("Spreadsheet %s, has duplicated fields in column %s.", sheetName, uniqueColumn));
		}
	}
	
	/**
	 * Validates the metadata from the sheet.
	 * 
	 * @param sheetName
	 *            the name of the sheet.
	 * @param metadata
	 *            the metadata to validate.
	 */
	protected void validateMetadata(final String sheetName, final Multimap<String, String> metadata) {

		for (final Entry<String, Collection<String>> entry : metadata.asMap().entrySet()) {

			final String displayName = entry.getKey();
			final Set<String> keywords = Sets.newHashSet();

			for (final String keyword : entry.getValue()) {

				validateNonEmptyAttribute(sheetName, keyword, MessageFormat.format("Keyword is empty in ''{0}'' group", displayName));

				if (!keywords.add(keyword)) {
					addDefect(sheetName, DefectType.GROUP, MessageFormat.format("''{0}'' keyword is duplicated in group ''{1}''.", keyword, displayName));
				}

			}
		}
	}

	/**
	 * Validates the given attribute against emptiness.
	 * 
	 * @param sheetName
	 *            the name of the sheet.
	 * @param attribute
	 *            the attribute to be validated.
	 * @param errorMessage
	 *            the error message if the attribute is empty.
	 */
	protected void validateNonEmptyAttribute(final String sheetName, final String attribute, final String errorMessage) {
		if (StringUtils.isEmpty(attribute)) {
			addDefect(sheetName, DefectType.EMPTINESS, errorMessage);
		}
	}

	/**
	 * Validates if the given value is numeric or not.
	 * 
	 * @param sheetName
	 *            the name of the sheet.
	 * @param attribute
	 *            the numeric attribute to validate.
	 * @param errorMessage
	 *            the error message if the attribute is not valid.
	 */
	protected void validateNumberFormat(final String sheetName, final String attribute, final String errorMessage) {
		if (!StringUtils.isEmpty(attribute)) {
			if (!attribute.matches("\\d*\\.?\\d*")) {
				addDefect(sheetName, DefectType.INCORRECT_FORMAT, errorMessage);
			}
		}
	}

	/**
	 * Validates if the value from the sheet and database are equal or not.
	 * 
	 * @param sheetName
	 *            the name of the sheet.
	 * @param sheetValue
	 *            the value from the sheet.
	 * @param databaseValue
	 *            the value from the database.
	 * @param errorMessage
	 *            the error message if the attributes are not equal.
	 */
	protected void validateMatchingAttribute(final String sheetName, final String sheetValue, final String databaseValue, final String errorMessage) {
		if (!sheetValue.equals(databaseValue)) {
			addDefect(sheetName, DefectType.DIFFERENCES, errorMessage);
		}
	}

	/**
	 * Adds a new defect to the defects map.
	 * 
	 * @param sheetName
	 *            the name of the sheet.
	 * @param defectType
	 *            the type of the defect.
	 * @param errorMessage
	 *            the error message for the defect.
	 */
	protected void addDefect(final String sheetName, final DefectType defectType, final String errorMessage) {

		if (null == defects.get(sheetName)) {

			final TerminologyImportValidationDefect defect = new TerminologyImportValidationDefect(sheetName);

			defect.addDefect(defectType, errorMessage);
			defects.put(sheetName, defect);

		} else {

			defects.get(sheetName).addDefect(defectType, errorMessage);

		}

	}
	
	protected void addUniqueAttribute(final String attributeName, final String attributeValue, final String sheetName) {
		Multimap<String, String> attribuetValueSheetMultimap = uniqueAttributes.get(attributeName);
		if (null == attribuetValueSheetMultimap) {
			attribuetValueSheetMultimap = HashMultimap.create();
		}
		attribuetValueSheetMultimap.put(attributeValue, sheetName);
		uniqueAttributes.put(attributeName, attribuetValueSheetMultimap);
	}

	public boolean isClearImport() {
		return importType == TerminologyImportType.CLEAR;
	}

	public boolean isMergeImport() {
		return importType == TerminologyImportType.MERGE;
	}

	public boolean isReplaceImport() {
		return importType == TerminologyImportType.REPLACE;
	}

	public Collection<TerminologyImportValidationDefect> getDefects() {
		return defects.values();
	}

	public TerminologyImportExcelParser getExcelParser() {
		return excelParser;
	}

	public Map<String, T> getDatabaseComponents() {
		return databaseComponents;
	}

	private void checkDuplication() {
		
		for (final String attributeName : uniqueAttributes.keySet()) {
			final Multimap<String, String> attributeValueSheetNamesMap = uniqueAttributes.get(attributeName);
			for (final String attributeValue : attributeValueSheetNamesMap.keySet()) {
				final Collection<String> sheetNames = attributeValueSheetNamesMap.get(attributeValue);
				
				if (sheetNames.size() > 1) {
					for (final String sheetName : sheetNames) {
						addDefect(sheetName, DefectType.DIFFERENCES, "'" + attributeName + "' attribute must be unique.");
					}
				}
				
			}
		}
		
	}

}