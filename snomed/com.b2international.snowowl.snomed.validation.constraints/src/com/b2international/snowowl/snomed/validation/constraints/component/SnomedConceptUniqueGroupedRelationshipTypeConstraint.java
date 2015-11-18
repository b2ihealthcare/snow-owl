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

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.validation.ComponentValidationConstraint;
import com.b2international.snowowl.core.validation.ComponentValidationDiagnostic;
import com.b2international.snowowl.core.validation.ComponentValidationDiagnosticImpl;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedStatementBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.services.ISnomedRelationshipNameProvider;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * Active relationships must be of unique type within a relationship group.
 * 
 */
public class SnomedConceptUniqueGroupedRelationshipTypeConstraint extends
		ComponentValidationConstraint<SnomedConceptIndexEntry> {

	public static final String ID = "com.b2international.snowowl.snomed.validation.constraints.component.SnomedConceptUniqueGroupedRelationshipTypeConstraint";
	
	private static final class UniqueRelationshipTypePredicate implements Predicate<SnomedRelationshipIndexEntry> {
		
		private final Multimap<Byte, SnomedRelationshipIndexEntry> groupToRelationshipMultimap;

		private UniqueRelationshipTypePredicate(final Multimap<Byte, SnomedRelationshipIndexEntry> groupToRelationshipMultimap) {
			this.groupToRelationshipMultimap = groupToRelationshipMultimap;
		}
		
		/*
		 * (non-Javadoc)
		 * @see com.google.common.base.Predicate#apply(java.lang.Object)
		 */
		@Override
		public boolean apply(final SnomedRelationshipIndexEntry input) {
			// don't check ungrouped relationships
			if (input.getGroup() == 0) {
				return true;
			}
			final Collection<SnomedRelationshipIndexEntry> relationshipsInGroup = groupToRelationshipMultimap.get(input.getGroup());
			for (final SnomedRelationshipIndexEntry relationship : relationshipsInGroup) {
				if (relationship != input && relationshipsEquivalent(input, relationship))
					return false;
			}
			return true;
		}

		private boolean relationshipsEquivalent(final SnomedRelationshipIndexEntry r1, final SnomedRelationshipIndexEntry r2) {
			if (r1.getAttributeId() == null) {
				if (r2.getAttributeId() != null)
					return false;
			} else if (!r1.getAttributeId().equals(r2.getAttributeId()))
				return false;
			
			if (!r1.getCharacteristicTypeId().equals(r2.getCharacteristicTypeId())) {
				return false;
			}
			
			if (r1.getGroup() != r2.getGroup()) {
				return false;
			}
			
			/* 
			 * Equivalent relationships are permitted in union groups, signified by a nonzero union group number
			 */
			if (r1.getUnionGroup() != r2.getUnionGroup() || r1.getUnionGroup() != 0) {
				return false;
			}
			
			return true;
		}
	}
	
	@Override
	public ComponentValidationDiagnostic validate(final IBranchPath branchPath, final SnomedConceptIndexEntry component) {
		final SnomedStatementBrowser statementBrowser = ApplicationContext.getInstance().getService(SnomedStatementBrowser.class);
		final List<SnomedRelationshipIndexEntry> outboundStatements = statementBrowser.getOutboundStatements(branchPath, component);
		final Multimap<Byte, SnomedRelationshipIndexEntry> groupToRelationshipMultimap = ArrayListMultimap.create();
		// populate map by groups, omit inactive relationships and ungrouped relationships (if group equals with 0 -> relationship is ungrouped)
		for (final SnomedRelationshipIndexEntry relationship : outboundStatements) {
			if (relationship.isActive() && 0 < relationship.getGroup())
				groupToRelationshipMultimap.put(relationship.getGroup(), relationship);
		}
		// predicate for checking whether a given relationship is equivalent to another one in the same group
		final Predicate<SnomedRelationshipIndexEntry> isUniqueWithinGroupPredicate = new UniqueRelationshipTypePredicate(groupToRelationshipMultimap);

		// process relationships against map
		final List<ComponentValidationDiagnostic> diagnostics = Lists.newArrayList();
		for (final SnomedRelationshipIndexEntry relationship : groupToRelationshipMultimap.values()) {
			if (relationship.isActive() && !isUniqueWithinGroupPredicate.apply(relationship)) {
				final String relationshipLabel = ApplicationContext.getServiceForClass(ISnomedRelationshipNameProvider.class).getComponentLabel(branchPath, relationship.getId());
				final String message = "'" + component.getLabel() + "' has a relationship '" + relationshipLabel 
						+ "', which has a non-unique type within its group '" + relationship.getGroup() + "'.";
				diagnostics.add(new ComponentValidationDiagnosticImpl(component.getId(), message, ID, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, error()));
			}
		};
		
		if (isEmpty(diagnostics)) {
			return createOk(component.getId(), ID, SnomedTerminologyComponentConstants.CONCEPT_NUMBER);
		} else {
			return new ComponentValidationDiagnosticImpl(component.getId(), ID, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, diagnostics);
		}
		
	}

}