/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.fhir;

import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.commons.exceptions.NotImplementedException;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.codesystem.CodeSystemVersion;
import com.b2international.snowowl.core.plugin.Component;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.fhir.core.codesystems.IdentifierUse;
import com.b2international.snowowl.fhir.core.codesystems.NarrativeStatus;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.ValidateCodeResult;
import com.b2international.snowowl.fhir.core.model.dt.Identifier;
import com.b2international.snowowl.fhir.core.model.dt.Narrative;
import com.b2international.snowowl.fhir.core.model.valueset.Compose;
import com.b2international.snowowl.fhir.core.model.valueset.ExpandValueSetRequest;
import com.b2international.snowowl.fhir.core.model.valueset.Include;
import com.b2international.snowowl.fhir.core.model.valueset.ValidateCodeRequest;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSet;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSet.Builder;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSetFilter;
import com.b2international.snowowl.fhir.core.model.valueset.expansion.Contains;
import com.b2international.snowowl.fhir.core.model.valueset.expansion.Expansion;
import com.b2international.snowowl.fhir.core.model.valueset.expansion.UriParameter;
import com.b2international.snowowl.fhir.core.provider.IValueSetApiProvider;
import com.b2international.snowowl.fhir.core.search.FhirParameter.PrefixedValue;
import com.b2international.snowowl.fhir.core.search.FhirSearchParameter;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSets;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRefSetMemberSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRefSetSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.fhir.SnomedUri.QueryPart;
import com.b2international.snowowl.snomed.fhir.SnomedUri.QueryPartDefinition;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Provider for the SNOMED CT FHIR Value Set support
 * 
 * @since 6.4
 * @see IValueSetApiProvider
 */
public final class SnomedValueSetApiProvider extends SnomedFhirApiProvider implements IValueSetApiProvider {

	@Component
	public static final class Factory implements IValueSetApiProvider.Factory {
		
		@Override
		public IValueSetApiProvider create(IEventBus bus, List<ExtendedLocale> locales) {
			return new SnomedValueSetApiProvider(bus, locales);
		}
		
	}
	
	public SnomedValueSetApiProvider(IEventBus bus, List<ExtendedLocale> locales) {
		super(bus, locales);
	}
	
	@Override
	public boolean isSupported(ComponentURI componentURI) {
		return componentURI.codeSystem().startsWith(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);
	}

	@Override
	public Collection<ValueSet> getValueSets(final Set<FhirSearchParameter> searchParameters) {
		
		//might be nicer to maintain the order by version
		Collection<ValueSet> valueSets = Lists.newArrayList();
		
		//Collect every version on every extension
		List<CodeSystemVersion> codeSystemVersionList = collectCodeSystemVersions(repositoryId);
		
		List<ValueSet> simpleTypevalueSets = collectSimpleTypeRefsets(codeSystemVersionList, searchParameters);
		valueSets.addAll(simpleTypevalueSets);
		
		List<ValueSet> queryTypeVirtualRefsets = collectQueryTypeVirtualRefsets(codeSystemVersionList, searchParameters);
		valueSets.addAll(queryTypeVirtualRefsets);
		
		return valueSets;
	}
	
	@Override
	public ValueSet getValueSet(ComponentURI componentURI) {
		
		CodeSystemVersion codeSystemVersion = findCodeSystemVersion(componentURI, "ValueSet.id");
		
		//Simple type reference set
		if (componentURI.terminologyComponentId()!= SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER) {
		
			return getSimpleTypeRefsetSearchRequestBuilder(componentURI.identifier())
				.build(componentURI.codeSystemUri())
				.execute(getBus())
				.then(refsets -> {
					return refsets.stream()
						.map(r -> buildSimpleTypeValueSet(r, codeSystemVersion, getLocales()))
						.map(ValueSet.Builder::build)
						.collect(Collectors.toList());
				})
				.getSync()
				.stream()
				.findFirst()
				.orElseThrow(() -> new NotFoundException("Active value set", componentURI.toString()));
		} else {
			
			//Query type reference set
			return SnomedRequests.prepareSearchMember()
				.one()
				.filterByRefSetType(Sets.newHashSet(SnomedRefSetType.QUERY))
				.filterByActive(true)
				.filterById(componentURI.identifier())
				.setLocales(getLocales())
				.setExpand("referencedComponent(expand(pt()))")
				.build(componentURI.codeSystemUri())
				.execute(getBus())
				.then(members -> {
					return members.stream()
						.map(member -> buildQueryTypeValueSet(member, (SnomedConcept) member.getReferencedComponent(), codeSystemVersion, getLocales()))
						.map(ValueSet.Builder::build)
						.collect(Collectors.toList());
				})
				.getSync()
				.stream()
				.findFirst()
				.orElseThrow(() -> new NotFoundException("Active member", componentURI.toString()));
		}
		
	}

	@Override
	public ValueSet expandValueSet(ComponentURI componentURI) {
		
		CodeSystemVersion codeSystemVersion = findCodeSystemVersion(componentURI, "ValueSet.id");

		if (componentURI.terminologyComponentId()!= SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER) {
			return buildSimpleTypeRefsetValueSet(componentURI.identifier(), codeSystemVersion);
		} 
		else {
			//Query type reference set member
			return SnomedRequests.prepareSearchMember()
				.one()
				.filterById(componentURI.identifier())
				.filterByRefSetType(Sets.newHashSet(SnomedRefSetType.QUERY))
				.setLocales(getLocales())
				.setExpand("referencedComponent(expand(pt()))")
				.build(componentURI.codeSystemUri())
				.execute(getBus())
				.then(members -> {
					return members.stream()
						.map(member -> buildExpandedQueryTypeValueSet(member, (SnomedConcept) member.getReferencedComponent(), codeSystemVersion, getLocales()))
						.map(ValueSet.Builder::build)
						.collect(Collectors.toList());
				})
				.getSync()
				.stream()
				.findFirst()
				.orElseThrow(() -> new NotFoundException("No active member found for ", componentURI.toString()));
		}
	}
	
	private ValueSet buildSimpleTypeRefsetValueSet(String componentId, CodeSystemVersion codeSystemVersion) {
		
		int all = Integer.MAX_VALUE;
		
		return getSimpleTypeRefsetSearchRequestBuilder(componentId)
			.setExpand("members(expand(referencedComponent(expand(pt()))), limit:"+ all +")")
			.build(codeSystemVersion.getUri())
			.execute(getBus())
			.then(refsets -> {
				return refsets.stream()
					.map(r -> buildExpandedSimpleTypeValueSet(r, codeSystemVersion, getLocales()))
					.map(ValueSet.Builder::build)
					.collect(Collectors.toList());
			})
			.getSync()
			.stream()
			.findFirst()
			.orElseThrow(() -> new NotFoundException("Active value set", codeSystemVersion.getPath() + "/" + componentId));
	}
	
	/*
	 * Implicit value set expansion
	 */
	//url=http://snomed.info/sct?fhir_vs=isa/SCT_ID for SNOMED CT
	@Override
	public ValueSet expandValueSet(String uriString) {
		
		String locationName = "$expand.url";
		SnomedUri snomedUri = SnomedUri.fromUriString(uriString, locationName);
		
		CodeSystemVersion codeSystemVersion = getCodeSystemVersion(snomedUri.getVersionTag());
		
		if (!snomedUri.hasQueryPart()) {
			throw new BadRequestException("Query part is missing for value set expansion.", locationName);
		} else {
			QueryPart queryPart = snomedUri.getQueryPart();
			if (!queryPart.isValueSetQuery()) {
				throw new BadRequestException(String.format("Invalid query part '%s' for value sets.", queryPart.getQueryParameter()), locationName);
			} else {
				QueryPartDefinition queryPartDefinition = queryPart.getQueryPartDefinition();
				switch (queryPartDefinition) {
				
				case NONE:
					//Entire SNOMED CT, makes no real sense
					return buildSubsumptionValueSet(Concepts.ROOT_CONCEPT, codeSystemVersion, false);
				case REFSET:
					return buildSimpleTypeRefsetValueSet(queryPart.getQueryValue(), codeSystemVersion);
				case REFSETS:
					//All simple type refsets
					return buildSimpleTypeRefsetValueSets(codeSystemVersion);
				case ISA:
					return buildSubsumptionValueSet(queryPart.getQueryValue(), codeSystemVersion, true);
				default:
					//should not happen
					throw new BadRequestException("Unknown query part definition '" + queryPartDefinition + "'.", locationName);
				}
			}
		}
	}
	
	@Override
	public ValueSet expandValueSet(ExpandValueSetRequest request) {
		//same as the GET url parameter
		if (request.getValueSet() == null) {
			return expandValueSet(request.getUrl().getUriValue());
		}
		
		//valueset is sent for expansion
		ValueSet valueSet = request.getValueSet();
		Compose compose = valueSet.getCompose();
		if (compose == null) {
			throw new BadRequestException("Compose is null or empty. Nothing to expand", "$expand.valueSet.compose[]");
		}
		
		Collection<Include> includes = compose.getIncludes();
		for (Include include : includes) {
			Collection<ValueSetFilter> filters = include.getFilters();
			for (ValueSetFilter valueSetFilter : filters) {
				//TODO:
			}
		}
		
		throw new NotImplementedException();
	}
	
	/*
	 * Implicit value set validation
	 */
	//url=http://snomed.info/sct?fhir_vs=isa/SCT_ID for SNOMED CT
	@Override
	public ValidateCodeResult validateCode(ValidateCodeRequest validateCodeRequest) {
		
		String locationName = "$validate-code.url";
		if (validateCodeRequest.getUrl() == null) {
			throw new BadRequestException("URL is missing for value set.", locationName);
		}
		
		SnomedUri snomedUri = SnomedUri.fromUriString(validateCodeRequest.getUrl().getUriValue(), locationName);
		CodeSystemVersion codeSystemVersion = getCodeSystemVersion(snomedUri.getVersionTag());
		String componentId = validateCodeRequest.getCode();

		if (!snomedUri.hasQueryPart()) {
			throw new BadRequestException("Query part is missing for value set code validation.", locationName);
		} 
		
		QueryPart queryPart = snomedUri.getQueryPart();
		
		if (!queryPart.isValueSetQuery()) {
			throw new BadRequestException(String.format("Invalid query part '%s' for value sets.", queryPart.getQueryParameter()), locationName);
		} 
		
		QueryPartDefinition queryPartDefinition = queryPart.getQueryPartDefinition();
		
		switch (queryPartDefinition) {
		
		//Entire SNOMED CT as a value set
		case NONE:
			Optional<SnomedConcept> validatedConceptOptional = SnomedRequests.prepareSearchConcept()
				.filterById(componentId)
				.filterByActive(true)
				.setLocales(getLocales())
				.setExpand("pt()")
				.build(codeSystemVersion.getUri())
				.execute(getBus())
				.getSync()
				.first();
				
			if (!validatedConceptOptional.isPresent()) {
				return ValidateCodeResult.builder().valueSetMemberNotFoundResult(snomedUri.toString(), componentId, "<<SNOMED CT").build();
			}
			return ValidateCodeResult.builder().okResult(getPreferredTermOrId(validatedConceptOptional.get())).build();
		
		//Single refset - Simple type only for now
		case REFSET:
			
			//reference set should exist
			try {
				SnomedConcept snomedConcept = SnomedRequests.prepareGetConcept(queryPart.getQueryValue())
					.setLocales(getLocales())
					.setExpand("pt()")
					.build(codeSystemVersion.getUri())
					.execute(getBus())
					.getSync();
				
				if (!snomedConcept.isActive()) {
					return ValidateCodeResult.builder()
						.result(false)
						.message("Reference set is inactive [" + snomedUri + "]")
						.display(snomedConcept.getPt().getTerm()).build();
				}
			} catch (NotFoundException nfex) {
				return ValidateCodeResult.builder().artefactNotFoundResult(snomedUri.toString()).build();
			}
			
			//refset is valid, is there an active member?
			Optional<SnomedReferenceSetMember> optionalMember = SnomedRequests.prepareSearchMember()
				.one()
				.filterByRefSetType(ImmutableList.of(SnomedRefSetType.SIMPLE))
				.filterByRefSet(queryPart.getQueryValue())
				.filterByReferencedComponentType(SnomedTerminologyComponentConstants.CONCEPT)
				.filterByActive(true)
				.filterByReferencedComponent(componentId)
				.setLocales(getLocales())
				.setExpand("referencedComponent(expand(pt()))")
				.build(codeSystemVersion.getUri())
				.execute(getBus())
				.getSync()
				.first();
			
			if (!optionalMember.isPresent()) {
				return ValidateCodeResult.builder().valueSetMemberNotFoundResult(snomedUri.toString(), componentId, queryPart.getQueryValue()).build();
			}
			SnomedConcept concept = (SnomedConcept) optionalMember.get().getReferencedComponent();
			return ValidateCodeResult.builder().okResult(concept.getPt().getTerm()).build();
		
		//All simple type reference sets
		case REFSETS:
			//All simple type refsets
			Optional<SnomedReferenceSetMember> optionalRefsetMember = SnomedRequests.prepareSearchMember()
				.one()
				.filterByRefSetType(ImmutableList.of(SnomedRefSetType.SIMPLE))
				.filterByReferencedComponentType(SnomedTerminologyComponentConstants.CONCEPT)
				.filterByActive(true)
				.filterByReferencedComponent(componentId)
				.setLocales(getLocales())
				.setExpand("referencedComponent(expand(pt()))")
				.build(codeSystemVersion.getUri())
				.execute(getBus())
				.getSync()
				.first();
		
			if (!optionalRefsetMember.isPresent()) {
				return ValidateCodeResult.builder().valueSetMemberNotFoundResult(snomedUri.toString(), componentId, "All simple type reference sets").build();
			}
			SnomedConcept snomedConcept = (SnomedConcept) optionalRefsetMember.get().getReferencedComponent();
			return ValidateCodeResult.builder().okResult(snomedConcept.getPt().getTerm()).build();
		
		//<<conceptId
		case ISA:
			//evaluate the ECL expression
			Optional<SnomedConcept> foundConceptOptional = SnomedRequests.prepareSearchConcept()
				.all()
				.filterByEcl("<<" + queryPart.getQueryValue())
				.filterById(componentId)
				.filterByActive(true)
				.setLocales(getLocales())
				.setExpand("pt()")
				.build(codeSystemVersion.getUri())
				.execute(getBus())
				.getSync()
				.first();
			
			if (!foundConceptOptional.isPresent()) {
				return ValidateCodeResult.builder().valueSetMemberNotFoundResult(snomedUri.toString(), componentId, "<<" + componentId).build();
			}
			return ValidateCodeResult.builder().okResult(foundConceptOptional.get().getPt().getTerm()).build();
			
		default:
			//should not happen
			throw new BadRequestException("Unknown query part definition '" + queryPartDefinition + "'.", locationName);
		}
		
	}
	
	@Override
	public ValidateCodeResult validateCode(ValidateCodeRequest validateCodeRequest, ComponentURI componentURI) {
		
		CodeSystemVersion codeSystemVersion = findCodeSystemVersion(componentURI, "ValueSet.id");
		
		//simple type reference
		if (componentURI.terminologyComponentId()!= SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER) {
			return validateSimpleTypReferenceSet(componentURI, codeSystemVersion.getParentBranchPath(), validateCodeRequest);
		} 
		
		//query type refset
		else {
			//Query type reference set member
			Optional<SnomedReferenceSetMember> optionalRefsetMember = SnomedRequests.prepareSearchMember()
				.one()
				.filterById(componentURI.identifier())
				.filterByActive(true)
				.filterByRefSetType(Sets.newHashSet(SnomedRefSetType.QUERY))
				.setLocales(getLocales())
				.setExpand("referencedComponent(expand(pt()))")
				.build(componentURI.codeSystemUri())
				.execute(getBus())
				.getSync()
				.stream()
				.findFirst();
			
			if (!optionalRefsetMember.isPresent()) {
				throw new NotFoundException("Reference set member", componentURI.identifier());
			}
			
			SnomedReferenceSetMember referenceSetMember = optionalRefsetMember.get();
			String eclExpression = (String) referenceSetMember.getProperties().get(SnomedRf2Headers.FIELD_QUERY);
			
			//evaluate the ECL expression
			SnomedConcepts snomedConcepts = SnomedRequests.prepareSearchConcept()
				.all()
				.filterByEcl(eclExpression)
				.filterByActive(true)
				.setLocales(getLocales())
				.setExpand("pt()")
				.build(componentURI.codeSystemUri())
				.execute(getBus())
				.getSync();
			
			
			String componentId = validateCodeRequest.getCode();
			Optional<SnomedConcept> optionalConcept = snomedConcepts.stream().filter(c -> c.getId().equals(componentId)).findFirst();
			if (!optionalConcept.isPresent()) {
				return ValidateCodeResult.builder()
					.valueSetMemberNotFoundResult(validateCodeRequest.getSystem(), validateCodeRequest.getCode(), componentURI.toString())
					.build();
			}
			
			SnomedConcept concept = optionalConcept.get();
			if (!concept.isActive()) {
				return ValidateCodeResult.builder().result(false).message("Active members is pointing to an inactive concept.").display(getPreferredTermOrId(concept)).build();
			}
		}

		//All good
		return ValidateCodeResult.builder().okResult(validateCodeRequest.getCode()).build();
	}
	
	/**
	 * @param validateCodeRequest 
	 * @return
	 */
	private ValidateCodeResult validateSimpleTypReferenceSet(ComponentURI componentURI, String branchPath, ValidateCodeRequest validateCodeRequest) {
		
		String componentId = validateCodeRequest.getCode();
		String codeSystem = validateCodeRequest.getSystem();
		
		//only SNOMED CT components can be present in a SNOMED CT reference set
		if (!codeSystem.startsWith(SnomedUri.SNOMED_BASE_URI_STRING)) {
			return ValidateCodeResult.builder()
					.result(false)
					.message(String.format("SNOMED CT reference sets can reference SNOMED CT components only. Referenced component is %s.", codeSystem))
					.build();
		}
		
		//fetch the simple type refset
		String refsetId = componentURI.identifier();
		SnomedReferenceSets snomedReferenceSets = SnomedRequests.prepareSearchRefSet()
			.filterByActive(true)
			.filterById(refsetId)
			.filterByType(SnomedRefSetType.SIMPLE)
			.setLocales(getLocales())
			.setExpand("members(expand(referencedComponent(expand(pt()))), limit:"+ Integer.MAX_VALUE +")")
			.build(componentURI.codeSystemUri())
			.execute(getBus())
			.getSync();
		
		//Reference set not found
		if (!snomedReferenceSets.first().isPresent()) {
			throw new NotFoundException("Reference set", refsetId);
		} 
		
		//Refset is found, how about the concept as as member?
		Optional<SnomedReferenceSetMember> refsetMemberOptional = snomedReferenceSets.first().get().getMembers().stream()
				.filter(m-> m.getReferencedComponent().getId().equals(componentId)).findFirst();
		
		if (!refsetMemberOptional.isPresent()) {
			return ValidateCodeResult.builder().valueSetMemberNotFoundResult(codeSystem, componentId, refsetId).build();
		}
		
		SnomedReferenceSetMember snomedReferenceSetMember = refsetMemberOptional.get();
		SnomedConcept concept = (SnomedConcept) snomedReferenceSetMember.getReferencedComponent();
		if (snomedReferenceSetMember.isActive() && !snomedReferenceSetMember.getReferencedComponent().isActive()) {
			return ValidateCodeResult.builder().result(true).message("Active members is pointing to an inactive concept.").display(getPreferredTermOrId(concept)).build();
		}
		return ValidateCodeResult.builder().okResult(concept.getPt().getTerm()).build();
	}
	
	private ValueSet buildSimpleTypeRefsetValueSets(CodeSystemVersion codeSystemVersion) {
		
		int all = Integer.MAX_VALUE;
		
		//collect all simple type reference set members
		Set<SnomedConcept> memberConcepts = SnomedRequests.prepareSearchRefSet()
			.filterByActive(true)
			.filterByType(SnomedRefSetType.SIMPLE)
			.filterByReferencedComponentType(SnomedTerminologyComponentConstants.CONCEPT)
			.setLocales(getLocales())
			.setExpand("members(expand(referencedComponent(expand(pt()))), limit:"+ all +")")
			.build(codeSystemVersion.getUri())
			.execute(getBus())
			
			//collect the members
			.then(refsets -> {
				return refsets.stream()
						
						//get the members
						.map(r -> r.getMembers())
						
						//convert to membersList
						.map(ms -> ms.getItems())
						
						//flatten the nested collections
						.flatMap(Collection::stream)
						
						//get the referenced components
						.map(member -> member.getReferencedComponent())

						//convert to referenced concepts
						.map(SnomedConcept.class::cast).collect(Collectors.toSet());
			})
			.getSync();
		
		Builder builder = ValueSet.builder(UUID.randomUUID().toString());
		
		//TODO: module needs to be added as well
		SnomedUri uri = SnomedUri.builder().version(codeSystemVersion.getEffectiveDate()).build();
				
		Identifier identifier = Identifier.builder()
			.use(IdentifierUse.OFFICIAL)
			.system(uri.toString())
			//.value(referenceSetId)
			.build();
		
		builder.status(PublicationStatus.ACTIVE)
			.date(Date.from(codeSystemVersion.getEffectiveTime().atStartOfDay().toInstant(ZoneOffset.UTC)))
			.language(getLocales().get(0).getLanguageTag())
			.version(codeSystemVersion.getVersion())
			.identifier(identifier)
			.url(uri.toUri())
			.name("SNOMED CT all simple type reference sets");
		
		builder.text(Narrative.builder()
			.status(NarrativeStatus.GENERATED)
			.div("<div>This is the Value Set representation of all the active simple type SNOMED CT reference sets, requested by the SNOMED CT URI query part (?refset) .</div>")
			.build());
		
		com.b2international.snowowl.fhir.core.model.valueset.expansion.Expansion.Builder expansionBuilder = Expansion.builder()
				.identifier("1")
				.timestamp(new Date())
				.total(memberConcepts.size())
				.addParameter(UriParameter.builder()
					.name("version")
					.value(uri.toUri())
					.build());
		
		for (SnomedConcept concept : memberConcepts) {
			
			Contains content = Contains.builder()
				.system(SnomedUri.SNOMED_BASE_URI)
				.code(concept.getId())
				.display(getPreferredTermOrId(concept))
				.build();
			expansionBuilder.addContains(content);
		}
		
		builder.expansion(expansionBuilder.build());
		return builder.build();		
	}
	
	private ValueSet buildSubsumptionValueSet(String parentConceptId, CodeSystemVersion codeSystemVersion, boolean fetchAll) {
		
		Builder builder = ValueSet.builder(UUID.randomUUID().toString());
		
		SnomedUri uri = SnomedUri.builder().version(codeSystemVersion.getEffectiveDate()).build();
				
		Identifier identifier = Identifier.builder()
			.use(IdentifierUse.OFFICIAL)
			.system(uri.toString())
			//.value("1")
			.build();
		
		builder.status(PublicationStatus.ACTIVE)
			.date(Date.from(codeSystemVersion.getEffectiveTime().atStartOfDay().toInstant(ZoneOffset.UTC)))
			.language(getLocales().get(0).getLanguageTag())
			.version(codeSystemVersion.getVersion())
			.identifier(identifier)
			.url(uri.toUri());
		
		String narrativeText = String.format("<div>This is the Value Set representation of the SNOMED CT concept [%s] and its decendants, requested by the SNOMED CT URI query part (isa) .</div>", parentConceptId);
		
		builder.text(Narrative.builder()
			.status(NarrativeStatus.GENERATED)
			.div(narrativeText)
			.build());
		
		//evaluate the ECL expression
		SnomedConceptSearchRequestBuilder snomedConceptSearchRequestBuilder = SnomedRequests.prepareSearchConcept()
			.filterByEcl("<<" + parentConceptId)
			.filterByActive(true)
			.setLocales(getLocales())
			.setExpand("pt()");
		
		if (fetchAll) {
			snomedConceptSearchRequestBuilder.all();
		}
		
		SnomedConcepts snomedConcepts  = snomedConceptSearchRequestBuilder.build(codeSystemVersion.getUri())
			.execute(getBus())
			.getSync();
		
		com.b2international.snowowl.fhir.core.model.valueset.expansion.Expansion.Builder expansionBuilder = Expansion.builder()
				.identifier("1")
				.timestamp(new Date())
				.total(snomedConcepts.getTotal())
				.addParameter(UriParameter.builder()
					.name("version")
					.value(uri.toUri())
					.build());
		
		snomedConcepts.forEach(c -> {
			
			Contains content = Contains.builder()
					.system(SnomedUri.SNOMED_BASE_URI)
					.code(c.getId())
					.display(getPreferredTermOrId(c))
					.build();
			expansionBuilder.addContains(content);
		});
		
		builder.name(String.format("SNOMED CT concept [ID: %s] and descendants (<<%s)", parentConceptId, parentConceptId))
			.expansion(expansionBuilder.build());
		
		return builder.build();
	}

	private ValueSet.Builder buildExpandedQueryTypeValueSet(SnomedReferenceSetMember refsetMember, SnomedConcept referencedComponent, CodeSystemVersion codeSystemVersion, List<ExtendedLocale> locales) {
		
		ComponentURI componentURI = ComponentURI.of(codeSystemVersion.getUri(), SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER, refsetMember.getId());
		
		Builder builder = createValueSetBuilder(componentURI, refsetMember, codeSystemVersion);

		String narrativeText = String.format("<div>This is the Value Set representation of the reference set member [%s] from the Query Type Reference Set [%s].</div>", refsetMember.getId(), refsetMember.getReferenceSetId());
		
		builder.text(Narrative.builder()
			.status(NarrativeStatus.GENERATED)
			.div(narrativeText)
			.build());
		
		String eclExpression = (String) refsetMember.getProperties().get(SnomedRf2Headers.FIELD_QUERY);

		//evaluate the ECL expression
		SnomedConcepts snomedConcepts = SnomedRequests.prepareSearchConcept()
			.all()
			.filterByEcl(eclExpression)
			.filterByActive(true)
			.setLocales(locales)
			.setExpand("pt()")
			.build(codeSystemVersion.getUri())
			.execute(getBus())
			.getSync();
		
		SnomedUri uri = SnomedUri.builder().version(codeSystemVersion.getEffectiveDate()).build();
		
		com.b2international.snowowl.fhir.core.model.valueset.expansion.Expansion.Builder expansionBuilder = Expansion.builder()
				.identifier(refsetMember.getId())
				.timestamp(new Date())
				.total(snomedConcepts.getTotal())
				.addParameter(UriParameter.builder()
					.name("version")
					.value(uri.toUri())
					.build());
		
		snomedConcepts.forEach(c -> {
			
			Contains content = Contains.builder()
					.system(SnomedUri.SNOMED_BASE_URI)
					.code(c.getId())
					.display(getPreferredTermOrId(c))
					.build();
			expansionBuilder.addContains(content);
		});
		
		String pt = getPreferredTermOrId(referencedComponent);
		return builder
			.name(pt)
			.title(pt)
			.expansion(expansionBuilder.build());
	}
	
	private ValueSet.Builder buildSimpleTypeValueSet(final SnomedComponent snomedComponent, final CodeSystemVersion codeSystemVersion, final List<ExtendedLocale> locales) {
		
		ComponentURI componentURI = ComponentURI.of(codeSystemVersion.getUri(), SnomedTerminologyComponentConstants.REFSET_NUMBER, snomedComponent.getId());
		
		Builder builder = createValueSetBuilder(componentURI, snomedComponent, codeSystemVersion);
		addSimpleTypeProperties(builder, snomedComponent, codeSystemVersion);
		
		Include include = Include.builder()
			.system(SnomedUri.SNOMED_BASE_URI_STRING)
			.addFilters(ValueSetFilter.builder()
				.refsetExpression(snomedComponent.getId())
				.build())
			.build();
		
		Compose compose = Compose.builder()
			.addInclude(include)
			.build();
		
		return builder.compose(compose);
	}
	
	private ValueSet.Builder buildExpandedSimpleTypeValueSet(SnomedReferenceSet referenceSet, CodeSystemVersion codeSystemVersion, final List<ExtendedLocale> locales) {
		
		ComponentURI componentURI = ComponentURI.of(codeSystemVersion.getUri(), SnomedTerminologyComponentConstants.REFSET_NUMBER, referenceSet.getId());
		
		Builder builder = createValueSetBuilder(componentURI, referenceSet, codeSystemVersion);
		addSimpleTypeProperties(builder, referenceSet, codeSystemVersion);
		
		SnomedReferenceSetMembers members = referenceSet.getMembers();
		
		SnomedUri uri = SnomedUri.builder().version(codeSystemVersion.getEffectiveDate()).build();
		
		com.b2international.snowowl.fhir.core.model.valueset.expansion.Expansion.Builder expansionBuilder = Expansion.builder()
			.identifier(referenceSet.getId())
			.timestamp(new Date())
			.total(members.getTotal())
			.addParameter(UriParameter.builder()
				.name("version")
				.value(uri.toUri())
				.build());
			
		for (SnomedReferenceSetMember snomedReferenceSetMember : members) {
			
			//skip inactive members
			if (!snomedReferenceSetMember.isActive()) continue;
			
			SnomedConcept concept = (SnomedConcept) snomedReferenceSetMember.getReferencedComponent();
			
			Contains content = Contains.builder()
					.system(SnomedUri.SNOMED_BASE_URI)
					.code(concept.getId())
					.display(getPreferredTermOrId(concept))
					.build();
			expansionBuilder.addContains(content);
		}
		return builder.expansion(expansionBuilder.build());
	}
	
	//Collect every version on every extension
	private List<ValueSet> collectSimpleTypeRefsets(List<CodeSystemVersion> codeSystemVersionList, Set<FhirSearchParameter> searchParameters) {
		
		Optional<FhirSearchParameter> idParamOptional = getSearchParam(searchParameters, "_id");
		Optional<FhirSearchParameter> nameOptional = getSearchParam(searchParameters, "_name"); 

		List<ValueSet> simpleTypevalueSets = codeSystemVersionList.stream().map(csve -> {
			
			SnomedRefSetSearchRequestBuilder requestBuilder = SnomedRequests.prepareSearchRefSet().all();
			
			if (idParamOptional.isPresent()) {
				Collection<String> uris = idParamOptional.get().getValues().stream()
						.map(PrefixedValue::getValue)
						.collect(Collectors.toSet());
				
				Collection<String> ids = collectIds(uris);
				
				requestBuilder.filterByIds(ids);
			}
			
			//TODO - referenced component name?
			if (nameOptional.isPresent()) {
				Collection<String> names = nameOptional.get().getValues().stream()
						.map(PrefixedValue::getValue)
						.collect(Collectors.toSet());
				//requestBuilder.filterByNameExact(names);SNOMEDCT/FHIR_SIMPLE_TYPE_REFSET_VERSION/103/11000154102
			}
			
			return requestBuilder
				.filterByType(SnomedRefSetType.SIMPLE)
				.filterByReferencedComponentType(SnomedTerminologyComponentConstants.CONCEPT)
				.build(csve.getUri())
				.execute(getBus())
				.then(refsets -> {
					return refsets.stream()
						.map(r -> buildSimpleTypeValueSet(r, csve, getLocales()))
						.map(ValueSet.Builder::build)
						.collect(Collectors.toList());
				})
				.getSync();
				
		}).collect(Collectors.toList())
			.stream().flatMap(List::stream).collect(Collectors.toList()); //List<List<?> -> List<?>
		
		return simpleTypevalueSets;
	}
	
	/*
	 * In SNOMED CT for each member of a Query Type Reference set 'creates' a Simple Type Reference Set,
	 * hence we create a FHIR value set for each member - even if these are not persisted within Snow Owl.
	 * We assign their member id as part of the logical id
	 * Collect every version on every extension
	 */
	private List<ValueSet> collectQueryTypeVirtualRefsets(List<CodeSystemVersion> codeSystemVersionList, Set<FhirSearchParameter> searchParameters) {
		
		Optional<FhirSearchParameter> idParamOptional = getSearchParam(searchParameters, "_id");
		Optional<FhirSearchParameter> nameOptional = getSearchParam(searchParameters, "_name"); 
		
		List<ValueSet> simpleTypevalueSets = codeSystemVersionList.stream().map(csve -> {
		
			SnomedRefSetMemberSearchRequestBuilder requestBuilder = SnomedRequests.prepareSearchMember().all();
			
			if (idParamOptional.isPresent()) {
				Collection<String> uris = idParamOptional.get().getValues().stream()
						.map(PrefixedValue::getValue)
						.collect(Collectors.toSet());
				
				Collection<String> ids = collectIds(uris);
				requestBuilder.filterByIds(ids);
			}
			
			//TODO - referenced component name?
			if (nameOptional.isPresent()) {
				Collection<String> names = nameOptional.get().getValues().stream()
						.map(PrefixedValue::getValue)
						.collect(Collectors.toSet());
				//requestBuilder.filterByNameExact(names);
			}
			
			return requestBuilder
				.filterByRefSetType(ImmutableList.of(SnomedRefSetType.QUERY))
				.filterByReferencedComponentType(SnomedTerminologyComponentConstants.CONCEPT)
				.filterByActive(true)
				.setLocales(getLocales())
				.setExpand("referencedComponent(expand(pt()))")
				.build(csve.getUri())
				.execute(getBus())
				.then(members -> {
					return members.stream()
						.map(member -> buildQueryTypeValueSet(member, (SnomedConcept) member.getReferencedComponent(), csve, getLocales()))
						.map(ValueSet.Builder::build)
						.collect(Collectors.toList());
					
				})
			.getSync();
		
		}).collect(Collectors.toList())
			.stream().flatMap(List::stream).collect(Collectors.toList()); //List<List<?> -> List<?>
				
			return simpleTypevalueSets;
	}
	
	/**
	 * @return
	 */
	private SnomedRefSetSearchRequestBuilder getSimpleTypeRefsetSearchRequestBuilder(String componentId) {
		
		SnomedRefSetSearchRequestBuilder searchBuilder = SnomedRequests.prepareSearchRefSet()
			.filterByActive(true)
			.filterById(componentId)
			.filterByType(SnomedRefSetType.SIMPLE)
			.setLocales(getLocales());
		
		return searchBuilder;
	}
	
	private Builder createValueSetBuilder(ComponentURI componentURI, SnomedComponent snomedComponent, CodeSystemVersion codeSystemVersion) {
		
		//Refset | Member
		String refsetComponentId = snomedComponent.getId();
		
		Builder builder = ValueSet.builder(componentURI.toString());
		
		//TODO: module needs to be added as well
		SnomedUri uri = SnomedUri.builder().version(codeSystemVersion.getEffectiveDate()).build();
				
		Identifier identifier = Identifier.builder()
			.use(IdentifierUse.OFFICIAL)
			.system(uri.toString())
			.value(refsetComponentId)
			.build();
		
		builder
			.status(snomedComponent.isActive() ? PublicationStatus.ACTIVE : PublicationStatus.RETIRED)
			.date(Date.from(codeSystemVersion.getEffectiveTime().atStartOfDay(ZoneOffset.UTC).toInstant()))
			.language(getLocales().get(0).getLanguageTag())
			.version(codeSystemVersion.getVersion())
			.identifier(identifier)
			.url(uri.toUri());
		
		return builder;
	}
	
	//Common Simple type refset properties
	private void addSimpleTypeProperties(Builder builder, SnomedComponent snomedComponent, CodeSystemVersion codeSystemVersion) {
		
		String referenceSetId = snomedComponent.getId();

		SnomedConcept refsetConcept = SnomedRequests.prepareGetConcept(referenceSetId)
			.setExpand("pt()")
			.setLocales(getLocales())
			.build(codeSystemVersion.getUri())
			.execute(getBus())
			.getSync();
		
		String pt = getPreferredTermOrId(refsetConcept);
		builder.name(pt).title(pt);
	}


	private ValueSet.Builder buildQueryTypeValueSet(final SnomedReferenceSetMember refsetMember, final SnomedConcept referencedComponent, final CodeSystemVersion codeSystemVersion, final List<ExtendedLocale> locales) {
	
		ComponentURI componentURI = ComponentURI.of(codeSystemVersion.getUri(), SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER, refsetMember.getId());
		
		Builder builder = createValueSetBuilder(componentURI, refsetMember, codeSystemVersion);

		String narrativeText = String.format("<div>This is the Value Set representation of the reference set member [%s] from the Query Type Reference Set [%s].</div>", refsetMember.getId(), refsetMember.getReferenceSetId());
		
		builder.text(Narrative.builder()
			.status(NarrativeStatus.GENERATED)
			.div(narrativeText)
			.build());
		
		String eclExpression = (String) refsetMember.getProperties().get(SnomedRf2Headers.FIELD_QUERY);
		
		Include include = Include.builder()
			.system(SnomedUri.SNOMED_BASE_URI_STRING)
			.addFilters(ValueSetFilter.builder()
				.eclExpression(eclExpression)
				.build())
			.build();
			
		Compose compose = Compose.builder()
			.addInclude(include)
			.build();
		
		String pt = getPreferredTermOrId(referencedComponent);
		return builder
			.name(pt)
			.title(pt)
			.compose(compose);
	}
	
}
