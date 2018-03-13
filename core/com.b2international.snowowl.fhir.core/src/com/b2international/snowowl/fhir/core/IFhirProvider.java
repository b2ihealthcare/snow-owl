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

/**
 * Extension point interface for code system specific FHIR API support
 * 
 * @see <a href="https://www.hl7.org/fhir/2016May/terminologies.html#system">FHIR:Terminologies:System</a> to determine whether a code system is supported
 *
 * 
 * @see 'com.b2international.snowowl.fhir.provider' for the extension point definition
 * @since 6.3
 */
public interface IFhirProvider {

	/**
	 * @param uri
	 * @return true if the code system represented by the URI is supported
	 */
	boolean isSupported(String uri);

	void lookup(String version, String code);
	
}
