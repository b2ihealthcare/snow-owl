/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.domain.refset;

import static com.google.common.collect.Maps.newHashMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.core.terminology.TerminologyComponent;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

/**
 * Represents a SNOMED&nbsp;CT Reference Set Member.
 * <br>
 * Reference sets returned by search requests are populated based on the expand parameters passed into the {@link BaseResourceRequestBuilder#setExpand(String)}
 * methods. The expand parameters can be nested allowing a fine control for the details returned in the resultset.  
 * 
 * The supported expand parameters are:
 * <p>
 * <ul>
 * <li>{@code targetComponent()} - returns the target component of the member</li>
 * <li>{@code referencedComponent()} - returns the referenced component of the member</li>
 * </ul>
 * 
 * Expand parameters can be nested to further expand or filter the details returned. For example:
 * <p>
 * {@code referencedComponent(expand(pt()))}, would return the preferred term of a <i>Concept</i> type referenced component.
 * 
 * @see SnomedConcept
 * @see SnomedDescription
 * @see SnomedRelationship
 * @see SnomedReferenceSet
 * 
 * @since 4.5
 */
@TerminologyComponent(
	id = SnomedTerminologyComponentConstants.REFSET_MEMBER, 
	shortId = SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER,
	name = "SNOMED CT RefSet Member",
	componentCategory = ComponentCategory.SET_MEMBER,
	docType = SnomedRefSetMemberIndexEntry.class
)
public final class SnomedReferenceSetMember extends SnomedComponent {

	private static final long serialVersionUID = -7471488952871955209L;

	/**
	 * Enumerates expandable property keys.
	 * 
	 * @since 7.0
	 */
	public static final class Expand {
		public static final String REFERENCED_COMPONENT = "referencedComponent";
	}
	
	public static final Function<SnomedReferenceSetMember, String> GET_REFERENCED_COMPONENT_ID = (member) -> member.getReferencedComponent().getId();
	
	/**
	 * @since 6.16 
	 */
	public static final class Fields extends SnomedComponent.Fields {
		
		public static final String TYPE = "type";
		public static final String REFERENCED_COMPONENT_ID = SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID;
		public static final String REFSET_ID = "referencedSetId";
		
		public static final Set<String> ALL = ImmutableSet.of(
				// RF2 fields
				ID,
				ACTIVE,
				EFFECTIVE_TIME,
				MODULE_ID,
				REFSET_ID,
				REFERENCED_COMPONENT_ID,
				// special fieldss
				TYPE,
				RELEASED);
		
	}
	
	private SnomedRefSetType type;
	private SnomedCoreComponent referencedComponent;
	private String referenceSetId;
	private Map<String, Object> properties = newHashMap();

	@Override
	public short getTerminologyComponentId() {
		return SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER;
	}
	
	/**
	 * @return the containing reference set's type
	 */
	public SnomedRefSetType type() {
		return type;
	}

	/**
	 * Returns the component referenced by this SNOMED CT Reference Set Member. It includes only the SNOMED CT ID property by default, see
	 * {@link SnomedCoreComponent#getId()}.
	 * 
	 * @return
	 */
	public SnomedCoreComponent getReferencedComponent() {
		return referencedComponent;
	}

	/**
	 * Returns the identifier of the SNOMED CT Reference Set this SNOMED CT Reference Set Member belongs to.
	 * 
	 * @return
	 */
	public String getReferenceSetId() {
		return referenceSetId;
	}

	/**
	 * Returns special properties of the SNOMED CT Reference Set or an empty {@link Map} if none found.
	 * 
	 * @return
	 */
	@JsonIgnore
	public Map<String, Object> getProperties() {
		return properties;
	}
	
	@JsonAnyGetter
	private Map<String, Object> getPropertiesJson() {
		HashMap<String, Object> jsonMap = newHashMap(properties);
		jsonMap.computeIfPresent(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME, (k,v) -> EffectiveTimes.format(v, DateFormats.SHORT, ""));
		jsonMap.computeIfPresent(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME, (k,v) -> EffectiveTimes.format(v, DateFormats.SHORT, ""));
		return jsonMap;
	}
	
	public void setType(SnomedRefSetType type) {
		this.type = type;
	}
	
	public void setReferencedComponent(SnomedCoreComponent referencedComponent) {
		this.referencedComponent = referencedComponent;
	}
	
	public void setReferenceSetId(String referenceSetId) {
		this.referenceSetId = referenceSetId;
	}
	
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
	
	@JsonAnySetter
	private void setPropertiesJson(String key, Object value) {
		switch (key) {
			case SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME: //$FALL-THROUGH$
			case SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME:
				properties.put(key, Strings.isNullOrEmpty((String) value) ? null : EffectiveTimes.parse((String) value, DateFormats.SHORT));
				break;
			default:
				properties.put(key, value);
		}
	}
	
	@Override
	public Request<TransactionContext, String> toCreateRequest(String containerId) {
		return SnomedRequests.prepareNewMember()
				.setId(getId())
				.setActive(isActive())
				.setReferencedComponentId(containerId)
				.setReferenceSetId(getReferenceSetId())
				.setModuleId(getModuleId())
				.setProperties(getProperties())
				.build();
	}
	
	@Override
	public Request<TransactionContext, Boolean> toUpdateRequest() {
		final Map<String, Object> changes = newHashMap(getProperties());
		changes.put(SnomedRf2Headers.FIELD_ACTIVE, isActive());
		changes.put(SnomedRf2Headers.FIELD_MODULE_ID, getModuleId());
		return SnomedRequests.prepareUpdateMember()
				.setMemberId(getId())
				.setSource(changes)
				.build();
	}
	
}
