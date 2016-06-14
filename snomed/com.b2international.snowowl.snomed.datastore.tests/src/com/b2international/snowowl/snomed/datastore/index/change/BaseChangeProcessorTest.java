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
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.spi.cdo.InternalCDOObject;

import com.b2international.commons.VerhoeffCheck;
import com.b2international.index.revision.BaseRevisionIndexTest;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.index.revision.RevisionIndexRead;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.CDOCommitChangeSet;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.index.ChangeSetProcessor;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.SnomedFactory;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.id.gen.RandomItemIdGenerationStrategy;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.snor.PredicateIndexEntry;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

/**
 * @since 4.7
 */
public abstract class BaseChangeProcessorTest extends BaseRevisionIndexTest {

	// fixtures
	private final Map<String, Concept> conceptsById = newHashMap();
	private final Map<String, SnomedRefSet> refSetsById = newHashMap();
	private final AtomicLong storageKeys = new AtomicLong(1L);
	
	private CDOView view = mock(CDOView.class);
	private Collection<CDOObject> newComponents = newHashSet();
	private Collection<CDOObject> dirtyComponents = newHashSet();
	private Map<CDOID, EClass> detachedComponents = newHashMap();
	private Map<CDOID, CDORevisionDelta> revisionDeltas = Collections.emptyMap();
	
	@Override
	protected final Collection<Class<?>> getTypes() {
		return ImmutableList.<Class<?>>of(
				SnomedConceptDocument.class,
				SnomedDescriptionIndexEntry.class,
				SnomedRelationshipIndexEntry.class,
				SnomedRefSetMemberIndexEntry.class,
				PredicateIndexEntry.class);
	}
	
	protected final void registerNew(CDOObject object) {
		newComponents.add(object);
	}
	
	protected final void registerDirty(CDOObject object) {
		dirtyComponents.add(object);
	}
	
	protected final void registerDetached(CDOID storageKey, EClass type) {
		detachedComponents.put(storageKey, type);
	}
	
	protected final void process(final ChangeSetProcessor processor) {
		final ICDOCommitChangeSet commitChangeSet = new CDOCommitChangeSet(view, "test", "test", newComponents, dirtyComponents, detachedComponents, revisionDeltas, 1L);
		index().read(RevisionBranch.MAIN_PATH, new RevisionIndexRead<Void>() {
			@Override
			public Void execute(RevisionSearcher index) throws IOException {
				processor.process(commitChangeSet, index);
				return null;
			}
		});
	}
	
	protected final long nextStorageKey() {
		return storageKeys.getAndIncrement();
	}
	
	protected final CDOID nextStorageKeyAsCDOID() {
		return CDOIDUtil.createLong(nextStorageKey());
	}

	protected final void withCDOID(CDOObject description, long storageKey) {
		if (description instanceof InternalCDOObject) {
			final CDOID id = CDOIDUtil.createLong(storageKey);
			((InternalCDOObject) description).cdoInternalSetID(id);
		}
	}

	protected final SnomedRefSet getRegularRefSet(String id) {
		if (!refSetsById.containsKey(id)) {
			final SnomedRefSet refSet = SnomedRefSetFactory.eINSTANCE.createSnomedRegularRefSet();
			withCDOID(refSet, nextStorageKey());
			refSet.setIdentifierId(id);
			refSetsById.put(id, refSet);
		}
		return refSetsById.get(id);
	}
	
	protected final SnomedRefSet getStructuralRefSet(String id) {
		if (!refSetsById.containsKey(id)) {
			final SnomedRefSet refSet = SnomedRefSetFactory.eINSTANCE.createSnomedStructuralRefSet();
			withCDOID(refSet, nextStorageKey());
			refSet.setIdentifierId(id);
			refSetsById.put(id, refSet);
		}
		return refSetsById.get(id);
	}
	
	protected final Concept getConcept(String id) {
		if (!conceptsById.containsKey(id)) {
			final Concept concept = SnomedFactory.eINSTANCE.createConcept();
			withCDOID(concept, nextStorageKey());
			concept.setId(id);
			conceptsById.put(id, concept);
		}
		return conceptsById.get(id);
	}
	
	protected final Concept module() {
		return getConcept(Concepts.MODULE_SCT_CORE);
	}

	protected final static String generateConceptId() {
		return generateSnomedId(ComponentCategory.CONCEPT);
	}
	
	protected final static String generateDescriptionId() {
		return generateSnomedId(ComponentCategory.DESCRIPTION);
	}

	protected final static String generateRelationshipId() {
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
