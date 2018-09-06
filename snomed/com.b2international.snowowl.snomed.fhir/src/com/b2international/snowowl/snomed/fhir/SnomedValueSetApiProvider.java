/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.fhir.core.LogicalId;
import com.b2international.snowowl.fhir.core.codesystems.IdentifierUse;
import com.b2international.snowowl.fhir.core.codesystems.NarrativeStatus;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.dt.Identifier;
import com.b2international.snowowl.fhir.core.model.dt.Narrative;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.valueset.Compose;
import com.b2international.snowowl.fhir.core.model.valueset.Include;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSet;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSet.Builder;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSetFilter;
import com.b2international.snowowl.fhir.core.model.valueset.expansion.Contains;
import com.b2international.snowowl.fhir.core.model.valueset.expansion.Expansion;
import com.b2international.snowowl.fhir.core.model.valueset.expansion.UriParameter;
import com.b2international.snowowl.fhir.core.provider.FhirApiProvider;
import com.b2international.snowowl.fhir.core.provider.IValueSetApiProvider;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRefSetSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.fhir.SnomedUri.QueryPart;
import com.b2international.snowowl.snomed.fhir.SnomedUri.QueryPartDefinition;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

/**
 * Provider for the SNOMED CT FHIR support
 * @since 6.4
 * @see IValueSetApiProvider
 */
public final class SnomedValueSetApiProvider extends FhirApiProvider implements IValueSetApiProvider {

	private static final Set<String> SUPPORTED_URIS = ImmutableSet.of(
		SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME,
		SnomedTerminologyComponentConstants.SNOMED_INT_LINK,
		SnomedUri.SNOMED_BASE_URI_STRING
	);
	
	private String repositoryId;
	
	public SnomedValueSetApiProvider() {
		this.repositoryId = SnomedDatastoreActivator.REPOSITORY_UUID;
	}
	
	@Override
	public Collection<ValueSet> getValueSets() {
		
		//might be nicer to maintain the order by version
		Collection<ValueSet> valueSets = Lists.newArrayList();
		
		//Collect every version on every extension
		List<CodeSystemVersionEntry> codeSystemVersionList = collectCodeSystemVersions(repositoryId);
		
		List<ValueSet> simpleTypevalueSets = collectSimpleTypeRefsets(codeSystemVersionList);
		valueSets.addAll(simpleTypevalueSets);
		
		List<ValueSet> queryTypeVirtualRefsets = collectQueryTypeVirtualRefsets(codeSystemVersionList);
		valueSets.addAll(queryTypeVirtualRefsets);
		
		return valueSets;
	}
	
	@Override
	public ValueSet getValueSet(LogicalId logicalId) {
		
		CodeSystemVersionEntry codeSystemVersion = findCodeSystemVersion(logicalId);
		
		if (!logicalId.isMemberId()) {
		
			return getSimpleTypeRefsetSearchRequestBuilder(logicalId.getComponentId())
				.build(repositoryId, logicalId.getBranchPath())
				.execute(getBus())
				.then(refsets -> {
					return refsets.stream()
						.map(r -> buildSimpleTypeValueSet(r, codeSystemVersion, displayLanguage))
						.map(ValueSet.Builder::build)
						.collect(Collectors.toList());
				})
				.getSync()
				.stream()
				.findFirst()
				.orElseThrow(() -> new NotFoundException("Active value set", logicalId.toString()));
		} else {
			
			return SnomedRequests.prepareSearchMember()
				.one()
				.filterByActive(true)
				.filterById(logicalId.getMemberId())
				.setLocales(ImmutableList.of(ExtendedLocale.valueOf(displayLanguage)))
				.setExpand("referencedComponent(expand(pt()))")
				.build(repositoryId, logicalId.getBranchPath())
				.execute(getBus())
				.then(members -> {
					return members.stream()
						.map(member -> buildQueryTypeValueSet(member, (SnomedConcept) member.getReferencedComponent(), codeSystemVersion, displayLanguage))
						.map(ValueSet.Builder::build)
						.collect(Collectors.toList());
				})
				.getSync()
				.stream()
				.findFirst()
				.orElseThrow(() -> new NotFoundException("No active member found for ", logicalId.toString()));
		}
		
	}

	@Override
	public ValueSet expandValueSet(LogicalId logicalId) {
		
		if (!logicalId.isMemberId()) {
			CodeSystemVersionEntry codeSystemVersion = findCodeSystemVersion(logicalId);
			return buildSimpleTypeRefsetValueSet(logicalId.getComponentId(), codeSystemVersion);
		} 
		else {
			
			CodeSystemVersionEntry codeSystemVersion = findCodeSystemVersion(logicalId);
			
			//Query type reference set member
			return SnomedRequests.prepareSearchMember()
				.one()
				.filterById(logicalId.getMemberId())
				.setLocales(ImmutableList.of(ExtendedLocale.valueOf(displayLanguage)))
				.setExpand("referencedComponent(expand(pt()))")
				.build(repositoryId, logicalId.getBranchPath())
				.execute(getBus())
				.then(members -> {
					return members.stream()
						.map(member -> buildExpandedQueryTypeValueSet(member, (SnomedConcept) member.getReferencedComponent(), codeSystemVersion, displayLanguage))
						.map(ValueSet.Builder::build)
						.collect(Collectors.toList());
				})
				.getSync()
				.stream()
				.findFirst()
				.orElseThrow(() -> new NotFoundException("No active member found for ", logicalId.toString()));
		}
	}
	
	private ValueSet buildSimpleTypeRefsetValueSet(String componentId, CodeSystemVersionEntry codeSystemVersion) {
		
		int all = Integer.MAX_VALUE;
		
		return getSimpleTypeRefsetSearchRequestBuilder(componentId)
			.setExpand("members(expand(referencedComponent(expand(pt()))), limit:"+ all +")")
			.build(repositoryId, codeSystemVersion.getPath())
			.execute(getBus())
			.then(refsets -> {
				return refsets.stream()
					.map(r -> buildExpandedSimpleTypeValueSet(r, codeSystemVersion, displayLanguage))
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
		
		//validateVersion(snomedUri, lookup.getVersion());
		
		CodeSystemVersionEntry codeSystemVersion = getCodeSystemVersion(snomedUri.getVersionTag());
		
		if (snomedUri.hasQueryPart()) {
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
		} else {
			throw new BadRequestException("Query part is missing for value set expansion.", locationName);
		}
	}
	
	private ValueSet buildSimpleTypeRefsetValueSets(CodeSystemVersionEntry codeSystemVersion) {
		
		int all = Integer.MAX_VALUE;
		
		//collect all simple type reference set members
		Set<SnomedConcept> memberConcepts = SnomedRequests.prepareSearchRefSet()
			.filterByActive(true)
			.filterByType(SnomedRefSetType.SIMPLE)
			.filterByReferencedComponentType(SnomedTerminologyComponentConstants.CONCEPT)
			.setLocales(ImmutableList.of(ExtendedLocale.valueOf(displayLanguage)))
			.setExpand("members(expand(referencedComponent(expand(pt()))), limit:"+ all +")")
			.build(repositoryId, codeSystemVersion.getPath())
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
			.date(new Date(codeSystemVersion.getEffectiveDate()))
			.language(displayLanguage)
			.version(codeSystemVersion.getVersionId())
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
				.display(concept.getPt().getTerm())
				.build();
			expansionBuilder.addContains(content);
		}
		
		builder.expansion(expansionBuilder.build());
		return builder.build();		
	}
	
	private ValueSet buildSubsumptionValueSet(String parentConceptId, CodeSystemVersionEntry codeSystemVersion, boolean fetchAll) {
		
		Builder builder = ValueSet.builder(UUID.randomUUID().toString());
		
		SnomedUri uri = SnomedUri.builder().version(codeSystemVersion.getEffectiveDate()).build();
				
		Identifier identifier = Identifier.builder()
			.use(IdentifierUse.OFFICIAL)
			.system(uri.toString())
			//.value("1")
			.build();
		
		builder.status(PublicationStatus.ACTIVE)
			.date(new Date(codeSystemVersion.getEffectiveDate()))
			.language(displayLanguage)
			.version(codeSystemVersion.getVersionId())
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
			.setLocales(ImmutableList.of(ExtendedLocale.valueOf(displayLanguage)))
			.setExpand("pt()");
		
		if (fetchAll) {
			snomedConceptSearchRequestBuilder.all();
		}
		
		SnomedConcepts snomedConcepts  = snomedConceptSearchRequestBuilder.build(repositoryId, codeSystemVersion.getPath())
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
					.display(c.getPt().getTerm())
					.build();
			expansionBuilder.addContains(content);
		});
		
		builder.name(String.format("SNOMED CT concept [ID: %s] and descendants (<<%s)", parentConceptId, parentConceptId))
			.expansion(expansionBuilder.build());
		
		return builder.build();
	}

	private ValueSet.Builder buildExpandedQueryTypeValueSet(SnomedReferenceSetMember refsetMember, SnomedConcept referencedComponent, CodeSystemVersionEntry codeSystemVersion, String displayLanguage) {
		
		LogicalId logicalId = new LogicalId(repositoryId, codeSystemVersion.getPath(), refsetMember.getReferenceSetId(), refsetMember.getId());
		
		Builder builder = createValueSetBuilder(logicalId, refsetMember, codeSystemVersion);

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
			.setLocales(ImmutableList.of(ExtendedLocale.valueOf(displayLanguage)))
			.setExpand("pt()")
			.build(repositoryId, logicalId.getBranchPath())
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
					.display(c.getPt().getTerm())
					.build();
			expansionBuilder.addContains(content);
		});
		
		return builder
			.name(referencedComponent.getPt().getTerm())
			.title(referencedComponent.getPt().getTerm())
			.expansion(expansionBuilder.build());
	}
	
	private ValueSet.Builder buildSimpleTypeValueSet(final SnomedComponent snomedComponent, final CodeSystemVersionEntry codeSystemVersion, final String displayLanguage) {
		
		LogicalId logicalId = new LogicalId(repositoryId, codeSystemVersion.getPath(), snomedComponent.getId());
		
		Builder builder = createValueSetBuilder(logicalId, snomedComponent, codeSystemVersion);
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
		
		return builder.addCompose(compose);
	}
	
	private ValueSet.Builder buildExpandedSimpleTypeValueSet(SnomedReferenceSet referenceSet, CodeSystemVersionEntry codeSystemVersion, String displayLanguage) {
		
		LogicalId logicalId = new LogicalId(repositoryId, codeSystemVersion.getPath(), referenceSet.getId());
		
		Builder builder = createValueSetBuilder(logicalId, referenceSet, codeSystemVersion);
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
			
			SnomedConcept concept = (SnomedConcept) snomedReferenceSetMember.getReferencedComponent();
			
			Contains content = Contains.builder()
					.system(SnomedUri.SNOMED_BASE_URI)
					.code(concept.getId())
					.display(concept.getPt().getTerm())
					.build();
			expansionBuilder.addContains(content);
		}
		return builder.expansion(expansionBuilder.build());
	}
	
	//Collect every version on every extension
	private List<ValueSet> collectSimpleTypeRefsets(List<CodeSystemVersionEntry> codeSystemVersionList) {
		
		List<ValueSet> simpleTypevalueSets = codeSystemVersionList.stream().map(csve -> {
			
			return SnomedRequests.prepareSearchRefSet()
				.all()
				.filterByType(SnomedRefSetType.SIMPLE)
				.filterByReferencedComponentType(SnomedTerminologyComponentConstants.CONCEPT)
				.build(repositoryId, csve.getPath())
				.execute(getBus())
				.then(refsets -> {
					return refsets.stream()
						.map(r -> buildSimpleTypeValueSet(r, csve, displayLanguage))
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
	private List<ValueSet> collectQueryTypeVirtualRefsets(List<CodeSystemVersionEntry> codeSystemVersionList) {
		
		List<ValueSet> simpleTypevalueSets = codeSystemVersionList.stream().map(csve -> {
		
			return SnomedRequests.prepareSearchMember()
				.all()
				.filterByRefSetType(ImmutableList.of(SnomedRefSetType.QUERY))
				.filterByReferencedComponentType(SnomedTerminologyComponentConstants.CONCEPT)
				.filterByActive(true)
				.setLocales(ImmutableList.of(ExtendedLocale.valueOf(displayLanguage)))
				.setExpand("referencedComponent(expand(pt()))")
				.build(repositoryId, csve.getPath())
				.execute(getBus())
				.then(members -> {
					return members.stream()
						.map(member -> buildQueryTypeValueSet(member, (SnomedConcept) member.getReferencedComponent(), csve, displayLanguage))
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
			.setLocales(ImmutableList.of(ExtendedLocale.valueOf(displayLanguage)));
		
		return searchBuilder;
	}
	
	/**
	 * @param snomedComponent 
	 * @param codeSystemVersion 
	 * @return
	 */
	private Builder createValueSetBuilder(LogicalId logicalId, SnomedComponent snomedComponent, CodeSystemVersionEntry codeSystemVersion) {
		
		String referenceSetId = snomedComponent.getId();
		
		Builder builder = ValueSet.builder(logicalId.toString());
		
		//TODO: module needs to be added as well
		SnomedUri uri = SnomedUri.builder().version(codeSystemVersion.getEffectiveDate()).build();
				
		Identifier identifier = Identifier.builder()
			.use(IdentifierUse.OFFICIAL)
			.system(uri.toString())
			.value(referenceSetId)
			.build();
		
		builder
			.status(snomedComponent.isActive() ? PublicationStatus.ACTIVE : PublicationStatus.RETIRED)
			.date(new Date(codeSystemVersion.getEffectiveDate()))
			.language(displayLanguage)
			.version(codeSystemVersion.getVersionId())
			.identifier(identifier)
			.url(uri.toUri());
		
		return builder;
	}
	
	//Common Simple type refset properties
	private void addSimpleTypeProperties(Builder builder, SnomedComponent snomedComponent, CodeSystemVersionEntry codeSystemVersion) {
		
		String referenceSetId = snomedComponent.getId();

		SnomedConcept refsetConcept = SnomedRequests.prepareGetConcept(referenceSetId)
			.setExpand("pt()")
			.setLocales(ImmutableList.of(ExtendedLocale.valueOf(displayLanguage)))
			.build(getRepositoryId(), codeSystemVersion.getPath())
			.execute(getBus())
			.getSync();
		
		builder.name(refsetConcept.getPt().getTerm())
			.title(refsetConcept.getPt().getTerm());
	}


	private ValueSet.Builder buildQueryTypeValueSet(final SnomedReferenceSetMember refsetMember, final SnomedConcept referencedComponent, final CodeSystemVersionEntry codeSystemVersion, final String displayLanguage) {
	
		LogicalId logicalId = new LogicalId(repositoryId, codeSystemVersion.getPath(), refsetMember.getReferenceSetId(), refsetMember.getId());
		
		Builder builder = createValueSetBuilder(logicalId, refsetMember, codeSystemVersion);

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
		
		return builder
			.name(referencedComponent.getPt().getTerm())
			.title(referencedComponent.getPt().getTerm())
			.addCompose(compose);
		
	}
	
	@Override
	protected String getRepositoryId() {
		return repositoryId;
	}
	
	@Override
	public boolean isSupported(LogicalId logicalId) {
		return logicalId.getRepositoryId().startsWith(SnomedDatastoreActivator.REPOSITORY_UUID);
	}
	
	@Override
	public Collection<String> getSupportedURIs() {
		return SUPPORTED_URIS;
	}
	
	@Override
	public final boolean isSupported(String uri) {
		if (Strings.isNullOrEmpty(uri)) return false;
		
		boolean foundInList = getSupportedURIs().stream()
				.filter(uri::equalsIgnoreCase)
				.findAny()
				.isPresent();
			
		//extension and version is part of the URI
		boolean extensionUri = uri.startsWith(SnomedUri.SNOMED_BASE_URI_STRING);
		
		return foundInList || extensionUri;
	}

	protected Uri getFhirUri() {
		return SnomedUri.SNOMED_BASE_URI;
	}

	@Override
	protected String getCodeSystemShortName() {
		return SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME;
	}

}
