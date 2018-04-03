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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Set;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.fhir.core.FhirProvider;
import com.b2international.snowowl.fhir.core.IFhirProvider;
import com.b2international.snowowl.fhir.core.codesystems.CommonConceptProperties;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem.Builder;
import com.b2international.snowowl.fhir.core.model.codesystem.SupportedConceptProperty;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.lookup.LookupRequest;
import com.b2international.snowowl.fhir.core.model.lookup.LookupResult;
import com.b2international.snowowl.fhir.core.model.lookup.Property;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptGetRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Provider for the SNOMED CT FHIR support
 * @since 6.4
 * @see IFhirProvider
 * @see FhirProvider
 */
public final class SnomedFhirProvider extends FhirProvider {

	private static final String URI_BASE = "http://snomed.info";
	private static final Uri FHIR_URI = new Uri(URI_BASE + "/sct");
	private static final Path SNOMED_INT_PATH = Paths.get(SnomedDatastoreActivator.REPOSITORY_UUID, SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);
	private static final Set<String> SUPPORTED_URIS = ImmutableSet.of(
		SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME,
		SnomedTerminologyComponentConstants.SNOMED_INT_LINK,
		FHIR_URI.getUriValue()
	);
	
	public SnomedFhirProvider() {
		super(SnomedDatastoreActivator.REPOSITORY_UUID);
	}
	
	@Override
	public boolean isSupported(Path path) {
		return SNOMED_INT_PATH.equals(path);
	}

	@Override
	public LookupResult lookup(LookupRequest lookup) {
		String version = lookup.getVersion();
		String branchPath = getBranchPath(version);
		
		validateRequestedProperties(lookup);
		
		boolean requestedChild = lookup.containsProperty(CommonConceptProperties.CHILD.getCode());
		boolean requestedParent = lookup.containsProperty(CommonConceptProperties.PARENT.getCode());
		
		String expandDescendants = requestedChild ? ",descendants(direct:true,expand(pt()))" : "";
		String expandAncestors = requestedParent ? ",ancestors(direct:true,expand(pt()))" : "";
		String displayLanguage = lookup.getDisplayLanguage() != null ? lookup.getDisplayLanguage().getCodeValue() : "en-GB";
		
		SnomedConceptGetRequestBuilder req = SnomedRequests.prepareGetConcept(lookup.getCode().getCodeValue())
				.setExpand(String.format("pt()%s%s", expandDescendants, expandAncestors))
				.setLocales(ImmutableList.of(ExtendedLocale.valueOf(displayLanguage)));
		
		return req.build(repositoryId(), branchPath)
			.execute(getBus())
			.then(concept -> mapToLookupResult(concept, lookup))
			.getSync();
		
	}

	private LookupResult mapToLookupResult(SnomedConcept concept, LookupRequest lookup) {
		boolean requestedChild = lookup.containsProperty(CommonConceptProperties.CHILD.getCode());
		boolean requestedParent = lookup.containsProperty(CommonConceptProperties.PARENT.getCode());
		
		final LookupResult.Builder result = LookupResult.builder()
				.name(SnomedTerminologyComponentConstants.SNOMED_NAME)
				.version(lookup.getVersion())
				.display(getPreferredTermOrId(concept));
			
		if (requestedChild && concept.getDescendants() != null) {
			for (SnomedConcept child : concept.getDescendants()) {
				Property childProperty = Property.builder()
						.code(CommonConceptProperties.CHILD.getCode())
						.value(child.getId())
						.description(getPreferredTermOrId(child))
						.build();
				result.addProperty(childProperty);
			}
		}
		
		if (requestedParent && concept.getAncestors() != null) {
			for (SnomedConcept parent : concept.getAncestors()) {
				Property parentProperty = Property.builder()
						.code(CommonConceptProperties.PARENT.getCode())
						.value(parent.getId())
						.description(getPreferredTermOrId(parent))
						.build();
				result.addProperty(parentProperty);
			}
		}
		
		return result.build();
	}

	private String getPreferredTermOrId(SnomedConcept concept) {
		return concept.getPt() == null ? concept.getId() : concept.getPt().getTerm();
	}

	@Override
	protected Collection<CommonConceptProperties> getSupportedConceptProperties() {
		return ImmutableSet.of(CommonConceptProperties.CHILD, CommonConceptProperties.PARENT);
	}
	
	@Override
	public Collection<String> getSupportedURIs() {
		return SUPPORTED_URIS;
	}

	@Override
	protected Uri getFhirUri() {
		return FHIR_URI;
	}
	
	@Override
	protected Builder appendCodeSystemSpecificProperties(Builder builder) {
		return builder
				.addProperty(SupportedConceptProperty.builder(CommonConceptProperties.CHILD).build())
				.addProperty(SupportedConceptProperty.builder(CommonConceptProperties.PARENT).build());
	}

}
