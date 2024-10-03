/*
 * Copyright 2022-2024 B2i Healthcare, https://b2ihealthcare.com
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
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry.Fields.MRCM_RULE_REFSET_ID;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.commons.options.Options;
import com.b2international.snomed.ecl.Ecl;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.MrcmAttributeType;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.datastore.config.SnomedMrcmConfig;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;

/**
 * @since 8.8.0
 */
final class SnomedMrcmTypeRequest extends SearchResourceRequest<BranchContext, SnomedReferenceSetMembers> {
	
	private static final long serialVersionUID = 1L;
	
	private Set<String> selfIds;
	private Set<String> ruleParentIds; 
	private Set<String> refSetIds;
	private List<String> moduleIds;
	private MrcmAttributeType attributeType;
	
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

	public void setAttributeType(MrcmAttributeType attributeType) {
		this.attributeType = attributeType;
	}
	
	@Override
	public SnomedReferenceSetMembers doExecute(BranchContext context) {
		Set<String> inScopeRefSetIds = SnomedRequests.prepareSearchMember()
				.filterByActive(true)
				.filterByRefSetType(SnomedRefSetType.MRCM_MODULE_SCOPE)
				.filterByReferencedComponent(moduleIds)
				.setFields(ID, MRCM_RULE_REFSET_ID)
				.stream(context)
				.flatMap(members -> members.stream())
				.map(m -> (String) m.getProperties().get(SnomedRf2Headers.FIELD_MRCM_RULE_REFSET_ID))
				.collect(Collectors.toSet());
		
		if (inScopeRefSetIds.isEmpty()) {
			return new SnomedReferenceSetMembers(0, 0);
		}
		
		final String eclConstraint;
		
		SnomedMrcmConfig mrcmConfiguration = ApplicationContext.getInstance()
			.getServiceChecked(SnowOwlConfiguration.class)
			.getModuleConfig(SnomedCoreConfiguration.class)
			.getMrcmConfiguration();
		
		String allowedDataAttributesExpression = mrcmConfiguration.getAllowedDataAttributesExpression();
		String allowedObjectAttributesExpression = mrcmConfiguration.getAllowedObjectAttributesExpression();
		
		switch (attributeType) {
		case DATA: 
			eclConstraint = allowedDataAttributesExpression;
			break;
		case OBJECT: 
			eclConstraint = allowedObjectAttributesExpression;
			break;
		case ALL: 
			eclConstraint = Ecl.or(allowedDataAttributesExpression, allowedObjectAttributesExpression);
			break;
		default: 
			eclConstraint = Ecl.ANY;
		};
		
		final Set<String> typeIds = SnomedRequests.prepareSearchConcept()
			.filterByActive(true)
			.filterByEcl(eclConstraint)
			.setFields(SnomedConceptDocument.Fields.ID)
			.stream(context)
			.flatMap(concepts -> concepts.stream())
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
			.filterByActive(true)
			.filterByRefSetType(SnomedRefSetType.MRCM_ATTRIBUTE_DOMAIN)
			.filterByRefSet(inScopeRefSetIds)
			.filterByReferencedComponent(typeIds);
		
		if (!domainIds.isEmpty()) {
			requestBuilder.filterByProps(Options.from(Map.of(SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID, domainIds)));	
		}
		
		return requestBuilder
				.setLimit(limit())
				.setSearchAfter(searchAfter())
				.build()
				.execute(context);
	}

	@Override
	protected SnomedReferenceSetMembers createEmptyResult(int limit) {
		return new SnomedReferenceSetMembers(limit, 0);
	}
	
}
