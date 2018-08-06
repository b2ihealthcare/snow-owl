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
import java.util.Optional;
import java.util.Set;
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
import com.b2international.snowowl.fhir.core.provider.CodeSystemApiProvider;
import com.b2international.snowowl.fhir.core.provider.FhirApiProvider;
import com.b2international.snowowl.fhir.core.provider.ICodeSystemApiProvider;
import com.b2international.snowowl.fhir.core.provider.IValueSetApiProvider;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

/**
 * Provider for the SNOMED CT FHIR support
 * @since 6.4
 * @see ICodeSystemApiProvider
 * @see CodeSystemApiProvider
 */
public final class SnomedValueSetApiProvider extends FhirApiProvider implements IValueSetApiProvider {

	//private static final String URI_BASE = "http://snomed.info";
	//private static final Uri SNOMED_CT_URI = new Uri(URI_BASE + "/sct");
	//private static final Path SNOMED_INT_PATH = Paths.get(SnomedDatastoreActivator.REPOSITORY_UUID, SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);
	
	private static final Set<String> SUPPORTED_URIS = ImmutableSet.of(
		SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME,
		SnomedTerminologyComponentConstants.SNOMED_INT_LINK,
		SnomedUri.SNOMED_BASE_URI_STRING
	);
	
	private String displayLanguage = "en-us"; //what should we do with this, probably grab it from the request and fall back to a default language
	
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
		
		Optional<CodeSystemVersionEntry> codeSystemOptional = CodeSystemRequests.prepareSearchCodeSystemVersion()
			.one()
			.filterByBranchPath(logicalId.getBranchPath())
			.build(repositoryId)
			.execute(getBus())
			.getSync()
			.first();
		
		CodeSystemVersionEntry codeSystemVersion = codeSystemOptional.orElseThrow(() -> 
			new BadRequestException(String.format("Could not find corresponding version [%s] for value set id [%s].", logicalId.getBranchPath(), logicalId), "ValueSet.id"));
		
		if (!logicalId.isMemberId()) {
		
			return SnomedRequests.prepareSearchRefSet()
				.filterById(logicalId.getComponentId())
				.filterByType(SnomedRefSetType.SIMPLE)
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
	
	//Collect every version on every extension
	private List<ValueSet> collectSimpleTypeRefsets(List<CodeSystemVersionEntry> codeSystemVersionList) {
		
		List<ValueSet> simpleTypevalueSets = codeSystemVersionList.stream().map(csve -> {
			
			return SnomedRequests.prepareSearchRefSet()
				.all()
				.filterByType(SnomedRefSetType.SIMPLE)
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
				.filterByRefSetType(ImmutableList.of(SnomedRefSetType.QUERY))
				.filterByReferencedComponentType(SnomedTerminologyComponentConstants.CONCEPT)
				.filterByActive(true)
				.setLocales(ImmutableList.of(ExtendedLocale.valueOf(displayLanguage)))
				.setExpand("referencedComponent(expand(pt()))")
				.all()
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
	
	
	private ValueSet.Builder buildSimpleTypeValueSet(final SnomedComponent snomedComponent, final CodeSystemVersionEntry codeSystemVersion, final String displayLanguage) {
		
		LogicalId logicalId = new LogicalId(repositoryId, codeSystemVersion.getPath(), snomedComponent.getId());
		
		Builder builder = createValueSetBuilder(logicalId, snomedComponent, codeSystemVersion);
		String referenceSetId = snomedComponent.getId();

		SnomedConcept refsetConcept = SnomedRequests.prepareGetConcept(referenceSetId)
			.setExpand("pt()")
			.setLocales(ImmutableList.of(ExtendedLocale.valueOf(displayLanguage)))
			.build(getRepositoryId(), codeSystemVersion.getPath())
			.execute(getBus())
			.getSync();
		
		Include include = Include.builder()
			.system(SnomedUri.SNOMED_BASE_URI_STRING)
			.addFilters(ValueSetFilter.builder()
				.refsetExpression(referenceSetId)
				.build())
			.build();
		
		Compose compose = Compose.builder()
			.addInclude(include)
			.build();
		
		return builder
			.name(refsetConcept.getPt().getTerm())
			.title(refsetConcept.getPt().getTerm())
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
