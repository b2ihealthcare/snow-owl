/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.rest;

import java.util.Map;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMap;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.base.MoreObjects;

/**
 * REST resource input that carries a FHIR concept map and a map translating FHIR system URIs to Snow Owl's resource URIs.
 * <p>
 * Example: <code>http://loinc.org</code> &rarr; <code>codesystems/LOINC/2.73</code>
 * 
 * @since 8.6.0
 */
public class ConceptMapWithUriMapping {

	private ConceptMap conceptMap;
	private Map<String, ResourceURI> systemUriOverrides;

	@JsonUnwrapped
	public ConceptMap getConceptMap() {
		return conceptMap;
	}
	
	public void setConceptMap(ConceptMap conceptMap) {
		this.conceptMap = conceptMap;
	}
	
	public Map<String, ResourceURI> getSystemUriOverrides() {
		return systemUriOverrides;
	}
	
	public void setSystemUriOverrides(Map<String, ResourceURI> systemUriOverrides) {
		this.systemUriOverrides = systemUriOverrides;
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("conceptMap", conceptMap)
			.add("systemUriOverrides", systemUriOverrides)
			.toString();
	}
}
