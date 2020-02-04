/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.google.common.collect.ImmutableSet;

/**
 * @since 4.5
 */
final class SnomedRefSetMemberUpdateRequest implements SnomedComponentRequest<Boolean> {

	@NotEmpty
	private final String memberId;
	
	@NotEmpty
	private final Map<String, Object> properties;

	@NotNull
	private final Boolean force;
	
	SnomedRefSetMemberUpdateRequest(String memberId, Map<String, Object> properties, Boolean force) {
		this.memberId = memberId;
		this.properties = properties;
		this.force = force;
	}
	
	String getMemberId() {
		return memberId;
	}
	
	boolean hasProperty(String key) {
		return properties.containsKey(key);
	}
	
	String getComponentId(String key) {
		final Object value = properties.get(key);
		
		try {
			
			if (value == null) {
				return null;
			} else if (value instanceof Map) {
				final Map<?, ?> component = (Map<?, ?>) value;
				final Object componentId = component.get(SnomedRf2Headers.FIELD_ID);
				return (String) componentId;
			} else {
				return (String) value;
			}
			
		} catch (final ClassCastException cce) {
			throw new BadRequestException("Property '%s' must be a String, or have a nested String property named 'id'.", key);
		}
	}
	
	String getProperty(String key) {
		return getProperty(key, String.class);
	}
	
	<T> T getProperty(String key, Class<T> valueType) {
		final Object value = properties.get(key);
		
		try {
			return valueType.cast(value);
		} catch (final ClassCastException cce) {
			throw new BadRequestException("Property '%s' must be a %s.", key, valueType.getSimpleName());
		}
	}
	
	@Override
	public Set<String> getRequiredComponentIds(TransactionContext context) {
		return ImmutableSet.of(memberId);
	}

	@Override
	public Boolean execute(TransactionContext context) {
		SnomedRefSetMemberIndexEntry member = context.lookup(memberId, SnomedRefSetMemberIndexEntry.class);
		SnomedRefSetMemberIndexEntry.Builder updatedMember = SnomedRefSetMemberIndexEntry.builder(member); 

		/* 
		 * TODO: Generalize the logic below: any attempts of retrieving a missing component during component update
		 * (with the exception of the component that is being updated) should return a 400 response instead of a 404. 
		 */
		try {

			SnomedRefSetType type = member.getReferenceSetType();

			boolean changed = false;

			changed |= updateStatus(member, updatedMember);
			changed |= updateModule(member, updatedMember);
			changed |= updateEffectiveTime(member, updatedMember);

			SnomedRefSetMemberUpdateDelegate delegate = getDelegate(type);
			changed |= delegate.execute(member, updatedMember, context);

			if (changed) {
				if (!isEffectiveTimeUpdate()) {
					updatedMember.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME);
				}
				context.update(member, updatedMember.build());
			}

			return changed;

		} catch (ComponentNotFoundException e) {
			throw e.toBadRequestException();
		}
	}
	
	private SnomedRefSetMemberUpdateDelegate getDelegate(SnomedRefSetType referenceSetType) {
		switch (referenceSetType) {
			case ASSOCIATION:
				return new SnomedAssociationMemberUpdateDelegate(this);
			case ATTRIBUTE_VALUE:
				return new SnomedAttributeValueMemberUpdateDelegate(this);
			case COMPLEX_MAP:
				return new SnomedComplexMapMemberUpdateDelegate(this);
			case COMPLEX_BLOCK_MAP:
				return new SnomedComplexBlockMapMemberUpdateDelegate(this);
			case CONCRETE_DATA_TYPE:
				return new SnomedConcreteDomainMemberUpdateDelegate(this);
			case DESCRIPTION_TYPE:
				return new SnomedDescriptionTypeMemberUpdateDelegate(this);
			case EXTENDED_MAP:
				return new SnomedExtendedMapMemberUpdateDelegate(this);
			case LANGUAGE:
				return new SnomedLanguageMemberUpdateDelegate(this);
			case MODULE_DEPENDENCY:
				return new SnomedModuleDependencyMemberUpdateDelegate(this);
			case QUERY:
				return new SnomedQueryMemberUpdateDelegate(this);
			case SIMPLE: 
				return new SnomedSimpleMemberUpdateDelegate(this);
			case SIMPLE_MAP:
				return new SnomedSimpleMapMemberUpdateDelegate(this);
			case SIMPLE_MAP_WITH_DESCRIPTION:
				return new SnomedSimpleMapMemberWithDescriptionUpdateDelegate(this);
			case OWL_AXIOM: //$FALL-THROUGH$
			case OWL_ONTOLOGY:
				return new SnomedOWLExpressionMemberUpdateDelegate(this);
			case MRCM_DOMAIN:
				return new SnomedMRCMDomainMemberUpdateDelegate(this);
			case MRCM_ATTRIBUTE_DOMAIN:
				return new SnomedMRCMAttributeDomainMemberUpdateDelegate(this);
			case MRCM_ATTRIBUTE_RANGE:
				return new SnomedMRCMAttributeRangeMemberUpdateDelegate(this);
			case MRCM_MODULE_SCOPE:
				return new SnomedMRCMModuleScopeMemberUpdateDelegate(this);
			default: 
				throw new IllegalStateException("Unexpected reference set type '" + referenceSetType + "'.");
		}
	}
	
	private boolean updateStatus(SnomedRefSetMemberIndexEntry original, SnomedRefSetMemberIndexEntry.Builder member) {
		Boolean newStatus = getProperty(SnomedRf2Headers.FIELD_ACTIVE, Boolean.class);
		if (newStatus != null && !newStatus.equals(original.isActive())) {
			member.active(newStatus);
			return true;
		} else {
			return false;
		}
	}

	private boolean updateModule(SnomedRefSetMemberIndexEntry original, SnomedRefSetMemberIndexEntry.Builder member) {
		String newModuleId = getComponentId(SnomedRf2Headers.FIELD_MODULE_ID);
		if (newModuleId != null && !newModuleId.equals(original.getModuleId())) {
			member.moduleId(newModuleId);
			return true;
		} else {
			return false;
		}
	}

	private boolean updateEffectiveTime(SnomedRefSetMemberIndexEntry original, SnomedRefSetMemberIndexEntry.Builder member) {
		if (!isEffectiveTimeUpdate()) {
			return false;
		}
		
		Date newEffectiveTime = EffectiveTimes.parse(getProperty(SnomedRf2Headers.FIELD_EFFECTIVE_TIME), DateFormats.SHORT);
		if (!Objects.equals(newEffectiveTime, EffectiveTimes.toDate(original.getEffectiveTime()))) {
			if (newEffectiveTime == null) {
				// if effective time is null, then unset the effective time but don't change the released flag
				member.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME);
			} else {
				// otherwise set the value and toggle the "relased" flag
				member.effectiveTime(newEffectiveTime.getTime());
				member.released(true);
			}
			return true;
		} else {
			return false;
		}
	}

	private boolean isEffectiveTimeUpdate() {
		return force && hasProperty(SnomedRf2Headers.FIELD_EFFECTIVE_TIME);
	}

}
