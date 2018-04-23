/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.ClassUtils;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * @since 4.5
 */
final class SnomedRefSetMemberCreateRequest implements Request<TransactionContext, String> {

	@Nonnull
	private String id;

	@Nonnull
	private Boolean active = Boolean.TRUE;
	
	@NotEmpty
	private String moduleId;
	
	@NotEmpty
	private String referenceSetId;
	
	private String referencedComponentId;
	
	private Map<String, Object> properties = newHashMap();

	SnomedRefSetMemberCreateRequest() {
	}
	
	String getId() {
		return id;
	}

	Boolean isActive() {
		return active;
	}
	
	String getModuleId() {
		return moduleId;
	}
	
	String getReferenceSetId() {
		return referenceSetId;
	}
	
	String getReferencedComponentId() {
		return referencedComponentId;
	}
	
	boolean hasProperty(String key) {
		return properties.containsKey(key);
	}
	
	String getComponentId(String key) {
		Object value = properties.get(key);
		if (value == null) {
			return null;
		} else if (value instanceof Map) {
			return ClassUtils.checkAndCast(((Map<?, ?>) value).get(SnomedRf2Headers.FIELD_ID), String.class);
		} else {
			return ClassUtils.checkAndCast(value, String.class);
		}
	}
	
	String getProperty(String key) {
		return getProperty(key, String.class);
	}
	
	<T> T getProperty(String key, Class<T> valueType) {
		Object value = properties.get(key);
		if (value == null) {
			return null;
		} else {
			return ClassUtils.checkAndCast(value, valueType);
		}
	}
	
	void setReferencedComponentId(String referencedComponentId) {
		this.referencedComponentId = referencedComponentId;
	}
	
	void setReferenceSetId(String referenceSetId) {
		this.referenceSetId = referenceSetId;
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
	public Set<String> getRequiredComponentIds(TransactionContext context) {
		try {
			SnomedRefSet refSet = context.lookup(referenceSetId, SnomedRefSet.class);
			SnomedRefSetMemberCreateDelegate delegate = getDelegate(refSet.getType());
			return delegate.getRequiredComponentIds();
		} catch (ComponentNotFoundException e) {
			throw e.toBadRequestException();
		}
	}

	@Override
	public String execute(TransactionContext context) {
		/* 
		 * TODO: Generalize the logic below: any attempts of retrieving a missing component during component creation
		 * should return a 400 response instead of a 404. 
		 */
		try {
			SnomedRefSet refSet = context.lookup(referenceSetId, SnomedRefSet.class);
			SnomedRefSetMemberCreateDelegate delegate = getDelegate(refSet.getType());
			return delegate.execute(refSet, context);
		} catch (ComponentNotFoundException e) {
			throw e.toBadRequestException();
		}
	}

	private SnomedRefSetMemberCreateDelegate getDelegate(SnomedRefSetType referenceSetType) {
		switch (referenceSetType) {
			case ASSOCIATION:
				return new SnomedAssociationMemberCreateDelegate(this);
			case ATTRIBUTE_VALUE:
				return new SnomedAttributeValueMemberCreateDelegate(this);
			case COMPLEX_MAP:
				return new SnomedComplexMapMemberCreateDelegate(this);
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
			case SIMPLE_MAP_WITH_DESCRIPTION:
				return new SnomedSimpleMapMemberWithDescriptionCreateDelegate(this);
			default: 
				throw new IllegalStateException("Unexpected reference set type '" + referenceSetType + "'.");
		}
	}
}
