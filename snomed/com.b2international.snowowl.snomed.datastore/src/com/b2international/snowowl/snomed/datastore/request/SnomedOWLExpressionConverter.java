/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toSet;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.otf.owltoolkit.conversion.AxiomRelationshipConversionService;
import org.snomed.otf.owltoolkit.domain.AxiomRepresentation;
import org.snomed.otf.owltoolkit.domain.Relationship;

import com.b2international.commons.options.Options;
import com.b2international.commons.time.TimeUtil;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedOWLRelationshipDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

/**
 * @since 6.14 
 */
public final class SnomedOWLExpressionConverter {

	private static final Logger LOG = LoggerFactory.getLogger(SnomedOWLExpressionConverter.class);
	
	private final BranchContext context;
	private final Supplier<AxiomRelationshipConversionService> conversionService = Suppliers.memoize(() -> {
		
		Stopwatch watch = Stopwatch.createStarted();
		
		Set<Long> ungroupedAttributes = getUngroupedAttributes();
		Set<Long> objectAttributes = getObjectAttributes();
		Set<Long> dataAttributes = getDataAttributes();
		
		AxiomRelationshipConversionService conversionService = new AxiomRelationshipConversionService(
				ungroupedAttributes,
				objectAttributes,
				dataAttributes);
		
		LOG.debug(
				"SNOMED OWL Toolkit axiom conversion service initialization took {} (ungrouped attributes {}, model objects {}, model attributes {})",
				TimeUtil.toString(watch), ungroupedAttributes.size(), objectAttributes.size(), dataAttributes.size());
		
		return conversionService;
		
	});
	
	public SnomedOWLExpressionConverter(BranchContext context) {
		this.context = checkNotNull(context);
	}

	public SnomedOWLExpressionConverterResult toSnomedOWLRelationships(String referencedComponentId, String owlExpression) {
		// skip empty or unparseable axioms
		if (Strings.isNullOrEmpty(owlExpression) || owlExpression.startsWith("Prefix") || owlExpression.startsWith("Ontology")) {
			return SnomedOWLExpressionConverterResult.EMPTY;
		}
		
		try {
			final Long referencedComponentIdLong = Long.valueOf(referencedComponentId);
			final AxiomRepresentation axiomRepresentation = conversionService.get().convertAxiomToRelationships(owlExpression);
			
			boolean gci = false;
			Map<Integer, List<Relationship>> relationships = null;
			if (axiomRepresentation != null) {
				if (referencedComponentIdLong.equals(axiomRepresentation.getLeftHandSideNamedConcept())) {
					gci = false;
					relationships = axiomRepresentation.getRightHandSideRelationships();
				} else if (referencedComponentIdLong.equals(axiomRepresentation.getRightHandSideNamedConcept())) {
					gci = true;
					relationships = axiomRepresentation.getLeftHandSideRelationships();
				}
			}
			
			if (relationships == null) {
				return SnomedOWLExpressionConverterResult.EMPTY;
			}
			
			List<SnomedOWLRelationshipDocument> axiomRelationships = relationships
				.values()
				.stream()
				.flatMap(List::stream)
				.map(r -> {
					return new SnomedOWLRelationshipDocument(Long.toString(r.getTypeId()), Long.toString(r.getDestinationId()), r.getGroup());
				})
				.collect(Collectors.toList());
			
			return new SnomedOWLExpressionConverterResult(gci ? null : axiomRelationships, gci ? axiomRelationships : null);
		} catch (Exception e) {
			LOG.error("Failed to convert OWL axiom '{}' to relationship representations for concept '{}'", owlExpression, referencedComponentId, e);
			return SnomedOWLExpressionConverterResult.EMPTY;
		}
	}

	private Set<Long> getUngroupedAttributes() {
		return SnomedRequests.prepareSearchMember()
			.all()
			.filterByActive(true)
			.filterByProps(Options.builder()
					.put(SnomedRefSetMemberIndexEntry.Fields.MRCM_GROUPED, false)
					.build())
			.filterByRefSetType(SnomedRefSetType.MRCM_ATTRIBUTE_DOMAIN)
			.build()
			.execute(context)
			.stream()
			.map(SnomedReferenceSetMember::getReferencedComponent)
			.map(SnomedCoreComponent::getId)
			.map(Long::valueOf)
			.collect(Collectors.toSet());
	}
	
	private Set<Long> getObjectAttributes() {
		return SnomedRequests.prepareSearchConcept()
			.all()
			.filterByActive(true)
			.filterByStatedAncestor(Concepts.CONCEPT_MODEL_OBJECT_ATTRIBUTE)
			.setFields(SnomedConceptDocument.Fields.ID)
			.build()
			.execute(context)
			.stream()
			.map(SnomedConcept::getId)
			.map(Long::valueOf)
			.collect(toSet());
	}
	
	private Set<Long> getDataAttributes() {
		return SnomedRequests.prepareSearchConcept()
			.all()
			.filterByActive(true)
			.filterByStatedAncestor(Concepts.CONCEPT_MODEL_DATA_ATTRIBUTE)
			.setFields(SnomedConceptDocument.Fields.ID)
			.build()
			.execute(context)
			.stream()
			.map(SnomedConcept::getId)
			.map(Long::valueOf)
			.collect(toSet());
	}
	
}
