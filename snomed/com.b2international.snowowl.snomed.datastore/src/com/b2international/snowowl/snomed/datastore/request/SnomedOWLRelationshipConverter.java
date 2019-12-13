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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.otf.owltoolkit.conversion.AxiomRelationshipConversionService;
import org.snomed.otf.owltoolkit.domain.AxiomRepresentation;
import org.snomed.otf.owltoolkit.domain.Relationship;

import com.b2international.commons.time.TimeUtil;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedOWLRelationshipDocument;
import com.google.common.base.Stopwatch;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Maps;

/**
 * @since 6.23
 */
public final class SnomedOWLRelationshipConverter {

	private static final Logger LOG = LoggerFactory.getLogger(SnomedOWLRelationshipConverter.class);
	
	private final Supplier<AxiomRelationshipConversionService> conversionService;
	
	public SnomedOWLRelationshipConverter(Set<Long> ungroupedAttributes, Collection<Long> objectAttributes, Collection<Long> dataAttributes) {
		this.conversionService = Suppliers.memoize(() -> {
			Stopwatch stopwatch = Stopwatch.createStarted();
			AxiomRelationshipConversionService service = withTccl(() -> new AxiomRelationshipConversionService(ungroupedAttributes, objectAttributes, dataAttributes));
			LOG.debug("SNOMED OWL Toolkit conversion service initialization took {}", TimeUtil.toString(stopwatch));
			return service;
		});
	}
	
	public String fromSnomedOwlRelationships(boolean gci, boolean isPrimitive, String referencedComponentId, List<SnomedOWLRelationshipDocument> relationships) {
		final AxiomRepresentation axiomRepresentation = new AxiomRepresentation();
		final Long referencedComponentIdLong = Long.valueOf(referencedComponentId);
		final Map<Integer, List<Relationship>> owlToolkitRelationships = Maps.newHashMap();
		final Builder<Integer, Relationship> groupedRelationshipMapBuilder = ImmutableMultimap.builder();
		
		for (SnomedOWLRelationshipDocument relationship : relationships) {
			Relationship owlToolKitRelationship = new Relationship(
					relationship.getGroup(),
					Long.valueOf(relationship.getTypeId()),
					Long.valueOf(relationship.getDestinationId()));
			groupedRelationshipMapBuilder.put(relationship.getGroup(), owlToolKitRelationship);
		}
		
		final ImmutableMultimap<Integer, Relationship> groupedRelationshipMap = groupedRelationshipMapBuilder.build();
		for (Integer group : groupedRelationshipMap.keySet()) {
			owlToolkitRelationships.put(group, FluentIterable.from(groupedRelationshipMap.get(group)).toList());
		}
		
		if (gci) {
			axiomRepresentation.setLeftHandSideRelationships(owlToolkitRelationships);
			axiomRepresentation.setRightHandSideNamedConcept(referencedComponentIdLong);
		} else {
			axiomRepresentation.setLeftHandSideNamedConcept(referencedComponentIdLong);
			axiomRepresentation.setRightHandSideRelationships(owlToolkitRelationships);
		}
		
		axiomRepresentation.setPrimitive(isPrimitive);
		return conversionService.get().convertRelationshipsToAxiom(axiomRepresentation);
	}
	
	private static <T> T withTccl(final Callable<T> callable) {

		final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

		try {

			Thread.currentThread().setContextClassLoader(SnomedOWLRelationshipConverter.class.getClassLoader());

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
