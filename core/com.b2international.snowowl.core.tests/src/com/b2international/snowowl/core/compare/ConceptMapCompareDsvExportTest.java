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

import static com.b2international.snowowl.core.compare.ConceptMapCompareChangeKind.DIFFERENT_TARGET;
import static com.b2international.snowowl.core.compare.ConceptMapCompareChangeKind.MISSING;
import static com.b2international.snowowl.core.compare.ConceptMapCompareChangeKind.PRESENT;
import static com.b2international.snowowl.core.compare.ConceptMapCompareChangeKind.SAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import com.b2international.snowowl.core.domain.ConceptMapMapping;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * @since 7.13
 */
public class ConceptMapCompareDsvExportTest {
	
	private static final Set<ConceptMapCompareChangeKind> ALL = ImmutableSet.copyOf(ConceptMapCompareChangeKind.values());

	private static final List<ConceptMapCompareResultItem> ITEMS = ImmutableList.of(
			createItem(DIFFERENT_TARGET, "A", "123037004", "Body structure", "H00-H59", "Chapter VII - Diseases of the eye and adnexa"),
			createItem(DIFFERENT_TARGET, "B", "123037004", "Body structure", "H60-H95", "Chapter VII - Diseases of the ear and mastoid process"),
			createItemUnspecifiedTarget(PRESENT, "B", "361083003", "Normal anatomy"),
			createItem(PRESENT, "B", "442083009", "Anatomical or acquired body structure", "P00-P96", "Chapter XVI - Certain conditions originating in the perinatal preioid"),
			createItemUnspecifiedTarget(MISSING, "A", "14734007", "Administrative procedure (procedure)"),
			createItem(SAME, "A", "91832008", "Anatomical organizational pattern", "I00-I99", "Chapter IX - Diseases of the circulatory system"),
			createItem(SAME, "B", "258331007", "Anatomical site notations for tumor staging", "C00-D48", "Chapter II - Neoplasms"),
			createItemUnspecifiedTarget(SAME, "B", "278001007", "Nonspecific site"));
	
	@Test
	public void testExport() throws IOException {
		final File file = ConceptMapCompareDsvExporter.export(ITEMS, ALL);
		assertNotNull(file);
		assertFile(file, ITEMS);
	}

	@Test
	public void testFilteredExport() throws IOException {
		final File file = ConceptMapCompareDsvExporter.export(ITEMS, ImmutableSet.of(DIFFERENT_TARGET, MISSING));
		assertNotNull(file);
		assertFile(file, ImmutableList.of(
				createItem(DIFFERENT_TARGET, "A", "123037004", "Body structure", "H00-H59", "Chapter VII - Diseases of the eye and adnexa"),
				createItem(DIFFERENT_TARGET, "B", "123037004", "Body structure", "H60-H95", "Chapter VII - Diseases of the ear and mastoid process"),
				createItemUnspecifiedTarget(MISSING, "A", "14734007", "Administrative procedure (procedure)")));
	}
	
	@Test
	public void testExportEmptyList() throws IOException {
		final File file = ConceptMapCompareDsvExporter.export(Collections.emptyList(), ALL);
		assertNotNull(file);
		assertFile(file, Collections.emptyList());
	}
	
	private void assertFile(final File file, final List<ConceptMapCompareResultItem> expectedItems) throws IOException {
		final CsvMapper mapper = new CsvMapper();
		final CsvSchema schema = mapper.schemaFor(ConceptMapCompareDsvExportModel.class)
				.withHeader()
				.withColumnSeparator(';');
		
		final List<ConceptMapCompareDsvExportModel> convertedItems = expectedItems.stream().map(ConceptMapCompareDsvExporter::toExportModel).collect(Collectors.toList());

		try (InputStream in = Files.newInputStream(Paths.get(file.getPath()), StandardOpenOption.READ)) {
			
			final ObjectReader reader = mapper.readerFor(ConceptMapCompareDsvExportModel.class).with(schema);
			final List<Object> items = reader.readValues(in).readAll();
			
			assertEquals(expectedItems.size(), items.size());
			
			for (Object obj : items) {
				assertTrue(convertedItems.contains(obj));
			}
			
		} catch (IOException e) {
			throw e;
		} finally {
			Files.delete(Paths.get(file.getPath()));
		}
	}
	
	private static ConceptMapCompareResultItem createItemUnspecifiedTarget(ConceptMapCompareChangeKind changeKind, String containerTerm, String sourceId, String sourceTerm) {
		ConceptMapMapping mapping = ConceptMapMapping.builder()
				.containerTerm(containerTerm)
				.sourceComponentURI(ComponentURI.of("SNOMEDCT-CA", (short)100, sourceId))
				.sourceTerm(sourceTerm)
				.targetComponentURI(ComponentURI.UNSPECIFIED)
				.targetTerm("")
				.build();
		return new ConceptMapCompareResultItem(changeKind, mapping);
	}
	
	private static ConceptMapCompareResultItem createItem(ConceptMapCompareChangeKind changeKind, String containerTerm, String sourceId, String sourceTerm, String targetId, String targetTerm) {
		ConceptMapMapping mapping = ConceptMapMapping.builder()
				.containerTerm(containerTerm)
				.sourceComponentURI(ComponentURI.of("SNOMEDCT-CA", (short)100, sourceId))
				.sourceTerm(sourceTerm)
				.targetComponentURI(ComponentURI.of("ICD-10-CA", (short)300, targetId))
				.targetTerm(targetTerm)
				.build();
		return new ConceptMapCompareResultItem(changeKind, mapping);
	}
}
