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

import org.hl7.fhir.r5.model.DataType;
import org.hl7.fhir.r5.model.Parameters;
import org.hl7.fhir.r5.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.r5.model.StringType;
import org.hl7.fhir.r5.model.UriType;

import com.b2international.commons.StringUtils;
import com.b2international.commons.collections.Collections3;
import com.google.common.collect.ImmutableList;

/**
 * Model object for the lookup service request response.
 * 
 * @see <a href="https://www.hl7.org/fhir/codesystem-operations.html#lookup">FHIR:CodeSystem:Operations:lookup</a>
 * @since 6.4
 */
public final class LookupResult {

	// The URI of the code system requested (1..1)
	private UriType system;

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

	public UriType getSystem() {
		return system;
	}

	public void setSystem(final UriType system) {
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

	public Parameters toParameters() {
		final Parameters parameters = new Parameters();

		/*
		 * XXX: "addParameter" checks if the input is not null; values in LookupResult
		 * are already sparsely populated by FhirLookupRequest, so we can go ahead and
		 * add all of them.
		 */
		parameters.addParameter("system", system);
		parameters.addParameter("name", name);
		parameters.addParameter("version", version);
		parameters.addParameter("display", display);
		parameters.addParameter("definition", definition);

		for (final LookupDesignation designation : designations) {
			final ParametersParameterComponent designationParameters = parameters.addParameter().setName("designation");
			addDesignationParameter(designation, designationParameters);
		}

		for (final LookupProperty property : properties) {
			final ParametersParameterComponent propertyParameters = parameters.addParameter().setName("property");
			addPropertyParameter(property, propertyParameters);
		}

		return parameters;
	}

	private void addDesignationParameter(final LookupDesignation designation, final ParametersParameterComponent designationComponent) {
		addPart(designationComponent, "language", designation.getLanguage());
		addPart(designationComponent, "use", designation.getUse());
		addPart(designationComponent, "additionalUse", designation.getAdditionalUse());
		addPart(designationComponent, "value", designation.getValue());
	}

	private void addPropertyParameter(final LookupProperty property, final ParametersParameterComponent propertyComponent) {
		addPart(propertyComponent, "code", property.getCode());
		addPart(propertyComponent, "valueString", property.getValueString());
		addPart(propertyComponent, "valueBoolean", property.getValueBoolean());
		addPart(propertyComponent, "valueCode", property.getValueCode());
		addPart(propertyComponent, "valueCoding", property.getValueCoding());
		addPart(propertyComponent, "valueDateTime", property.getValueDateTime());
		addPart(propertyComponent, "valueDecimal", property.getValueDecimal());
		addPart(propertyComponent, "valueInteger", property.getValueInteger());
		addPart(propertyComponent, "description", property.getDescription());

		// XXX: Note that the specification doesn't seem to support more than a single level of nested properties, but we won't prohibit it here either.
		for (final LookupProperty subProperty : property.getSubProperties()) {
			final ParametersParameterComponent subPropertyParameters = propertyComponent.addPart().setName("subproperty");
			addPropertyParameter(subProperty, subPropertyParameters);
		}
	}

	private void addPart(final ParametersParameterComponent parameterComponent, final String name, final DataType value) {
		if (value != null) {
			parameterComponent.addPart().setName(name).setValue(value);
		}
	}

	private void addPart(final ParametersParameterComponent designationComponent, final String name, final String value) {
		if (!StringUtils.isEmpty(value)) {
			addPart(designationComponent, name, new StringType(value));
		}
	}

	private void addPart(final ParametersParameterComponent parameterComponent, final String name, final List<? extends DataType> values) {
		if (values != null) {
			values.forEach(v -> addPart(parameterComponent, name, v));
		}
	}
}
