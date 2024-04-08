/*
 * Copyright 2011-2024 B2i Healthcare, https://b2ihealthcare.com
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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.*;

import javax.annotation.Nonnull;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.datastore.converter.SnomedReferenceSetConverter;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import jakarta.validation.constraints.NotEmpty;

/**
 * @since 4.5
 */
final class SnomedRefSetMemberCreateRequest implements SnomedComponentCreateRequest {

	private static final long serialVersionUID = 1L;

	// Not @NotEmpty, it can be populated after the request is validated (eg. auto-generated ids)
	private String id;

	@Nonnull
	private Boolean active = Boolean.TRUE;
	
	@NotEmpty
	private String refsetId;
	
	// Not @NotEmpty, it can be populated after the request is validated (eg. nested create requests)
	private String moduleId;

	// Not @NotEmpty, it can be populated after the request is validated (eg. nested create requests)
	private String referencedComponentId;
	
	private Map<String, Object> properties = new HashMap<>(2);

	SnomedRefSetMemberCreateRequest() { }
	
	String getId() {
		return id;
	}

	@Override
	public Boolean isActive() {
		return active;
	}
	
	@Override
	public String getModuleId() {
		return moduleId;
	}
	
	String getRefsetId() {
		return refsetId;
	}
	
	String getReferencedComponentId() {
		return referencedComponentId;
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
	
	void setReferencedComponentId(String referencedComponentId) {
		this.referencedComponentId = referencedComponentId;
	}
	
	void setRefsetId(String refsetId) {
		this.refsetId = refsetId;
	}
	
	void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
	
	void setActive(Boolean active) {
		this.active = active;
	}
	
	void setProperties(Map<String, Object> properties) {
		this.properties.putAll(properties);
	}

	void setId(String id) {
		this.id = id;
	}
	
	/**
	 * @return the set of core component SCTIDs mentioned in any reference set member property
	 */
	@Override
	public Set<String> getRequiredComponentIds(TransactionContext context) {
		try {
			SnomedReferenceSet refSet = getRefSet(context);
			SnomedRefSetMemberCreateDelegate delegate = getDelegate(refSet.getType());
			ImmutableSet.Builder<String> requiredComponentIds = ImmutableSet.<String>builder().addAll(delegate.getRequiredComponentIds());
			requiredComponentIds.add(refsetId);
			if (!Strings.isNullOrEmpty(referencedComponentId)) {
				requiredComponentIds.add(referencedComponentId);
			}
			if (!Strings.isNullOrEmpty(moduleId)) {
				requiredComponentIds.add(moduleId);
			}
			return requiredComponentIds.build();
		} catch (ComponentNotFoundException e) {
			throw e.toBadRequestException();
		}
	}

	@Override
	public String execute(TransactionContext context) {
		// generate id field as UUID if not defined otherwise
		if (Strings.isNullOrEmpty(id)) {
			id = UUID.randomUUID().toString();
		} else {
			// otherwise validate that it is a valid UUID
			try {
				UUID.fromString(id);
			} catch (IllegalArgumentException e) {
				throw new BadRequestException("Reference set Member uses an illegal non-UUID identifier '%s'.", id);
			}
		}
		
		/* 
		 * TODO: Generalize the logic below: any attempts of retrieving a missing component during component creation
		 * should return a 400 response instead of a 404. 
		 */
		try {
			SnomedReferenceSet refSet = getRefSet(context);
			SnomedRefSetMemberCreateDelegate delegate = getDelegate(refSet.getType());
			return delegate.execute(refSet, context);
		} catch (ComponentNotFoundException e) {
			throw e.toBadRequestException();
		}
	}

	private SnomedReferenceSet getRefSet(TransactionContext context) {
		final SnomedReferenceSet refSet = new SnomedReferenceSetConverter(context, Options.builder().build(), Collections.emptyList()).convert(context.lookup(refsetId, SnomedConceptDocument.class));
		checkArgument(refSet.getType() != null, "Reference Set Properties are missing from identifier concept document: %s.", refSet.getId());
		return refSet;
	}

	private SnomedRefSetMemberCreateDelegate getDelegate(SnomedRefSetType referenceSetType) {
		switch (referenceSetType) {
			case ASSOCIATION:
				return new SnomedAssociationMemberCreateDelegate(this);
			case ATTRIBUTE_VALUE:
				return new SnomedAttributeValueMemberCreateDelegate(this);
			case COMPLEX_MAP:
				return new SnomedComplexMapMemberCreateDelegate(this);
			case COMPLEX_BLOCK_MAP:
				return new SnomedComplexBlockMapMemberCreateDelegate(this);
			case CONCRETE_DATA_TYPE:
				return new SnomedConcreteDomainMemberCreateDelegate(this);
			case DESCRIPTION_TYPE:
				return new SnomedDescriptionTypeMemberCreateDelegate(this);
			case EXTENDED_MAP:
				return new SnomedExtendedMapMemberCreateDelegate(this);
			case LANGUAGE:
				return new SnomedLanguageMemberCreateDelegate(this);
			case MODULE_DEPENDENCY:
				return new SnomedModuleDependencyMemberCreateDelegate(this);
			case QUERY:
				return new SnomedQueryMemberCreateDelegate(this);
			case SIMPLE: 
				return new SnomedSimpleMemberCreateDelegate(this);
			case SIMPLE_MAP:
				return new SnomedSimpleMapMemberCreateDelegate(this);
			case SIMPLE_MAP_TO:
				return new SnomedSimpleMapToMemberCreateDelegate(this);
			case SIMPLE_MAP_WITH_DESCRIPTION:
				return new SnomedSimpleMapMemberWithDescriptionCreateDelegate(this);
			case OWL_AXIOM:
			case OWL_ONTOLOGY:
				return new SnomedOWLExpressionMemberCreateDelegate(this, referenceSetType);
			case MRCM_DOMAIN:
				return new SnomedMRCMDomainMemberCreateDelegate(this);
			case MRCM_ATTRIBUTE_DOMAIN:
				return new SnomedMRCMAttributeDomainMemberCreateDelegate(this);
			case MRCM_ATTRIBUTE_RANGE:
				return new SnomedMRCMAttributeRangeMemberCreateDelegate(this);
			case MRCM_MODULE_SCOPE:
				return new SnomedMRCMModuleScopeMemberCreateDelegate(this);
			default: 
				throw new IllegalStateException("Unexpected reference set type '" + referenceSetType + "'.");
		}
	}

}
