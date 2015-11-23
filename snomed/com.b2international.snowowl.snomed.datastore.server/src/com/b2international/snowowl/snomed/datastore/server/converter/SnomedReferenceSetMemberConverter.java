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

import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMemberImpl;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.snomedrefset.SnomedQueryRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * @since 4.5
 */
public class SnomedReferenceSetMemberConverter implements Function<SnomedRefSetMemberIndexEntry, SnomedReferenceSetMember> {

	SnomedReferenceSetMemberConverter() {}
	
	@Override
	public SnomedReferenceSetMember apply(SnomedRefSetMemberIndexEntry input) {
		final SnomedReferenceSetMemberImpl member = new SnomedReferenceSetMemberImpl();
		member.setId(input.getId());
		member.setEffectiveTime(EffectiveTimes.toDate(input.getEffectiveTimeAsLong()));
		member.setReleased(input.isReleased());
		member.setActive(input.isActive());
		member.setModuleId(input.getModuleId());
		member.setReferenceSetId(input.getRefSetIdentifierId());
		if (SnomedRefSetType.QUERY == input.getRefSetType()) {
			// in case of query type refset the actual ESCG query is stored in the specialFieldId prop
			final Builder<String, Object> props = ImmutableMap.builder();
			props.put(SnomedRf2Headers.FIELD_QUERY, input.getQuery());
			member.setProperties(props.build());
		}
		expandReferencedComponent(member, input.getReferencedComponentId(), input.getReferencedComponentType());
		return member;
	}

	public SnomedReferenceSetMember apply(SnomedRefSetMember input) {
		final SnomedReferenceSetMemberImpl member = new SnomedReferenceSetMemberImpl();
		member.setId(input.getUuid());
		member.setEffectiveTime(input.getEffectiveTime());
		member.setReleased(input.isReleased());
		member.setActive(input.isActive());
		member.setModuleId(input.getModuleId());
		member.setReferenceSetId(input.getRefSetIdentifierId());
		if (input instanceof SnomedQueryRefSetMember) {
			final Builder<String, Object> props = ImmutableMap.builder();
			props.put(SnomedRf2Headers.FIELD_QUERY, ((SnomedQueryRefSetMember) input).getQuery());
			member.setProperties(props.build());
		}
		expandReferencedComponent(member, input.getReferencedComponentId(), input.getReferencedComponentType());
		return member;
	}
	
	private void expandReferencedComponent(SnomedReferenceSetMemberImpl member, String referencedComponentId, String referencedComponentType) {
		// TODO support referencedComponent expansion if required
		final SnomedCoreComponent component; 
		switch (referencedComponentType) {
		// TODO support query type refset refcomp expansion, currently it's a concept
		case SnomedTerminologyComponentConstants.REFSET:
		case SnomedTerminologyComponentConstants.CONCEPT:
			component = new SnomedConcept();
			((SnomedConcept) component).setId(referencedComponentId);
			break;
		case SnomedTerminologyComponentConstants.DESCRIPTION:
			component = new SnomedDescription();
			((SnomedDescription) component).setId(referencedComponentId);
			break;
		case SnomedTerminologyComponentConstants.RELATIONSHIP:
			component = new SnomedRelationship();
			((SnomedRelationship) component).setId(referencedComponentId);
			break;
		default: throw new UnsupportedOperationException("UnsupportedReferencedComponentType");
		}
		member.setReferencedComponent(component);
	}
	
	private void expandReferencedComponent(SnomedReferenceSetMemberImpl member, String referencedComponentId, short referencedComponentType) {
		expandReferencedComponent(member, referencedComponentId, CoreTerminologyBroker.getInstance().getTerminologyComponentId(referencedComponentType));
	}

}
