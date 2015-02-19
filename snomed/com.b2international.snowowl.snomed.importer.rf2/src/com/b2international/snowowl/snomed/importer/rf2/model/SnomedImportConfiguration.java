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
package com.b2international.snowowl.snomed.importer.rf2.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import org.supercsv.cellprocessor.ift.CellProcessor;

/**
 * Import configuration containing the following elements:
 * <ul>
 * <li>component type to import
 * <li>column mapping for CSV files
 * <li>the CSV bean class to use
 * <li>the expected CSV header
 * <li>the database index defintions
 * </ul>
 * 
 * 
 * @param <T> the CSV bean type 
 */
public class SnomedImportConfiguration<T> {
	
	private final ComponentImportType type;
	private final Map<String, CellProcessor> cellProcessorMapping;
	private final Class<T> rowClass;
	private final String[] expectedHeader;
	private final List<IndexConfiguration> indexes;

	public SnomedImportConfiguration(final ComponentImportType type, final Map<String, CellProcessor> cellProcessorMapping, final Class<T> rowClass, 
			final String[] expectedHeader, final List<IndexConfiguration> indexes) {
		
		this.type = checkNotNull(type, "type");
		this.cellProcessorMapping = checkNotNull(cellProcessorMapping, "cellProcessorMapping");
		this.rowClass = checkNotNull(rowClass, "rowClass");
		this.expectedHeader = checkNotNull(expectedHeader, "expectedHeader");
		this.indexes = checkNotNull(indexes, "indexes");
	}

	public ComponentImportType getType() {
		return type;
	}

	public Map<String, CellProcessor> getCellProcessorMapping() {
		return cellProcessorMapping;
	}

	public Class<T> getRowClass() {
		return rowClass;
	}
	
	public String[] getExpectedHeader() {
		return expectedHeader;
	}
	
	public List<IndexConfiguration> getIndexes() {
		return indexes;
	}
}