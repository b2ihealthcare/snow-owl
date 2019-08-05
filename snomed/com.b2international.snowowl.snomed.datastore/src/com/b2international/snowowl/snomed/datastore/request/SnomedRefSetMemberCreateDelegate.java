/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Set;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.core.terminology.TerminologyRegistry;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

/**
 * @since 5.0
 */
abstract class SnomedRefSetMemberCreateDelegate {

	private final SnomedRefSetMemberCreateRequest request;

	protected SnomedRefSetMemberCreateDelegate(SnomedRefSetMemberCreateRequest request) {
		this.request = request;
	}
	
	String getId() {
		return request.getId();
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
	
	String getComponentId(String key) {
		return request.getComponentId(key);
	}

	<T> T getProperty(String key, Class<T> valueType) {
		return request.getProperty(key, valueType);
	}

	void setReferencedComponentId(String referencedComponentId) {
		request.setReferencedComponentId(referencedComponentId);
	}

	protected final void checkRefSetType(SnomedReferenceSet refSet, SnomedRefSetType expectedType) {
		if (!expectedType.equals(refSet.getType())) {
			throw new BadRequestException("Reference set '%s' is of type '%s', expected type '%s'.", refSet.getId(), refSet.getType(), expectedType);
		}
	}

	protected final void checkReferencedComponent(SnomedReferenceSet refSet) {
		if (Strings.isNullOrEmpty(getReferencedComponentId())) {
			throw new BadRequestException("'%s' cannot be null or empty for '%s' type reference sets.", SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID, refSet.getType());
		}

		// XXX referenced component ID for query type reference set cannot be defined, validate only if defined
		SnomedIdentifiers.validate(getReferencedComponentId());

		String expectedReferencedComponentType = refSet.getReferencedComponentType();
		if (!Strings.isNullOrEmpty(expectedReferencedComponentType) && !TerminologyRegistry.UNSPECIFIED.equals(expectedReferencedComponentType)) {
			String actualReferencedComponentType = SnomedTerminologyComponentConstants.getTerminologyComponentId(getReferencedComponentId());
			if (!expectedReferencedComponentType.equals(actualReferencedComponentType)) {
				throw new BadRequestException("'%s' reference set can't reference '%s | %s' component. Only '%s' components are allowed.", 
						refSet.getId(), 
						getReferencedComponentId(), 
						actualReferencedComponentType,
						expectedReferencedComponentType);
			}
		}
	}

	protected final void checkComponentExists(SnomedReferenceSet refSet, TransactionContext context, String key) {
		checkComponentExists(refSet, context, key, getComponentId(key));
	}

	protected final void checkComponentExists(SnomedReferenceSet refSet, TransactionContext context, String key, String componentId) {
		short referencedComponentType = SnomedTerminologyComponentConstants.getTerminologyComponentIdValue(componentId);

		try {

			switch (referencedComponentType) {
			case SnomedTerminologyComponentConstants.CONCEPT_NUMBER:
				context.lookup(componentId, SnomedConceptDocument.class);
				break;
			case SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER:
				context.lookup(componentId, SnomedDescriptionIndexEntry.class);
				break;
			case SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER:
				context.lookup(componentId, SnomedRelationshipIndexEntry.class);
				break;
			default:
				throw new BadRequestException("Property '%s' must be an identifier for a core component for '%s' reference set members.", key, refSet.getId());
			}

		} catch (ComponentNotFoundException e) {
			throw e.toBadRequestException();
		}
	}

	protected final void checkHasProperty(String key) {
		if (!hasProperty(key)) {
			throw new BadRequestException("Property '%s' must be set for '%s' reference set members.", key, request.getReferenceSetId());
		}
	}

	protected final void checkNonEmptyProperty(String key) {
		checkHasProperty(key);
		if (CompareUtils.isEmpty(getProperty(key, Object.class))) {
			throw new BadRequestException("Property '%s' may not be null or empty for '%s' reference set members.", key, request.getReferenceSetId());
		}
	}

	/**
	 * Subclasses may override to return additional required component IDs, like special IDs that are required to execute the delegate properly. 
	 * Common reference set properties (like moduleId and referencedComponentId) can be excluded, they are already checked externally.
	 * @return
	 */
	protected Set<String> getRequiredComponentIds() {
		return ImmutableSet.of();
	}

	abstract String execute(SnomedReferenceSet refSet, TransactionContext context);
}
