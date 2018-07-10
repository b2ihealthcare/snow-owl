/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

/**
 * @since 6.5
 * @param <C>
 * @param <T>
 * @param <R>
 */
public final class SnomedBulkRequest<R> extends DelegatingRequest<TransactionContext, TransactionContext, R> {

	SnomedBulkRequest(Request<TransactionContext, R> next) {
		super(next);
	}

	@Override
	public R execute(TransactionContext context) {
		ImmutableList.Builder<SnomedComponentRequest<?>> requests = ImmutableList.builder();
		collectNestedRequests(next(), requests);
		
		// Prefetch all component IDs mentioned in reference set member creation requests, abort if any of them can not be found
		final Set<String> requiredComponentIds = requests.build()
			.stream()
			.flatMap(request -> request.getRequiredComponentIds(context).stream())
			.filter(componentId -> SnomedTerminologyComponentConstants.getTerminologyComponentIdValueSafe(componentId) != -1L) // just in case filter out invalid component IDs
			.collect(Collectors.toSet());
		
		final Multimap<Class<? extends SnomedDocument>, String> componentIdsByType = FluentIterable.from(requiredComponentIds)
				.index(componentId -> {
					switch (SnomedIdentifiers.getComponentCategory(componentId)) {
					case CONCEPT: return SnomedConceptDocument.class;
					case DESCRIPTION: return SnomedDescriptionIndexEntry.class;
					case RELATIONSHIP: return SnomedRelationshipIndexEntry.class;
					default: throw new UnsupportedOperationException("Cannot determine CDO class from component ID '" + componentId + "'.");
					}
				});
		
		try {
				for (final Entry<Class<? extends SnomedDocument>, Collection<String>> idsForType : componentIdsByType.asMap().entrySet()) {
				context.lookup(idsForType.getValue(), idsForType.getKey());	
			}
		} catch (final ComponentNotFoundException e) {
			throw e.toBadRequestException();
		}
		
		// bind additional caches to the context
		TransactionContext newContext = context.inject()
			.bind(Synonyms.class, new Synonyms(context))
			.build();
		
		return next(newContext);
	}

	private static void collectNestedRequests(Request<?, ?> root, ImmutableList.Builder<SnomedComponentRequest<?>> requests) {
		if (root instanceof SnomedComponentRequest<?>) {
			requests.add((SnomedComponentRequest<?>) root);
		} else if (root instanceof DelegatingRequest<?, ?, ?>) {
			collectNestedRequests(((DelegatingRequest<?, ?, ?>) root).next(), requests);
		} else if (root instanceof BulkRequest<?>) {
			((BulkRequest<?>) root).getRequests().forEach(req -> {
				collectNestedRequests(req, requests);
			});
		}
	}
	
}
