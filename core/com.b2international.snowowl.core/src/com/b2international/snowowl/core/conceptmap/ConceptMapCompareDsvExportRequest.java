/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.conceptmap;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.compare.ConceptMapCompareChangeKind;
import com.b2international.snowowl.core.compare.ConceptMapCompareResultItem;
import com.b2international.snowowl.core.events.Request;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.base.Throwables;

/**
 * @since 7.13
 */
final class ConceptMapCompareDsvExportRequest implements Request<ServiceProvider, File> {

	private static final long serialVersionUID = 1L;
	
	private Character delimiter;
	private String filePath;
	private Set<ConceptMapCompareChangeKind> changeKinds;
	private List<ConceptMapCompareResultItem> items;

	ConceptMapCompareDsvExportRequest() {
	}
	
	public void setDelimiter(Character delimiter) {
		this.delimiter = delimiter;
	}
	
	public void setChangeKinds(Set<ConceptMapCompareChangeKind> changeKinds) {
		this.changeKinds = changeKinds;
	}
	
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public void setItems(List<ConceptMapCompareResultItem> items) {
		this.items = items;
	}
	
	@Override
	public File execute(ServiceProvider context) {
	 final List<ConceptMapCompareResultItem> resultsToExport = items.stream()
				.filter(item -> changeKinds.contains(item.getChangeKind()))
				.collect(Collectors.toList());
		
		final CsvMapper mapper = new CsvMapper();
		final CsvSchema schema = mapper.schemaFor(ConceptMapCompareResultItem.class)
				.withHeader()
				.withoutQuoteChar()
				.withColumnSeparator(delimiter)
				.withNullValue("");
		
		try (OutputStream newOutputStream = Files.newOutputStream(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
			mapper.writer(schema).writeValue(newOutputStream, resultsToExport);
		} catch (Exception e) {
			throw new BadRequestException("An error occured durin Concept Map Compare DSV export: %s", Throwables.getRootCause(e).getMessage());
		}
		
		return Paths.get(filePath).toFile();
	}

}
