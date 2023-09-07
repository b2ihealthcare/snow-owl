/*
 * Copyright 2021-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.List;

import org.hl7.fhir.r5.model.CodeSystem.CodeSystemFilterComponent;
import org.hl7.fhir.r5.model.CodeSystem.PropertyComponent;
import org.hl7.fhir.r5.model.CodeSystem.PropertyType;
import org.hl7.fhir.r5.model.Enumerations.FilterOperator;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.fhir.core.request.codesystem.FhirCodeSystemResourceConverter;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.ImmutableList;

/**
 * @since 8.0
 */
public final class SnomedFhirCodeSystemResourceConverter implements FhirCodeSystemResourceConverter {

	private static final CodeSystemFilterComponent FILTER_IS_A = new CodeSystemFilterComponent("concept", FilterOperator.ISA, "[concept id]")
		.setDescription("Filter to include concepts that subsume the given concept (have a transitive inferred isA relationship to the concept given).");
	private static final CodeSystemFilterComponent FILTER_REFSET_MEMBER_OF = new CodeSystemFilterComponent("concept", FilterOperator.IN, "[concept id]")
		.setDescription("Filter to include concepts that are active members of the reference set given.");
	private static final CodeSystemFilterComponent FILTER_EXPRESSION = new CodeSystemFilterComponent("expression", FilterOperator.EQUAL, "[expression constraint]")
		.setDescription("Filter result of the given SNOMED CT expression constraint");
	private static final CodeSystemFilterComponent FILTER_POST_COORDINATED_EXPRESSIONS = new CodeSystemFilterComponent("expressions", FilterOperator.EQUAL, "true or false")
		.setDescription("Whether post-coordinated expressions are included in the value set");
	
	private static final List<CodeSystemFilterComponent> FILTERS = ImmutableList.of(
		FILTER_IS_A,
		FILTER_REFSET_MEMBER_OF,
		FILTER_EXPRESSION, 
		FILTER_POST_COORDINATED_EXPRESSIONS
	);
	
	// Whether the code is active or not (defaults to false). 
	// This is derived from the active column in the Concept file of the RF2 Distribution (by inverting the value)
	private static final PropertyComponent PROPERTY_INACTIVE = new PropertyComponent("inactive", PropertyType.BOOLEAN)
		.setDescription("Whether the code is active or not (defaults to false).");
	
	// True if the description logic definition of the concept includes sufficient conditions
	// (i.e., if the concept is not primitive - found in the value of definitionStatusId in the concept file).
	private static final PropertyComponent PROPERTY_SUFFICIENTLY_DEFINED = new PropertyComponent("sufficientlyDefined", PropertyType.BOOLEAN)
		.setDescription("True if the description logic definition of the concept includes sufficient conditions.");
	
	// The SNOMED CT concept id of the module that the concept belongs to.
	private static final PropertyComponent PROPERTY_MODULE_ID = new PropertyComponent("moduleId", PropertyType.CODE)
		.setDescription("The SNOMED CT concept id of the module that the concept belongs to.");
	
	// Effective time in DateTime format.
	// Note: This property is not part of the FHIR specification (https://www.hl7.org/fhir/snomedct.html)
	private static final PropertyComponent PROPERTY_EFFECTIVE_TIME = new PropertyComponent("effectiveTime", PropertyType.DATETIME)
		.setDescription("Effective time in DateTime format.");
	
	@Override
	public List<PropertyComponent> expandProperties(ServiceProvider context, ResourceURI resourceURI, List<ExtendedLocale> locales) {
		final ImmutableList.Builder<PropertyComponent> properties = ImmutableList.builder();
		
		// add common properties
		properties.addAll(FhirCodeSystemResourceConverter.super.expandProperties(context, resourceURI, locales));
		
		// add basic properties
		properties.add(PROPERTY_INACTIVE); 
		properties.add(PROPERTY_MODULE_ID); 
		properties.add(PROPERTY_EFFECTIVE_TIME); 
		properties.add(PROPERTY_SUFFICIENTLY_DEFINED); 
		properties.add(PROPERTY_PARENT); 
		properties.add(PROPERTY_CHILD); 
		
		// Fetch available attributes and register them as supported concept properties
		SnomedRequests.prepareSearchConcept()
			.all()
			.filterByActive(true)
			.filterByAncestor(Concepts.CONCEPT_MODEL_ATTRIBUTE)
			.setExpand("pt()")
			.setLocales(locales)
			.build(resourceURI)
			.getRequest()
			.execute(context)
			.stream()
			.map(attributeConcept -> {
				final String displayName = attributeConcept.getPt() == null ? attributeConcept.getId() : attributeConcept.getPt().getTerm();
				return new PropertyComponent(attributeConcept.getId(), PropertyType.CODE)
					.setUri(String.format("%s/id/%s", SnomedTerminologyComponentConstants.SNOMED_URI_BASE, attributeConcept.getId()))
					.setDescription(displayName); 
			})
			.forEach(properties::add);
		
		return properties.build();
	}
	
	@Override
	public List<CodeSystemFilterComponent> expandFilters(ServiceProvider context, ResourceURI resourceURI, List<ExtendedLocale> list) {
		return FILTERS;
	}
}
