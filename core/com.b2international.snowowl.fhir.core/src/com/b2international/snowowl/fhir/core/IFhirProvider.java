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

import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.lookup.LookupRequest;
import com.b2international.snowowl.fhir.core.model.lookup.LookupResult;

/**
 * Extension point interface for code system specific FHIR API support
 * 
 * @see <a href="https://www.hl7.org/fhir/2016May/terminologies.html#system">FHIR:Terminologies:System</a> to determine whether a code system is supported
 *
 * 
 * @see 'com.b2international.snowowl.fhir.core.provider' for the extension point definition
 * @since 6.3
 */
public interface IFhirProvider {

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
