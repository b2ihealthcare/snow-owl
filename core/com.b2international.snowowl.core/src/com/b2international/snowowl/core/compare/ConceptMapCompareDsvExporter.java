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
package com.b2international.snowowl.core.compare;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.domain.ConceptMapMapping;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

/**
 * @since 7.13
 */
public final class ConceptMapCompareDsvExporter {
	
	private static final char DELIMITER = ';';
	
	public static File export(List<ConceptMapCompareResultItem> items, Set<ConceptMapCompareChangeKind> changeKinds) throws IOException {
		final String filePath = Paths.get(System.getProperty("user.home"), String.format("concept-map-compare-results-%s.txt", Dates.now())).toString();
		return export(items, changeKinds, filePath);
	}
	
	public static File export(final List<ConceptMapCompareResultItem> items, final Set<ConceptMapCompareChangeKind> changeKinds, final String filePath) throws IOException {
		
		 final List<ConceptMapCompareDsvExportModel> resultsToExport = items.stream()
				.filter(item -> changeKinds.contains(item.getChangeKind()))
				.sorted((i1, i2) -> i1.getChangeKind().compareTo(i2.getChangeKind()))
				.map(item -> toExportModel(item))
				.collect(Collectors.toList());
		
		final CsvMapper mapper = new CsvMapper();
		final CsvSchema schema = mapper.schemaFor(ConceptMapCompareDsvExportModel.class)
				.withHeader()
				.withColumnSeparator(DELIMITER)
				.withNullValue("");
		
		try (OutputStream newOutputStream = Files.newOutputStream(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
			mapper.writer(schema).writeValue(newOutputStream, resultsToExport);
		} catch (Exception e) {
			throw e;
		}
		
		return Paths.get(filePath).toFile();
	}
	
	public static ConceptMapCompareDsvExportModel toExportModel(final ConceptMapCompareResultItem item) {
		final ConceptMapMapping mapping = item.getMapping();
		return new ConceptMapCompareDsvExportModel(
				item.getChangeKind().toString(),
				ConceptMapCompareChangeKind.SAME.equals(item.getChangeKind()) ? "Both" : mapping .getContainerTerm(),
				mapping.getSourceComponentURI().codeSystem(),
				mapping.getSourceComponentURI().identifier(),
				mapping.getSourceTerm(),
				mapping.getTargetComponentURI().codeSystem(),
				mapping.getTargetComponentURI().identifier(),
				mapping.getTargetTerm());
	}
	
}
