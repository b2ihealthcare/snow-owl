/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.*;
import java.util.stream.Collectors;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.*;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Strings;
import com.google.common.collect.*;

/**
 * @since 7.4
 * @param <T>
 */
public final class InactivationPropertiesExpander {

	private static final int BATCH_SIZE = 10_000;
	
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
		
		final Multimap<String, SnomedReferenceSetMember> membersByReferencedComponentId = ArrayListMultimap.create();

		SnomedRequests.prepareSearchMember()
			.setLimit(10_000)
			.filterByActive(true)
			// all association type refsets and the indicator
			.filterByRefSet(String.format("<%s OR %s", Concepts.REFSET_ASSOCIATION_TYPE, inactivationIndicatorRefSetId))
			.filterByReferencedComponent(referencedComponentIds)
			.stream(context)
			.flatMap(SnomedReferenceSetMembers::stream)
			.forEachOrdered(member -> {
				membersByReferencedComponentId.put(member.getReferencedComponentId(), member);
			});

		final Options inactivationPropertiesExpand = expand.getOptions(SnomedCoreComponent.Expand.INACTIVATION_PROPERTIES);
		
		Map<String, SnomedConcept> associationTargetComponentsById = Collections.emptyMap();
		Map<String, SnomedConcept>inactivationIndicatorsById = Collections.emptyMap();
		
		final Options nestedExpands = inactivationPropertiesExpand.getOptions("expand");
		if (nestedExpands != null && nestedExpands.containsKey(InactivationProperties.Expand.ASSOCIATION_TARGETS)) {
			final Options associationTargetsExpand = nestedExpands.getOptions(InactivationProperties.Expand.ASSOCIATION_TARGETS).getOptions("expand");
			
			final Set<String> componentsToExpand = membersByReferencedComponentId.values().stream()
					.filter(member -> SnomedRefSetType.ASSOCIATION.equals(member.type()))
					.map(member -> (String) member.getProperties().get(SnomedRf2Headers.FIELD_TARGET_COMPONENT_ID))
					.filter(id -> !Strings.isNullOrEmpty(id))
					.collect(Collectors.toSet());
			
			if (associationTargetsExpand != null && associationTargetsExpand.containsKey(AssociationTarget.Expand.TARGET_COMPONENT)) {
				associationTargetComponentsById = expandConceptByIdMap(associationTargetsExpand.getOptions(AssociationTarget.Expand.TARGET_COMPONENT).getOptions("expand"), componentsToExpand, context);
			}
			
		}
		
		if (nestedExpands != null && nestedExpands.containsKey(InactivationProperties.Expand.INACTIVATION_INDICATOR)) {
			final Options inactivationIndicatorExpand = nestedExpands.getOptions(InactivationProperties.Expand.INACTIVATION_INDICATOR);
			
			final Set<String> componentsToExpand = membersByReferencedComponentId.values().stream()
					.filter(member -> SnomedRefSetType.ATTRIBUTE_VALUE.equals(member.type()))
					.map(member -> (String) member.getProperties().get(SnomedRf2Headers.FIELD_VALUE_ID))
					.filter(id -> !Strings.isNullOrEmpty(id))
					.collect(Collectors.toSet());
			
			inactivationIndicatorsById = expandConceptByIdMap(inactivationIndicatorExpand.getOptions("expand"), componentsToExpand, context);
		}
		
		for (SnomedCoreComponent result : results) {
			final Collection<SnomedReferenceSetMember> referringMembers = membersByReferencedComponentId.get(result.getId());
			final ImmutableList.Builder<AssociationTarget> associationTargets = ImmutableList.builder();
			final Set<String> inactivationIndicatorIds = Sets.newHashSet();
			
			for (SnomedReferenceSetMember referringMember : referringMembers) {
				if (SnomedRefSetType.ASSOCIATION.equals(referringMember.type())) {
					final AssociationTarget associationTarget = new AssociationTarget();
					final String targetComponentId = (String) referringMember.getProperties().get(SnomedRf2Headers.FIELD_TARGET_COMPONENT_ID);
					
					associationTarget.setReferenceSetId(referringMember.getRefsetId());
					associationTarget.setTargetComponentId(targetComponentId);

					if (associationTargetComponentsById.containsKey(targetComponentId)) {
						associationTarget.setTargetComponent(associationTargetComponentsById.get(targetComponentId));
					}
					
					associationTargets.add(associationTarget);
				} else if (SnomedRefSetType.ATTRIBUTE_VALUE.equals(referringMember.type())) {
					inactivationIndicatorIds.add((String) referringMember.getProperties().get(SnomedRf2Headers.FIELD_VALUE_ID));
				}
			}

			final String inactivationIndicatorId = Iterables.getFirst(inactivationIndicatorIds, null);
			
			InactivationProperties inactivationProperties = new InactivationProperties();
			inactivationProperties.setInactivationIndicatorId(inactivationIndicatorId);
			inactivationProperties.setAssociationTargets(associationTargets.build());
			
			if (!Strings.isNullOrEmpty(inactivationIndicatorId) && inactivationIndicatorsById.containsKey(inactivationIndicatorId)) {
				inactivationProperties.setInactivationIndicator(inactivationIndicatorsById.get(inactivationIndicatorId));
			}
			
			result.setInactivationProperties(inactivationProperties);
		}
		
	}

	private Map<String, SnomedConcept> expandConceptByIdMap(final Options expand, final Set<String> componentsToExpand, final BranchContext context) {
		final Map<String, SnomedConcept> conceptsById = Maps.newHashMap();
		
		Iterables.partition(componentsToExpand, BATCH_SIZE).forEach(idsFilter -> {
			SnomedRequests.prepareSearchConcept()
			.setLimit(idsFilter.size())
			.filterByIds(idsFilter)
			.setLocales(locales)
			.setExpand(expand)
			.stream(context)
			.flatMap(SnomedConcepts::stream)
			.forEachOrdered(concept -> {
				conceptsById.put(concept.getId(), concept);
			});
		});
		
		return conceptsById;
	}
}
