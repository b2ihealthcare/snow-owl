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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.otf.owltoolkit.conversion.AxiomRelationshipConversionService;
import org.snomed.otf.owltoolkit.domain.AxiomRepresentation;
import org.snomed.otf.owltoolkit.domain.Relationship;

import com.b2international.commons.options.Options;
import com.b2international.commons.time.TimeUtil;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedOWLRelationshipDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.base.Throwables;

/**
 * @since 6.14 
 */
public final class SnomedOWLExpressionConverter {

	private static final Logger LOG = LoggerFactory.getLogger(SnomedOWLExpressionConverter.class);
	
	private final BranchContext context;
	
	private final Supplier<AxiomRelationshipConversionService> conversionService = Suppliers.memoize(() -> {
		Stopwatch stopwatch = Stopwatch.createStarted();
		AxiomRelationshipConversionService service = withTccl(() -> new AxiomRelationshipConversionService(getUngroupedAttributes()));
		LOG.debug("SNOMED OWL Toolkit conversion service initialization took {}", TimeUtil.toString(stopwatch));
		return service;
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
	
	private static <T> T withTccl(final Callable<T> callable) {

		final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

		try {

			Thread.currentThread().setContextClassLoader(SnomedOWLExpressionConverter.class.getClassLoader());

			try {
				return callable.call();
			} catch (final Exception e) {
				Throwables.propagateIfPossible(e);
				throw new RuntimeException(e);
			}

		} finally {
			Thread.currentThread().setContextClassLoader(contextClassLoader);
		}

	}
	
}
