/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.domain;

import java.util.Set;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

/**
 * @since 4.6
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "id", visible = true)
@JsonTypeIdResolver(SnomedCoreComponent.SnomedComponentCategoryResolver.class)
public abstract class SnomedCoreComponent extends SnomedComponent {

	private static final long serialVersionUID = 1L;

	/**
	 * @since 6.16
	 */
	public static abstract class Expand extends SnomedComponent.Expand {
		public static final String INACTIVATION_PROPERTIES = "inactivationProperties";
		public static final String REFERRING_MEMBERS = "members";
	}
	
	/**
	 * @since 6.16
	 */
	public static abstract class Fields extends SnomedComponent.Fields {
	}
	
	/**
	 * The preferred association targets for the concept inactivation indicators in the SNOMED CT International Edition.
	 * <p>
	 * 	Note that "Pending move" is present for completeness, however the process requires the concept to stay active, and so can not be handled by
	 * 	component inactivation correctly at this time.
	 * </p>
	 * 
	 * @since 7.4
	 */
	public static final Multimap<String, String> PREFERRED_INDICATOR_ASSOCIATION_TARGETS = ImmutableMultimap.<String, String>builder()
			.put(Concepts.DUPLICATE, Concepts.REFSET_SAME_AS_ASSOCIATION)
			.put(Concepts.OUTDATED, Concepts.REFSET_REPLACED_BY_ASSOCIATION)
			.put(Concepts.AMBIGUOUS, Concepts.REFSET_POSSIBLY_EQUIVALENT_TO_ASSOCIATION)
			.put(Concepts.ERRONEOUS, Concepts.REFSET_REPLACED_BY_ASSOCIATION)
			.put(Concepts.LIMITED, Concepts.REFSET_WAS_A_ASSOCIATION)
			.put(Concepts.MOVED_ELSEWHERE, Concepts.REFSET_MOVED_TO_ASSOCIATION)
			.put(Concepts.PENDING_MOVE, Concepts.REFSET_MOVED_TO_ASSOCIATION)
			.put(Concepts.NOT_SEMANTICALLY_EQUIVALENT, Concepts.REFSET_REFERS_TO_ASSOCIATION)
			.put(Concepts.NONCONFORMANCE_TO_EDITORIAL_POLICY, "") // No historical association reference set
			.build();
	
	private InactivationProperties inactivationProperties;
	private SnomedReferenceSetMembers members;
	private Set<String> memberOf;
	private Set<String> activeMemberOf;

	/**
	 * Returns the available inactivation properties of this concept. Requires expansion parameter 'inactivationProperties()' in read requests.
	 * 
	 * @return the expanded inactivation properties or <code>null</code> if expansion was not requested 
	 */
	public InactivationProperties getInactivationProperties() {
		return inactivationProperties;
	}
	
	/**
	 * Returns the expanded reference set members if any, otherwise it returns a <code>null</code> {@link SnomedReferenceSetMembers}.
	 * @return
	 */
	public SnomedReferenceSetMembers getMembers() {
		return members;
	}
	
	/**
	 * @return a sorted {@link Set} of reference set IDs where this SNOMED CT component has at least one member (regardless of status)
	 */
	public Set<String> getMemberOf() {
		return memberOf;
	}
	
	/**
	 * @return a sorted {@link Set} of reference set IDs where this SNOMED CT component has at least one _active_ member
	 */
	public Set<String> getActiveMemberOf() {
		return activeMemberOf;
	}

	public void setInactivationProperties(InactivationProperties inactivationProperties) {
		this.inactivationProperties = inactivationProperties;
	}
	
	public void setMembers(SnomedReferenceSetMembers members) {
		this.members = members;
	}
	
	public void setMemberOf(Set<String> memberOf) {
		this.memberOf = memberOf;
	}
	
	public void setActiveMemberOf(Set<String> activeMemberOf) {
		this.activeMemberOf = activeMemberOf;
	}
	
	/**
	 * @since 5.5
	 */
	static class SnomedComponentCategoryResolver implements TypeIdResolver {

		private JavaType baseType;

		@Override
		public void init(JavaType bt) {
			baseType = bt;
		}
		
		@Override
		public Id getMechanism() {
			return Id.CUSTOM;
		}

		@Override
		public String idFromValue(Object value) {
			return idFromValueAndType(value, value.getClass());
		}

		@Override
		public String idFromBaseType() {
			throw new UnsupportedOperationException();
		}
		
	    @Override
	    public String idFromValueAndType(Object value, Class<?> clazz) {
	    	if (value instanceof SnomedCoreComponent) {
	    		return ((SnomedCoreComponent) value).getId();
	    	}
	    	throw new IllegalArgumentException("Unsupported value: " + value);
	    }

	    @Override
	    public String getDescForKnownTypeIds() {
	    	return null;
	    }
	    
	    @Override
		public JavaType typeFromId(DatabindContext context, String type) {
	    	final Class<? extends SnomedCoreComponent> clazz = getCoreComponentClass(type);
	    	return TypeFactory.defaultInstance().constructSpecializedType(baseType, clazz);
	    }

	}

	/**
	 * Creates an empty {@link SnomedCoreComponent} subclass instance based on the componentId. Only the {@link SnomedComponent#getId()} will be set
	 * on the returned instance.
	 * 
	 * @param componentId
	 * @return
	 */
	public static SnomedCoreComponent create(String componentId) {
		try {
			return getCoreComponentClass(componentId).getConstructor(String.class).newInstance(componentId);
		} catch (Exception e) {
			throw new SnowowlRuntimeException(e);
		}
	}

	/**
	 * @param componentId
	 * @return the core component class type based on the component category digit in the given componentId
	 */
	public static Class<? extends SnomedCoreComponent> getCoreComponentClass(String componentId) {
		switch (SnomedIdentifiers.getComponentCategory(componentId)) {
		case CONCEPT:
			return SnomedConcept.class;
		case DESCRIPTION:
			return SnomedDescription.class;
		case RELATIONSHIP:
			return SnomedRelationship.class;
		default:
			throw new IllegalArgumentException("Unknown core component type: " + componentId);
		}
	}
	
}
