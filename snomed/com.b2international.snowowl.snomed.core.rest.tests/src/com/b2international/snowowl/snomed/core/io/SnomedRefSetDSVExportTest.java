/*
 * Copyright 2017-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.junit.*;

import com.b2international.commons.FileUtils;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.attachments.AttachmentRegistry;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.request.CommitResult;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.cis.domain.SctId;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.internal.rf2.AbstractSnomedDsvExportItem;
import com.b2international.snowowl.snomed.datastore.internal.rf2.SimpleSnomedDsvExportItem;
import com.b2international.snowowl.snomed.datastore.internal.rf2.SnomedDsvExportItemType;
import com.b2international.snowowl.snomed.datastore.request.*;
import com.b2international.snowowl.test.commons.Services;
import com.b2international.snowowl.test.commons.TestMethodNameRule;
import com.b2international.snowowl.test.commons.rest.RestExtensions;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

/**
 * @since 5.11
 */
public class SnomedRefSetDSVExportTest {

	private static final String REPOSITORY_ID = SnomedTerminologyComponentConstants.TOOLING_ID;
	
	private static final List<ExtendedLocale> LOCALES = ImmutableList.of(ExtendedLocale.valueOf("en-gb"));

	private static final String DELIMITER = "|";
	
	@Rule
	public final TestMethodNameRule methodName = new TestMethodNameRule();
	
	private IEventBus bus;
	
	private AttachmentRegistry fileRegistry;

	private File tempDir;

	private String branchPath;
	
	@Before
	public void setup() {
		bus = Services.bus();
		fileRegistry = ApplicationContext.getInstance().getService(AttachmentRegistry.class);
		tempDir = Files.createTempDir();
		branchPath = createBranch(methodName.get());
	}
	
	@After
	public void destroy() {
		FileUtils.deleteDirectory(tempDir);
	}
	
	@Test
	public void simpleTypeDSVExport() throws Exception {
		String refsetId = createRefset(branchPath, SnomedRefSetType.SIMPLE);
		addMember(branchPath, refsetId, Concepts.SUBSTANCE);
		addMember(branchPath, refsetId, Concepts.FINDING_SITE);
		
		UUID fileId = 
			SnomedRequests.dsv()
				.prepareExport()
				.setLocales(LOCALES)
				.setDelimiter(DELIMITER)
				.setDescriptionIdExpected(true)
				.setRelationshipTargetExpected(true)
				.setRefSetId(refsetId)
				.setExportItems(createExportItems(branchPath, refsetId))
				.build(branchPath)
				.execute(bus)
				.getSync();

		File dsvExportFile = new File(tempDir, String.format("dsv-export-%s.zip", fileId.toString()));
		OutputStream outputStream = new FileOutputStream(dsvExportFile);
		fileRegistry.download(fileId, outputStream);
		Assert.assertTrue("Export file must exist!", dsvExportFile.exists());

		List<String> dsvExportLines = Files.readLines(dsvExportFile, Charsets.UTF_8);
		Assert.assertTrue(MessageFormat.format("Expected 4 lines in the exported file (2 header and 2 member lines) instead of {0} lines.", dsvExportLines.size()), dsvExportLines.size() == 4);
	}

	@Test
	public void mapTypeDSVExport() throws Exception {
		String refsetId = createRefset(branchPath, SnomedRefSetType.SIMPLE_MAP);
		addMember(branchPath, refsetId, Concepts.SUBSTANCE, Collections.singletonMap(SnomedRf2Headers.FIELD_MAP_TARGET, "XXX"));
		addMember(branchPath, refsetId, Concepts.FINDING_SITE, Collections.singletonMap(SnomedRf2Headers.FIELD_MAP_TARGET, "XXX"));
		
		UUID fileId = SnomedRequests.dsv()
				.prepareExport()
				.setLocales(LOCALES)
				.setDelimiter(DELIMITER)
				.setDescriptionIdExpected(true)
				.setRelationshipTargetExpected(true)
				.setRefSetId(refsetId)
				.setExportItems(createExportItems(branchPath, refsetId))
				.build(branchPath)
				.execute(bus)
				.getSync();

		File dsvExportFile = new File(tempDir, String.format("dsv-export-%s.csv", fileId.toString()));
		OutputStream outputStream = new FileOutputStream(dsvExportFile);
		fileRegistry.download(fileId, outputStream);
		Assert.assertTrue("Export file must exist!", dsvExportFile.exists());

		List<String> dsvExportLines = Files.readLines(dsvExportFile, Charsets.UTF_8);
		Assert.assertTrue(MessageFormat.format("Expected 3 lines in the exported file (2 header and 2 member lines) instead of {0} lines.", dsvExportLines.size()), dsvExportLines.size() == 3);
	}
	
	private String createBranch(String branchName) {
		return RepositoryRequests.branching().prepareCreate().setParent(Branch.MAIN_PATH).setName(branchName).build(REPOSITORY_ID).execute(bus).getSync();
	}

	private List<AbstractSnomedDsvExportItem> createExportItems(String branchPath, String refsetId) {
			SnomedConcepts concepts = SnomedRequests.prepareSearchConcept()
				.all()
				.filterByEcl(String.format("^%s", refsetId))
				.setExpand("relationships()")
				.build(branchPath)
				.execute(bus)
				.getSync();
		
		return transformToExportItems();
	}

	private Set<String> idsOf(SnomedConcepts concepts) {
		return concepts.getItems().stream()
							.map(SnomedConcept::getId)
							.collect(Collectors.toSet());
	}

	private Set<String> ancestorsOf(SnomedConcepts concepts) {
		return concepts.getItems()
					.stream()
					.flatMap(item -> SnomedConcept.GET_PARENTS.apply(item).stream())
					.collect(Collectors.toSet());
	}

	private Set<String> relationshipKeysOf(SnomedConcepts concepts) {
		return concepts.getItems()
				.stream()
				.flatMap(concept -> concept.getRelationships().stream())
				.filter(r -> r.isActive() && !r.hasValue() &&
						(Concepts.STATED_RELATIONSHIP.equals(r.getCharacteristicTypeId())
						|| Concepts.ADDITIONAL_RELATIONSHIP.equals(r.getCharacteristicTypeId())))
				.map(r -> String.format("%s=%s", r.getTypeId(), r.getDestinationId()))
				.collect(Collectors.toSet());
	}

	private void addMember(String branchPath, String refsetId, String referencedComponentId) {
		addMember(branchPath, refsetId, referencedComponentId, Collections.emptyMap());
	}
	
	private void addMember(String branchPath, String refsetId, String referencedComponentId, Map<String, Object> properties) {
		SnomedRequests.prepareNewMember()
				.setModuleId(Concepts.MODULE_SCT_CORE)
				.setActive(true)
				.setRefsetId(refsetId)
				.setReferencedComponentId(referencedComponentId)
				.setProperties(properties)
				.setId(UUID.randomUUID().toString())
				.build(branchPath, RestExtensions.USER, "test")
				.execute(bus)
				.getSync();
	}

	private String createRefset(String branchPath, SnomedRefSetType type) {
		SnomedDescriptionCreateRequestBuilder fsn = toDescriptionRequest(Concepts.FULLY_SPECIFIED_NAME, "term-test");
		SnomedDescriptionCreateRequestBuilder pt = toDescriptionRequest(Concepts.SYNONYM, "test");

		SnomedRelationshipCreateRequestBuilder statedIsA = toRelationshipRequest(Concepts.IS_A, Concepts.STATED_RELATIONSHIP, SnomedRefSetUtil.getParentConceptId(type));
		SnomedRelationshipCreateRequestBuilder inferredIsA = toRelationshipRequest(Concepts.IS_A, Concepts.INFERRED_RELATIONSHIP, SnomedRefSetUtil.getParentConceptId(type));

		SnomedRefSetCreateRequestBuilder refSet = toRefSetRequest(type);
		
		String conceptId = generateId();
		createConcept(branchPath, fsn, pt, statedIsA, inferredIsA, refSet, conceptId);
		
		return conceptId;
	}

	private SnomedRefSetCreateRequestBuilder toRefSetRequest(SnomedRefSetType type) {
		return SnomedRequests.prepareNewRefSet()
				.setType(type)
				.setReferencedComponentType(SnomedConcept.TYPE);
	}

	private CommitResult createConcept(
			final String branchPath,
			SnomedDescriptionCreateRequestBuilder fsn,
			SnomedDescriptionCreateRequestBuilder pt, 
			SnomedRelationshipCreateRequestBuilder statedIsA,
			SnomedRelationshipCreateRequestBuilder inferredIsA, 
			SnomedRefSetCreateRequestBuilder refSet,
			String conceptId) {
		
		SnomedConceptCreateRequestBuilder builder = 
				SnomedRequests.prepareNewConcept()
					.setId(conceptId)
					.setModuleId(Concepts.MODULE_SCT_CORE)
					.addDescription(fsn)
					.addDescription(pt)
					.addRelationship(statedIsA)
					.addRelationship(inferredIsA);
		if (refSet != null)
			builder.setRefSet(refSet);
		
		return builder
					.build(branchPath, "test", "test")
					.execute(bus)
					.getSync();
	}

	private String generateId() {
		return SnomedRequests.identifiers()
					.prepareGenerate()
					.setNamespace(Concepts.B2I_NAMESPACE)
					.setCategory(ComponentCategory.CONCEPT)
					.setQuantity(1)
					.buildAsync()
					.execute(bus)
					.getSync()
					.first()
					.map(SctId::getSctid)
					.orElseThrow(() -> new IllegalStateException("Couldn't generate identifier concept ID"));
	}

	private SnomedRelationshipCreateRequestBuilder toRelationshipRequest(String typeId, String characteristicTypeId, String desctinationId) {
		return SnomedRequests.prepareNewRelationship()
				.setIdFromNamespace(Concepts.B2I_NAMESPACE)
				.setModuleId(Concepts.MODULE_SCT_CORE)
				.setDestinationId(desctinationId)
				.setTypeId(typeId)
				.setCharacteristicTypeId(characteristicTypeId);
	}

	private SnomedDescriptionCreateRequestBuilder toDescriptionRequest(String typeId, String term) {
		return SnomedRequests.prepareNewDescription()
				.setIdFromNamespace(Concepts.B2I_NAMESPACE)
				.setModuleId(Concepts.MODULE_SCT_CORE)
				.setTerm(term)
				.setTypeId(typeId)
				.preferredIn(Concepts.REFSET_LANGUAGE_TYPE_UK);
	}
	
	private List<AbstractSnomedDsvExportItem> transformToExportItems() {
		List<AbstractSnomedDsvExportItem> results = Lists.newArrayList();
		
		results.add(new SimpleSnomedDsvExportItem(SnomedDsvExportItemType.PREFERRED_TERM));
		results.add(new SimpleSnomedDsvExportItem(SnomedDsvExportItemType.MODULE));
		results.add(new SimpleSnomedDsvExportItem(SnomedDsvExportItemType.EFFECTIVE_TIME));
		results.add(new SimpleSnomedDsvExportItem(SnomedDsvExportItemType.STATUS_LABEL));
		results.add(new SimpleSnomedDsvExportItem(SnomedDsvExportItemType.DEFINITION_STATUS));
		
		return results;
	}
}
