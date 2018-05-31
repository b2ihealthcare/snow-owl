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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.b2international.commons.StringUtils;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * Parser class to parse the excel sheet for the terminology imports.
 * 
 * @since 3.4
 */
public class TerminologyImportExcelParser {

	private final Map<String, LinkedList<String>> properties = new HashMap<String, LinkedList<String>>();
	private final Map<String, Multimap<String, String>> metadata = new HashMap<String, Multimap<String, String>>();
	private final Map<String, Set<Row>> members = new HashMap<String, Set<Row>>();

	/**
	 * Parses the given sheets into property, metadata and member collections. 
	 * 
	 * @param sheets the sheets to parse.
	 * @param propertyIndex the index of the last property attribute.
	 */
	public void parse(final Collection<Sheet> sheets, final int propertyIndex) {

		for (final Sheet sheet : sheets) {

			final int memberStartIndex = getMemberStartIndex(sheet, propertyIndex);

			properties.put(sheet.getSheetName(), processProperties(sheet, propertyIndex));
			metadata.put(sheet.getSheetName(), processKeywords(sheet, propertyIndex, memberStartIndex));
			members.put(sheet.getSheetName(), processMembers(sheet, memberStartIndex));

		}

	}
	
	public Map<String, LinkedList<String>> getProperties() {
		return properties;
	}

	public Map<String, Multimap<String, String>> getMetadata() {
		return metadata;
	}

	public Map<String, Set<Row>> getMembers() {
		return members;
	}

	private int getMemberStartIndex(final Sheet sheet, final int propertyIndex) {

		int i = propertyIndex;

		while (i < sheet.getLastRowNum()) {

			final Row row = sheet.getRow(i);

			if (null != row) {
				
				final String value = ExcelUtilities.extractContentAsString(row.getCell(0, Row.CREATE_NULL_AS_BLANK));

				if (!StringUtils.isEmpty(value) && (value.equalsIgnoreCase("code") || value.equalsIgnoreCase("effective time"))) {
					break;
				}
				
			}

			i++;

		}

		return i;

	}

	private LinkedList<String> processProperties(final Sheet sheet, final int propertyIndex) {
		final LinkedList<String> componentProperties = Lists.newLinkedList();
		for (int i = 0; i < propertyIndex; i++) {
			final Row row = sheet.getRow(i);
			if (row != null) {
				componentProperties.add(ExcelUtilities.extractContentAsString(row.getCell(1, Row.CREATE_NULL_AS_BLANK)));
			}
		}
		return componentProperties;
	}

	private Multimap<String, String> processKeywords(Sheet sheet, int propertyIndex, int memberStartIndex) {

		final Multimap<String, String> componentMetadata = HashMultimap.create();

		for (int i = propertyIndex + 1; i < memberStartIndex - 1; i++) {
			
			final Row row = sheet.getRow(i);

			if (null != row) {
				
				final String groupName = ExcelUtilities.extractContentAsString(row.getCell(0, Row.CREATE_NULL_AS_BLANK));
				final String keyword = ExcelUtilities.extractContentAsString(row.getCell(1, Row.CREATE_NULL_AS_BLANK));

				if (!groupName.isEmpty()) {
					
					componentMetadata.put(groupName, keyword);
					
				}
				
			}
		}

		return componentMetadata;

	}

	private Set<Row> processMembers(final Sheet sheet, int memberStartIndex) {

		final int lastRowNum = sheet.getLastRowNum();
		final Set<Row> componentMembers = Sets.newHashSet();

		for (int i = memberStartIndex + 1; i <= lastRowNum; i++) {

			final Row row = sheet.getRow(i);

			if (null != row) {

				boolean hasValue = false;
				short lastCellNum = row.getLastCellNum();

				for (int j = 0; j < lastCellNum; j++) {

					if (!StringUtils.isEmpty(ExcelUtilities.extractContentAsString(row.getCell(j)))) {

						hasValue = true;
						break;
						
					}

				}

				if (hasValue) {
					
					componentMembers.add(row);
					
				}
			}

		}

		return componentMembers;

	}

}