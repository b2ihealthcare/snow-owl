/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.index.revision.Revision.Fields.ID;
import static com.b2international.snowowl.snomed.common.SnomedConstants.Concepts.CONCEPT_MODEL_DATA_ATTRIBUTE;
import static com.b2international.snowowl.snomed.common.SnomedConstants.Concepts.CONCEPT_MODEL_OBJECT_ATTRIBUTE;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry.Fields.MRCM_RULE_REFSET_ID;
import static com.google.common.base.Strings.nullToEmpty;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;

/**
 * @since 8.7.0
 */
public class MrcmTypeRequest implements Request<BranchContext, SnomedReferenceSetMembers> {
	
	private static final long serialVersionUID = 1L;

	public enum ATTRIBUTE_TYPE {
		DATA,
		OBJECT,
		ALL;
		
		public static ATTRIBUTE_TYPE getByNameIgnoreCase(String name) {
			for (final ATTRIBUTE_TYPE type : values()) {
				if (nullToEmpty(name).equalsIgnoreCase(type.toString())) {
					return type;
				}
			}
			throw new BadRequestException("Unknown attribute type: '%s'", name);
		}
	}
	
	private Set<String> selfIds;
	private Set<String> ruleParentIds; 
	private Set<String> refSetIds;
	private List<String> moduleIds;
	private ATTRIBUTE_TYPE attributeType;
	
	public void setSelfIds(Set<String> selfIds) {
		this.selfIds = selfIds;
	}

	public void setParentIds(Set<String> ruleParentIds) {
		this.ruleParentIds = ruleParentIds;
	}

	public void setRefSetIds(Set<String> refSetIds) {
		this.refSetIds = refSetIds;
	}

	public void setModuleIds(List<String> moduleIds) {
		this.moduleIds = moduleIds;
	}

	public void setAttributeType(ATTRIBUTE_TYPE attributeType) {
		this.attributeType = attributeType;
	}
	
	@Override
	public SnomedReferenceSetMembers execute(BranchContext context) {
		Set<String> inScopeRefSetIds = SnomedRequests.prepareSearchMember()
				.all()
				.filterByActive(true)
				.filterByRefSetType(SnomedRefSetType.MRCM_MODULE_SCOPE)
				.filterByReferencedComponent(moduleIds)
				.setFields(ID, MRCM_RULE_REFSET_ID)
				.build()
				.execute(context)
				.stream()
				.map(m -> (String) m.getProperties().get(SnomedRf2Headers.FIELD_MRCM_RULE_REFSET_ID))
				.collect(Collectors.toSet());
		
		if (inScopeRefSetIds.isEmpty()) {
			return new SnomedReferenceSetMembers(0, 0);
		}
		
		final String eclConstraint;
		
		switch (attributeType) {
		case DATA: 
			eclConstraint = String.format("<%s", CONCEPT_MODEL_DATA_ATTRIBUTE);
			break;
		case OBJECT: 
			eclConstraint = String.format("<%s", CONCEPT_MODEL_OBJECT_ATTRIBUTE);
			break;
		case ALL: 
			eclConstraint = String.format("<%s OR <%s", CONCEPT_MODEL_OBJECT_ATTRIBUTE, CONCEPT_MODEL_DATA_ATTRIBUTE );
			break;
		default: 
			eclConstraint = "*";
		};
		
		final Set<String> typeIds = SnomedRequests.prepareSearchConcept()
			.all()
			.filterByActive(true)
			.filterByEcl(eclConstraint)
			.setFields(SnomedConceptDocument.Fields.ID)
			.build()
			.execute(context)
			.stream()
			.map(SnomedConcept::getId)
			.collect(Collectors.toSet());
		
		if (typeIds.isEmpty()) {
			return new SnomedReferenceSetMembers(0, 0);
		}
		
		Set<String> domainIds = new HashSet<>();
		
		if (selfIds != null) {
			domainIds.addAll(selfIds);
		}
		
		if (ruleParentIds != null) {
			domainIds.addAll(ruleParentIds);
		}
		
		if (refSetIds != null) {
			domainIds.addAll(refSetIds);
		}
		
		SnomedRefSetMemberSearchRequestBuilder requestBuilder = SnomedRequests.prepareSearchMember()
			.all()
			.filterByActive(true)
			.filterByRefSetType(SnomedRefSetType.MRCM_ATTRIBUTE_DOMAIN)
			.filterByRefSet(inScopeRefSetIds)
			.filterByReferencedComponent(typeIds);
		
		if (!domainIds.isEmpty()) {
			requestBuilder.filterByProps(Options.from(Map.of(SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID, domainIds)));	
		}
		
		return requestBuilder.build().execute(context);
	}
	
}
