/*
 * Copyright 2018-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.codesystem;

import java.util.List;

import com.b2international.commons.collections.Collections3;
import com.google.common.collect.ImmutableList;

import ca.uhn.fhir.model.primitive.UriDt;

/**
 * Model object for the lookup service request response.
 * 
 * @see <a href="https://www.hl7.org/fhir/codesystem-operations.html#lookup">FHIR:CodeSystem:Operations:lookup</a>
 * @since 6.4
 */
public final class LookupResult {

	// The URI of the code system requested (1..1)
	private UriDt system;

	// A display name for the code system (1..1)
	private String name;

	// The version that these details are based on (0..1)
	private String version;

	// The preferred display for this concept (1..1)
	private String display;

	// A statement of the meaning of the concept from the code system (0..1)
	private String definition;

	// Additional representations for this concept (0..*)
	private List<LookupDesignation> designations = ImmutableList.of();

	/*
	 * One or more properties that contain additional information about the code,
	 * including status. For complex terminologies (e.g. SNOMED CT, LOINC,
	 * medications), these properties serve to decompose the code (0..*)
	 */
	private List<LookupProperty> properties = ImmutableList.of();

	public UriDt getSystem() {
		return system;
	}

	public void setSystem(final UriDt system) {
		this.system = system;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(final String version) {
		this.version = version;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(final String display) {
		this.display = display;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(final String definition) {
		this.definition = definition;
	}

	public List<LookupDesignation> getDesignations() {
		return designations;
	}

	public void setDesignations(final Iterable<LookupDesignation> designations) {
		this.designations = Collections3.toImmutableList(designations);
	}

	public List<LookupProperty> getProperties() {
		return properties;
	}

	public void setProperties(final Iterable<LookupProperty> properties) {
		this.properties = Collections3.toImmutableList(properties);
	}
}
