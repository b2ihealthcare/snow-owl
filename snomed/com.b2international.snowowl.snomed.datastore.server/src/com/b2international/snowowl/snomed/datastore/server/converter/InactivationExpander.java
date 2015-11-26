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
package com.b2international.snowowl.snomed.datastore.server.converter;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.AssociationType;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public abstract class InactivationExpander<T extends SnomedComponent> {

	private BranchContext context;
	private String inactivationIndicatorId;
	
	public InactivationExpander(BranchContext context, String inactivationIndicatorId) {
		this.context = context;
		this.inactivationIndicatorId = inactivationIndicatorId;
	}

	void expand(List<T> results) {
		
		final FluentIterable<T> inactiveResults = FluentIterable.from(results)
				.filter(Predicates.not(BaseSnomedComponentConverter.ACTIVE_PREDICATE));

		final Set<String> componentIds = inactiveResults
				.transform(BaseSnomedComponentConverter.ID_FUNCTION)
				.toSet();
		
		List<String> refSetIds = newArrayList();
		for (final AssociationType associationType : AssociationType.values()) {
			refSetIds.add(associationType.getConceptId());
		}
		
		refSetIds.add(inactivationIndicatorId);
		
		final SnomedReferenceSetMembers members = SnomedRequests.prepareMemberSearch()
			.all()
			.filterByRefSet(refSetIds)
			.filterByReferencedComponent(componentIds)
			.filterByActive(true)
			.build()
			.execute(context);
		
		Multimap<String, SnomedReferenceSetMember> membersByReferencedComponentId = Multimaps.index(members, new Function<SnomedReferenceSetMember, String>() {
			@Override
			public String apply(SnomedReferenceSetMember input) {
				return input.getReferencedComponent().getId();
			}
		});
		
		for (T result : inactiveResults) {
			final Collection<SnomedReferenceSetMember> descriptionMembers = membersByReferencedComponentId.get(result.getId());
			final List<SnomedReferenceSetMember> associationMembers = newArrayList();
			final List<SnomedReferenceSetMember> inactivationMembers = newArrayList(); 
			
			for (SnomedReferenceSetMember descriptionMember : descriptionMembers) {
				if (SnomedRefSetType.ASSOCIATION.equals(descriptionMember.type())) {
					associationMembers.add(descriptionMember);
				} else if (SnomedRefSetType.ATTRIBUTE_VALUE.equals(descriptionMember.type())) {
					inactivationMembers.add(descriptionMember);
				}
			}
			
			if (!inactivationMembers.isEmpty()) {
				final String valueId = (String) Iterables.getFirst(inactivationMembers, null).getProperties().get(SnomedRf2Headers.FIELD_VALUE_ID);
				setInactivationIndicator(result, valueId);
			}
			
			Multimap<AssociationType, String> associationTargets = HashMultimap.create();
			for (SnomedReferenceSetMember associationMember : associationMembers) {
				AssociationType type = AssociationType.getByConceptId(associationMember.getReferenceSetId());
				String targetId = (String) associationMember.getProperties().get(SnomedRf2Headers.FIELD_TARGET_COMPONENT_ID);
				associationTargets.put(type, targetId);
			}
			
			if (!associationTargets.isEmpty()) {
				setAssociationTargets(result, associationTargets);
			}
		}
	}

	protected abstract void setInactivationIndicator(T result, final String valueId);

	protected abstract void setAssociationTargets(T result, Multimap<AssociationType, String> associationTargets);
}
