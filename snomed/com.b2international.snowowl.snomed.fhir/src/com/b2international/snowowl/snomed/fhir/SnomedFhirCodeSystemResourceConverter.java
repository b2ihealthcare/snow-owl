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
import java.util.stream.Stream;

import org.hl7.fhir.r5.model.CodeSystem.CodeSystemFilterComponent;
import org.hl7.fhir.r5.model.CodeSystem.PropertyComponent;
import org.hl7.fhir.r5.model.CodeSystem.PropertyType;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.fhir.core.model.codesystem.CommonConceptProperties;
import com.b2international.snowowl.fhir.core.request.codesystem.FhirCodeSystemResourceConverter;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.SnomedDisplayTermType;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.ImmutableList;

/**
 * @since 8.0
 */
public final class SnomedFhirCodeSystemResourceConverter implements FhirCodeSystemResourceConverter {

	private static final List<CodeSystemFilterComponent> FILTERS = Stream.of(SnomedPropertyFilters.values())
		.map(SnomedPropertyFilters::getFilterComponent)
		.collect(ImmutableList.toImmutableList());

	@Override
	public List<PropertyComponent> expandProperties(final ServiceProvider context, final ResourceURI resourceURI, final List<ExtendedLocale> locales) {
		final ImmutableList.Builder<PropertyComponent> properties = ImmutableList.builder();

		// add common properties
		properties.addAll(FhirCodeSystemResourceConverter.super.expandProperties(context, resourceURI, locales));

		// add basic properties
		properties.add(SnomedConceptProperties.INACTIVE.getComponent()); 
		properties.add(SnomedConceptProperties.MODULE_ID.getComponent()); 
		properties.add(SnomedConceptProperties.EFFECTIVE_TIME.getComponent()); 
		properties.add(SnomedConceptProperties.SUFFICIENTLY_DEFINED.getComponent()); 
		properties.add(CommonConceptProperties.PARENT.getComponent()); 
		properties.add(CommonConceptProperties.CHILD.getComponent()); 

		// Fetch available attributes and register them as supported concept properties
		SnomedRequests.prepareSearchConcept()
			.filterByActive(true)
			.filterByAncestor(Concepts.CONCEPT_MODEL_ATTRIBUTE)
			.setExpand(SnomedDisplayTermType.PT.getExpand())
			.setLocales(locales)
			.setLimit(1000)
			.stream(context, rb -> rb.build(resourceURI))
			.flatMap(SnomedConcepts::stream)
			.map(attributeConcept -> {
				final String propertyName = attributeConcept.getId();
				// "The equivalent URI for the laterality property is http://snomed.info/id/272741003."
				final String propertyUri = String.format("%s/id/%s", SnomedTerminologyComponentConstants.SNOMED_URI_BASE, propertyName);
				final String displayName = SnomedDisplayTermType.PT.getLabel(attributeConcept);
	
				return new PropertyComponent(propertyName, PropertyType.CODE)
						.setUri(propertyUri)
						.setDescription(displayName); 
			})
			.forEachOrdered(properties::add);

		return properties.build();
	}

	@Override
	public List<CodeSystemFilterComponent> expandFilters(final ServiceProvider context, final ResourceURI resourceURI, final List<ExtendedLocale> locales) {
		return FILTERS;
	}
}
