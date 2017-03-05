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

import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.MODULE_SCT_CORE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.ROOT_CONCEPT;
import static com.b2international.snowowl.snomed.api.rest.CodeSystemRestRequests.createCodeSystem;
import static com.b2international.snowowl.snomed.api.rest.CodeSystemVersionRestRequests.createVersion;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingRestRequests.createBranch;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedExportRestRequests.createExport;
import static com.b2international.snowowl.snomed.api.rest.SnomedExportRestRequests.getExport;
import static com.b2international.snowowl.snomed.api.rest.SnomedExportRestRequests.getExportFile;
import static com.b2international.snowowl.snomed.api.rest.SnomedExportRestRequests.getExportId;
import static com.b2international.snowowl.snomed.api.rest.SnomedRefSetRestRequests.updateRefSetMemberEffectiveTime;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewConcept;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewDescription;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewRefSet;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewRefSetMember;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewRelationship;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
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
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

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
				.put("codeSystemShortName", "SNOMEDCT-DELTA")
				.put("type", Rf2ReleaseType.DELTA.name())
				.put("branchPath", versionPath.getPath())
				.put("startEffectiveTime", versionEffectiveTime)
				.put("endEffectiveTime", versionEffectiveTime)
				.build();

		String exportId = getExportId(createExport(config));

		getExport(exportId).statusCode(200)
		.body("type", equalTo(Rf2ReleaseType.DELTA.name()))
		.body("branchPath", equalTo(versionPath.getPath()))
		.body("startEffectiveTime", equalTo(versionEffectiveTime))
		.body("endEffectiveTime", equalTo(versionEffectiveTime));

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
				.put("codeSystemShortName", "SNOMEDCT-GAMMA")
				.put("type", Rf2ReleaseType.DELTA.name())
				.put("branchPath", branchPath.getPath())
				.put("startEffectiveTime", relationshipEffectiveTime)
				.put("endEffectiveTime", relationshipEffectiveTime)
				.put("includeUnpublished", true)
				.build();

		String exportId = getExportId(createExport(config));

		getExport(exportId).statusCode(200)
		.body("type", equalTo(Rf2ReleaseType.DELTA.name()))
		.body("branchPath", equalTo(branchPath.getPath()))
		.body("startEffectiveTime", equalTo(relationshipEffectiveTime))
		.body("endEffectiveTime", equalTo(relationshipEffectiveTime))
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
	
	@Test
	public void exportContentFromVersionBranchFixerTask() throws Exception {
		String codeSystemShortName = "SNOMEDCT-FIXERTASK";
		createCodeSystem(branchPath, codeSystemShortName).statusCode(201);
		
		// create a refset, a concept, and reference the concept from the refset
		final String createdRefSetId = createNewRefSet(branchPath, SnomedRefSetType.SIMPLE);
		final String createdConceptId = createNewConcept(branchPath, ROOT_CONCEPT);
		final String memberId = createNewRefSetMember(branchPath, createdConceptId, createdRefSetId);
		
		final String versionEffectiveTime = "20170301";
		createVersion(codeSystemShortName, "v1", versionEffectiveTime).statusCode(201);

		IBranchPath versionPath = BranchPathUtils.createPath(branchPath, "v1");
		IBranchPath taskBranch = BranchPathUtils.createPath(versionPath, "Fix01");
		
		// create fixer branch for version branch
		createBranch(taskBranch).statusCode(201);
		
		// change an existing component
		final String newEffectiveTime = "20170302";
		updateRefSetMemberEffectiveTime(taskBranch, memberId, newEffectiveTime);
		
		getComponent(taskBranch, SnomedComponentType.MEMBER, memberId).statusCode(200)
			.body("effectiveTime", equalTo(newEffectiveTime))
			.body("released", equalTo(true));
		
		// add a new component with the same effective time as the version branch
		final String unpublishedMemberId = createNewRefSetMember(taskBranch, createdConceptId, createdRefSetId);
		updateRefSetMemberEffectiveTime(taskBranch, unpublishedMemberId, versionEffectiveTime);
		getComponent(taskBranch, SnomedComponentType.MEMBER, unpublishedMemberId).statusCode(200)
			.body("effectiveTime", equalTo(versionEffectiveTime))
			.body("released", equalTo(true));
		
		final Map<Object, Object> config = ImmutableMap.builder()
				.put("type", Rf2ReleaseType.SNAPSHOT.name())
				.put("branchPath", taskBranch.getPath())
				.put("startEffectiveTime", versionEffectiveTime)
				.build();
			
		final String exportId = getExportId(createExport(config));
		
		getExport(exportId)
			.and().body("type", equalTo(Rf2ReleaseType.SNAPSHOT.name()))
			.and().body("branchPath", equalTo(taskBranch.getPath()))
			.and().body("startEffectiveTime", equalTo(versionEffectiveTime));
		
		final File exportArchive = getExportFile(exportId);
		
		String refsetMemberLine = getComponentLine(ImmutableList.<String>of(memberId, newEffectiveTime, "1", MODULE_SCT_CORE, createdRefSetId, createdConceptId));
		String invalidRefsetMemberLine = getComponentLine(ImmutableList.<String>of(memberId, versionEffectiveTime, "1", MODULE_SCT_CORE, createdRefSetId, createdConceptId));
		
		String newRefsetMemberLine = getComponentLine(ImmutableList.<String>of(unpublishedMemberId, versionEffectiveTime, "1", MODULE_SCT_CORE, createdRefSetId, createdConceptId));
		
		final Multimap<String, Pair<Boolean, String>> fileToLinesMap = ArrayListMultimap.<String, Pair<Boolean, String>>create();
		
		String refsetFileName = "der2_Refset_PTOfConceptSnapshot";
		
		fileToLinesMap.put(refsetFileName, Pair.of(true, refsetMemberLine));
		fileToLinesMap.put(refsetFileName, Pair.of(true, newRefsetMemberLine));
		fileToLinesMap.put(refsetFileName, Pair.of(false, invalidRefsetMemberLine));
		
		assertArchiveContainsLines(exportArchive, fileToLinesMap);
	}
	
//	@Test
//	public void exportContentFromVersionBranchFixerTaskWithTransientEffectiveTime() throws Exception {
//		
//		assertNewVersionCreated();
//		
//		// create concept ref. component
//		final Map<?, ?> conceptReq = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
//		final String createdConceptId = assertComponentCreated(createMainPath(), SnomedComponentType.CONCEPT, conceptReq);
//		
//		// create refset
//		String refsetName = "TransientExampleRefset";
//		
//		final Map<String, Object> fsnDescription = ImmutableMap.<String, Object>builder()
//				.put("typeId", FULLY_SPECIFIED_NAME)
//				.put("term", refsetName + " (qualifier)")
//				.put("languageCode", "en")
//				.put("acceptability", PREFERRED_ACCEPTABILITY_MAP)
//				.build();
//		
//		final Map<String, Object> ptDescription = ImmutableMap.<String, Object>builder()
//				.put("typeId", SYNONYM)
//				.put("term", refsetName)
//				.put("languageCode", "en")
//				.put("acceptability", PREFERRED_ACCEPTABILITY_MAP)
//				.build();
//
//		final Map<String, Object> conceptBuilder = ImmutableMap.<String, Object>builder()
//				.put("moduleId", MODULE_SCT_CORE)
//				.put("descriptions", ImmutableList.of(fsnDescription, ptDescription))
//				.put("parentId", Concepts.REFSET_SIMPLE_TYPE)
//				.build();
//		
//		final Map<String, Object> refSetReq = ImmutableMap.<String, Object>builder()
//				.putAll(conceptBuilder)
//				.put("commitComment", String.format("New %s type reference set with %s members", SnomedRefSetType.SIMPLE, SnomedTerminologyComponentConstants.CONCEPT))
//				.put("type", SnomedRefSetType.SIMPLE)
//				.put("referencedComponentType", SnomedTerminologyComponentConstants.CONCEPT)
//				.build();
//		
//		final String createdRefSetId = assertComponentCreated(createMainPath(), SnomedComponentType.REFSET, refSetReq);
//		assertComponentExists(createMainPath(), SnomedComponentType.REFSET, createdRefSetId);
//		
//		// create member
//		final Map<String, Object> memberReq = createRefSetMemberRequestBody(createdConceptId, createdRefSetId);
//		String memberId = assertComponentCreated(createMainPath(), SnomedComponentType.MEMBER, memberReq);
//		
//		final Date dateForNewVersion = SnomedVersioningApiAssert.getLatestAvailableVersionDate("SNOMEDCT");
//		
//		String versionName = Dates.formatByGmt(dateForNewVersion);
//		String versionEffectiveDate = Dates.formatByGmt(dateForNewVersion, DateFormats.SHORT);
//		
//		whenCreatingVersion(versionName, versionEffectiveDate)
//			.then().assertThat().statusCode(201);
//		
//		givenAuthenticatedRequest(ADMIN_API)
//			.when().get("/codesystems/SNOMEDCT/versions/{id}", versionName)
//			.then().assertThat().statusCode(200);
//		
//		IBranchPath versionPath = BranchPathUtils.createPath(BranchPathUtils.createMainPath(), versionName);
//		String testBranchName = "Fix01";
//		IBranchPath taskBranch = BranchPathUtils.createPath(versionPath, testBranchName);
//		
//		// create fixer branch for version branch
//		SnomedBranchingApiAssert.givenBranchWithPath(taskBranch);
//		
//		// change an existing component
//
//		assertComponentHasProperty(taskBranch, SnomedComponentType.MEMBER, memberId, "active", true);
//		assertComponentHasProperty(taskBranch, SnomedComponentType.MEMBER, memberId, "effectiveTime", versionEffectiveDate);
//		assertComponentHasProperty(taskBranch, SnomedComponentType.MEMBER, memberId, "released", true);
//		
//		Map<String, Object> memberUpdateRequest = ImmutableMap.<String, Object>builder()
//				.put("active", false)
//				.put("commitComment", "Inactivate member")
//				.build();
//		
//		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
//			.with().contentType(ContentType.JSON)
//			.and().body(memberUpdateRequest)
//			.when().put("/{path}/{componentType}/{id}?force=false", taskBranch.getPath(), SnomedComponentType.MEMBER.toLowerCasePlural(), memberId)
//			.then().log().ifValidationFails()
//			.statusCode(204);
//		
//		assertComponentHasProperty(taskBranch, SnomedComponentType.MEMBER, memberId, "active", false);
//		assertComponentHasProperty(taskBranch, SnomedComponentType.MEMBER, memberId, "effectiveTime", null);
//		assertComponentHasProperty(taskBranch, SnomedComponentType.MEMBER, memberId, "released", true);
//		
//		// add a new component
//		
//		final Map<String, Object> newMemberReq = createRefSetMemberRequestBody(createdConceptId, createdRefSetId);
//		String newMemberId = assertComponentCreated(taskBranch, SnomedComponentType.MEMBER, newMemberReq);
//		
//		assertComponentHasProperty(taskBranch, SnomedComponentType.MEMBER, newMemberId, "effectiveTime", null);
//		assertComponentHasProperty(taskBranch, SnomedComponentType.MEMBER, newMemberId, "released", false);
//		
//		final Map<Object, Object> config = ImmutableMap.builder()
//				.put("type", "SNAPSHOT")
//				.put("branchPath", taskBranch.getPath())
//				.put("startEffectiveTime", versionEffectiveDate)
//				.put("transientEffectiveTime", versionEffectiveDate)
//				.put("includeUnpublished", true)
//				.build();
//			
//		final String exportId = assertExportConfigurationCanBeCreated(config);
//		
//		assertExportConfiguration(exportId)
//			.and().body("type", equalTo("SNAPSHOT"))
//			.and().body("branchPath", equalTo(taskBranch.getPath()))
//			.and().body("startEffectiveTime", equalTo(versionEffectiveDate))
//			.and().body("transientEffectiveTime", equalTo(versionEffectiveDate))
//			.and().body("includeUnpublished", equalTo(true));
//		
//		final File exportArchive = assertExportFileCreated(exportId);
//		
//		String refsetMemberLine = getComponentLine(ImmutableList.<String>of(memberId, versionEffectiveDate, "0", MODULE_SCT_CORE, createdRefSetId, createdConceptId));
//		String invalidRefsetMemberLine = getComponentLine(ImmutableList.<String>of(memberId, versionEffectiveDate, "1", MODULE_SCT_CORE, createdRefSetId, createdConceptId));
//		
//		String newRefsetMemberLine = getComponentLine(ImmutableList.<String>of(newMemberId, versionEffectiveDate, "1", MODULE_SCT_CORE, createdRefSetId, createdConceptId));
//		
//		final Multimap<String, Pair<Boolean, String>> fileToLinesMap = ArrayListMultimap.<String, Pair<Boolean, String>>create();
//		
//		String refsetFileName = "der2_Refset_TransientExampleRefset";
//		
//		fileToLinesMap.put(refsetFileName, Pair.of(true, refsetMemberLine));
//		fileToLinesMap.put(refsetFileName, Pair.of(true, newRefsetMemberLine));
//		fileToLinesMap.put(refsetFileName, Pair.of(false, invalidRefsetMemberLine));
//		
//		assertArchiveContainsLines(exportArchive, fileToLinesMap);
//	}
//	
//	@Test
//	public void exportPublishedAndUnpublishedTextDefinitionsIntoSeparateFile() throws Exception {
//		
//		// create new version
//		assertNewVersionCreated();
//		
//		// create new concept
//		final Map<?, ?> conceptRequestBody = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
//		String conceptId = assertComponentCreated(createMainPath(), SnomedComponentType.CONCEPT, conceptRequestBody);
//
//		// create new text definition
//		String textDefinitionTerm = "Text Definition with effective time";
//		Map<?, ?> textDefinitionRequestBody = ImmutableMap.builder()
//			.put("conceptId", conceptId)
//			.put("moduleId", MODULE_SCT_CORE)
//			.put("typeId", Concepts.TEXT_DEFINITION)
//			.put("term", textDefinitionTerm)
//			.put("languageCode", "en")
//			.put("acceptability", ACCEPTABLE_ACCEPTABILITY_MAP)
//			.put("commitComment", "new text definition")
//			.build();
//		
//		String textDefinitionId = assertComponentCreated(createMainPath(), SnomedComponentType.DESCRIPTION, textDefinitionRequestBody);
//
//		// version new concept
//		Date conceptVersionDate = assertNewVersionCreated();
//		String conceptEffectiveDate = Dates.formatByGmt(conceptVersionDate, DateFormats.SHORT);
//
//		// create new text definition
//		String unpublishedTextDefinitionTerm = "Text Definition without effective time";
//		Map<?, ?> unpublishedTextDefinitionRequestBody = ImmutableMap.builder()
//			.put("conceptId", conceptId)
//			.put("moduleId", MODULE_SCT_CORE)
//			.put("typeId", Concepts.TEXT_DEFINITION)
//			.put("term", unpublishedTextDefinitionTerm)
//			.put("languageCode", "en")
//			.put("acceptability", ACCEPTABLE_ACCEPTABILITY_MAP)
//			.put("commitComment", "new text definition")
//			.build();
//		
//		String unpublishedTextDefinitionId = assertComponentCreated(createMainPath(), SnomedComponentType.DESCRIPTION, unpublishedTextDefinitionRequestBody);
//		
//		// do not create new version
//		
//		final Map<Object, Object> config = ImmutableMap.builder()
//				.put("type", "DELTA")
//				.put("branchPath", Branch.MAIN_PATH)
//				.put("startEffectiveTime", conceptEffectiveDate)
//				.put("endEffectiveTime", conceptEffectiveDate)
//				.put("includeUnpublished", true)
//				.build();
//			
//		final String exportId = assertExportConfigurationCanBeCreated(config);
//		
//		assertExportConfiguration(exportId)
//			.and().body("type", equalTo("DELTA"))
//			.and().body("branchPath", equalTo(Branch.MAIN_PATH))
//			.and().body("startEffectiveTime", equalTo(conceptEffectiveDate))
//			.and().body("endEffectiveTime", equalTo(conceptEffectiveDate))
//			.and().body("includeUnpublished", equalTo(true));
//		
//		final File exportArchive = assertExportFileCreated(exportId);
//		
//		String textDefinitionLine = getComponentLine(ImmutableList.<String> of(textDefinitionId, conceptEffectiveDate, "1", MODULE_SCT_CORE, conceptId, "en",
//				Concepts.TEXT_DEFINITION, textDefinitionTerm, CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE.getConceptId()));
//		
//		String unpublishedTextDefinitionLine = getComponentLine(ImmutableList.<String> of(unpublishedTextDefinitionId, "", "1", MODULE_SCT_CORE, conceptId, "en",
//				Concepts.TEXT_DEFINITION, unpublishedTextDefinitionTerm, CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE.getConceptId()));
//
//		final Multimap<String, Pair<Boolean, String>> fileToLinesMap = ArrayListMultimap.<String, Pair<Boolean, String>>create();
//				
//		fileToLinesMap.put("sct2_Description", Pair.of(false, textDefinitionLine));
//		fileToLinesMap.put("sct2_Description", Pair.of(false, unpublishedTextDefinitionLine));
//		
//		fileToLinesMap.put("sct2_TextDefinition", Pair.of(true, textDefinitionLine));
//		fileToLinesMap.put("sct2_TextDefinition", Pair.of(true, unpublishedTextDefinitionLine));
//		
//		assertArchiveContainsLines(exportArchive, fileToLinesMap);
//	}
//	
//	@Test
//	public void exportShouldAlwaysCreateTextDefinitionDescriptionAndLanguageRefsetFiles() throws Exception {
//
//		final Map<?, ?> conceptRequestBody = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
//		String conceptId = assertComponentCreated(createMainPath(), SnomedComponentType.CONCEPT, conceptRequestBody);
//		
//		final Date dateForNewVersion = SnomedVersioningApiAssert.getLatestAvailableVersionDate("SNOMEDCT");
//		
//		String versionName = Dates.formatByGmt(dateForNewVersion);
//		String versionEffectiveDate = Dates.formatByGmt(dateForNewVersion, DateFormats.SHORT);
//		
//		whenCreatingVersion(versionName, versionEffectiveDate)
//			.then().assertThat().statusCode(201);
//		
//		givenAuthenticatedRequest(ADMIN_API)
//			.when().get("/codesystems/SNOMEDCT/versions/{id}", versionName)
//			.then().assertThat().statusCode(200);
//		
//		assertComponentHasProperty(createMainPath(), SnomedComponentType.CONCEPT, conceptId, "definitionStatus", DefinitionStatus.PRIMITIVE.name());
//		
//		final Map<?, ?> updateRequest = ImmutableMap.builder()
//				.put("definitionStatus", DefinitionStatus.FULLY_DEFINED)
//				.put("commitComment", "Changed definition status of concept")
//				.build();
//		
//		assertComponentCanBeUpdated(createMainPath(), SnomedComponentType.CONCEPT, conceptId, updateRequest);
//		assertComponentHasProperty(createMainPath(), SnomedComponentType.CONCEPT, conceptId, "definitionStatus", DefinitionStatus.FULLY_DEFINED.name());
//		
//		// create new version
//		
//		final Date newVersion = SnomedVersioningApiAssert.getLatestAvailableVersionDate("SNOMEDCT");
//		
//		String newVersionName = Dates.formatByGmt(newVersion);
//		String newVersionEffectiveDate = Dates.formatByGmt(newVersion, DateFormats.SHORT);
//		
//		whenCreatingVersion(newVersionName, newVersionEffectiveDate)
//			.then().assertThat().statusCode(201);
//		
//		givenAuthenticatedRequest(ADMIN_API)
//			.when().get("/codesystems/SNOMEDCT/versions/{id}", newVersionName)
//			.then().assertThat().statusCode(200);
//		
//		final Map<Object, Object> config = ImmutableMap.builder()
//				.put("type", "DELTA")
//				.put("branchPath", Branch.MAIN_PATH)
//				.put("startEffectiveTime", newVersionEffectiveDate)
//				.put("endEffectiveTime", newVersionEffectiveDate)
//				.build();
//			
//		final String exportId = assertExportConfigurationCanBeCreated(config);
//		
//		assertExportConfiguration(exportId)
//			.and().body("type", equalTo("DELTA"))
//			.and().body("branchPath", equalTo(Branch.MAIN_PATH))
//			.and().body("startEffectiveTime", equalTo(newVersionEffectiveDate))
//			.and().body("endEffectiveTime", equalTo(newVersionEffectiveDate));
//		
//		final File exportArchive = assertExportFileCreated(exportId);
//		
//		final Multimap<String, Pair<Boolean, String>> fileToLinesMap = ArrayListMultimap.<String, Pair<Boolean, String>>create();
//				
//		fileToLinesMap.put("sct2_Description", Pair.of(false, ""));
//		fileToLinesMap.put("sct2_TextDefinition", Pair.of(false, ""));
//		fileToLinesMap.put("der2_cRefset_Language", Pair.of(false, ""));
//		
//		assertArchiveContainsLines(exportArchive, fileToLinesMap);
//	}
//	
//	@Test
//	public void exportDescriptionsTextDefinitionsAndLanguageRefsetMembersPerLanguageCode() throws Exception {
//		
//		// create new version
//		assertNewVersionCreated();
//		
//		// create new concept
//		final Map<?, ?> conceptRequestBody = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
//		String conceptId = assertComponentCreated(createMainPath(), SnomedComponentType.CONCEPT, conceptRequestBody);
//
//		final String englishTextDefinitionTerm = "Text Definition with effective time";
//		String englishTextDefinitionId = createAcceptableDescription(conceptId, englishTextDefinitionTerm, "en", Concepts.TEXT_DEFINITION);
//		
//		final String danishTextDefinitionTerm = "Danish Text Definition with effective time";
//		String danishTextDefinitionId = createAcceptableDescription(conceptId, danishTextDefinitionTerm, "da", Concepts.TEXT_DEFINITION);
//		
//		final String englishDescriptionTerm = "Description with effective time";
//		String englishDescriptionId = createAcceptableDescription(conceptId, englishDescriptionTerm, "en", Concepts.SYNONYM);
//		
//		final String danishDescriptionTerm = "Danish Description with effective time";
//		String danishDescriptionId = createAcceptableDescription(conceptId, danishDescriptionTerm, "da", Concepts.SYNONYM);
//
//		// version new concept
//		Date conceptVersionDate = assertNewVersionCreated();
//		String conceptEffectiveDate = Dates.formatByGmt(conceptVersionDate, DateFormats.SHORT);
//
//		final String unpublishedEnglishTextDefinitionTerm = "Unpublished Text Definition";
//		String unpublishedEnglishTextDefinitionId = createAcceptableDescription(conceptId, unpublishedEnglishTextDefinitionTerm, "en", Concepts.TEXT_DEFINITION);
//		
//		final String unpublishedDanishTextDefinitionTerm = "Unpublished Danish Text Definition";
//		String unpublishedDanishTextDefinitionId = createAcceptableDescription(conceptId, unpublishedDanishTextDefinitionTerm, "da", Concepts.TEXT_DEFINITION);
//		
//		final String unpublishedEnglishDescriptionTerm = "Unpublished Description";
//		String unpublishedEnglishDescriptionId = createAcceptableDescription(conceptId, unpublishedEnglishDescriptionTerm, "en", Concepts.SYNONYM);
//		
//		final String unpublishedDanishDescriptionTerm = "Unpublished Danish Description";
//		String unpublishedDanishDescriptionId = createAcceptableDescription(conceptId, unpublishedDanishDescriptionTerm, "da", Concepts.SYNONYM);
//		
//		// do not create new version
//		
//		final Map<Object, Object> config = ImmutableMap.builder()
//				.put("type", "DELTA")
//				.put("branchPath", Branch.MAIN_PATH)
//				.put("startEffectiveTime", conceptEffectiveDate)
//				.put("endEffectiveTime", conceptEffectiveDate)
//				.put("includeUnpublished", true)
//				.build();
//			
//		final String exportId = assertExportConfigurationCanBeCreated(config);
//		
//		assertExportConfiguration(exportId)
//			.and().body("type", equalTo("DELTA"))
//			.and().body("branchPath", equalTo(Branch.MAIN_PATH))
//			.and().body("startEffectiveTime", equalTo(conceptEffectiveDate))
//			.and().body("endEffectiveTime", equalTo(conceptEffectiveDate))
//			.and().body("includeUnpublished", equalTo(true));
//		
//		final File exportArchive = assertExportFileCreated(exportId);
//		
//		String englishTextDefinitionLine = createDescriptionLine(englishTextDefinitionId, conceptEffectiveDate, conceptId, "en", Concepts.TEXT_DEFINITION, englishTextDefinitionTerm);
//		String danishTextDefinitionLine = createDescriptionLine(danishTextDefinitionId, conceptEffectiveDate, conceptId, "da", Concepts.TEXT_DEFINITION, danishTextDefinitionTerm);
//		String englishDescriptionLine = createDescriptionLine(englishDescriptionId, conceptEffectiveDate, conceptId, "en", Concepts.SYNONYM, englishDescriptionTerm);
//		String danishDescriptionLine = createDescriptionLine(danishDescriptionId, conceptEffectiveDate, conceptId, "da", Concepts.SYNONYM, danishDescriptionTerm);
//		
//		String unpublishedEnglishTextDefinitionLine = createDescriptionLine(unpublishedEnglishTextDefinitionId, "", conceptId, "en", Concepts.TEXT_DEFINITION, unpublishedEnglishTextDefinitionTerm);
//		String unpublishedDanishTextDefinitionLine = createDescriptionLine(unpublishedDanishTextDefinitionId, "", conceptId, "da", Concepts.TEXT_DEFINITION, unpublishedDanishTextDefinitionTerm);
//		String unpublishedEnglishDescriptionLine = createDescriptionLine(unpublishedEnglishDescriptionId, "", conceptId, "en", Concepts.SYNONYM, unpublishedEnglishDescriptionTerm);
//		String unpublishedDanishDescriptionLine = createDescriptionLine(unpublishedDanishDescriptionId, "", conceptId, "da", Concepts.SYNONYM, unpublishedDanishDescriptionTerm);
//
//		String englishTextDefinitionMemberLine = createAcceptableLanguageRefsetMemberLine(englishTextDefinitionId, conceptEffectiveDate);
//		String danishTextDefinitionMemberLine = createAcceptableLanguageRefsetMemberLine(danishTextDefinitionId, conceptEffectiveDate);
//		String englishDescriptionMemberLine = createAcceptableLanguageRefsetMemberLine(englishDescriptionId, conceptEffectiveDate);
//		String danishDescriptionMemberLine = createAcceptableLanguageRefsetMemberLine(danishDescriptionId, conceptEffectiveDate);
//		
//		String unpublishedEnglishTextDefinitionMemberLine = createAcceptableLanguageRefsetMemberLine(unpublishedEnglishTextDefinitionId, "");
//		String unpublishedDanishTextDefinitionMemberLine = createAcceptableLanguageRefsetMemberLine(unpublishedDanishTextDefinitionId, "");
//		String unpublishedEnglishDescriptionMemberLine = createAcceptableLanguageRefsetMemberLine(unpublishedEnglishDescriptionId, "");
//		String unpublishedDanishDescriptionMemberLine = createAcceptableLanguageRefsetMemberLine(unpublishedDanishDescriptionId, "");
//		
//		final Multimap<String, Pair<Boolean, String>> fileToLinesMap = ArrayListMultimap.<String, Pair<Boolean, String>>create();
//				
//		fileToLinesMap.put("sct2_Description_Delta-en", Pair.of(false, englishTextDefinitionLine));
//		fileToLinesMap.put("sct2_Description_Delta-en", Pair.of(false, danishTextDefinitionLine));
//		fileToLinesMap.put("sct2_Description_Delta-en", Pair.of(true, englishDescriptionLine));
//		fileToLinesMap.put("sct2_Description_Delta-en", Pair.of(false, danishDescriptionLine));
//		
//		fileToLinesMap.put("sct2_Description_Delta-da", Pair.of(false, englishTextDefinitionLine));
//		fileToLinesMap.put("sct2_Description_Delta-da", Pair.of(false, danishTextDefinitionLine));
//		fileToLinesMap.put("sct2_Description_Delta-da", Pair.of(false, englishDescriptionLine));
//		fileToLinesMap.put("sct2_Description_Delta-da", Pair.of(true, danishDescriptionLine));
//		
//		fileToLinesMap.put("sct2_TextDefinition_Delta-en", Pair.of(true, englishTextDefinitionLine));
//		fileToLinesMap.put("sct2_TextDefinition_Delta-en", Pair.of(false, danishTextDefinitionLine));
//		fileToLinesMap.put("sct2_TextDefinition_Delta-en", Pair.of(false, englishDescriptionLine));
//		fileToLinesMap.put("sct2_TextDefinition_Delta-en", Pair.of(false, danishDescriptionLine));
//
//		fileToLinesMap.put("sct2_TextDefinition_Delta-da", Pair.of(false, englishTextDefinitionLine));
//		fileToLinesMap.put("sct2_TextDefinition_Delta-da", Pair.of(true, danishTextDefinitionLine));
//		fileToLinesMap.put("sct2_TextDefinition_Delta-da", Pair.of(false, englishDescriptionLine));
//		fileToLinesMap.put("sct2_TextDefinition_Delta-da", Pair.of(false, danishDescriptionLine));
//		
//		fileToLinesMap.put("sct2_Description_Delta-en", Pair.of(false, unpublishedEnglishTextDefinitionLine));
//		fileToLinesMap.put("sct2_Description_Delta-en", Pair.of(false, unpublishedDanishTextDefinitionLine));
//		fileToLinesMap.put("sct2_Description_Delta-en", Pair.of(true, unpublishedEnglishDescriptionLine));
//		fileToLinesMap.put("sct2_Description_Delta-en", Pair.of(false, unpublishedDanishDescriptionLine));
//		
//		fileToLinesMap.put("sct2_Description_Delta-da", Pair.of(false, unpublishedEnglishTextDefinitionLine));
//		fileToLinesMap.put("sct2_Description_Delta-da", Pair.of(false, unpublishedDanishTextDefinitionLine));
//		fileToLinesMap.put("sct2_Description_Delta-da", Pair.of(false, unpublishedEnglishDescriptionLine));
//		fileToLinesMap.put("sct2_Description_Delta-da", Pair.of(true, unpublishedDanishDescriptionLine));
//		
//		fileToLinesMap.put("sct2_TextDefinition_Delta-en", Pair.of(true, unpublishedEnglishTextDefinitionLine));
//		fileToLinesMap.put("sct2_TextDefinition_Delta-en", Pair.of(false, unpublishedDanishTextDefinitionLine));
//		fileToLinesMap.put("sct2_TextDefinition_Delta-en", Pair.of(false, unpublishedEnglishDescriptionLine));
//		fileToLinesMap.put("sct2_TextDefinition_Delta-en", Pair.of(false, unpublishedDanishDescriptionLine));
//
//		fileToLinesMap.put("sct2_TextDefinition_Delta-da", Pair.of(false, unpublishedEnglishTextDefinitionLine));
//		fileToLinesMap.put("sct2_TextDefinition_Delta-da", Pair.of(true, unpublishedDanishTextDefinitionLine));
//		fileToLinesMap.put("sct2_TextDefinition_Delta-da", Pair.of(false, unpublishedEnglishDescriptionLine));
//		fileToLinesMap.put("sct2_TextDefinition_Delta-da", Pair.of(false, unpublishedDanishDescriptionLine));
//		
//		fileToLinesMap.put("der2_cRefset_LanguageDelta-en", Pair.of(true, englishTextDefinitionMemberLine));
//		fileToLinesMap.put("der2_cRefset_LanguageDelta-en", Pair.of(false, danishTextDefinitionMemberLine));
//		fileToLinesMap.put("der2_cRefset_LanguageDelta-en", Pair.of(true, englishDescriptionMemberLine));
//		fileToLinesMap.put("der2_cRefset_LanguageDelta-en", Pair.of(false, danishDescriptionMemberLine));
//		
//		fileToLinesMap.put("der2_cRefset_LanguageDelta-da", Pair.of(false, englishTextDefinitionMemberLine));
//		fileToLinesMap.put("der2_cRefset_LanguageDelta-da", Pair.of(true, danishTextDefinitionMemberLine));
//		fileToLinesMap.put("der2_cRefset_LanguageDelta-da", Pair.of(false, englishDescriptionMemberLine));
//		fileToLinesMap.put("der2_cRefset_LanguageDelta-da", Pair.of(true, danishDescriptionMemberLine));
//		
//		fileToLinesMap.put("der2_cRefset_LanguageDelta-en", Pair.of(true, unpublishedEnglishTextDefinitionMemberLine));
//		fileToLinesMap.put("der2_cRefset_LanguageDelta-en", Pair.of(false, unpublishedDanishTextDefinitionMemberLine));
//		fileToLinesMap.put("der2_cRefset_LanguageDelta-en", Pair.of(true, unpublishedEnglishDescriptionMemberLine));
//		fileToLinesMap.put("der2_cRefset_LanguageDelta-en", Pair.of(false, unpublishedDanishDescriptionMemberLine));
//		
//		fileToLinesMap.put("der2_cRefset_LanguageDelta-da", Pair.of(false, unpublishedEnglishTextDefinitionMemberLine));
//		fileToLinesMap.put("der2_cRefset_LanguageDelta-da", Pair.of(true, unpublishedDanishTextDefinitionMemberLine));
//		fileToLinesMap.put("der2_cRefset_LanguageDelta-da", Pair.of(false, unpublishedEnglishDescriptionMemberLine));
//		fileToLinesMap.put("der2_cRefset_LanguageDelta-da", Pair.of(true, unpublishedDanishDescriptionMemberLine));
//		
//		assertArchiveContainsLines(exportArchive, fileToLinesMap);
//	}
//	
//	private String getLanguageRefsetMemberId(String descriptionId) {
//		final Collection<Map<String, Object>> members = assertDescriptionExists(createMainPath(), descriptionId, "members()").extract().body().path("members.items");
//		return String.valueOf(Iterables.getOnlyElement(members).get("id"));
//	}
//	
//	private String createDescriptionLine(String id, String effectiveTime, String conceptId, String languageCode, String type, String term) {
//		return getComponentLine(ImmutableList.<String> of(id, effectiveTime, "1", MODULE_SCT_CORE, conceptId, languageCode,
//				type, term, CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE.getConceptId()));
//	}
//	
//	private String createAcceptableLanguageRefsetMemberLine(String descriptionId, String effectiveTime) {
//		return getComponentLine(ImmutableList.<String> of(getLanguageRefsetMemberId(descriptionId), effectiveTime, "1", MODULE_SCT_CORE, Concepts.REFSET_LANGUAGE_TYPE_UK, descriptionId,
//				Acceptability.ACCEPTABLE.getConceptId()));
//	}
//	
//	private String createAcceptableDescription(String conceptId, String term, String languageCode, String typeId) {
//		
//		final Map<?, ?> descriptionRequestBody = ImmutableMap.builder()
//			.put("conceptId", conceptId)
//			.put("moduleId", MODULE_SCT_CORE)
//			.put("typeId", typeId)
//			.put("term", term)
//			.put("languageCode", languageCode)
//			.put("acceptability", ACCEPTABLE_ACCEPTABILITY_MAP)
//			.put("commitComment", "new description")
//			.build();
//		
//		return assertComponentCreated(createMainPath(), SnomedComponentType.DESCRIPTION, descriptionRequestBody);
//	}
	
//	private String createAdditionalRelationshipOnMain() {
//		final Map<?, ?> additionalRequestBody = givenRelationshipRequestBody(BLEEDING, TEMPORAL_CONTEXT, FINDING_CONTEXT, MODULE_SCT_CORE,
//				CharacteristicType.ADDITIONAL_RELATIONSHIP, "New relationship on MAIN");
//		final String additionalRelationshipId = assertComponentCreated(createMainPath(), SnomedComponentType.RELATIONSHIP, additionalRequestBody);
//		assertComponentHasProperty(createMainPath(), SnomedComponentType.RELATIONSHIP, additionalRelationshipId, "characteristicType",
//				CharacteristicType.ADDITIONAL_RELATIONSHIP.name());
//		return additionalRelationshipId;
//	}
//
//	private String createInferredRelationshipOnMain() {
//		final Map<?, ?> inferredRequestBody = givenRelationshipRequestBody(DISEASE, TEMPORAL_CONTEXT, FINDING_CONTEXT, MODULE_SCT_CORE,
//				CharacteristicType.INFERRED_RELATIONSHIP, "New relationship on MAIN");
//		final String inferredRelationshipId = assertComponentCreated(createMainPath(), SnomedComponentType.RELATIONSHIP, inferredRequestBody);
//		assertComponentHasProperty(createMainPath(), SnomedComponentType.RELATIONSHIP, inferredRelationshipId, "characteristicType",
//				CharacteristicType.INFERRED_RELATIONSHIP.name());
//		return inferredRelationshipId;
//	}
//
//	private String createStatedRelationshipOnMain() {
//		final Map<?, ?> statedRequestBody = givenRelationshipRequestBody(DISEASE, TEMPORAL_CONTEXT, FINDING_CONTEXT, MODULE_SCT_CORE,
//				"New relationship on MAIN");
//		final String statedRelationshipId = assertComponentCreated(createMainPath(), SnomedComponentType.RELATIONSHIP, statedRequestBody);
//		assertComponentHasProperty(createMainPath(), SnomedComponentType.RELATIONSHIP, statedRelationshipId, "characteristicType",
//				CharacteristicType.STATED_RELATIONSHIP.name());
//		return statedRelationshipId;
//	}

	private String getComponentLine(final List<String> lineElements) {
		return Joiner.on("\t").join(lineElements);
	}
	
//	private Date assertNewVersionCreated() {
//		final Map<?, ?> conceptRequestBody = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
//		assertComponentCreated(createMainPath(), SnomedComponentType.CONCEPT, conceptRequestBody);
//		
//		final Date dateForNewVersion = SnomedVersioningApiAssert.getLatestAvailableVersionDate("SNOMEDCT");
//		
//		final String versionName = Dates.formatByGmt(dateForNewVersion);
//		final String versionEffectiveDate = Dates.formatByGmt(dateForNewVersion, DateFormats.SHORT);
//		
//		whenCreatingVersion(versionName, versionEffectiveDate)
//			.then().assertThat().statusCode(201);
//		
//		givenAuthenticatedRequest(ADMIN_API)
//			.when().get("/codesystems/SNOMEDCT/versions/{id}", versionName)
//			.then().assertThat().statusCode(200);
//		
//		return dateForNewVersion;
//	}
}
