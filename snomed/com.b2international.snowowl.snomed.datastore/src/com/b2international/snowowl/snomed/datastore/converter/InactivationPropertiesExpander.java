/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.AssociationTarget;
import com.b2international.snowowl.snomed.core.domain.InactivationProperties;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

/**
 * @since 7.4
 * @param <T>
 */
public final class InactivationPropertiesExpander {

	private final BranchContext context;
	private final Options expand;
	private final List<ExtendedLocale> locales;
	private final String inactivationIndicatorRefSetId;
	
	public InactivationPropertiesExpander(BranchContext context, Options expand, List<ExtendedLocale> locales, String inactivationIndicatorRefSetId) {
		this.context = context;
		this.expand = expand;
		this.locales = locales;
		this.inactivationIndicatorRefSetId = inactivationIndicatorRefSetId;
	}

	void expand(List<? extends SnomedCoreComponent> results, Set<String> referencedComponentIds) {
		if (!expand.containsKey(SnomedCoreComponent.Expand.INACTIVATION_PROPERTIES)) {
			return;
		}
		
		final SnomedReferenceSetMembers members = SnomedRequests.prepareSearchMember()
			.all()
			.filterByActive(true)
			// all association type refsets and the indicator
			.filterByRefSet(String.format("<%s OR %s", Concepts.REFSET_ASSOCIATION_TYPE, inactivationIndicatorRefSetId))
			.filterByReferencedComponent(referencedComponentIds)
			.build()
			.execute(context);
		
		final Multimap<String, SnomedReferenceSetMember> membersByReferencedComponentId = Multimaps.index(members, SnomedReferenceSetMember::getReferencedComponentId);
		
		for (SnomedCoreComponent result : results) {
			final Collection<SnomedReferenceSetMember> referringMembers = membersByReferencedComponentId.get(result.getId());
			final ImmutableList.Builder<AssociationTarget> associationTargets = ImmutableList.builder();
			final Set<String> inactivationIndicatorIds = Sets.newHashSet();
			
			for (SnomedReferenceSetMember referringMember : referringMembers) {
				if (SnomedRefSetType.ASSOCIATION.equals(referringMember.type())) {
					final AssociationTarget associationTarget = new AssociationTarget();
					associationTarget.setReferenceSetId(referringMember.getRefsetId());
					associationTarget.setTargetComponent((SnomedCoreComponent) referringMember.getProperties().get(SnomedRf2Headers.FIELD_TARGET_COMPONENT));
					associationTargets.add(associationTarget);
				} else if (SnomedRefSetType.ATTRIBUTE_VALUE.equals(referringMember.type())) {
					inactivationIndicatorIds.add((String) referringMember.getProperties().get(SnomedRf2Headers.FIELD_VALUE_ID));
				}
			}

			InactivationProperties inactivationProperties = new InactivationProperties();
			inactivationProperties.setInactivationIndicatorId(Iterables.getFirst(inactivationIndicatorIds, null));
			inactivationProperties.setAssociationTargets(associationTargets.build());
			result.setInactivationProperties(inactivationProperties);
		}
		
		// TODO further expand the inactivationProperties nested properties if requested 
	}

}
