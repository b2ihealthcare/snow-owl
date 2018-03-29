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
import java.util.Collection;

import com.b2international.snowowl.fhir.core.FhirProvider;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.lookup.LookupRequest;
import com.b2international.snowowl.fhir.core.model.lookup.LookupResult;

/**
 * Provider for the SNOMED CT FHIR support
 * @since 6.4
 */
public class SnomedFhirProvider extends FhirProvider {

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.fhir.core.IFhirProvider#isSupported(java.nio.file.Path)
	 */
	@Override
	public boolean isSupported(Path path) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.fhir.core.IFhirProvider#lookup(com.b2international.snowowl.fhir.core.model.LookupRequest)
	 */
	@Override
	public LookupResult lookup(LookupRequest lookupRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.fhir.core.IFhirProvider#getSupportedURIs()
	 */
	@Override
	public Collection<String> getSupportedURIs() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.fhir.core.IFhirProvider#getCodeSystems()
	 */
	@Override
	public Collection<CodeSystem> getCodeSystems() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.fhir.core.FhirProvider#getFhirUri()
	 */
	@Override
	protected Uri getFhirUri() {
		// TODO Auto-generated method stub
		return null;
	}

}
