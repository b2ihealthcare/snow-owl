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
package com.b2international.snowowl.snomed.datastore.server.request;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.ClassUtils;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.BaseRequest;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Strings;

/**
 * @since 4.5
 */
public class SnomedRefSetMemberCreateRequest extends BaseRequest<TransactionContext, String> {

	private static final String REFSET_DESCRIPTION = "refSetDescription";

	@NotEmpty
	private String moduleId;
	
	@NotEmpty
	private String referenceSetId;
	
	private String referencedComponentId;
	
	private Map<String, Object> properties = newHashMap();

	SnomedRefSetMemberCreateRequest() {
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
	
	void setProperties(Map<String, Object> properties) {
		
		this.properties.putAll(properties);
	}
	
	@Override
	public String execute(TransactionContext context) {
		final SnomedReferenceSet refSet;
		// TODO convert this 404 -> 400 logic into an interceptor one level higher (like all create requests should work the same way)
		try {
			refSet = SnomedRequests.prepareGetReferenceSet().setComponentId(referenceSetId).build().execute(context);
		} catch (ComponentNotFoundException e) {
			throw e.toBadRequestException();
		}
		final SnomedRefSetType type = refSet.getType();
		checkInput(refSet, type);
		
		final SnomedRefSetMember member;
		switch (type) {
		case SIMPLE:
			member = SnomedComponents
				.newSimpleMember()
				.withReferencedComponent(referencedComponentId)
				.withModule(moduleId)
				.withRefSet(referenceSetId)
				.addTo(context);
			break;
		case QUERY:
			member = createQueryTypeMember(context);
			break;
		default: throw new UnsupportedOperationException("Not implemented support for creation of '"+type+"' members");
		}
		
		return member.getUuid();
	}

	@Override
	protected Class<String> getReturnType() {
		return String.class;
	}
	
	private String getQuery() {
		return ClassUtils.checkAndCast(properties.get(SnomedRf2Headers.FIELD_QUERY), String.class);
	}
	
	private String getNewRefSetDescription() {
		return ClassUtils.checkAndCast(properties.get(REFSET_DESCRIPTION), String.class);
	}
	
	private void checkInput(final SnomedReferenceSet refSet, final SnomedRefSetType type) {
		RefSetSupport.check(type);
		if (!Strings.isNullOrEmpty(referencedComponentId)) {
			if (SnomedRefSetType.QUERY == type) {
				throw new BadRequestException("'%s' type reference set members can't reference components manually, specify a '%s' property instead.", type, SnomedRf2Headers.FIELD_QUERY);
			}
			// XXX referenced component ID for query type reference set cannot be defined, validate only if defined
			// TODO support other terminologies when enabling mappings
			SnomedIdentifiers.validate(referencedComponentId);
			final String referencedComponentType = SnomedTerminologyComponentConstants.getTerminologyComponentId(referencedComponentId);
			if (!refSet.getReferencedComponentType().equals(referencedComponentType)) {
				throw new BadRequestException("'%s' reference set can't reference '%s | %s' component. Only '%s' components are allowed.", refSet.getId(), referencedComponentId, referencedComponentType, refSet.getReferencedComponentType());
			}
		} else if (SnomedRefSetType.QUERY != type) {
			throw new BadRequestException("'%s' cannot be null or empty for '%s' type reference sets.", SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID, type);
		} else if (!properties.containsKey(SnomedRf2Headers.FIELD_QUERY)) {
			// only QUERY type refset members can have empty refCompIds during creation, but it should have a single additional property called 'query'
			// TODO support refCompIds for query type member as well, it creates the refset with the specified ID (probably required for the thick client as well)
			throw new BadRequestException("'%s' cannot be null or empty for '%s' type reference sets.", SnomedRf2Headers.FIELD_QUERY, type);
		} else {
			// if contains the query, validate
			final String query = getQuery();
			if (Strings.isNullOrEmpty(query)) {
				throw new BadRequestException("'%s' cannot be empty or null", SnomedRf2Headers.FIELD_QUERY);
			}
			final String refSetDescription = getNewRefSetDescription();
			if (Strings.isNullOrEmpty(refSetDescription)) {
				throw new BadRequestException("'%s' cannot be empty or null", REFSET_DESCRIPTION);
			}
		}
	}

	private SnomedRefSetMember createQueryTypeMember(TransactionContext context) {
		// create identifier concept for the new refset
		// TODO can a user change the location of the new query type refset or not
		final SnomedConceptCreateRequestBuilder conceptReq = SnomedRequests
				.prepareNewConcept()
				.setModuleId(moduleId)
				.setParent(Concepts.REFSET_SIMPLE_TYPE);
		
		// TODO acceptability in a refset description
		conceptReq.addDescription(
				SnomedRequests
					.prepareNewDescription()
					.setTerm(getNewRefSetDescription())
					.setModuleId(moduleId)
					.setTypeId(Concepts.FULLY_SPECIFIED_NAME)
					.preferredIn(Concepts.REFSET_LANGUAGE_TYPE_UK));
		
		conceptReq.addDescription(
				SnomedRequests
					.prepareNewDescription()
					.setTerm(getNewRefSetDescription())
					.setModuleId(moduleId)
					.setTypeId(Concepts.SYNONYM)
					.preferredIn(Concepts.REFSET_LANGUAGE_TYPE_UK));
		
		// create new simple type reference set first
		final String memberRefSetId = SnomedRequests
			.prepareNewRefSet()
			.setType(SnomedRefSetType.SIMPLE)
			.setReferencedComponentType(SnomedTerminologyComponentConstants.CONCEPT)
			.setIdentifierConcept(conceptReq)
			.build()
			.execute(context);
		
		// then add all matching members 
		final SnomedConcepts matchingEscgConcepts = SnomedRequests.prepareConceptSearch().filterByEscg(getQuery()).all().build().execute(context);
		for (ISnomedConcept concept : matchingEscgConcepts.getItems()) {
			 SnomedComponents
				.newSimpleMember()
				.withReferencedComponent(concept.getId())
				.withModule(moduleId)
				.withRefSet(memberRefSetId)
				.addTo(context);
		}
		
		return SnomedComponents
			.newQueryMember()
			.withModule(moduleId)
			.withRefSet(referenceSetId)
			.withReferencedComponent(memberRefSetId)
			.withQuery(getQuery())
			.addTo(context);
	}

}
