/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Strings;

/**
 * @since 5.0
 */
abstract class SnomedRefSetMemberCreateDelegate {

	private final SnomedRefSetMemberCreateRequest request;

	protected SnomedRefSetMemberCreateDelegate(SnomedRefSetMemberCreateRequest request) {
		this.request = request;
	}

	Boolean isActive() {
		return request.isActive();
	}

	String getModuleId() {
		return request.getModuleId();
	}

	String getReferenceSetId() {
		return request.getReferenceSetId();
	}

	String getReferencedComponentId() {
		return request.getReferencedComponentId();
	}

	boolean hasProperty(String key) {
		return request.hasProperty(key);
	}

	String getProperty(String key) {
		return request.getProperty(key);
	}

	<T> T getProperty(String key, Class<T> valueType) {
		return request.getProperty(key, valueType);
	}

	void setReferencedComponentId(String referencedComponentId) {
		request.setReferencedComponentId(referencedComponentId);
	}

	protected void checkRefSetType(SnomedRefSet refSet, SnomedRefSetType expectedType) {
		if (!expectedType.equals(refSet.getType())) {
			throw new BadRequestException("Reference set '%s' is of type '%s', expected type '%s'.", refSet.getIdentifierId(), refSet.getType(), expectedType);
		}
	}

	protected void checkReferencedComponentId(SnomedRefSet refSet) {
		if (Strings.isNullOrEmpty(getReferencedComponentId())) {
			throw new BadRequestException("'%s' cannot be null or empty for '%s' type reference sets.", SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID, refSet.getType());
		}

		// XXX referenced component ID for query type reference set cannot be defined, validate only if defined
		// TODO support other terminologies when enabling mappings
		SnomedIdentifiers.validate(getReferencedComponentId());

		short refSetReferencedComponentType = refSet.getReferencedComponentType();
		if (CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT != refSetReferencedComponentType) {
			short referencedComponentType = SnomedTerminologyComponentConstants.getTerminologyComponentIdValue(getReferencedComponentId());
			if (refSetReferencedComponentType != referencedComponentType) {
				String expectedType = SnomedTerminologyComponentConstants.getId(referencedComponentType);
				String actualType = SnomedTerminologyComponentConstants.getId(refSetReferencedComponentType);
				throw new BadRequestException("'%s' reference set can't reference '%s | %s' component. Only '%s' components are allowed.", 
						refSet.getIdentifierId(), 
						getReferencedComponentId(), 
						expectedType, 
						actualType);
			}
		}
	}

	protected void checkHasProperty(SnomedRefSet refSet, String key) {
		if (!hasProperty(key)) {
			throw new BadRequestException("Property '%s' must be set for '%s' reference set members.", key, refSet.getIdentifierId());
		}
	}

	protected void checkNonEmptyProperty(SnomedRefSet refSet, String key) {
		checkHasProperty(refSet, key);
		if (CompareUtils.isEmpty(getProperty(key, Object.class))) {
			throw new BadRequestException("Property '%s' may not be null or empty for '%s' reference set members.", key, refSet.getIdentifierId());
		}
	}

	abstract String execute(SnomedRefSet refSet, TransactionContext context);
}
