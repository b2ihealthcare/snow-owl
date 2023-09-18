/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.r5;

import static com.b2international.snowowl.fhir.core.model.r5.FhirDataTypeConverter.toFhirDataType;

import java.util.Collection;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.fhir.core.model.Designation;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupResult;
import com.b2international.snowowl.fhir.core.model.codesystem.Property;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.FhirProperty;
import com.b2international.snowowl.fhir.core.model.dt.SubProperty;

/**
 * @since 9.0
 */
public class FhirLookupConverter {

	public static org.hl7.fhir.r5.model.Parameters toParameters(final LookupResult lookupResult) {
		final var parameters = new org.hl7.fhir.r5.model.Parameters();

		/*
		 * XXX: "addParameter" checks if the input is not null; values in LookupResult
		 * are already sparsely populated by FhirLookupRequest, so we can go ahead and
		 * add all of them.
		 */
//		parameters.addParameter("system", lookupResult.getSystem());
		parameters.addParameter("name", lookupResult.getName());
		parameters.addParameter("version", lookupResult.getVersion());
		parameters.addParameter("display", lookupResult.getDisplay());
//		parameters.addParameter("definition", lookupResult.getDefinition());

		final Collection<Designation> designations = lookupResult.getDesignation();
		if (designations != null) {
			for (final Designation designation : designations) {
				final var designationParameters = parameters.addParameter().setName("designation");
				addParameter(designationParameters, designation);
			}
		}
		
		final Collection<Property> properties = lookupResult.getProperty();
		if (properties != null) {
			for (final Property property : properties) {
				final var propertyParameters = parameters.addParameter().setName("property");
				addParameter(propertyParameters, property);
			}
		}

		return parameters;
	}

	private static void addParameter(
		final org.hl7.fhir.r5.model.Parameters.ParametersParameterComponent designationComponent, 
		final Designation designation
	) {
		addPart(designationComponent, "language", designation.getLanguage());
		
		final Coding use = designation.getUse();
		if (use != null) {
			addPart(designationComponent, "use", new org.hl7.fhir.r5.model.Coding(
				use.getSystemValue(), 
				use.getVersion(), 
				use.getCodeValue(),
				use.getDisplay()));
		}
		
//		addPart(designationComponent, "additionalUse", designation.getAdditionalUse());
		addPart(designationComponent, "value", designation.getValue());
	}

	private static void addParameter(
		final org.hl7.fhir.r5.model.Parameters.ParametersParameterComponent propertyComponent, 
		final FhirProperty fhirProperty
	) {
		if (fhirProperty instanceof final Property property) {
			addPart(propertyComponent, "code", property.getCode());
			addPart(propertyComponent, "description", property.getDescription());
		} else if (fhirProperty instanceof final SubProperty subProperty) {
			addPart(propertyComponent, "code", subProperty.getCode());
			addPart(propertyComponent, "description", subProperty.getDescription());
		}
		
		final Object value = fhirProperty.getValue();
		final var dataType = toFhirDataType(value);

		if (dataType != null) {
			final String name = "value" + StringUtils.capitalizeFirstLetter(dataType.fhirType());
			addPart(propertyComponent, name, dataType);
		}

		if (fhirProperty instanceof final Property property) {
			for (final SubProperty subProperty : property.getSubProperty()) {
				final var subPropertyParameters = propertyComponent.addPart().setName("subproperty");
				addParameter(subPropertyParameters, subProperty);
			}
		}
	}

	private static void addPart(
		final org.hl7.fhir.r5.model.Parameters.ParametersParameterComponent parameterComponent, 
		final String name, 
		final org.hl7.fhir.r5.model.DataType value
	) {
		if (value != null) {
			parameterComponent.addPart().setName(name).setValue(value);
		}
	}

	private static void addPart(
		final org.hl7.fhir.r5.model.Parameters.ParametersParameterComponent parameterComponent, 
		final String name, 
		final String value
	) {
		if (!StringUtils.isEmpty(value)) {
			addPart(parameterComponent, name, new org.hl7.fhir.r5.model.StringType(value));
		}
	}
}
