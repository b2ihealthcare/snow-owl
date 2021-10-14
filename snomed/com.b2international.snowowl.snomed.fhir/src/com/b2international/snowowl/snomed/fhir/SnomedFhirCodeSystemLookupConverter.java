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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.Concept;
import com.b2international.snowowl.fhir.core.codesystems.CommonConceptProperties;
import com.b2international.snowowl.fhir.core.model.Designation;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequest;
import com.b2international.snowowl.fhir.core.model.codesystem.Property;
import com.b2international.snowowl.fhir.core.model.codesystem.SupportedCodeSystemRequestProperties;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.request.codesystem.FhirCodeSystemLookupConverter;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.SnomedDisplayTermType;
import com.b2international.snowowl.snomed.core.domain.RelationshipValue;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

/**
 * @since 8.0
 */
public final class SnomedFhirCodeSystemLookupConverter implements FhirCodeSystemLookupConverter {

	@Override
	public String configureConceptExpand(LookupRequest request) {
		boolean requestedChild = request.containsProperty(CommonConceptProperties.CHILD.getCode());
		boolean requestedParent = request.containsProperty(CommonConceptProperties.PARENT.getCode());
		String expandDescendants = requestedChild ? ",descendants(direct:true,expand(pt()))" : "";
		String expandAncestors = requestedParent ? ",ancestors(direct:true,expand(pt()))" : "";
		return String.format("descriptions(expand(type(expand(pt()))),sort:\"typeId,term\"),pt()%s%s", expandDescendants, expandAncestors);
	}
	
	@Override
	public List<Designation> expandDesignations(ServiceProvider context, CodeSystem codeSystem, Concept concept, LookupRequest request, String acceptLanguage) {
		SnomedConcept snomedConcept = concept.getInternalConceptAs();
		if (request.isPropertyRequested(SupportedCodeSystemRequestProperties.DESIGNATION)) {
			List<Designation> designations = new ArrayList<>();
			for (SnomedDescription description : snomedConcept.getDescriptions()) {
				Coding coding = Coding.builder()
					.system(codeSystem.getUrl().getUriValue())
					.code(description.getTypeId())
					.display(SnomedDisplayTermType.PT.getLabel(description.getType()))
					.build();
				
				designations.add(
					Designation.builder()
						.languageCode(description.getLanguageCode())
						.use(coding)
						.value(description.getTerm())
					.build()
				);
			}
			return designations;
		} else {
			return FhirCodeSystemLookupConverter.super.expandDesignations(context, codeSystem, concept, request, acceptLanguage);
		}
	}
	
	@Override
	public List<Property> expandProperties(ServiceProvider context, CodeSystem codeSystem, Concept concept, LookupRequest lookupRequest) {
		SnomedConcept snomedConcept = concept.getInternalConceptAs();
		
		List<Property> properties = new ArrayList<>();
		
		// add basic SNOMED properties
		if (lookupRequest.isPropertyRequested(CoreSnomedConceptProperties.INACTIVE)) {
			properties.add(CoreSnomedConceptProperties.INACTIVE.propertyOf(!snomedConcept.isActive()));
		}
		
		if (lookupRequest.isPropertyRequested(CoreSnomedConceptProperties.MODULE_ID)) {
			properties.add(CoreSnomedConceptProperties.MODULE_ID.propertyOf(snomedConcept.getModuleId()));
		}
		
		if (lookupRequest.isPropertyRequested(CoreSnomedConceptProperties.SUFFICIENTLY_DEFINED)) {
			properties.add(CoreSnomedConceptProperties.SUFFICIENTLY_DEFINED.propertyOf(!snomedConcept.isPrimitive()));
		}
		
		if (lookupRequest.isPropertyRequested(CoreSnomedConceptProperties.EFFECTIVE_TIME)) {
			properties.add(CoreSnomedConceptProperties.EFFECTIVE_TIME.propertyOf(EffectiveTimes.format(snomedConcept.getEffectiveTime(), DateFormats.SHORT)));
		}
		
		// Optionally requested properties
		boolean requestedChild = lookupRequest.containsProperty(CommonConceptProperties.CHILD.getCode());
		boolean requestedParent = lookupRequest.containsProperty(CommonConceptProperties.PARENT.getCode());
		
			
		if (requestedChild && snomedConcept.getDescendants() != null) {
			for (SnomedConcept child : snomedConcept.getDescendants()) {
				properties.add(CommonConceptProperties.CHILD.propertyOf(child.getId(), SnomedDisplayTermType.PT.getLabel(child)));
			}
		}
		
		if (requestedParent && snomedConcept.getAncestors() != null) {
			for (SnomedConcept parent : snomedConcept.getAncestors()) {
				properties.add(CommonConceptProperties.PARENT.propertyOf(parent.getId(), SnomedDisplayTermType.PT.getLabel(parent)));
			}
		}
		
		// Relationship Properties
		Set<String> relationshipTypeIds = lookupRequest.getPropertyCodes().stream()
			.map(p -> {
				if (p.startsWith(SnomedTerminologyComponentConstants.SNOMED_URI_BASE)) {
					return p.substring(p.lastIndexOf('/') + 1, p.length()); // URI prefixed properties
				} else {
					return p; // use as is, so users won't need to define full URI for each SNOMED CT property, ID is enough
				}
			})
			.filter(SnomedIdentifiers::isConceptIdentifier) // only SNOMED CT Concept IDs
			.collect(Collectors.toSet());
		
		if (!relationshipTypeIds.isEmpty()) {
			SnomedRequests.prepareSearchRelationship()
				.all()
				.filterByActive(true)
				.filterByCharacteristicType(Concepts.INFERRED_RELATIONSHIP)
				.filterBySource(concept.getId())
				.filterByTypes(relationshipTypeIds)
				.build(codeSystem.getResourceURI())
				.getRequest()
				.execute(context)
				.forEach(r -> {
					Property.Builder propertyBuilder = Property.builder()
						.code(r.getTypeId());
					
					if (r.hasValue()) {
						RelationshipValue value = r.getValueAsObject();
						value.map(
							i -> propertyBuilder.valueInteger(i),
							d -> propertyBuilder.valueDecimal(d.doubleValue()),
							s -> propertyBuilder.valueString(s));
					} else {
						propertyBuilder.valueCode(r.getDestinationId());
					}
					
					properties.add(propertyBuilder.build());
				});
		}
		
		if (properties.isEmpty()) {
			return null;
		} else {
			return properties;
		}
	}
	
}
