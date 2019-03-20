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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.snomed.otf.owltoolkit.conversion.AxiomRelationshipConversionService;
import org.snomed.otf.owltoolkit.conversion.ConversionException;
import org.snomed.otf.owltoolkit.domain.AxiomRepresentation;
import org.snomed.otf.owltoolkit.domain.Relationship;

import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedOWLRelationshipDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

/**
 * @since 6.14 
 */
public final class SnomedOWLExpressionConverter {

	private final BranchContext context;
	private final Supplier<AxiomRelationshipConversionService> conversionService = Suppliers.memoize(() -> new AxiomRelationshipConversionService(getUngroupedRelationshipTypes()));

	public SnomedOWLExpressionConverter(BranchContext context) {
		this.context = checkNotNull(context);
	}

	public SnomedOWLExpressionConverterResult toSnomedOWLRelationships(String referencedComponentId, String owlExpression) {
		if (Strings.isNullOrEmpty(owlExpression)) {
			return SnomedOWLExpressionConverterResult.EMPTY;
		}
		
		try {
			final Long referencedComponentIdLong = Long.valueOf(referencedComponentId);
			final AxiomRepresentation axiomRepresentation = conversionService.get().convertAxiomToRelationships(referencedComponentIdLong, owlExpression);
			
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
		} catch (ConversionException e) {
			throw new SnowowlRuntimeException(e);
		}
	}

	private Set<Long> getUngroupedRelationshipTypes() {
		return SnomedRequests.prepareSearchMember()
			.all()
			.filterByActive(true)
			.filterByProps(Options.builder()
					.put(SnomedRefSetMemberIndexEntry.Fields.MRCM_GROUPED, true)
					.build())
			.filterByRefSetType(SnomedRefSetType.MRCM_ATTRIBUTE_DOMAIN)
			.setFields(SnomedRefSetMemberIndexEntry.Fields.ID, SnomedRefSetMemberIndexEntry.Fields.REFERENCED_COMPONENT_ID)
			.build()
			.execute(context)
			.stream()
			.map(SnomedReferenceSetMember::getReferencedComponent)
			.map(SnomedCoreComponent::getId)
			.map(Long::parseLong)
			.collect(Collectors.toSet());
	}
	
	
	
}
