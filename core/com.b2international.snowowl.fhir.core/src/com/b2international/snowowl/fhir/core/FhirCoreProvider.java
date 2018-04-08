/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core;

import java.nio.file.Path;
import java.util.Collection;

import com.b2international.snowowl.fhir.core.codesystems.FhirCodeSystem;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.lookup.LookupRequest;
import com.b2international.snowowl.fhir.core.model.lookup.LookupResult;
import com.google.common.collect.Sets;
import com.google.common.reflect.ClassPath;

public class FhirCoreProvider extends FhirProvider {

	public FhirCoreProvider(String repositoryId) {
		super(repositoryId);
	}

	@Override
	public boolean isSupported(Path path) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public LookupResult lookup(LookupRequest lookupRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getSupportedURIs() {

		Collection<String> codeSytemUris = Sets.newHashSet();
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try {
			for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
			  if (info.getName().startsWith("com.b2international.snowowl.fhir.core.codesystems.")) {
			    final Class<?> clazz = info.load();
			    if (clazz.isAssignableFrom(FhirCodeSystem.class)) {
			    		FhirCodeSystem fhirCodeSystem = (FhirCodeSystem) clazz.newInstance();
			    		codeSytemUris.add(fhirCodeSystem.getCodeSystemUri());
			    }
			  }
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return codeSytemUris;
	}

	@Override
	protected Uri getFhirUri() {
		// TODO Auto-generated method stub
		return null;
	}

	

}
