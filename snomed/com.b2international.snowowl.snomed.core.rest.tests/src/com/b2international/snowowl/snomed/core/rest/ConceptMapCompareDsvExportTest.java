/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest;

import static com.b2international.snowowl.core.compare.ConceptMapCompareChangeKind.DIFFERENT_TARGET;
import static com.b2international.snowowl.core.compare.ConceptMapCompareChangeKind.MISSING;
import static com.b2international.snowowl.core.compare.ConceptMapCompareChangeKind.PRESENT;
import static com.b2international.snowowl.core.compare.ConceptMapCompareChangeKind.SAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.assertj.core.util.Files;
import org.junit.Test;

import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.compare.ConceptMapCompareChangeKind;
import com.b2international.snowowl.core.compare.ConceptMapCompareResultItem;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.domain.ConceptMapMapping;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.test.commons.Services;

/**
 * @since 7.13
 */
public class ConceptMapCompareDsvExportTest {
	
	private static final Set<ConceptMapCompareChangeKind> ALL = Set.of(ConceptMapCompareChangeKind.values());

	private static final List<ConceptMapCompareResultItem> ITEMS = List.of(
		createItem(DIFFERENT_TARGET, "A", "123037004", "Body structure", "H00-H59", "Chapter VII - Diseases of the eye and adnexa"),
		createItem(DIFFERENT_TARGET, "B", "123037004", "Body structure", "H60-H95", "Chapter VII - Diseases of the ear and mastoid process"),
		createItemUnspecifiedTarget(PRESENT, "B", "361083003", "Normal anatomy"),
		createItem(PRESENT, "B", "442083009", "Anatomical or acquired body structure", "P00-P96", "Chapter XVI - Certain conditions originating in the perinatal preioid"),
		createItemUnspecifiedTarget(MISSING, "A", "14734007", "Administrative procedure (procedure)"),
		createItem(SAME, "A", "91832008", "Anatomical organizational pattern", "I00-I99", "Chapter IX - Diseases of the circulatory system"),
		createItem(SAME, "B", "258331007", "Anatomical site notations for tumor staging", "C00-D48", "Chapter II - Neoplasms"),
		createItemUnspecifiedTarget(SAME, "B", "278001007", "Nonspecific site")
	);
	
	@Test
	public void testExport() throws IOException {
		final File file = CodeSystemRequests.prepareConceptMapCompareDsvExport(ITEMS, Paths.get(System.getProperty("user.home"), String.format("concept-map-compare-results-%s.txt", Dates.now())).toString())
				.delimiter(';')
				.changeKids(ALL)
				.build()
				.execute(Services.context());
		
		assertNotNull(file);
		assertFile(file, ITEMS);
	}

	@Test
	public void testFilteredExport() throws IOException {
		final File file = CodeSystemRequests.prepareConceptMapCompareDsvExport(ITEMS, Paths.get(System.getProperty("user.home"), String.format("concept-map-compare-results-%s.txt", Dates.now())).toString())
				.delimiter(';')
				.changeKids(Set.of(DIFFERENT_TARGET, MISSING))
				.build()
				.execute(Services.context());
		
		assertNotNull(file);
		assertFile(file, List.of(
			createItem(DIFFERENT_TARGET, "A", "123037004", "Body structure", "H00-H59", "Chapter VII - Diseases of the eye and adnexa"),
			createItem(DIFFERENT_TARGET, "B", "123037004", "Body structure", "H60-H95", "Chapter VII - Diseases of the ear and mastoid process"),
			createItemUnspecifiedTarget(MISSING, "A", "14734007", "Administrative procedure (procedure)"))
		);
	}
	
	@Test
	public void testExportEmptyList() throws IOException {
		final File file = CodeSystemRequests.prepareConceptMapCompareDsvExport(Collections.emptyList(), Paths.get(System.getProperty("user.home"), String.format("concept-map-compare-results-%s.txt", Dates.now())).toString())
				.delimiter(';')
				.changeKids(ALL)
				.build()
				.execute(Services.context());
		
		assertNotNull(file);
		assertFile(file, Collections.emptyList());
	}
	
	private void assertFile(final File file, final List<ConceptMapCompareResultItem> expectedItems) throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			int i = 0;
			//Read header
			String line = reader.readLine();

			line = reader.readLine();
			while (line != null) {
				final String[] fields = line.split(";", -1);
				final ConceptMapCompareResultItem item = expectedItems.get(i);
				assertThat(fields).containsOnly(toFieldsArray(item));
				
				i++;
				line = reader.readLine();
			}
			
			assertEquals(expectedItems.size(), i);
		} catch (Exception e) {
			throw e;
		} finally {
			Files.delete(file);
		}
	}
	
	private String[] toFieldsArray(ConceptMapCompareResultItem item) {
		final ConceptMapMapping mapping = item.getMapping();
		return new String[] {
				item.getChangeKind().name(),
				ConceptMapCompareChangeKind.SAME.equals(item.getChangeKind()) ? "Both" : mapping .getContainerTerm(),
				mapping.getSourceComponentURI().resourceId(),
				mapping.getSourceComponentURI().identifier(),
				mapping.getSourceTerm(),
				mapping.getTargetComponentURI().resourceId(),
				mapping.getTargetComponentURI().identifier(),
				mapping.getTargetTerm()};
	}

	private static ConceptMapCompareResultItem createItemUnspecifiedTarget(ConceptMapCompareChangeKind changeKind, String containerTerm, String sourceId, String sourceTerm) {
		ConceptMapMapping mapping = ConceptMapMapping.builder()
				.containerTerm(containerTerm)
				.sourceComponentURI(ComponentURI.of(CodeSystem.uri("SNOMEDCT-CA"), SnomedConcept.TYPE, sourceId))
				.sourceTerm(sourceTerm)
				.targetComponentURI(ComponentURI.UNSPECIFIED)
				.targetTerm("")
				.build();
		return new ConceptMapCompareResultItem(changeKind, mapping);
	}
	
	private static ConceptMapCompareResultItem createItem(ConceptMapCompareChangeKind changeKind, String containerTerm, String sourceId, String sourceTerm, String targetId, String targetTerm) {
		ConceptMapMapping mapping = ConceptMapMapping.builder()
				.containerTerm(containerTerm)
				.sourceComponentURI(ComponentURI.of(CodeSystem.uri("SNOMEDCT-CA"), SnomedConcept.TYPE, sourceId))
				.sourceTerm(sourceTerm)
				.targetComponentURI(ComponentURI.of(CodeSystem.uri("ICD-10-CA"), "concept", targetId))
				.targetTerm(targetTerm)
				.build();
		return new ConceptMapCompareResultItem(changeKind, mapping);
	}
}
