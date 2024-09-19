/*
 * Copyright 2021-2024 B2i Healthcare, https://b2ihealthcare.com
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

import static com.b2international.snowowl.snomed.fhir.SnomedFhirConstants.*;

import java.util.List;

import org.hl7.fhir.r5.model.CodeSystem;
import org.hl7.fhir.r5.model.CodeSystem.PropertyType;

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
public class SnomedFhirCodeSystemResourceConverter implements FhirCodeSystemResourceConverter {

	@Override
	public List<CodeSystem.PropertyComponent> expandProperties(ServiceProvider context, ResourceURI resourceURI, List<ExtendedLocale> locales) {
		final ImmutableList.Builder<CodeSystem.PropertyComponent> properties = ImmutableList.builder();
		
		// add common properties
		properties.addAll(FhirCodeSystemResourceConverter.super.expandProperties(context, resourceURI, locales));
		
		// add basic properties
		properties.add(SNOMED_PROPERTY_EFFECTIVE_TIME); 
		properties.add(SNOMED_PROPERTY_INACTIVE); 
		properties.add(SNOMED_PROPERTY_MODULE_ID);
		properties.add(SNOMED_PROPERTY_NORMAL_FORM);
		properties.add(SNOMED_PROPERTY_NORMAL_FORM_TERSE);
		properties.add(SNOMED_PROPERTY_SEMANTIC_TAG);
		properties.add(SNOMED_PROPERTY_SUFFICIENTLY_DEFINED); 
		
		// fetch available relationship types and register them as supported concept property
		// TODO concrete domain values???
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
			.map(typeConcept -> {
				final String displayName = typeConcept.getPt() == null ? typeConcept.getId() : typeConcept.getPt().getTerm();
				return new CodeSystem.PropertyComponent(typeConcept.getId(), PropertyType.CODE)
						.setUri(SnomedTerminologyComponentConstants.SNOMED_URI_BASE + "/id/" + typeConcept.getId())
						.setDescription(displayName);
			})
			.forEach(properties::add);
		
		return properties.build();
	}
	
	@Override
	public List<CodeSystem.CodeSystemFilterComponent> expandFilters(ServiceProvider context, ResourceURI resourceURI, List<ExtendedLocale> list) {
		return SUPPORTED_SNOMED_CODESYSTEM_FILTERS;
	}
	
}
