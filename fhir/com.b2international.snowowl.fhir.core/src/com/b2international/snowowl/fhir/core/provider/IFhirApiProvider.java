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
package com.b2international.snowowl.fhir.core.provider;

import java.util.Collection;

import com.b2international.snowowl.fhir.core.LogicalId;

/**
 * FHIR API provider interface with common functionality for all resource-based FHIR providers.
 *  
 * @since 6.7
 */
public interface IFhirApiProvider {
	
	/**
	 * @param uri
	 * @return true if the code system represented by the URI is supported
	 */
	boolean isSupported(String uri);
	
	/**
	 * @param logicalId - logical code system path (repositoryId:branchPath)
	 * @return true if this provider supports the code system represented by the logical id
	 */
	boolean isSupported(LogicalId logicalId);
	
	/**
	 * Returns the code system URIs supported.
	 * @return collection of URIs representing the supported code systems
	 */
	Collection<String> getSupportedURIs();

}
