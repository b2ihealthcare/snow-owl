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
package com.b2international.snowowl.snomed.core.refset.automap;

import static java.util.Collections.emptyMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.b2international.commons.StringUtils;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * The editor model
 * 
 */
public class RefSetAutoMapperModel {

	@XStreamOmitField
	private boolean isDirty;
	private List<AutoMapEntry> content;

	private String targetRefSetTypeId;

	private int maximumNumberOfColumns;
	private int mappedSourceColumnIndex;
	/**
	 * Stores the indexes of the special fields (e.g. ID).
	 */
	private Map<String, Integer> columnConfiguration;
	private List<String> header;

	/**
	 * Default constructor is used when the create automapping wizard populates the fields of the model.
	 */
	public RefSetAutoMapperModel() {
		this.isDirty = false;
		this.maximumNumberOfColumns = -1;
		this.mappedSourceColumnIndex = 0;
	}

	public RefSetAutoMapperModel(List<List<String>> parsedContent, String targetRefsetType, Map<String, Integer> columnConfiguration,
			List<String> header) {
		this();
		this.setHeader(header);
		setTargetRefSetTypeId(targetRefsetType);
		this.columnConfiguration = columnConfiguration;
		this.content = generateAugmentedContent(parsedContent);
	}

	/**
	 * Input content contains only the parsed values. Automapper use an additional column to store the mapped values.
	 */
	private List<AutoMapEntry> generateAugmentedContent(List<List<String>> parsedContent) {
		List<AutoMapEntry> augmentedContent = new ArrayList<AutoMapEntry>();
		for (List<String> entry : parsedContent) {
			if (entry.size() > maximumNumberOfColumns) {
				maximumNumberOfColumns = entry.size();
			}

			augmentedContent.add(new AutoMapEntry(entry));
		}

		return augmentedContent;
	}

	/**
	 * @return the widest column size
	 */
	public int getMaximumNumberOfColumns() {
		return maximumNumberOfColumns;
	}

	/**
	 * Returns which column the user marked as the source of the mapping. Source means those values have to be resolved in SNOMED CT.
	 * 
	 * @return the mappedSourceColumnIndex
	 */
	public int getMappedSourceColumnIndex() {
		return mappedSourceColumnIndex;
	}

	/**
	 * Stores the index of the column which has to be used as the source of the mapping.
	 * 
	 * @param mappedSourceColumnIndex
	 *            the mappedSourceColumnIndex to set
	 */
	public void setMappedSourceColumnIndex(int mappedSourceColumnIndex) {
		this.mappedSourceColumnIndex = mappedSourceColumnIndex;
	}

	/**
	 * Returns the number of the parsed columns.
	 * 
	 * @return the number of parsed column headers.
	 */
	public int getNumberOfParsedColumns() {
		return getContent().isEmpty() ? 0 : getContent().get(0).getParsedValues().size();
	}

	/**
	 * Returns the parsed content. Key is the row number (starts from 0), value is the textual representation of each cell collected in a {@link List}
	 * . If the whole row is empty, the value is an empty list (<code>Collections.emptyList()}</code>). If a cell is empty, empty string is added to
	 * the list.
	 * 
	 * @return List<AutoMapEntry>
	 */
	public List<AutoMapEntry> getContent() {
		return content;
	}

	public void update(int rowIdentifier, String resolvedIdentifier) {
		if (!StringUtils.isEmpty(resolvedIdentifier)) {
			isDirty = true;
		}

		AutoMapEntry row = getContent().get(rowIdentifier);

		// don't override manually mapped values
		if (!row.getMappingMode().equals(MappingMode.MANUALLY_REVISED)) {
			row.setMappingMode(MappingMode.AUTOMAPPED);
			row.setAutoMappedId(resolvedIdentifier);
		}
	}

	public boolean isDirty() {
		return isDirty;
	}

	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}

	/**
	 * Returns the column index of a special column (e.g. ID).
	 * 
	 * @see RefSetAutoMapColumnConstants
	 * @param columnId
	 * @return the column index
	 * @throws IllegalArgumentException
	 *             when the columnId is not found in the column configuration.
	 */
	public int getColumnIndexById(String columnId) throws IllegalArgumentException {
		Integer columnIndex = columnConfiguration.get(columnId);
		if (columnIndex == null) {
			throw new IllegalArgumentException("No column index found for column id: " + columnId);
		}
		return columnIndex.intValue();
	}

	/**
	 * Returns the column index with the given header.
	 * 
	 * @param columnHeader
	 *            the header of the column
	 * @return the index of the column or -1 if the columnHeader is not found.
	 */
	public int getColumnIndexByHeader(String columnHeader) {
		if (!header.contains(columnHeader)) {
			return -1;
		}
		return header.indexOf(columnHeader);
	}

	public void setColumnConfiguration(Map<String, Integer> headerConfiguration) {
		this.columnConfiguration = headerConfiguration;
	}

	public Map<String, Integer> getColumnConfiguration() {
		return columnConfiguration;
	}

	public void setContent(List<List<String>> content) {
		this.content = generateAugmentedContent(content);
	}

	public String getTargetRefSetTypeId() {
		return targetRefSetTypeId;
	}

	public void setTargetRefSetTypeId(String targetRefSetTypeId) {
		this.targetRefSetTypeId = targetRefSetTypeId;
	}

	public List<String> getHeader() {
		return header;
	}

	public void setHeader(List<String> header) {
		this.header = header;
	}

	/**
	 * Validates the model and return invalid entries to error message map.
	 * 
	 * @return
	 */
	public Map<AutoMapEntry, String> validate() {
		/* place to add validation rules */
		return emptyMap();
	}

}