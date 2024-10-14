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

import java.util.*;
import java.util.stream.Collectors;

import org.hl7.fhir.r5.model.*;

import com.b2international.fhir.r5.operations.CodeSystemLookupParameters;
import com.b2international.fhir.r5.operations.CodeSystemLookupResultParameters;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.Concept;
import com.b2international.snowowl.core.domain.Description;
import com.b2international.snowowl.fhir.core.request.codesystem.FhirCodeSystemLookupConverter;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.SnomedDisplayTermType;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.RelationshipValue;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.request.SnomedConceptSearchRequestEvaluator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

/**
 * @since 8.0
 */
public final class SnomedFhirCodeSystemLookupConverter implements FhirCodeSystemLookupConverter {
	
	private static final String SNOMED_SYSTEM_URL = "http://snomed.info/sct";

	@Override
	public String configureConceptExpand(CodeSystemLookupParameters request) {
		boolean requestedChild = request.isPropertyRequested(CodeSystemLookupParameters.PROPERTY_CHILD);
		boolean requestedParent = request.isPropertyRequested(CodeSystemLookupParameters.PROPERTY_PARENT);
		String expandDescendants = requestedChild ? ",descendants(direct:true,expand(pt()))" : "";
		String expandAncestors = requestedParent ? ",ancestors(direct:true,expand(pt()))" : "";
		return String.format("descriptions(expand(type(expand(pt()))),sort:\"typeId,term\"),pt()%s%s", expandDescendants, expandAncestors);
	}
	
	@Override
	public List<CodeSystemLookupResultParameters.Designation> expandDesignations(ServiceProvider context, CodeSystem codeSystem, Concept concept, CodeSystemLookupParameters parameters, String acceptLanguage) {
		SnomedConcept snomedConcept = concept.getInternalConceptAs();
		if (parameters.isPropertyRequested(CodeSystemLookupParameters.PROPERTY_DESIGNATION)) {
			final SortedSet<Description> alternativeTerms = SnomedConceptSearchRequestEvaluator.generateGenericDescriptions(snomedConcept.getDescriptions());
			// convert to Designation model
			final List<CodeSystemLookupResultParameters.Designation> designations = new ArrayList<>(alternativeTerms.size());
			for (Description description : alternativeTerms) {
				SnomedDescription snomedDescription = description.getInternalDescription();
				// use context describes type of description
				Coding use = new Coding()
					.setSystem(codeSystem.getUrl())
					.setCode(snomedDescription.getTypeId())
					.setDisplay(SnomedDisplayTermType.PT.getLabel(snomedDescription.getType()));
				
				Map<String,Acceptability> acceptabilityMap = snomedDescription.getAcceptabilityMap();
				
				List<Extension> designationExtensions = new ArrayList<>(acceptabilityMap.size());
				
				for (String refsetId : acceptabilityMap.keySet().stream().sorted().toList()) {
					
					var acceptability = acceptabilityMap.get(refsetId);
					
					Extension useContextExtension = new Extension("http://snomed.info/fhir/StructureDefinition/designation-use-context");
					
					// Set code
					Extension code = new Extension("context");
					
					Coding codeCoding = new Coding()
							.setSystem(SNOMED_SYSTEM_URL)
							.setCode(refsetId);
					code.setValue(codeCoding);
					
					useContextExtension.addExtension(code);
					
					// Set role
					Extension role = new Extension("role");
					
					Coding roleCoding = new Coding()
							.setSystem(SNOMED_SYSTEM_URL)
							.setCode(acceptability.getConceptId())
							.setDisplay(acceptability.name());
					role.setValue(roleCoding);
					
					useContextExtension.addExtension(role);
					
					// Set type
					Extension type = new Extension("type");
					
					Coding typeCoding = new Coding()
							.setSystem(SNOMED_SYSTEM_URL)
							.setCode(snomedDescription.getTypeId())
							.setDisplay(SnomedDisplayTermType.PT.getLabel(snomedDescription.getType()));
					type.setValue(typeCoding);
					
					useContextExtension.addExtension(type);
					
					designationExtensions.add(useContextExtension);
				}
				
				var designation = new CodeSystemLookupResultParameters.Designation();
				
				designation
					// term and language code comes from the generated alternative term generic model
					.setValue(description.getTerm())
					.setLanguage(description.getLanguage())
					.setUse(use)
					.setExtension(designationExtensions);
				
				designations.add(designation);
			}
			return designations;
		} else {
			return FhirCodeSystemLookupConverter.super.expandDesignations(context, codeSystem, concept, parameters, acceptLanguage);
		}
	}
	
	@Override
	public List<CodeSystemLookupResultParameters.Property> expandProperties(ServiceProvider context, CodeSystem codeSystem, Concept concept, CodeSystemLookupParameters parameters) {
		SnomedConcept snomedConcept = concept.getInternalConceptAs();
		
		List<CodeSystemLookupResultParameters.Property> properties = new ArrayList<>();
		
		// add basic SNOMED properties
		if (parameters.isPropertyRequested(SnomedFhirConstants.SNOMED_PROPERTY_INACTIVE.getCode())) {
			properties.add(new CodeSystemLookupResultParameters.Property()
					.setCode(SnomedFhirConstants.SNOMED_PROPERTY_INACTIVE.getCode())
					.setValue(new BooleanType(!snomedConcept.isActive())));
		}
		
		if (parameters.isPropertyRequested(SnomedFhirConstants.SNOMED_PROPERTY_MODULE_ID.getCode())) {
			properties.add(new CodeSystemLookupResultParameters.Property()
					.setCode(SnomedFhirConstants.SNOMED_PROPERTY_MODULE_ID.getCode())
					.setValue(new CodeType(snomedConcept.getModuleId())));
		}
		
		if (parameters.isPropertyRequested(SnomedFhirConstants.SNOMED_PROPERTY_SUFFICIENTLY_DEFINED.getCode())) {
			properties.add(new CodeSystemLookupResultParameters.Property()
					.setCode(SnomedFhirConstants.SNOMED_PROPERTY_SUFFICIENTLY_DEFINED.getCode())
					.setValue(new BooleanType(!snomedConcept.isPrimitive())));
		}
		
		if (parameters.isPropertyRequested(SnomedFhirConstants.SNOMED_PROPERTY_EFFECTIVE_TIME.getCode())) {
			properties.add(new CodeSystemLookupResultParameters.Property()
					.setCode(SnomedFhirConstants.SNOMED_PROPERTY_EFFECTIVE_TIME.getCode())
					.setValue(new DateTimeType(EffectiveTimes.format(snomedConcept.getEffectiveTime(), DateFormats.SHORT))));
		}
		
		// Optionally requested properties
		boolean requestedChild = parameters.isPropertyRequested(CodeSystemLookupParameters.PROPERTY_CHILD);
		boolean requestedParent = parameters.isPropertyRequested(CodeSystemLookupParameters.PROPERTY_PARENT);
		
			
		if (requestedChild && snomedConcept.getDescendants() != null) {
			for (SnomedConcept child : snomedConcept.getDescendants()) {
				properties.add(new CodeSystemLookupResultParameters.Property()
						.setCode(CodeSystemLookupParameters.PROPERTY_CHILD)
						.setValue(new CodeType(child.getId()))
						.setDescription(SnomedDisplayTermType.PT.getLabel(child))
				);
			}
		}
		
		if (requestedParent && snomedConcept.getAncestors() != null) {
			for (SnomedConcept parent : snomedConcept.getAncestors()) {
				properties.add(new CodeSystemLookupResultParameters.Property()
						.setCode(CodeSystemLookupParameters.PROPERTY_PARENT)
						.setValue(new CodeType(parent.getId()))
						.setDescription(SnomedDisplayTermType.PT.getLabel(parent))
				);
			}
		}
		
		// Relationship Properties
		Set<String> relationshipTypeIds = parameters.getPropertyValues().stream()
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
				.build(codeSystem.getUrl())
				.getRequest()
				.execute(context)
				.forEach(r -> {
					CodeSystemLookupResultParameters.Property property = new CodeSystemLookupResultParameters.Property()
						.setCode(r.getTypeId());
					
					if (r.hasValue()) {
						RelationshipValue value = r.getValueAsObject();
						value.map(
							i -> property.setValue(new IntegerType(i)),
							d -> property.setValue(new DecimalType(d.doubleValue())),
							s -> property.setValue(new StringType(s)));
					} else {
						property.setValue(new CodeType(r.getDestinationId()));
						// TODO set description to destination concept PT?
					}
					
					
					properties.add(property);
				});
		}
		
		if (properties.isEmpty()) {
			return null;
		} else {
			return properties;
		}
	}
	
}
