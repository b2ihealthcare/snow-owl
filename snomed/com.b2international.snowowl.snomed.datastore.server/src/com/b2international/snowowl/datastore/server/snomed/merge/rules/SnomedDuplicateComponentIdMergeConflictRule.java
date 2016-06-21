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
package com.b2international.snowowl.datastore.server.snomed.merge.rules;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptySet;
import static java.util.Collections.singletonList;

import java.util.Collection;
import java.util.Set;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import com.b2international.snowowl.core.merge.MergeConflict;
import com.b2international.snowowl.core.merge.MergeConflict.ConflictType;
import com.b2international.snowowl.core.merge.MergeConflictImpl;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.server.cdo.AbstractMergeConflictRule;
import com.b2international.snowowl.datastore.utils.ComponentUtils2;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.ISnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

/**
 * @since 4.7
 */
public class SnomedDuplicateComponentIdMergeConflictRule extends AbstractMergeConflictRule {

	private static final Function<Component, String> COMPONENT_TO_ID_FUNCTION = new Function<Component, String>() {
		@Override
		public String apply(Component input) {
			return input.getId();
		}
	};
	
	@Override
	public Collection<MergeConflict> validate(CDOBranch sourceBranch, CDOTransaction transaction) {
		
		Collection<MergeConflict> conceptIdConflicts = checkExistingConceptIds(ComponentUtils2.getNewObjects(transaction, Concept.class), sourceBranch);
		Collection<MergeConflict> descriptionIdConflicts = checkExistingDescriptionIds(ComponentUtils2.getNewObjects(transaction, Description.class), sourceBranch);
		Collection<MergeConflict> relationshipIdConflicts = checkExistingRelationshipIds(ComponentUtils2.getNewObjects(transaction, Relationship.class), sourceBranch);
		
		return newHashSet(Iterables.concat(conceptIdConflicts, descriptionIdConflicts, relationshipIdConflicts));
	}

	private Collection<MergeConflict> checkExistingConceptIds(Iterable<Concept> concepts, CDOBranch sourceBranch) {
		
		Set<String> conceptIds = FluentIterable.from(concepts).transform(COMPONENT_TO_ID_FUNCTION).toSet();
		
		SnomedConcepts snomedConcepts = SnomedRequests.prepareSearchConcept()
			.setComponentIds(conceptIds)
			.setLimit(conceptIds.size())
			.build(BranchPathUtils.createPath(sourceBranch).getPath())
			.executeSync(getEventBus());
		
		if (!snomedConcepts.getItems().isEmpty()) {
			return FluentIterable.from(snomedConcepts.getItems()).transform(new Function<ISnomedConcept, MergeConflict>() {
				@Override
				public MergeConflict apply(ISnomedConcept input) {
					return MergeConflictImpl.builder()
						.withArtefactId(input.getId())
						.withArtefactType("Concept")
						.withConflictingAttributes(singletonList(SnomedPackage.Literals.COMPONENT__ID.getName()))
						.withType(ConflictType.CONFLICTING_CHANGE)
						.build();
				}
			}).toSet();
		}
		
		return emptySet();
	}

	private Collection<MergeConflict> checkExistingDescriptionIds(Iterable<Description> descriptions, CDOBranch sourceBranch) {
		
		Set<String> descriptionIds = FluentIterable.from(descriptions).transform(COMPONENT_TO_ID_FUNCTION).toSet();
		
		SnomedDescriptions snomedDescriptions = SnomedRequests.prepareSearchDescription()
			.setComponentIds(descriptionIds)
			.setLimit(descriptionIds.size())
			.build(BranchPathUtils.createPath(sourceBranch).getPath())
			.executeSync(getEventBus());
		
		if (!snomedDescriptions.getItems().isEmpty()) {
			return FluentIterable.from(snomedDescriptions.getItems()).transform(new Function<ISnomedDescription, MergeConflict>() {
				@Override
				public MergeConflict apply(ISnomedDescription input) {
					return MergeConflictImpl.builder()
								.withArtefactId(input.getId())
								.withArtefactType("Description")
								.withConflictingAttributes(singletonList(SnomedPackage.Literals.COMPONENT__ID.getName()))
								.withType(ConflictType.CONFLICTING_CHANGE)
								.build();
				}
			}).toSet();
		}
		
		return emptySet();
	}

	private Collection<MergeConflict> checkExistingRelationshipIds(Iterable<Relationship> relationships, CDOBranch sourceBranch) {
		
		Set<String> relationshipIds = FluentIterable.from(relationships).transform(COMPONENT_TO_ID_FUNCTION).toSet();
		
		SnomedRelationships snomedRelationships = SnomedRequests.prepareSearchRelationship()
			.setComponentIds(relationshipIds)
			.setLimit(relationshipIds.size())
			.build(BranchPathUtils.createPath(sourceBranch).getPath())
			.executeSync(getEventBus());
		
		if (!snomedRelationships.getItems().isEmpty()) {
			return FluentIterable.from(snomedRelationships.getItems()).transform(new Function<ISnomedRelationship, MergeConflict>() {
				@Override
				public MergeConflict apply(ISnomedRelationship input) {
					return MergeConflictImpl.builder()
								.withArtefactId(input.getId())
								.withArtefactType("Relationship")
								.withConflictingAttributes(singletonList(SnomedPackage.Literals.COMPONENT__ID.getName()))
								.withType(ConflictType.CONFLICTING_CHANGE)
								.build();
				}
			}).toSet();
		}
		
		return emptySet();
	}
	
}
