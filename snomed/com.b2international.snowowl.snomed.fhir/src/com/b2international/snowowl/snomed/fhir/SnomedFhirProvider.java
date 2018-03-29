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

import com.b2international.snowowl.core.exceptions.NotImplementedException;
import com.b2international.snowowl.fhir.core.FhirProvider;
import com.b2international.snowowl.fhir.core.IFhirProvider;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.lookup.LookupRequest;
import com.b2international.snowowl.fhir.core.model.lookup.LookupResult;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.google.common.collect.ImmutableSet;

/**
 * Provider for the SNOMED CT FHIR support
 * @since 6.4
 * @see IFhirProvider
 * @see FhirProvider
 */
public final class SnomedFhirProvider extends FhirProvider {

	private static final Uri FHIR_URI = new Uri("http://snomed.info/sct"); 
	private static final Path SNOMED_INT_PATH = Paths.get(SnomedDatastoreActivator.REPOSITORY_UUID, SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);
	private static final Set<String> SUPPORTED_URIS = ImmutableSet.of(
		SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME,
		SnomedTerminologyComponentConstants.SNOMED_INT_LINK
	);
	
	public SnomedFhirProvider() {
		super(SnomedDatastoreActivator.REPOSITORY_UUID);
	}
	
	@Override
	public boolean isSupported(Path path) {
		return SNOMED_INT_PATH.equals(path);
	}

	@Override
	public LookupResult lookup(LookupRequest lookupRequest) {
		throw new NotImplementedException();
	}

	@Override
	public Collection<String> getSupportedURIs() {
		return SUPPORTED_URIS;
	}

	@Override
	protected Uri getFhirUri() {
		return FHIR_URI;
	}

}
