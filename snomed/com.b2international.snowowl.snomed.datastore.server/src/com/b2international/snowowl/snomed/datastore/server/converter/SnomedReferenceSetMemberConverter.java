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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.CollectionResource;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMemberImpl;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.snomedrefset.SnomedQueryRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * @since 4.5
 */
public class SnomedReferenceSetMemberConverter implements ResourceConverter<SnomedRefSetMemberIndexEntry, SnomedRefSetMember, SnomedReferenceSetMember, SnomedReferenceSetMembers> {

	private final BranchContext context;
	private final List<String> expand;

	SnomedReferenceSetMemberConverter(BranchContext context, List<String> expand) {
		this.context = context;
		this.expand = expand;
	}

	@Override
	public SnomedReferenceSetMember convert(SnomedRefSetMemberIndexEntry component) {
		return convert(Collections.singleton(component)).getItems().iterator().next();
	}
	
	@Override
	public SnomedReferenceSetMember convert(SnomedRefSetMember object) {
		final SnomedReferenceSetMemberImpl member = new SnomedReferenceSetMemberImpl();
		member.setId(object.getUuid());
		member.setEffectiveTime(object.getEffectiveTime());
		member.setReleased(object.isReleased());
		member.setActive(object.isActive());
		member.setModuleId(object.getModuleId());
		member.setReferenceSetId(object.getRefSetIdentifierId());
		if (object instanceof SnomedQueryRefSetMember) {
			final Builder<String, Object> props = ImmutableMap.builder();
			props.put(SnomedRf2Headers.FIELD_QUERY, ((SnomedQueryRefSetMember) object).getQuery());
			member.setProperties(props.build());
		}
		expandReferencedComponent(member, object.getReferencedComponentId(), object.getReferencedComponentType());
		return member;
	}
	
	@Override
	public SnomedReferenceSetMembers convert(Collection<SnomedRefSetMemberIndexEntry> components) {
		return new SnomedReferenceSetMembers(
				FluentIterable
				.from(components)
				.transform(new Function<SnomedRefSetMemberIndexEntry, SnomedReferenceSetMember>() {
					@Override
					public SnomedReferenceSetMember apply(SnomedRefSetMemberIndexEntry input) {
						return toResource(input);
					}
				})
				// TODO add expansion
				.toList());
	}

	private SnomedReferenceSetMember toResource(SnomedRefSetMemberIndexEntry entry) {
		final SnomedReferenceSetMemberImpl member = new SnomedReferenceSetMemberImpl();
		member.setId(entry.getId());
		member.setEffectiveTime(EffectiveTimes.toDate(entry.getEffectiveTimeAsLong()));
		member.setReleased(entry.isReleased());
		member.setActive(entry.isActive());
		member.setModuleId(entry.getModuleId());
		member.setReferenceSetId(entry.getRefSetIdentifierId());
		if (SnomedRefSetType.QUERY == entry.getRefSetType()) {
			// in case of query type refset the actual ESCG query is stored in the specialFieldId prop
			final Builder<String, Object> props = ImmutableMap.builder();
			props.put(SnomedRf2Headers.FIELD_QUERY, entry.getQuery());
			member.setProperties(props.build());
		}
		expandReferencedComponent(member, entry.getReferencedComponentId(), entry.getReferencedComponentType());
		return member;
	}
	
	private void expandReferencedComponent(SnomedReferenceSetMemberImpl member, String referencedComponentId, String referencedComponentType) {
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
