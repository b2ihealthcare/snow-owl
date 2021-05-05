/*
 * Copyright 2018-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.provider.FhirApiProvider;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

/**
 * Abstract superclass provider for the SNOMED CT FHIR  support
 * 
 * @since 7.1
 * @see FhirApiProvider
 */
public abstract class SnomedFhirApiProvider extends FhirApiProvider {

	protected static final Set<String> SUPPORTED_URIS = ImmutableSet.of(
			SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME,
			SnomedTerminologyComponentConstants.SNOMED_INT_LINK,
			SnomedUri.SNOMED_BASE_URI_STRING
		);
	
	protected String repositoryId;
	
	public SnomedFhirApiProvider(IEventBus bus, List<ExtendedLocale> locales) {
		super(bus, locales);
		this.repositoryId = SnomedDatastoreActivator.REPOSITORY_UUID;
	}
	
	@Override
	protected String getRepositoryId() {
		return repositoryId;
	}
	
	@Override
	protected String getCodeSystemShortName() {
		return SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME;
	}
	
	public Collection<String> getSupportedURIs() {
		return SUPPORTED_URIS;
	}
	
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
	
	protected final String getPreferredTermOrId(SnomedConcept concept) {
		return concept.getPt() != null ? concept.getPt().getTerm() : concept.getId();
	}
	
	protected Collection<String> collectIds(Collection<String> uris) {
		return uris.stream()
			.filter(u -> u.contains("/"))
			.map(u -> u.substring(u.lastIndexOf('/') + 1, u.length()))
			.collect(Collectors.toSet());
	}
	
}
