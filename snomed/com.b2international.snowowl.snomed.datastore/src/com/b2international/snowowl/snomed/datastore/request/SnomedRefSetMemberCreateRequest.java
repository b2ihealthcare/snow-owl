/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import javax.annotation.Nonnull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.ClassUtils;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.BaseRequest;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * @since 4.5
 */
final class SnomedRefSetMemberCreateRequest extends BaseRequest<TransactionContext, String> {

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
	
	String getProperty(String key) {
		return getProperty(key, String.class);
	}
	
	<T> T getProperty(String key, Class<T> valueType) {
		return ClassUtils.checkAndCast(properties.get(key), valueType);
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

	@Override
	protected Class<String> getReturnType() {
		return String.class;
	}
	
	private SnomedRefSet getRefSetIfExists(TransactionContext context) {
		// TODO convert this 404 -> 400 logic into an interceptor one level higher (like all create requests should work the same way)
		try {
			return context.lookup(referenceSetId, SnomedRefSet.class);
		} catch (ComponentNotFoundException e) {
			throw e.toBadRequestException();
		}
	}
	
	@Override
	public String execute(TransactionContext context) {
		SnomedRefSet refSet = getRefSetIfExists(context);
		SnomedRefSetMemberCreateDelegate delegate = getDelegate(refSet.getType());
		return delegate.execute(refSet, context);
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
			default: 
				throw new IllegalStateException("Unexpected reference set type '" + referenceSetType + "'.");
		}
	}
}
