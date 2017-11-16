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
package com.b2international.snowowl.snomed.validation;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.PageableCollectionResource;
import com.b2international.snowowl.core.validation.eval.ValidationRuleEvaluator;
import com.b2international.snowowl.core.validation.rule.ValidationRule;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedComponentSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRefSetMemberSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRelationshipSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.request.SnomedSearchRequestBuilder;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

/**
 * @since 6.0
 */
public final class SnomedQueryValidationRuleEvaluator implements ValidationRuleEvaluator {

	private static final TypeReference<SnomedComponentValidationQuery<?, PageableCollectionResource<SnomedComponent>, SnomedComponent>> TYPE_REF = new TypeReference<SnomedComponentValidationQuery<?, PageableCollectionResource<SnomedComponent>, SnomedComponent>>() {};

	@Override
	public List<ComponentIdentifier> eval(BranchContext context, ValidationRule rule) throws Exception {
		checkArgument(type().equals(rule.getType()), "'%s' is not recognizable by this evaluator (accepts: %s)", rule, type());
		return context.service(ObjectMapper.class)
				.<SnomedComponentValidationQuery<?, PageableCollectionResource<SnomedComponent>, SnomedComponent>>readValue(rule.getImplementation(), TYPE_REF)
				.prepareSearch()
				.all() // always return all hits
				.setFields(SnomedComponentDocument.Fields.ID)
				.build()
				.execute(context)
				.stream()
				.map(SnomedComponent::getComponentIdentifier)
				.collect(Collectors.toList());
	}

	@Override
	public String type() {
		return "snomed-query";
	}
	
	@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="componentType")
	@JsonSubTypes({
		@JsonSubTypes.Type(name="concept", value=SnomedConceptValidationRuleQuery.class),
		@JsonSubTypes.Type(name="description", value=SnomedDescriptionValidationRuleQuery.class),
		@JsonSubTypes.Type(name="relationship", value=SnomedRelationshipValidationRuleQuery.class),
		@JsonSubTypes.Type(name="member", value=SnomedMemberValidationRuleQuery.class)
	})
	//<SB extends SnomedSearchRequestBuilder<SB, R>, R extends PageableCollectionResource<T>, T extends IComponent> SB
	private static abstract class SnomedComponentValidationQuery<SB extends SnomedSearchRequestBuilder<SB, R>, R extends PageableCollectionResource<T>, T extends SnomedComponent> {
		
		@JsonProperty private Boolean active;
		@JsonProperty private String effectiveTime;
		@JsonProperty private String module;

		public final SB prepareSearch() {
			return prepareSearch(createSearch());
		}

		protected abstract SB createSearch();

		@OverridingMethodsMustInvokeSuper
		protected SB prepareSearch(SB req) {
			return req.filterByActive(active)
					.filterByModule(module)
					.filterByEffectiveTime(effectiveTime);
		}		
	}
	
	private static abstract class SnomedCoreComponentValidationQuery<SB extends SnomedComponentSearchRequestBuilder<SB, R>, R extends PageableCollectionResource<T>, T extends SnomedCoreComponent> extends SnomedComponentValidationQuery<SB, R, T> {
		
		@JsonProperty private List<String> namespace;
		
		@Override
		protected SB prepareSearch(SB req) {
			return super.prepareSearch(req)
					.filterByNamespaces(namespace);
		}
		
	}
	
	private static final class SnomedConceptValidationRuleQuery extends SnomedCoreComponentValidationQuery<SnomedConceptSearchRequestBuilder, SnomedConcepts, SnomedConcept> {
		
		@JsonProperty private String ecl;
		@JsonProperty private String definitionStatus;
		
		@Override
		protected SnomedConceptSearchRequestBuilder createSearch() {
			return SnomedRequests.prepareSearchConcept();
		}
		
		@Override
		protected SnomedConceptSearchRequestBuilder prepareSearch(SnomedConceptSearchRequestBuilder req) {
			return super.prepareSearch(req)
					.filterByDefinitionStatus(definitionStatus)
					.filterByEcl(ecl);
		}
		
	}
	
	private static final class SnomedDescriptionValidationRuleQuery extends SnomedCoreComponentValidationQuery<SnomedDescriptionSearchRequestBuilder, SnomedDescriptions, SnomedDescription> {
		
		private static final Pattern REGEX = Pattern.compile("regex\\((.*)\\)");
		
		@JsonProperty private String term;
		@JsonProperty private String concept;
		@JsonProperty private String type;
		@JsonProperty private String caseSignificance;
		
		@Override
		protected SnomedDescriptionSearchRequestBuilder createSearch() {
			return SnomedRequests.prepareSearchDescription();
		}
		
		@Override
		protected SnomedDescriptionSearchRequestBuilder prepareSearch(SnomedDescriptionSearchRequestBuilder req) {
			if (!Strings.isNullOrEmpty(term)) {
				Matcher matcher = REGEX.matcher(term.trim());
				if (matcher.matches()) {
					req.filterByTermRegex(matcher.group(1));
				}
			}
			return super.prepareSearch(req)
					.filterByType(type)
					.filterByConcept(concept)
					.filterByCaseSignificance(caseSignificance);
		}
		
	}
	
	private static final class SnomedRelationshipValidationRuleQuery extends SnomedCoreComponentValidationQuery<SnomedRelationshipSearchRequestBuilder, SnomedRelationships, SnomedRelationship> {
		
		@JsonProperty private String source;
		@JsonProperty private String type;
		@JsonProperty private String destination;
		@JsonProperty private String characteristicType;
		@JsonProperty private String modifier;
		@JsonProperty private Integer groupMin;
		@JsonProperty private Integer groupMax;
		
		@Override
		protected SnomedRelationshipSearchRequestBuilder createSearch() {
			return SnomedRequests.prepareSearchRelationship();
		}
		
		@Override
		protected SnomedRelationshipSearchRequestBuilder prepareSearch(SnomedRelationshipSearchRequestBuilder req) {
			return super.prepareSearch(req)
					.filterByCharacteristicType(characteristicType)
					.filterBySource(source)
					.filterByType(type)
					.filterByDestination(destination)
					.filterByDestination(destination)
					.filterByGroup(groupMin, groupMax);
		}
		
	}
	
	private static final class SnomedMemberValidationRuleQuery extends SnomedComponentValidationQuery<SnomedRefSetMemberSearchRequestBuilder, SnomedReferenceSetMembers, SnomedReferenceSetMember> {

		@JsonProperty private String refSet;
		@JsonProperty private List<SnomedRefSetType> refSetType;

		@Override
		protected SnomedRefSetMemberSearchRequestBuilder createSearch() {
			return SnomedRequests.prepareSearchMember();
		}
		
		@Override
		protected SnomedRefSetMemberSearchRequestBuilder prepareSearch(SnomedRefSetMemberSearchRequestBuilder req) {
			return super.prepareSearch(req)
					.filterByRefSet(refSet)
					.filterByRefSetType(refSetType);
		}
		
	}
	
}
