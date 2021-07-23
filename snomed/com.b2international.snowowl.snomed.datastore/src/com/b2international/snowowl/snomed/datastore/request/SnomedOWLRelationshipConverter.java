/*
 * Copyright 2019-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.otf.owltoolkit.conversion.AxiomRelationshipConversionService;
import org.snomed.otf.owltoolkit.conversion.ConversionException;
import org.snomed.otf.owltoolkit.domain.AxiomRepresentation;
import org.snomed.otf.owltoolkit.domain.Relationship;
import org.snomed.otf.owltoolkit.domain.Relationship.ConcreteValue;

import com.b2international.commons.time.TimeUtil;
import com.b2international.snowowl.snomed.core.domain.RelationshipValue;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedOWLRelationshipDocument;
import com.google.common.base.Stopwatch;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

/**
 * @since 6.23
 */
public final class SnomedOWLRelationshipConverter {

	private static final Logger LOG = LoggerFactory.getLogger(SnomedOWLRelationshipConverter.class);

	private final Supplier<AxiomRelationshipConversionService> conversionService;

	public SnomedOWLRelationshipConverter(final Set<Long> ungroupedAttributes, final Collection<Long> objectAttributes, final Collection<Long> dataAttributes) {
		this.conversionService = Suppliers.memoize(() -> {
			final Stopwatch stopwatch = Stopwatch.createStarted();
			final AxiomRelationshipConversionService service = withTccl(() -> new AxiomRelationshipConversionService(ungroupedAttributes, objectAttributes, dataAttributes));
			LOG.debug("SNOMED OWL Toolkit conversion service initialization took {}", TimeUtil.toString(stopwatch));
			return service;
		});
	}

	public String fromSnomedOwlRelationships(
		final boolean gci, 
		final boolean isPrimitive, 
		final String conceptId, 
		final List<SnomedOWLRelationshipDocument> owlRelationships) {
		
		final AxiomRepresentation axiomRepresentation = new AxiomRepresentation();
		final Long conceptIdLong = Long.valueOf(conceptId);
		final ListMultimap<Integer, Relationship> relationships = ArrayListMultimap.create();

		for (final SnomedOWLRelationshipDocument owlRelationship : owlRelationships) {

			final Relationship relationship;
			if (owlRelationship.hasValue()) {
				relationship = new Relationship(
						owlRelationship.getRelationshipGroup(),
						Long.valueOf(owlRelationship.getTypeId()),
						toConcreteValue(owlRelationship.getValueAsObject()));
			} else {
				relationship = new Relationship(
						owlRelationship.getRelationshipGroup(),
						Long.valueOf(owlRelationship.getTypeId()),
						Long.valueOf(owlRelationship.getDestinationId()));
			}
			
			relationships.put(relationship.getGroup(), relationship);
		}

		final Map<Integer, List<Relationship>> relationshipsMap = Multimaps.asMap(relationships);
		
		if (gci) {
			axiomRepresentation.setLeftHandSideRelationships(relationshipsMap);
			axiomRepresentation.setRightHandSideNamedConcept(conceptIdLong);
		} else {
			axiomRepresentation.setLeftHandSideNamedConcept(conceptIdLong);
			axiomRepresentation.setRightHandSideRelationships(relationshipsMap);
		}

		axiomRepresentation.setPrimitive(isPrimitive);
		return convertRelationshipToAxiom(conceptId, axiomRepresentation);
	}

	private ConcreteValue toConcreteValue(final RelationshipValue value) {
		return value.map(
			i -> new ConcreteValue(ConcreteValue.Type.INTEGER, i.toString()), 
			d -> new ConcreteValue(ConcreteValue.Type.DECIMAL, d.toString()), 
			s -> new ConcreteValue(ConcreteValue.Type.STRING, s));
	}

	private String convertRelationshipToAxiom(final String conceptId, final AxiomRepresentation axiomRepresentation) {
		try {
			return conversionService.get().convertRelationshipsToAxiom(axiomRepresentation);
		} catch (final ConversionException e) {
			LOG.error("Failed to convert axiom for concept {}", conceptId, e);
			throw new RuntimeException(e);
		}
	}

	private static <T> T withTccl(final Callable<T> callable) {
		final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

		try {
			Thread.currentThread().setContextClassLoader(SnomedOWLRelationshipConverter.class.getClassLoader());

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
