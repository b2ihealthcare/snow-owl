/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest.domain;

import java.util.List;
import java.util.Set;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.options.Options;
import com.b2international.commons.options.OptionsBuilder;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.datastore.request.SnomedRefSetMemberSearchRequestBuilder;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.Parameter;

/**
 * @since 6.16
 */
public final class SnomedReferenceSetMemberRestSearch {

	@Parameter(description = "Member UUID(s) to match")
	private Set<String> id;
	
	@Parameter(description="The effective time to match (yyyyMMdd, exact matches only)")
	private String effectiveTime;
	
	@Parameter(description="The status to match")
	private Boolean active;
	
	@Parameter(description="The module identifier to match")
	private String module;
	
	@Parameter(description="The reference set identifier(s) to match, or a single ECL expression")
	private List<String> referenceSet;
	
	@Parameter(description="The referenced component identifier(s) to match")
	private List<String> referencedComponentId;

	// Special RF2 member columns go here
	@Parameter(description="The acceptability identifier(s) to match in case of language refset members")
	private List<String> acceptabilityId;
	
	@Parameter(description="The target component identifier(s) to match in case of association refset members")
	private List<String> targetComponent;
	
	@Parameter(description="The value identifier(s) to match in case of attribute value refset members")
	private List<String> valueId;
	
	@Parameter(description="The correlation identifdescription) to match in case of complex/extended map refset members")
	private List<String> correlationId;
	
	@Parameter(description="The description format identifier(s) to match in case of description format refset members")
	private List<String> descriptionFormat;
	
	@Parameter(description="The characteristic type identifier(s) to match in case of concrete domain refset members")
	private List<String> characteristicTypeId;
	
	@Parameter(description="The attribute type identifier(s) to match in case of concrete domain refset members")
	private List<String> typeId;
	
	@Parameter(description="The map category identifier(s) to match in case of extended refset members")
	private List<String> mapCategoryId;
	
	@Parameter(description="The mrcm domain identifier(s) to match in case of mrcm domain refset members")
	private List<String> domainId;
	
	@Parameter(description="The content type identifier(s) to match in case of mrcm attribute domain and range refset members")
	private List<String> contentTypeId;
	
	@Parameter(description="The rule strength identifier(s) to match in case of mrcm attribute domain and range refset members")
	private List<String> ruleStrengthId;
	
	@Parameter(description="The rule refset identifier(s) to match in case of mrcm module scope refset members")
	private List<String> mrcmRuleRefSetId;
	
	@Parameter(description="The relationship group value(s) to match in case of concrete domain refset members")
	private List<String> relationshipGroup;
	
	@Parameter(description="The map target value(s) to match in case of mapping refset members")
	private List<String> mapTarget;
	
	@Parameter(description="An MRCM rule refset member should be grouped or not")
	private Boolean grouped;
	
	@Parameter(description="Special filters for owlExpression axiom values")
	private SnomedOwlExpressionFilters owlExpression; 
	
	@Parameter(description="The scrollKeepAlive to start a scroll using this query")
	private String scrollKeepAlive;
	
	@Parameter(description="A scrollId to continue scrolling a previous query")
	private String scrollId;

	@Parameter(description="The search key to use for retrieving the next page of results")
	private String searchAfter;

	@Parameter(description="The maximum number of items to return")
	private int limit = 50;
	
	@Parameter(description="Expansion parameters")
	private String expand;
	
	@Parameter(description="Sort keys")
	private List<String> sort;
	
	public Set<String> getId() {
		return id;
	}

	public void setId(Set<String> id) {
		this.id = id;
	}

	public String getEffectiveTime() {
		return effectiveTime;
	}

	public void setEffectiveTime(String effectiveTime) {
		this.effectiveTime = effectiveTime;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public List<String> getReferenceSet() {
		return referenceSet;
	}

	public void setReferenceSet(List<String> referenceSet) {
		this.referenceSet = referenceSet;
	}

	public List<String> getReferencedComponentId() {
		return referencedComponentId;
	}

	public void setReferencedComponentId(List<String> referencedComponentId) {
		this.referencedComponentId = referencedComponentId;
	}

	public List<String> getTargetComponent() {
		return targetComponent;
	}
	
	public void setTargetComponent(List<String> targetComponent) {
		this.targetComponent = targetComponent;
	}
	
	public List<String> getAcceptabilityId() {
		return acceptabilityId;
	}

	public void setAcceptabilityId(List<String> acceptabilityId) {
		this.acceptabilityId = acceptabilityId;
	}

	public List<String> getValueId() {
		return valueId;
	}

	public void setValueId(List<String> valueId) {
		this.valueId = valueId;
	}

	public List<String> getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(List<String> correlationId) {
		this.correlationId = correlationId;
	}

	public List<String> getDescriptionFormat() {
		return descriptionFormat;
	}

	public void setDescriptionFormat(List<String> descriptionFormat) {
		this.descriptionFormat = descriptionFormat;
	}

	public List<String> getCharacteristicTypeId() {
		return characteristicTypeId;
	}

	public void setCharacteristicTypeId(List<String> characteristicTypeId) {
		this.characteristicTypeId = characteristicTypeId;
	}

	public List<String> getTypeId() {
		return typeId;
	}

	public void setTypeId(List<String> typeId) {
		this.typeId = typeId;
	}

	public List<String> getMapCategoryId() {
		return mapCategoryId;
	}

	public void setMapCategoryId(List<String> mapCategoryId) {
		this.mapCategoryId = mapCategoryId;
	}

	public List<String> getDomainId() {
		return domainId;
	}

	public void setDomainId(List<String> domainId) {
		this.domainId = domainId;
	}

	public List<String> getContentTypeId() {
		return contentTypeId;
	}

	public void setContentTypeId(List<String> contentTypeId) {
		this.contentTypeId = contentTypeId;
	}

	public List<String> getRuleStrengthId() {
		return ruleStrengthId;
	}

	public void setRuleStrengthId(List<String> ruleStrengthId) {
		this.ruleStrengthId = ruleStrengthId;
	}

	public List<String> getMrcmRuleRefSetId() {
		return mrcmRuleRefSetId;
	}

	public void setMrcmRuleRefSetId(List<String> mrcmRuleRefSetId) {
		this.mrcmRuleRefSetId = mrcmRuleRefSetId;
	}

	public List<String> getRelationshipGroup() {
		return relationshipGroup;
	}

	public void setRelationshipGroup(List<String> relationshipGroup) {
		this.relationshipGroup = relationshipGroup;
	}

	public List<String> getMapTarget() {
		return mapTarget;
	}

	public void setMapTarget(List<String> mapTarget) {
		this.mapTarget = mapTarget;
	}

	public Boolean getGrouped() {
		return grouped;
	}

	public void setGrouped(Boolean grouped) {
		this.grouped = grouped;
	}

	public SnomedOwlExpressionFilters getOwlExpression() {
		return owlExpression;
	}
	
	public void setOwlExpression(SnomedOwlExpressionFilters owlExpression) {
		this.owlExpression = owlExpression;
	}
	
	public String getScrollKeepAlive() {
		return scrollKeepAlive;
	}

	public void setScrollKeepAlive(String scrollKeepAlive) {
		this.scrollKeepAlive = scrollKeepAlive;
	}

	public String getScrollId() {
		return scrollId;
	}

	public void setScrollId(String scrollId) {
		this.scrollId = scrollId;
	}

	public String getSearchAfter() {
		return searchAfter;
	}

	public void setSearchAfter(String searchAfter) {
		this.searchAfter = searchAfter;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getExpand() {
		return expand;
	}

	public void setExpand(String expand) {
		this.expand = expand;
	}

	public List<String> getSort() {
		return sort;
	}

	public void setSort(List<String> sort) {
		this.sort = sort;
	}

	@JsonIgnore
	public Options toPropsFilter() {
		OptionsBuilder propFilter = Options.builder();
		if (!CompareUtils.isEmpty(getTargetComponent())) {
			propFilter.put(SnomedRf2Headers.FIELD_TARGET_COMPONENT_ID, getTargetComponent());
		}
		if (!CompareUtils.isEmpty(getAcceptabilityId())) {
			propFilter.put(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID, getAcceptabilityId());
		}
		if (!CompareUtils.isEmpty(getValueId())) {
			propFilter.put(SnomedRf2Headers.FIELD_TARGET_COMPONENT_ID, getValueId());
		}
		if (!CompareUtils.isEmpty(getCorrelationId())) {
			propFilter.put(SnomedRf2Headers.FIELD_CORRELATION_ID, getCorrelationId());
		}
		if (!CompareUtils.isEmpty(getDescriptionFormat())) {
			propFilter.put(SnomedRf2Headers.FIELD_DESCRIPTION_FORMAT, getDescriptionFormat());
		}
		if (!CompareUtils.isEmpty(getCharacteristicTypeId())) {
			propFilter.put(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID, getCharacteristicTypeId());
		}
		if (!CompareUtils.isEmpty(getTypeId())) {
			propFilter.put(SnomedRf2Headers.FIELD_TYPE_ID, getTypeId());
		}
		if (!CompareUtils.isEmpty(getMapCategoryId())) {
			propFilter.put(SnomedRf2Headers.FIELD_MAP_CATEGORY_ID, getMapCategoryId());
		}
		if (!CompareUtils.isEmpty(getDomainId())) {
			propFilter.put(SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID, getDomainId());
		}
		if (!CompareUtils.isEmpty(getContentTypeId())) {
			propFilter.put(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, getContentTypeId());
		}
		if (!CompareUtils.isEmpty(getRuleStrengthId())) {
			propFilter.put(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, getRuleStrengthId());
		}
		if (!CompareUtils.isEmpty(getRelationshipGroup())) {
			propFilter.put(SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP, getRelationshipGroup());
		}
		if (!CompareUtils.isEmpty(getMapTarget())) {
			propFilter.put(SnomedRf2Headers.FIELD_MAP_TARGET, getMapTarget());
		}
		if (getGrouped() != null) {
			propFilter.put(SnomedRf2Headers.FIELD_MRCM_GROUPED, getGrouped());
		}
		if (getOwlExpression() != null && !CompareUtils.isEmpty(getOwlExpression().getConceptId())) {
			propFilter.put(SnomedRefSetMemberSearchRequestBuilder.OWL_EXPRESSION_CONCEPTID, getOwlExpression().getConceptId());
		}
		if (getOwlExpression() != null && !CompareUtils.isEmpty(getOwlExpression().getDestinationId())) {
			propFilter.put(SnomedRefSetMemberSearchRequestBuilder.OWL_EXPRESSION_DESTINATIONID, getOwlExpression().getDestinationId());
		}
		if (getOwlExpression() != null && !CompareUtils.isEmpty(getOwlExpression().getTypeId())) {
			propFilter.put(SnomedRefSetMemberSearchRequestBuilder.OWL_EXPRESSION_TYPEID, getOwlExpression().getTypeId());
		}
		if (getOwlExpression() != null && getOwlExpression().getGci() != null) {
			propFilter.put(SnomedRefSetMemberSearchRequestBuilder.OWL_EXPRESSION_GCI, getOwlExpression().getGci());
		}
		return propFilter.build();
	}
	
}
