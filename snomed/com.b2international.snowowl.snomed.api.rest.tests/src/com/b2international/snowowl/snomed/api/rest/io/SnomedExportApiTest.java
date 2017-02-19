/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest.io;

import static com.b2international.snowowl.snomed.api.rest.CodeSystemRestRequests.createCodeSystem;
import static com.b2international.snowowl.snomed.api.rest.CodeSystemVersionRestRequests.createVersion;
import static com.b2international.snowowl.snomed.api.rest.SnomedExportRestRequests.createExport;
import static com.b2international.snowowl.snomed.api.rest.SnomedExportRestRequests.getExport;
import static com.b2international.snowowl.snomed.api.rest.SnomedExportRestRequests.getExportFile;
import static com.b2international.snowowl.snomed.api.rest.SnomedExportRestRequests.getExportId;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewConcept;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewDescription;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewRelationship;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;

import com.b2international.commons.Pair;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.*;

/**
 * @since 5.4
 */
public class SnomedExportApiTest extends AbstractSnomedApiTest {

	private static final Joiner TAB_JOINER = Joiner.on('\t');

	private static void assertArchiveContainsLines(File exportArchive, Multimap<String, Pair<Boolean, String>> fileToLinesMap) throws Exception {
		Multimap<String, Pair<Boolean, String>> resultMap = collectLines(exportArchive, fileToLinesMap);
		Set<String> difference = Sets.difference(fileToLinesMap.keySet(), resultMap.keySet());

		assertTrue(String.format("File(s) starting with <%s> are missing from the export archive", Joiner.on(", ").join(difference)), difference.isEmpty());

		for (Entry<String, Collection<Pair<Boolean, String>>> entry : fileToLinesMap.asMap().entrySet()) {
			for (Pair<Boolean, String> result : resultMap.get(entry.getKey())) {
				Pair<Boolean, String> originalLine = Iterables.getOnlyElement(FluentIterable.from(entry.getValue()).filter(new Predicate<Pair<Boolean, String>>() {
					@Override
					public boolean apply(Pair<Boolean, String> input) {
						return input.getB().equals(result.getB());
					}
				}));

				String message = String.format("Line: %s must %sbe contained in %s", originalLine.getB(), originalLine.getA() ? "" : "not ", entry.getKey());
				assertEquals(message, true, result.getA());
			}
		}
	}

	private static Multimap<String, Pair<Boolean, String>> collectLines(File exportArchive, Multimap<String, Pair<Boolean, String>> fileToLinesMap) throws Exception {
		Multimap<String, Pair<Boolean, String>> resultMap = ArrayListMultimap.create();

		try (FileSystem fs = FileSystems.newFileSystem(exportArchive.toPath(), null)) {
			for (Path path : fs.getRootDirectories()) {
				Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						for (String filePrefix : fileToLinesMap.asMap().keySet()) {
							if (file.getFileName().toString().startsWith(filePrefix)) {
								collectLines(resultMap, file, filePrefix, fileToLinesMap.get(filePrefix));
								break;
							}
						}

						return super.visitFile(file, attrs);
					}
				});

			}

		} catch (Exception e) {
			throw e;
		}

		return resultMap;
	}

	private static void collectLines(Multimap<String, Pair<Boolean, String>> resultMap, Path file, String filePrefix, Collection<Pair<Boolean, String>> expectedLines) throws IOException {
		List<String> lines = Files.readAllLines(file, Charsets.UTF_8);

		for (Pair<Boolean, String> line : expectedLines) {
			if (lines.contains(line.getB())) {
				resultMap.put(filePrefix, Pair.of(line.getA(), line.getB()));
			} else {
				resultMap.put(filePrefix, Pair.of(!line.getA(), line.getB()));
			}
		}
	}

	@Test
	public void createValidExportConfiguration() {
		Map<?, ?> config = ImmutableMap.builder()
				.put("type", Rf2ReleaseType.DELTA.name())
				.put("branchPath", branchPath.getPath())
				.build();

		String exportId = getExportId(createExport(config));

		getExport(exportId).statusCode(200)
		.body("type", equalTo(Rf2ReleaseType.DELTA.name()))
		.body("branchPath", equalTo(branchPath.getPath()));
	}

	@Test
	public void createInvalidExportConfiguration() {
		Map<?, ?> config = ImmutableMap.builder()
				.put("type", Rf2ReleaseType.DELTA.name())
				.build();

		createExport(config).statusCode(400);
	}

	@Test
	public void exportUnpublishedDeltaRelationships() throws Exception {
		String statedRelationshipId = createNewRelationship(branchPath, Concepts.ROOT_CONCEPT, Concepts.PART_OF, Concepts.NAMESPACE_ROOT, CharacteristicType.STATED_RELATIONSHIP);
		String inferredRelationshipId = createNewRelationship(branchPath, Concepts.ROOT_CONCEPT, Concepts.PART_OF, Concepts.NAMESPACE_ROOT, CharacteristicType.INFERRED_RELATIONSHIP);
		String additionalRelationshipId = createNewRelationship(branchPath, Concepts.ROOT_CONCEPT, Concepts.PART_OF, Concepts.NAMESPACE_ROOT, CharacteristicType.ADDITIONAL_RELATIONSHIP);

		String transientEffectiveTime = "20170301";

		Map<?, ?> config = ImmutableMap.builder()
				.put("type", Rf2ReleaseType.DELTA.name())
				.put("branchPath", branchPath.getPath())
				.put("transientEffectiveTime", transientEffectiveTime)
				.build();

		String exportId = getExportId(createExport(config));

		getExport(exportId).statusCode(200)
		.body("type", equalTo(Rf2ReleaseType.DELTA.name()))
		.body("branchPath", equalTo(branchPath.getPath()))
		.body("transientEffectiveTime", equalTo(transientEffectiveTime));

		File exportArchive = getExportFile(exportId);

		String statedLine = TAB_JOINER.join(statedRelationshipId, 
				transientEffectiveTime, 
				"1", 
				Concepts.MODULE_SCT_CORE, 
				Concepts.ROOT_CONCEPT, 
				Concepts.NAMESPACE_ROOT,
				"0",
				Concepts.PART_OF,
				CharacteristicType.STATED_RELATIONSHIP.getConceptId(),
				Concepts.EXISTENTIAL_RESTRICTION_MODIFIER); 

		String inferredLine = TAB_JOINER.join(inferredRelationshipId, 
				transientEffectiveTime, 
				"1", 
				Concepts.MODULE_SCT_CORE, 
				Concepts.ROOT_CONCEPT, 
				Concepts.NAMESPACE_ROOT,
				"0",
				Concepts.PART_OF,
				CharacteristicType.INFERRED_RELATIONSHIP.getConceptId(),
				Concepts.EXISTENTIAL_RESTRICTION_MODIFIER);

		String additionalLine = TAB_JOINER.join(additionalRelationshipId, 
				transientEffectiveTime, 
				"1", 
				Concepts.MODULE_SCT_CORE, 
				Concepts.ROOT_CONCEPT, 
				Concepts.NAMESPACE_ROOT,
				"0",
				Concepts.PART_OF,
				CharacteristicType.ADDITIONAL_RELATIONSHIP.getConceptId(),
				Concepts.EXISTENTIAL_RESTRICTION_MODIFIER); 

		Multimap<String, Pair<Boolean, String>> fileToLinesMap = ArrayListMultimap.<String, Pair<Boolean, String>>create();

		fileToLinesMap.put("sct2_StatedRelationship", Pair.of(true, statedLine));
		fileToLinesMap.put("sct2_StatedRelationship", Pair.of(false, inferredLine));
		fileToLinesMap.put("sct2_StatedRelationship", Pair.of(false, additionalLine));
		fileToLinesMap.put("sct2_Relationship", Pair.of(false, statedLine));
		fileToLinesMap.put("sct2_Relationship", Pair.of(true, inferredLine));
		fileToLinesMap.put("sct2_Relationship", Pair.of(true, additionalLine));

		assertArchiveContainsLines(exportArchive, fileToLinesMap);
	}

	@Test
	public void exportDeltaInDateRangeFromVersion() throws Exception {
		createCodeSystem(branchPath, "SNOMEDCT-DELTA").statusCode(201);

		String statedRelationshipId = createNewRelationship(branchPath, Concepts.ROOT_CONCEPT, Concepts.PART_OF, Concepts.NAMESPACE_ROOT, CharacteristicType.STATED_RELATIONSHIP);
		String inferredRelationshipId = createNewRelationship(branchPath, Concepts.ROOT_CONCEPT, Concepts.PART_OF, Concepts.NAMESPACE_ROOT, CharacteristicType.INFERRED_RELATIONSHIP);
		String additionalRelationshipId = createNewRelationship(branchPath, Concepts.ROOT_CONCEPT, Concepts.PART_OF, Concepts.NAMESPACE_ROOT, CharacteristicType.ADDITIONAL_RELATIONSHIP);

		String versionEffectiveTime = "20170302";
		createVersion("SNOMEDCT-DELTA", "v1", versionEffectiveTime).statusCode(201);
		IBranchPath versionPath = BranchPathUtils.createPath(branchPath, "v1");

		Map<?, ?> config = ImmutableMap.builder()
				.put("type", Rf2ReleaseType.DELTA.name())
				.put("branchPath", versionPath.getPath())
				.put("deltaStartEffectiveTime", versionEffectiveTime)
				.put("deltaEndEffectiveTime", versionEffectiveTime)
				.build();

		String exportId = getExportId(createExport(config));

		getExport(exportId).statusCode(200)
		.body("type", equalTo(Rf2ReleaseType.DELTA.name()))
		.body("branchPath", equalTo(versionPath.getPath()))
		.body("deltaStartEffectiveTime", equalTo(versionEffectiveTime))
		.body("deltaEndEffectiveTime", equalTo(versionEffectiveTime));

		File exportArchive = getExportFile(exportId);

		String statedLine = TAB_JOINER.join(statedRelationshipId, 
				versionEffectiveTime, 
				"1", 
				Concepts.MODULE_SCT_CORE, 
				Concepts.ROOT_CONCEPT, 
				Concepts.NAMESPACE_ROOT,
				"0",
				Concepts.PART_OF,
				CharacteristicType.STATED_RELATIONSHIP.getConceptId(),
				Concepts.EXISTENTIAL_RESTRICTION_MODIFIER); 

		String inferredLine = TAB_JOINER.join(inferredRelationshipId, 
				versionEffectiveTime, 
				"1", 
				Concepts.MODULE_SCT_CORE, 
				Concepts.ROOT_CONCEPT, 
				Concepts.NAMESPACE_ROOT,
				"0",
				Concepts.PART_OF,
				CharacteristicType.INFERRED_RELATIONSHIP.getConceptId(),
				Concepts.EXISTENTIAL_RESTRICTION_MODIFIER);

		String additionalLine = TAB_JOINER.join(additionalRelationshipId, 
				versionEffectiveTime, 
				"1", 
				Concepts.MODULE_SCT_CORE, 
				Concepts.ROOT_CONCEPT, 
				Concepts.NAMESPACE_ROOT,
				"0",
				Concepts.PART_OF,
				CharacteristicType.ADDITIONAL_RELATIONSHIP.getConceptId(),
				Concepts.EXISTENTIAL_RESTRICTION_MODIFIER); 

		Multimap<String, Pair<Boolean, String>> fileToLinesMap = ArrayListMultimap.<String, Pair<Boolean, String>>create();

		fileToLinesMap.put("sct2_StatedRelationship", Pair.of(true, statedLine));
		fileToLinesMap.put("sct2_StatedRelationship", Pair.of(false, inferredLine));
		fileToLinesMap.put("sct2_StatedRelationship", Pair.of(false, additionalLine));
		fileToLinesMap.put("sct2_Relationship", Pair.of(false, statedLine));
		fileToLinesMap.put("sct2_Relationship", Pair.of(true, inferredLine));
		fileToLinesMap.put("sct2_Relationship", Pair.of(true, additionalLine));

		assertArchiveContainsLines(exportArchive, fileToLinesMap);
	}

	@Test
	public void exportDeltaInDateRangeAndUnpublishedComponents() throws Exception {
		createCodeSystem(branchPath, "SNOMEDCT-GAMMA").statusCode(201);

		String statedRelationshipId = createNewRelationship(branchPath, Concepts.ROOT_CONCEPT, Concepts.PART_OF, Concepts.NAMESPACE_ROOT, CharacteristicType.STATED_RELATIONSHIP);
		String inferredRelationshipId = createNewRelationship(branchPath, Concepts.ROOT_CONCEPT, Concepts.PART_OF, Concepts.NAMESPACE_ROOT, CharacteristicType.INFERRED_RELATIONSHIP);
		String additionalRelationshipId = createNewRelationship(branchPath, Concepts.ROOT_CONCEPT, Concepts.PART_OF, Concepts.NAMESPACE_ROOT, CharacteristicType.ADDITIONAL_RELATIONSHIP);

		String relationshipEffectiveTime = "20170303";
		createVersion("SNOMEDCT-GAMMA", "v1", relationshipEffectiveTime).statusCode(201);

		String conceptId = createNewConcept(branchPath);
		String conceptEffectiveTime = "20170304";
		createVersion("SNOMEDCT-GAMMA", "v2", conceptEffectiveTime).statusCode(201);

		String descriptionId = createNewDescription(branchPath, conceptId);
		// do not version description

		Map<?, ?> config = ImmutableMap.builder()
				.put("type", Rf2ReleaseType.DELTA.name())
				.put("branchPath", branchPath.getPath())
				.put("deltaStartEffectiveTime", relationshipEffectiveTime)
				.put("deltaEndEffectiveTime", relationshipEffectiveTime)
				.put("includeUnpublished", true)
				.build();

		String exportId = getExportId(createExport(config));

		getExport(exportId).statusCode(200)
		.body("type", equalTo(Rf2ReleaseType.DELTA.name()))
		.body("branchPath", equalTo(branchPath.getPath()))
		.body("deltaStartEffectiveTime", equalTo(relationshipEffectiveTime))
		.body("deltaEndEffectiveTime", equalTo(relationshipEffectiveTime))
		.body("includeUnpublished", equalTo(true));

		File exportArchive = getExportFile(exportId);

		String statedLine = TAB_JOINER.join(statedRelationshipId, 
				relationshipEffectiveTime, 
				"1", 
				Concepts.MODULE_SCT_CORE, 
				Concepts.ROOT_CONCEPT, 
				Concepts.NAMESPACE_ROOT,
				"0",
				Concepts.PART_OF,
				CharacteristicType.STATED_RELATIONSHIP.getConceptId(),
				Concepts.EXISTENTIAL_RESTRICTION_MODIFIER); 

		String inferredLine = TAB_JOINER.join(inferredRelationshipId, 
				relationshipEffectiveTime, 
				"1", 
				Concepts.MODULE_SCT_CORE, 
				Concepts.ROOT_CONCEPT, 
				Concepts.NAMESPACE_ROOT,
				"0",
				Concepts.PART_OF,
				CharacteristicType.INFERRED_RELATIONSHIP.getConceptId(),
				Concepts.EXISTENTIAL_RESTRICTION_MODIFIER);

		String additionalLine = TAB_JOINER.join(additionalRelationshipId, 
				relationshipEffectiveTime, 
				"1", 
				Concepts.MODULE_SCT_CORE, 
				Concepts.ROOT_CONCEPT, 
				Concepts.NAMESPACE_ROOT,
				"0",
				Concepts.PART_OF,
				CharacteristicType.ADDITIONAL_RELATIONSHIP.getConceptId(),
				Concepts.EXISTENTIAL_RESTRICTION_MODIFIER); 

		String conceptLine = TAB_JOINER.join(conceptId, 
				conceptEffectiveTime, 
				"1", 
				Concepts.MODULE_SCT_CORE, 
				DefinitionStatus.PRIMITIVE.getConceptId());

		String descriptionLine = TAB_JOINER.join(descriptionId, 
				"", 
				"1", 
				Concepts.MODULE_SCT_CORE, 
				conceptId, 
				"en",
				Concepts.SYNONYM, 
				"Description term", 
				CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE.getConceptId());

		Multimap<String, Pair<Boolean, String>> fileToLinesMap = ArrayListMultimap.<String, Pair<Boolean, String>>create();

		fileToLinesMap.put("sct2_StatedRelationship", Pair.of(true, statedLine));
		fileToLinesMap.put("sct2_StatedRelationship", Pair.of(false, inferredLine));
		fileToLinesMap.put("sct2_StatedRelationship", Pair.of(false, additionalLine));
		fileToLinesMap.put("sct2_Relationship", Pair.of(true, inferredLine));
		fileToLinesMap.put("sct2_Relationship", Pair.of(true, additionalLine));
		fileToLinesMap.put("sct2_Relationship", Pair.of(false, statedLine));

		fileToLinesMap.put("sct2_Concept", Pair.of(false, conceptLine));
		fileToLinesMap.put("sct2_Description", Pair.of(true, descriptionLine));

		assertArchiveContainsLines(exportArchive, fileToLinesMap);
	}
}
