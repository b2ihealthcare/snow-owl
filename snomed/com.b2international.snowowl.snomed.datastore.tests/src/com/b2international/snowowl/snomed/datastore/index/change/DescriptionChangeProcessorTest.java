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
package com.b2international.snowowl.snomed.datastore.index.change;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.junit.Before;
import org.junit.Test;

import com.b2international.commons.VerhoeffCheck;
import com.b2international.index.revision.BaseRevisionIndexTest;
import com.b2international.index.revision.Revision;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.index.revision.RevisionIndexRead;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.CDOCommitChangeSet;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.index.ChangeSetProcessor;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedFactory;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.datastore.id.gen.RandomItemIdGenerationStrategy;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * @since 4.7
 */
public class DescriptionChangeProcessorTest extends BaseRevisionIndexTest {

	// fixtures
	private final Map<String, Concept> conceptsById = newHashMap();
	private final Map<String, SnomedRefSet> refSetsById = newHashMap();
	private final AtomicLong storageKeys = new AtomicLong(1L);
	
	private CDOView view = mock(CDOView.class);
	private Collection<CDOObject> newComponents = newHashSet();
	private Collection<CDOObject> dirtyComponents = newHashSet();
	private Map<CDOID, EClass> detachedComponents = newHashMap();
	private Map<CDOID, CDORevisionDelta> revisionDeltas = Collections.emptyMap();
	
	// test subject
	private DescriptionChangeProcessor processor;
	
	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableList.<Class<?>>of(SnomedDescriptionIndexEntry.class, SnomedRefSetMemberIndexEntry.class);
	}
	
	@Before
	public void givenProcessor() {
		processor = new DescriptionChangeProcessor();
	}
	
	@Test
	public void addNewDescriptionWithoutLanguageMembers() throws Exception {
		final Description description = createDescription(Concepts.FULLY_SPECIFIED_NAME, "Example FSN");
		newComponents.add(description);
		
		process(processor);
		
		final SnomedDescriptionIndexEntry expectedDoc = SnomedDescriptionIndexEntry.builder(description).build();
		final Revision currentDoc = Iterables.getOnlyElement(processor.getMappings().values());
		assertDocEquals(expectedDoc, currentDoc);
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void addNewDescriptionWithAcceptableLanguageMember() throws Exception {
		final Description description = createDescription(Concepts.FULLY_SPECIFIED_NAME, "Example FSN");
		final SnomedLanguageRefSetMember acceptableMember = createLangMember(description.getId(), Acceptability.ACCEPTABLE, Concepts.REFSET_LANGUAGE_TYPE_UK);
		description.getLanguageRefSetMembers().add(acceptableMember);
		newComponents.add(description);
		newComponents.add(acceptableMember);
		
		process(processor);
		
		final SnomedDescriptionIndexEntry expectedDoc = SnomedDescriptionIndexEntry.builder(description).acceptability(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.ACCEPTABLE).build();
		final Revision currentDoc = Iterables.getOnlyElement(processor.getMappings().values());
		assertDocEquals(expectedDoc, currentDoc);
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void addNewDescriptionWithPreferredLanguageMember() throws Exception {
		final Description description = createDescription(Concepts.FULLY_SPECIFIED_NAME, "Example FSN");
		final SnomedLanguageRefSetMember acceptableMember = createLangMember(description.getId(), Acceptability.PREFERRED, Concepts.REFSET_LANGUAGE_TYPE_UK);
		description.getLanguageRefSetMembers().add(acceptableMember);
		newComponents.add(description);
		newComponents.add(acceptableMember);
		
		process(processor);
		
		final SnomedDescriptionIndexEntry expectedDoc = SnomedDescriptionIndexEntry.builder(description).acceptability(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED).build();
		final Revision currentDoc = Iterables.getOnlyElement(processor.getMappings().values());
		assertDocEquals(expectedDoc, currentDoc);
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void deleteAcceptableLanguageMember() throws Exception {
		// create description as dirty
		final Description description = createDescriptionWithTwoLangMembers();
		final SnomedLanguageRefSetMember acceptableMember = getFirstMember(description, Acceptability.ACCEPTABLE);
		final SnomedLanguageRefSetMember preferredMember = getFirstMember(description, Acceptability.PREFERRED);
		
		// index current revisions, so change processor can find them (both the description and the members)
		indexRevision(RevisionBranch.MAIN_PATH, CDOIDUtil.getLong(description.cdoID()), SnomedDescriptionIndexEntry.builder(description)
				.acceptability(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED)
				.acceptability(Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.ACCEPTABLE)
				.build());
		indexRevision(RevisionBranch.MAIN_PATH, CDOIDUtil.getLong(acceptableMember.cdoID()), SnomedRefSetMemberIndexEntry.builder(acceptableMember).build());
		indexRevision(RevisionBranch.MAIN_PATH, CDOIDUtil.getLong(preferredMember.cdoID()), SnomedRefSetMemberIndexEntry.builder(preferredMember).build());
		
		// remove the acceptableMember and mark the description as dirty
		description.getLanguageRefSetMembers().remove(acceptableMember);
		dirtyComponents.add(description);
		// delete the acceptable member of the description
		detachedComponents.put(acceptableMember.cdoID(), SnomedRefSetPackage.Literals.SNOMED_LANGUAGE_REF_SET_MEMBER);
		
		process(processor);
		
		// expected that the new doc will have only the preferred acceptability
		final SnomedDescriptionIndexEntry expectedDoc = SnomedDescriptionIndexEntry.builder(description)
				.acceptability(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED)
				.build();
		final Revision currentDoc = Iterables.getOnlyElement(processor.getMappings().values());
		assertDocEquals(expectedDoc, currentDoc);
		// XXX the deleted member handled by another processor
		assertEquals(0, processor.getDeletions().size());
	}
	
	private SnomedLanguageRefSetMember getFirstMember(Description description, Acceptability acceptability) {
		for (SnomedLanguageRefSetMember member : description.getLanguageRefSetMembers()) {
			if (acceptability.getConceptId().equals(member.getAcceptabilityId())) {
				return member;
			}
		}
		return null;
	}

	private Description createDescriptionWithTwoLangMembers() {
		final Description description = createDescription(Concepts.FULLY_SPECIFIED_NAME, "Example FSN");
		final SnomedLanguageRefSetMember acceptableMember = createLangMember(description.getId(), Acceptability.ACCEPTABLE, Concepts.REFSET_LANGUAGE_TYPE_US);
		final SnomedLanguageRefSetMember preferredMember = createLangMember(description.getId(), Acceptability.PREFERRED, Concepts.REFSET_LANGUAGE_TYPE_UK);
		description.getLanguageRefSetMembers().add(acceptableMember);
		description.getLanguageRefSetMembers().add(preferredMember);
		return description;
	}

	@Test
	public void deletePreferredLanguageMember() throws Exception {
		// create description as dirty
		final Description description = createDescriptionWithTwoLangMembers();
		final SnomedLanguageRefSetMember acceptableMember = getFirstMember(description, Acceptability.ACCEPTABLE);
		final SnomedLanguageRefSetMember preferredMember = getFirstMember(description, Acceptability.PREFERRED);
		
		// index current revisions, so change processor can find them (both the description and the members)
		indexRevision(RevisionBranch.MAIN_PATH, CDOIDUtil.getLong(description.cdoID()), SnomedDescriptionIndexEntry.builder(description)
				.acceptability(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED)
				.acceptability(Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.ACCEPTABLE)
				.build());
		indexRevision(RevisionBranch.MAIN_PATH, CDOIDUtil.getLong(acceptableMember.cdoID()), SnomedRefSetMemberIndexEntry.builder(acceptableMember).build());
		indexRevision(RevisionBranch.MAIN_PATH, CDOIDUtil.getLong(preferredMember.cdoID()), SnomedRefSetMemberIndexEntry.builder(preferredMember).build());
		
		// remove the acceptableMember and mark the description as dirty
		description.getLanguageRefSetMembers().remove(preferredMember);
		dirtyComponents.add(description);
		// delete the acceptable member of the description
		detachedComponents.put(preferredMember.cdoID(), SnomedRefSetPackage.Literals.SNOMED_LANGUAGE_REF_SET_MEMBER);
		
		process(processor);
		
		// expected that the new doc will have only the preferred acceptability
		final SnomedDescriptionIndexEntry expectedDoc = SnomedDescriptionIndexEntry.builder(description)
				.acceptability(Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.ACCEPTABLE)
				.build();
		final Revision currentDoc = Iterables.getOnlyElement(processor.getMappings().values());
		assertDocEquals(expectedDoc, currentDoc);
		// XXX the deleted member handled by another processor
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void changeDescriptionCasesignificance() throws Exception {
		final Description description = createDescription(Concepts.FULLY_SPECIFIED_NAME, "Example FSN");
		indexRevision(RevisionBranch.MAIN_PATH, CDOIDUtil.getLong(description.cdoID()), SnomedDescriptionIndexEntry.builder(description).build());
		description.setCaseSignificance(getConcept(Concepts.ENTIRE_TERM_CASE_INSENSITIVE));
		dirtyComponents.add(description);
		
		process(processor);
		
		final SnomedDescriptionIndexEntry expectedDoc = SnomedDescriptionIndexEntry.builder(description).build();
		final Revision currentDoc = Iterables.getOnlyElement(processor.getMappings().values());
		assertDocEquals(expectedDoc, currentDoc);
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void changeLanguageMemberAcceptability() throws Exception {
		final Description description = createDescription(Concepts.FULLY_SPECIFIED_NAME, "Example FSN");
		final SnomedLanguageRefSetMember acceptableMember = createLangMember(description.getId(), Acceptability.ACCEPTABLE, Concepts.REFSET_LANGUAGE_TYPE_UK);
		description.getLanguageRefSetMembers().add(acceptableMember);
		// index revisions for previous state
		indexRevision(RevisionBranch.MAIN_PATH, CDOIDUtil.getLong(description.cdoID()), SnomedDescriptionIndexEntry.builder(description).acceptability(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.ACCEPTABLE).build());
		indexRevision(RevisionBranch.MAIN_PATH, CDOIDUtil.getLong(acceptableMember.cdoID()), SnomedRefSetMemberIndexEntry.builder(acceptableMember).build());
		
		// make the change
		acceptableMember.setAcceptabilityId(Acceptability.PREFERRED.getConceptId());
		dirtyComponents.add(acceptableMember);
		
		process(processor);
		
		// description doc must be reindexed with change acceptabilityMap
		final SnomedDescriptionIndexEntry expectedDoc = SnomedDescriptionIndexEntry.builder(description).
				acceptability(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED).build();
		final Revision currentDoc = Iterables.getOnlyElement(processor.getMappings().values());
		assertDocEquals(expectedDoc, currentDoc);
		assertEquals(0, processor.getDeletions().size());
	}

	@Test
	public void deleteDescription() throws Exception {
		final CDOID storageKey = nextStorageKeyAsCDOID();
		detachedComponents.put(storageKey, SnomedPackage.Literals.DESCRIPTION);
		
		process(processor);
		
		assertThat(processor.getMappings()).isEmpty();
		assertEquals(1, processor.getDeletions().size());
		final Entry<Class<? extends Revision>, Long> deletionEntry = Iterables.getOnlyElement(processor.getDeletions().entries());
		assertEquals(SnomedDescriptionIndexEntry.class, deletionEntry.getKey());
		assertEquals(CDOIDUtil.getLong(storageKey), deletionEntry.getValue().longValue());
	}
	
	private void process(final ChangeSetProcessor processor) {
		final ICDOCommitChangeSet commitChangeSet = new CDOCommitChangeSet(view, "test", "test", newComponents, dirtyComponents, detachedComponents, revisionDeltas, 1L);
		index().read(RevisionBranch.MAIN_PATH, new RevisionIndexRead<Void>() {
			@Override
			public Void execute(RevisionSearcher index) throws IOException {
				processor.process(commitChangeSet, index);
				return null;
			}
		});
	}

	private SnomedLanguageRefSetMember createLangMember(final String descriptionId, Acceptability acceptability, final String refSetId) {
		final SnomedLanguageRefSetMember member = SnomedRefSetFactory.eINSTANCE.createSnomedLanguageRefSetMember();
		withCDOID(member, nextStorageKey());
		member.setAcceptabilityId(acceptability.getConceptId());
		member.setActive(true);
		member.setModuleId(Concepts.MODULE_SCT_CORE);
		member.setReferencedComponentId(descriptionId);
		member.setRefSet(getStructuralRefSet(refSetId));
		member.setUuid(UUID.randomUUID().toString());
		return member;
	}
	
	private Description createDescription(String typeId, String term) {
		return createDescription(generateConceptId(), typeId, term);
	}
	
	private Description createDescription(String conceptId, String typeId, String term) {
		final Description description = SnomedFactory.eINSTANCE.createDescription();
		withCDOID(description, nextStorageKey());
		description.setActive(true);
		description.setCaseSignificance(getConcept(Concepts.ENTIRE_TERM_CASE_SENSITIVE));
		description.setConcept(getConcept(conceptId));
		description.setId(generateDescriptionId());
		description.setLanguageCode("en");
		description.setModule(module());
		description.setReleased(false);
		description.setTerm("Term");
		description.setType(getConcept(typeId));
		return description;
	}

	private long nextStorageKey() {
		return storageKeys.getAndIncrement();
	}
	
	private CDOID nextStorageKeyAsCDOID() {
		return CDOIDUtil.createLong(nextStorageKey());
	}

	private void withCDOID(CDOObject description, long storageKey) {
		if (description instanceof InternalCDOObject) {
			final CDOID id = CDOIDUtil.createLong(storageKey);
			((InternalCDOObject) description).cdoInternalSetID(id);
		}
	}

	private SnomedRefSet getRegularRefSet(String id) {
		if (!refSetsById.containsKey(id)) {
			final SnomedRefSet refSet = SnomedRefSetFactory.eINSTANCE.createSnomedRegularRefSet();
			withCDOID(refSet, nextStorageKey());
			refSet.setIdentifierId(id);
			refSetsById.put(id, refSet);
		}
		return refSetsById.get(id);
	}
	
	private SnomedRefSet getStructuralRefSet(String id) {
		if (!refSetsById.containsKey(id)) {
			final SnomedRefSet refSet = SnomedRefSetFactory.eINSTANCE.createSnomedStructuralRefSet();
			withCDOID(refSet, nextStorageKey());
			refSet.setIdentifierId(id);
			refSetsById.put(id, refSet);
		}
		return refSetsById.get(id);
	}
	
	private Concept getConcept(String id) {
		if (!conceptsById.containsKey(id)) {
			final Concept concept = SnomedFactory.eINSTANCE.createConcept();
			withCDOID(concept, nextStorageKey());
			concept.setId(id);
			conceptsById.put(id, concept);
		}
		return conceptsById.get(id);
	}
	
	private Concept module() {
		return getConcept(Concepts.MODULE_SCT_CORE);
	}

	private static String generateConceptId() {
		return generateSnomedId(ComponentCategory.CONCEPT);
	}
	
	private static String generateDescriptionId() {
		return generateSnomedId(ComponentCategory.DESCRIPTION);
	}

	private static String generateRelationshipId() {
		return generateSnomedId(ComponentCategory.RELATIONSHIP);
	}
	
	private static String generateSnomedId(ComponentCategory category) {
		final String selectedNamespace = "";
		final StringBuilder builder = new StringBuilder();
		// generate the SCT Item ID
		builder.append(new RandomItemIdGenerationStrategy().generateItemId());

		// append namespace and the first part of the partition-identifier
		if (Strings.isNullOrEmpty(selectedNamespace)) {
			builder.append('0');
		} else {
			builder.append(selectedNamespace);
			builder.append('1');
		}

		// append the second part of the partition-identifier
		builder.append(category.ordinal());

		// calc check-digit
		builder.append(VerhoeffCheck.calculateChecksum(builder, false));

		return builder.toString();
	}
	
}
