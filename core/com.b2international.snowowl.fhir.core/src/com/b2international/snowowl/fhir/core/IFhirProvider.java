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
package com.b2international.snowowl.fhir.core;

import java.nio.file.Path;
import java.util.Collection;

import com.b2international.commons.platform.Extensions;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.lookup.LookupRequest;
import com.b2international.snowowl.fhir.core.model.lookup.LookupResult;

/**
 * Extension point interface for code system specific FHIR API support. 
 * 
 * @see <a href="https://www.hl7.org/fhir/2016May/terminologies.html#system">FHIR:Terminologies:System</a> to determine whether a code system is supported
 *
 * 
 * @see 'com.b2international.snowowl.fhir.core.provider' for the extension point definition
 * @since 6.4
 */
public interface IFhirProvider {

	/**
	 * Registry that reads and instantiates FHIR API supporting services registered by the actual terminology plug-ins.
	 * 
	 * @since 6.4
	 */
	enum Registry {
		
		INSTANCE;
		
		private final static String FHIR_EXTENSION_POINT = "com.b2international.snowowl.fhir.core.provider"; //$NON-NLS-N$
		private final Collection<IFhirProvider> providers;
		
		private Registry() {
			this.providers = Extensions.getExtensions(FHIR_EXTENSION_POINT, IFhirProvider.class);
		}
		
		public static Collection<IFhirProvider> getProviders() {
			return INSTANCE.providers;
		}
		
		/**
		 * Returns the matching {@link IFhirProvider} for the given path (repository/shortName).
		 * @param logical code system path (e.g. icd10Store/ICD-10)
		 * @return FHIR provider
		 * @throws com.b2international.snowowl.fhir.core.exceptions.BadRequestException - if provider is not found with the given path
		 */
		public static IFhirProvider getFhirProvider(Path path) {
			return getProviders().stream()
					.filter(provider -> provider.isSupported(path))
					.findFirst()
					.orElseThrow(() -> new BadRequestException("Did not find FHIR module for code system: " + path, OperationOutcomeCode.MSG_NO_MODULE, "system=" + path));
		}
		
		/**
		 * Returns the matching {@link IFhirProvider} for the given URI.
		 * @param uriValue
		 * @return FHIR provider
		 */
		public static IFhirProvider getFhirProvider(String uriValue) {
			return getProviders().stream()
					.filter(provider -> provider.isSupported(uriValue))
					.findFirst()
					.orElseThrow(() -> new BadRequestException("Did not find FHIR module for code system: " + uriValue, OperationOutcomeCode.MSG_NO_MODULE, "system=" + uriValue));
		}
		
	}
	
	/**
	 * @param uri
	 * @return true if the code system represented by the URI is supported
	 */
	boolean isSupported(String uri);
	
	/**
	 * @param logical code system path (repositoryId/shortName)
	 * @return true if this provider supports the code system represented by the path
	 */
	boolean isSupported(Path path);

	/**
	 * Performs the lookup operation based on the parameter-based lookup request.
	 * <BR>From the spec:
	 * If no properties are specified, the server chooses what to return. The following properties are defined for all code systems: url, name, version (code system info) 
	 * and code information: display, definition, designation, parent and child, and for designations, lang.X where X is a designation language code. 
	 * Some of the properties are returned explicit in named parameters (when the names match), and the rest (except for lang.X) in the property parameter group
	 * @param lookupRequest
	 * @return result of the lookup
	 */
	LookupResult lookup(LookupRequest lookupRequest);

	/**
	 * Returns the code system URIs supported.
	 * @return collection of URIs representing the supported code systems
	 */
	Collection<String> getSupportedURIs();

	/**
	 * Returns the code systems supported by this provider
	 * @return collection of code systems supported
	 */
	Collection<CodeSystem> getCodeSystems();

	/**
	 * Returns the code system for the passed in code system URI
	 * @param codeSystemUri
	 * @return {@link CodeSystem}
	 * @throws BadRequestException if the code system is not supported by this provider
	 */
	CodeSystem getCodeSystem(String codeSystemUri);

	/**
	 * Returns the code system for the passed in logical path (repositoryId/shortName)
	 * @param codeSystemPath
	 * @return {@link CodeSystem}
	 * @throws BadRequestException if the code system is not supported by this provider
	 */
	CodeSystem getCodeSystem(Path codeSystemPath);

}
