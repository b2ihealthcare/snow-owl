/*
 * Copyright 2019-2024 B2i Healthcare, https://b2ihealthcare.com
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

import static com.google.common.collect.Lists.newArrayList;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.otf.owltoolkit.conversion.AxiomRelationshipConversionService;
import org.snomed.otf.owltoolkit.conversion.ConversionException;
import org.snomed.otf.owltoolkit.domain.AxiomRepresentation;
import org.snomed.otf.owltoolkit.domain.Relationship;
import org.snomed.otf.owltoolkit.domain.Relationship.ConcreteValue;

import com.b2international.commons.exceptions.ApiException;
import com.b2international.commons.options.Options;
import com.b2international.commons.time.TimeUtil;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.core.domain.RelationshipValue;
import com.b2international.snowowl.snomed.core.domain.RelationshipValueType;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedOWLRelationshipDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

/**
 * @see AxiomRelationshipConversionService
 * @see <a href="https://www.w3.org/TR/owl2-syntax/#Axioms">OWL 2 Structural Specification and 
 *      Functional-Style Syntax</a>
 * @since 6.14 
 */
public final class SnomedOWLExpressionConverter {

	private static final Logger LOG = LoggerFactory.getLogger(SnomedOWLExpressionConverter.class);
	
	private static final Pattern DIGIT_PATTERN = Pattern.compile(":(?<id>\\d+)");
	
	private static final Set<String> AXIOM_TYPES = Set.of(
		"SubClassOf", 
		"EquivalentClasses", 
		"SubObjectPropertyOf", 
		"SubDataPropertyOf",
		"SubAnnotationPropertyOf"
	);
	
	private final Supplier<AxiomRelationshipConversionService> conversionService;
	
	public SnomedOWLExpressionConverter(BranchContext context) {
		this(Suppliers.memoize(() -> getUngroupedAttributes(context)));
	}
	
	public SnomedOWLExpressionConverter(Supplier<Set<Long>> ungroupedAttributes) {
		this.conversionService = Suppliers.memoize(() -> {
			Stopwatch stopwatch = Stopwatch.createStarted();
			AxiomRelationshipConversionService service = withTccl(() -> new AxiomRelationshipConversionService(ungroupedAttributes.get()));
			LOG.debug("SNOMED OWL Toolkit conversion service initialization took {}", TimeUtil.toString(stopwatch));
			return service;
		});
	}

	private boolean isAxiomSupported(final String axiomExpression) {
		return AXIOM_TYPES.stream().anyMatch(axiomExpression::startsWith);
	}

	private AxiomRepresentation convertAxiom(final String conceptId, final String axiomExpression) {
		try {
			return conversionService.get().convertAxiomToRelationships(axiomExpression);
		} catch (final ConversionException e) {
			LOG.error("Failed to convert OWL axiom '{}' to relationship representations for concept '{}'", axiomExpression, conceptId, e);
			return null;
		}
	}

	public SnomedOWLExpressionConverterResult toSnomedOWLRelationships(String conceptId, String axiomExpression) {
		// Only attempt to convert axioms which the OWL toolkit supports
		if (Strings.isNullOrEmpty(axiomExpression) || !isAxiomSupported(axiomExpression)) {
			return SnomedOWLExpressionConverterResult.EMPTY;
		}
		
		try {
			
			final Long conceptIdLong = Long.valueOf(conceptId);
			final AxiomRepresentation axiomRepresentation = convertAxiom(conceptId, axiomExpression);
			
			if (axiomRepresentation == null) {
				return SnomedOWLExpressionConverterResult.EMPTY;
			}
			
			boolean gci = false;
			Map<Integer, List<Relationship>> relationships = null;
			
			if (conceptIdLong.equals(axiomRepresentation.getLeftHandSideNamedConcept())) {
				relationships = axiomRepresentation.getRightHandSideRelationships();
			} else if (conceptIdLong.equals(axiomRepresentation.getRightHandSideNamedConcept())) {
				/*
				 * XXX: EquivalentClasses axioms are not ordered in any meaningful way, so it
				 * can happen that the class expression representing relationships falls on the left-hand side
				 */
				gci = axiomRepresentation.isPrimitive();
				relationships = axiomRepresentation.getLeftHandSideRelationships();
			} else {
				LOG.warn("Illegal assignment of referenced component id ('{}') was detected for the OWL expression: '{}'", conceptId, axiomExpression);
			}
			
			if (relationships == null) {
				return SnomedOWLExpressionConverterResult.EMPTY;
			}
			
			final List<SnomedOWLRelationshipDocument> convertedRelationships = relationships.values()
				.stream()
				.flatMap(List::stream)
				.map(relationship -> {
					if (relationship.isConcrete()) {
						return SnomedOWLRelationshipDocument.createValue(
							Long.toString(relationship.getTypeId()), 
							toRelationshipValue(relationship.getValue()), 
							relationship.getGroup());
					} else {
						return SnomedOWLRelationshipDocument.create(
							Long.toString(relationship.getTypeId()), 
							Long.toString(relationship.getDestinationId()), 
							relationship.getGroup());
					}
				})
				.collect(Collectors.toList());
			
			return new SnomedOWLExpressionConverterResult(
				gci ? null : convertedRelationships, 
				gci ? convertedRelationships : null);
			
		} catch (ApiException e) {
			throw e;
		} catch (Exception e) {
			LOG.error("Failed to convert OWL axiom '{}' to relationship representations for concept '{}'", axiomExpression, conceptId, e);
			return SnomedOWLExpressionConverterResult.EMPTY;
		}
	}

	private RelationshipValue toRelationshipValue(ConcreteValue value) {
		/*
		 * XXX: preserve the OWL2 XSD type, but use the exact numeric representation
		 * given; eg. "50"^^decimal should have decimal type, with "50", a fraction-less
		 * BigDecimal as its value.
		 * 
		 * It is an implementation-dependent feature that "asString" returns the
		 * raw value for numbers as well, this might change in the future!
		 */
		final String rawValue = value.asString();
		final ConcreteValue.Type type = value.getType();
		switch (type) {
			case DECIMAL: return RelationshipValue.fromTypeAndObjects(RelationshipValueType.DECIMAL, new BigDecimal(rawValue), null);
			case INTEGER: return RelationshipValue.fromTypeAndObjects(RelationshipValueType.INTEGER, new BigDecimal(rawValue), null);
			case STRING: return new RelationshipValue(rawValue);
			default: throw new IllegalStateException("Unexpected concrete value type '" + type + "'.");
		}
	}
	
	public static List<String> getIdsFromOwlExpression(String expression) {
		
		if (Strings.isNullOrEmpty(expression)) {
			return List.of();
		}
		
		Matcher matcher = DIGIT_PATTERN.matcher(expression);
		
		List<String> expressionIds = newArrayList();
		
		while (matcher.find()) {
			
			String id = matcher.group("id");
			
			if (SnomedIdentifiers.isValid(id)) {
				expressionIds.add(id);
			}
			
		}
		
		return expressionIds;
		
	}

	private static Set<Long> getUngroupedAttributes(BranchContext context) {
		return SnomedRequests.prepareSearchMember()
			.setLimit(1000)
			.filterByActive(true)
			.filterByProps(Options.builder()
				.put(SnomedRefSetMemberIndexEntry.Fields.MRCM_GROUPED, false)
				.build())
			.filterByRefSetType(SnomedRefSetType.MRCM_ATTRIBUTE_DOMAIN)
			.stream(context)
			.flatMap(SnomedReferenceSetMembers::stream)
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
			} catch (final RuntimeException e) {
				throw e;
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}

		} finally {
			Thread.currentThread().setContextClassLoader(contextClassLoader);
		}
	}
}
