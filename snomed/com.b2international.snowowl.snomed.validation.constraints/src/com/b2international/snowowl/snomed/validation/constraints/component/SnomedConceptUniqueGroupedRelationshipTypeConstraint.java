/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.validation.constraints.component;

import static com.b2international.commons.CompareUtils.isEmpty;

import java.util.Collection;
import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.validation.ComponentValidationConstraint;
import com.b2international.snowowl.core.validation.ComponentValidationDiagnostic;
import com.b2international.snowowl.core.validation.ComponentValidationDiagnosticImpl;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.ISnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * Active relationships must be of unique type within a relationship group.
 * 
 */
public class SnomedConceptUniqueGroupedRelationshipTypeConstraint extends ComponentValidationConstraint<SnomedConceptDocument> {

	public static final String ID = "com.b2international.snowowl.snomed.validation.constraints.component.SnomedConceptUniqueGroupedRelationshipTypeConstraint";
	
	private static final class UniqueRelationshipTypePredicate implements Predicate<ISnomedRelationship> {
		
		private final Multimap<Integer, ISnomedRelationship> groupToRelationshipsMultimap;

		private UniqueRelationshipTypePredicate(final Multimap<Integer, ISnomedRelationship> groupToRelationshipMultimap) {
			this.groupToRelationshipsMultimap = groupToRelationshipMultimap;
		}
		
		@Override
		public boolean apply(final ISnomedRelationship rship) {
			
			Collection<ISnomedRelationship> relationshipsInGroup = groupToRelationshipsMultimap.get(rship.getGroup());
			List<ISnomedRelationship> relationshipsInGroupWithSameCharType = FluentIterable.from(relationshipsInGroup).filter(new Predicate<ISnomedRelationship>() {
				@Override public boolean apply(ISnomedRelationship input) {
					return input.getCharacteristicType() == rship.getCharacteristicType();
				}
			}).toList();
			
			for (final ISnomedRelationship relationship : relationshipsInGroupWithSameCharType) {
				if (relationship != rship && relationshipsEquivalent(rship, relationship)) {
					return false;
				}
			}
			return true;
		}

		private boolean relationshipsEquivalent(final ISnomedRelationship r1, final ISnomedRelationship r2) {
			if (r1.getTypeId() == null) {
				if (r2.getTypeId() != null) {
					return false;
				}
			} else if (!r1.getTypeId().equals(r2.getTypeId())) {
				return false;
			}
			
			// Equivalent relationships are permitted in union groups, signified by a nonzero union group number
			if (r1.getUnionGroup() != r2.getUnionGroup() || r1.getUnionGroup() != 0) {
				return false;
			}
			
			return true;
		}
	}
	
	@Override
	public ComponentValidationDiagnostic validate(final IBranchPath branchPath, final SnomedConceptDocument component) {
		
		SnomedRelationships relationships = SnomedRequests.prepareSearchRelationship()
			.all()
			.filterByActive(true)
			.filterBySource(component.getId())
			.setExpand("type(expand(pt()))")
			.setLocales(getLocales())
			.build(branchPath.getPath())
			.executeSync(getBus());
			
		final Multimap<Integer, ISnomedRelationship> groupToRelationshipsMultimap = ArrayListMultimap.create();
		
		for (ISnomedRelationship relationship : relationships.getItems()) {
			if (relationship.getGroup() > 0) {
				groupToRelationshipsMultimap.put(relationship.getGroup(), relationship);
			}
		}

		final List<ISnomedRelationship> invalidRelationships = FluentIterable.from(groupToRelationshipsMultimap.values())
				.filter(Predicates.not(new UniqueRelationshipTypePredicate(groupToRelationshipsMultimap))).toList();

		final List<ComponentValidationDiagnostic> diagnostics = Lists.newArrayList();

		for (final ISnomedRelationship relationship : invalidRelationships) {
			final String message = createErrorMessage(component, relationship, branchPath);
			diagnostics.add(new ComponentValidationDiagnosticImpl(component.getId(), message, ID, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, error()));
		}
		
		if (isEmpty(diagnostics)) {
			return createOk(component.getId(), ID, SnomedTerminologyComponentConstants.CONCEPT_NUMBER);
		} else {
			return new ComponentValidationDiagnosticImpl(component.getId(), ID, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, diagnostics);
		}
		
	}

	private String createErrorMessage(final SnomedConceptDocument component, final ISnomedRelationship relationship, final IBranchPath branchPath) {
		return String.format("'%s' has a relationship of type '%s' which is non-unique within its group (%s).", component.getLabel(),
				relationship.getTypeConcept().getPt().getTerm(), relationship.getGroup());
	}

	private List<ExtendedLocale> getLocales() {
		return ApplicationContext.getServiceForClass(LanguageSetting.class).getLanguagePreference();
	}
}
