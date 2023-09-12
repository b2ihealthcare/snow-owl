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

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hl7.fhir.r5.model.CodeSystem;
import org.hl7.fhir.r5.model.CodeType;
import org.hl7.fhir.r5.model.Coding;
import org.hl7.fhir.r5.model.DecimalType;
import org.hl7.fhir.r5.model.IntegerType;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.Concept;
import com.b2international.snowowl.fhir.core.model.ResourceConstants;
import com.b2international.snowowl.fhir.core.model.codesystem.*;
import com.b2international.snowowl.fhir.core.request.codesystem.FhirCodeSystemLookupConverter;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.SnomedDisplayTermType;
import com.b2international.snowowl.snomed.core.domain.RelationshipValue;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

/**
 * @since 8.0
 */
public final class SnomedFhirCodeSystemLookupConverter implements FhirCodeSystemLookupConverter {

	@Override
	public String configureConceptExpand(final LookupRequest request) {
		final String expandDescriptions = "descriptions(active:true,expand(type(expand(pt()))),sort:\"typeId,term\"),pt()";

		final boolean requestedChild = request.containsProperty(CommonConceptProperties.CHILD.getCode());
		final String expandDescendants = requestedChild ? ",descendants(direct:true)" : "";

		final boolean requestedParent = request.containsProperty(CommonConceptProperties.PARENT.getCode());
		final String expandAncestors = requestedParent ? ",ancestors(direct:true)" : "";

		return expandDescriptions + expandDescendants + expandAncestors;
	}

	@Override
	public List<LookupDesignation> expandDesignations(final ServiceProvider context, final CodeSystem codeSystem, final Concept concept, final LookupRequest request, final String acceptLanguage) {
		final SnomedConcept snomedConcept = concept.getInternalConceptAs();

		if (!request.containsProperty(LookupRequestProperties.DESIGNATION.getCode())) {
			return FhirCodeSystemLookupConverter.super.expandDesignations(context, codeSystem, concept, request, acceptLanguage);
		}

		final List<LookupDesignation> designations = snomedConcept.getDescriptions()
			.stream()
			.map(d -> {
				final SnomedConcept typeConcept = d.getType();
				final String typeDisplay = SnomedDisplayTermType.PT.getLabel(typeConcept);
				
				final LookupDesignation designation = new LookupDesignation();
				designation.setUse(new Coding(codeSystem.getUrl(), typeConcept.getId(), typeDisplay));
				designation.setLanguage(new CodeType(d.getLanguageCode()));
				designation.setValue(d.getTerm());
				return designation;
			})
			.toList();

		return designations;
	}

	@Override
	public List<LookupProperty> expandProperties(final ServiceProvider context, final CodeSystem codeSystem, final Concept concept, final LookupRequest lookupRequest) {
		final SnomedConcept snomedConcept = concept.getInternalConceptAs();
		final List<LookupProperty> properties = newArrayList();

		// SNOMED concept properties
		if (lookupRequest.containsProperty(SnomedConceptProperties.INACTIVE.getCode())) {
			properties.add(SnomedConceptProperties.INACTIVE.withValue(!snomedConcept.isActive()));
		}

		if (lookupRequest.containsProperty(SnomedConceptProperties.MODULE_ID.getCode())) {
			properties.add(SnomedConceptProperties.MODULE_ID.withValue(snomedConcept.getModuleId()));
		}

		if (lookupRequest.containsProperty(SnomedConceptProperties.SUFFICIENTLY_DEFINED.getCode())) {
			properties.add(SnomedConceptProperties.SUFFICIENTLY_DEFINED.withValue(!snomedConcept.isPrimitive()));
		}

		if (lookupRequest.containsProperty(SnomedConceptProperties.EFFECTIVE_TIME.getCode())) {
			properties.add(SnomedConceptProperties.EFFECTIVE_TIME.withValue(EffectiveTimes.format(snomedConcept.getEffectiveTime(), DateFormats.SHORT)));
		}

		// Common properties (requires expansion to be configured)
		final boolean requestedChild = lookupRequest.containsProperty(CommonConceptProperties.CHILD.getCode());
		if (requestedChild && snomedConcept.getDescendants() != null) {
			for (final SnomedConcept child : snomedConcept.getDescendants()) {
				// XXX: Unfortunately we can't add label information to a code property :(
				properties.add(CommonConceptProperties.CHILD.withValue(new CodeType(child.getId())));
			}
		}

		final boolean requestedParent = lookupRequest.containsProperty(CommonConceptProperties.PARENT.getCode());
		if (requestedParent && snomedConcept.getAncestors() != null) {
			for (final SnomedConcept parent : snomedConcept.getAncestors()) {
				properties.add(CommonConceptProperties.PARENT.withValue(new CodeType(parent.getId())));
			}
		}

		// Relationship types as properties
		final Set<String> relationshipTypeIds = lookupRequest.getPropertyCodes()
			.stream()
			.filter(SnomedIdentifiers::isConceptIdentifier) // only SNOMED CT Concept IDs
			.collect(Collectors.toSet());
		
		if (!relationshipTypeIds.isEmpty()) {
			final ResourceURI resourceUri = ResourceConstants.getResourceUri(codeSystem);
			
			SnomedRequests.prepareSearchRelationship()
				.setLimit(1000)
				.filterByActive(true)
				.filterByCharacteristicType(Concepts.INFERRED_RELATIONSHIP)
				.filterBySource(concept.getId())
				.filterByTypes(relationshipTypeIds)
				.stream(context, rb -> rb.build(resourceUri))
				.flatMap(SnomedRelationships::stream)
				.forEachOrdered(r -> {
					final SnomedConcept typeConcept = r.getType();
					final LookupProperty property = new LookupProperty(typeConcept.getId());
	
					if (r.hasValue()) {
						final RelationshipValue value = r.getValueAsObject();
						value.map(
							i -> property.setValueInteger(new IntegerType(i)),
							d -> property.setValueDecimal(new DecimalType(d)),
							s -> property.setValueString(s)
						);
					} else {
						property.setValueCode(new CodeType(r.getDestinationId()));
					}
	
					properties.add(property);
				});
		}

		return properties;
	}
}
