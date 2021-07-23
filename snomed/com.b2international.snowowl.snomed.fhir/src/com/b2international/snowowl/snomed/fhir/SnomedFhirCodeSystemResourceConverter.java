/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.fhir.core.codesystems.CommonConceptProperties;
import com.b2international.snowowl.fhir.core.model.codesystem.Filter;
import com.b2international.snowowl.fhir.core.model.codesystem.Filters;
import com.b2international.snowowl.fhir.core.model.codesystem.IConceptProperty;
import com.b2international.snowowl.fhir.core.request.codesystem.FhirCodeSystemResourceConverter;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.fhir.codesystems.CoreSnomedConceptProperties;
import com.google.common.collect.ImmutableList;

/**
 * @since 8.0
 */
public class SnomedFhirCodeSystemResourceConverter implements FhirCodeSystemResourceConverter {

	@Override
	public List<IConceptProperty> expandProperties(ServiceProvider context, ResourceURI resourceURI, List<ExtendedLocale> locales) {
		final ImmutableList.Builder<IConceptProperty> properties = ImmutableList.builder();
		// add common properties
		properties.addAll(FhirCodeSystemResourceConverter.super.expandProperties(context, resourceURI, locales));
		
		// add basic properties
		properties.add(CoreSnomedConceptProperties.INACTIVE); 
		properties.add(CoreSnomedConceptProperties.MODULE_ID); 
		properties.add(CoreSnomedConceptProperties.EFFECTIVE_TIME); 
		properties.add(CoreSnomedConceptProperties.SUFFICIENTLY_DEFINED); 
		properties.add(CommonConceptProperties.CHILD); 
		properties.add(CommonConceptProperties.PARENT); 
		
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
				return IConceptProperty.Dynamic.valueCode(SnomedTerminologyComponentConstants.SNOMED_URI_BASE + "/id", displayName, typeConcept.getId());
			})
			.forEach(properties::add);
		
		return properties.build();
	}
	
	@Override
	public List<Filter> expandFilters(ServiceProvider context, ResourceURI resourceURI, List<ExtendedLocale> list) {
		return List.of(
			Filters.EXPRESSION_FILTER, 
			Filters.EXPRESSIONS_FILTER,
			Filters.IS_A_FILTER, 
			Filters.REFSET_MEMBER_OF
		);
	}
	
}
