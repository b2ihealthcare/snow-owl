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

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.junit.Test;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongSet;
import com.b2international.index.revision.Revision;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.index.revision.RevisionIndexRead;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.PredicateUtils.ConstraintDomain;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.taxonomy.Taxonomies;
import com.b2international.snowowl.snomed.datastore.taxonomy.Taxonomy;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * @since 4.7
 */
public class ConceptChangeProcessorTest extends BaseChangeProcessorTest {

	private Collection<String> availableImages = ImmutableSet.of(Concepts.ROOT_CONCEPT);
	private LongSet allConceptIds = PrimitiveSets.newLongOpenHashSet();
	private Collection<ConstraintDomain> allConstraintDomains = newHashSet();
	
	private ConceptChangeProcessor process() {
		return index().read(RevisionBranch.MAIN_PATH, new RevisionIndexRead<ConceptChangeProcessor>() {
			@Override
			public ConceptChangeProcessor execute(RevisionSearcher searcher) throws IOException {
				final ICDOCommitChangeSet commitChangeSet = createChangeSet();
				final Taxonomy inferredTaxonomy = Taxonomies.inferred(searcher, commitChangeSet, allConceptIds);
				final Taxonomy statedTaxonomy = Taxonomies.stated(searcher, commitChangeSet, allConceptIds);
				final ConceptChangeProcessor processor = new ConceptChangeProcessor(BranchPathUtils.createMainPath(), allConceptIds, allConstraintDomains, availableImages, statedTaxonomy, inferredTaxonomy);
				processor.process(commitChangeSet, searcher);
				return processor;
			}
		});
	}
	
	@Test
	public void indexSingleConcept() throws Exception {
		final Concept concept = getConcept(generateConceptId());
		withCDOID(concept, nextStorageKey());
		concept.setActive(true);
		concept.setDefinitionStatus(getConcept(Concepts.FULLY_DEFINED));
		concept.setModule(module());
		concept.setExhaustive(false);
		registerNew(concept);
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = SnomedConceptDocument.builder()
				.id(concept.getId())
				.iconId(Concepts.ROOT_CONCEPT)
				.active(concept.isActive())
				.released(concept.isReleased())
				.exhaustive(concept.isExhaustive())
				.moduleId(concept.getModule().getId())
				.effectiveTime(EffectiveTimes.getEffectiveTime(concept.getEffectiveTime()))
				.primitive(false)
				.parents(PrimitiveSets.newLongOpenHashSet(SnomedConceptDocument.ROOT_ID))
				.ancestors(PrimitiveSets.newLongOpenHashSet())
				.statedParents(PrimitiveSets.newLongOpenHashSet(SnomedConceptDocument.ROOT_ID))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet())
				.referringRefSets(Collections.<String>emptySet())
				.referringMappingRefSets(Collections.<String>emptySet())
				.referringPredicates(Collections.<String>emptySet())
				.build();
		
		final Revision actual = Iterables.getOnlyElement(processor.getMappings().values());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void indexNewStatedChildConceptOfRoot() throws Exception {
		// index the ROOT concept as existing concept
		final long rootConceptId = Long.parseLong(Concepts.ROOT_CONCEPT);
		allConceptIds.add(rootConceptId);
		
		final Concept concept = getConcept(generateConceptId());
		withCDOID(concept, nextStorageKey());
		concept.setActive(true);
		concept.setDefinitionStatus(getConcept(Concepts.FULLY_DEFINED));
		concept.setModule(module());
		concept.setExhaustive(false);
		registerNew(concept);
		
		final Relationship relationship = createStatedRelationship(concept.getId(), Concepts.IS_A, Concepts.ROOT_CONCEPT);
		concept.getOutboundRelationships().add(relationship);
		registerNew(relationship);
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = SnomedConceptDocument.builder()
				.id(concept.getId())
				.iconId(Concepts.ROOT_CONCEPT)
				.active(concept.isActive())
				.released(concept.isReleased())
				.exhaustive(concept.isExhaustive())
				.moduleId(concept.getModule().getId())
				.effectiveTime(EffectiveTimes.getEffectiveTime(concept.getEffectiveTime()))
				.primitive(false)
				.parents(PrimitiveSets.newLongOpenHashSet(SnomedConceptDocument.ROOT_ID))
				.ancestors(PrimitiveSets.newLongOpenHashSet())
				.statedParents(PrimitiveSets.newLongOpenHashSet(rootConceptId))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(SnomedConceptDocument.ROOT_ID))
				.referringRefSets(Collections.<String>emptySet())
				.referringMappingRefSets(Collections.<String>emptySet())
				.referringPredicates(Collections.<String>emptySet())
				.build();
		
		final Revision actual = Iterables.getOnlyElement(processor.getMappings().values());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getDeletions().size());
	}

}
