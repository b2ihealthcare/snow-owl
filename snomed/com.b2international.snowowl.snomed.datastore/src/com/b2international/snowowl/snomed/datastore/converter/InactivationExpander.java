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
package com.b2international.snowowl.snomed.datastore.converter;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.AssociationType;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Function;
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

	void expand(List<T> results, Set<String> componentIds) {
		
		if (componentIds.isEmpty()) {
			return;
		}
		
		final List<String> refSetIds = newArrayList();
		for (final AssociationType associationType : AssociationType.values()) {
			refSetIds.add(associationType.getConceptId());
		}
		
		refSetIds.add(inactivationIndicatorId);
		
		final SnomedReferenceSetMembers members = SnomedRequests.prepareSearchMember()
			.all()
			.filterByRefSet(refSetIds)
			.filterByReferencedComponent(componentIds)
			.filterByActive(true)
			.build()
			.execute(context);
		
		if (members.getItems().isEmpty()) {
			return;
		}
		
		final Multimap<String, SnomedReferenceSetMember> membersByReferencedComponentId = Multimaps.index(members, new Function<SnomedReferenceSetMember, String>() {
			@Override
			public String apply(SnomedReferenceSetMember input) {
				return input.getReferencedComponent().getId();
			}
		});
		
		for (T result : results) {
			final Collection<SnomedReferenceSetMember> referringMembers = membersByReferencedComponentId.get(result.getId());
			final List<SnomedReferenceSetMember> associationMembers = newArrayList();
			final List<SnomedReferenceSetMember> inactivationMembers = newArrayList(); 
			
			for (SnomedReferenceSetMember referringMember : referringMembers) {
				if (SnomedRefSetType.ASSOCIATION.equals(referringMember.type())) {
					associationMembers.add(referringMember);
				} else if (SnomedRefSetType.ATTRIBUTE_VALUE.equals(referringMember.type())) {
					inactivationMembers.add(referringMember);
				}
			}
			
			if (!inactivationMembers.isEmpty()) {
				final String valueId = (String) Iterables.getFirst(inactivationMembers, null).getProperties().get(SnomedRf2Headers.FIELD_VALUE_ID);
				setInactivationIndicator(result, valueId);
			}
			
			Multimap<AssociationType, String> associationTargets = HashMultimap.create();
			for (SnomedReferenceSetMember associationMember : associationMembers) {
				AssociationType type = AssociationType.getByConceptId(associationMember.getReferenceSetId());
				final SnomedCoreComponent target = (SnomedCoreComponent) associationMember.getProperties().get(SnomedRf2Headers.FIELD_TARGET_COMPONENT);
				associationTargets.put(type, target.getId());
			}
			
			if (!associationTargets.isEmpty()) {
				setAssociationTargets(result, associationTargets);
			}
		}
	}

	protected abstract void setInactivationIndicator(T result, final String valueId);

	protected abstract void setAssociationTargets(T result, Multimap<AssociationType, String> associationTargets);
}
