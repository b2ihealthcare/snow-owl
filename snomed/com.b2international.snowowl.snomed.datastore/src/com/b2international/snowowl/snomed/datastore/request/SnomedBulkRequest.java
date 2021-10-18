/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.UUID;
import java.util.stream.Collectors;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.core.request.DeleteRequest;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.datastore.index.entry.*;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

/**
 * @since 6.5
 * @param <R> - the response type
 */
public final class SnomedBulkRequest<R> extends DelegatingRequest<TransactionContext, TransactionContext, R> {

	private static final long serialVersionUID = 1L;

	public SnomedBulkRequest(Request<TransactionContext, R> next) {
		super(next);
	}

	@Override
	public R execute(TransactionContext context) {
		ImmutableList.Builder<SnomedComponentRequest<?>> requests = ImmutableList.builder();
		ImmutableList.Builder<DeleteRequest> deletions = ImmutableList.builder();
		collectNestedRequests(next(), requests, deletions);
		
		// Prefetch all component IDs mentioned in reference set member creation requests, abort if any of them can not be found
		final Set<String> requiredComponentIds = requests.build()
			.stream()
			.flatMap(request -> request.getRequiredComponentIds(context).stream())
			.filter(componentId -> SnomedComponent.getTypeSafe(componentId) != null || isMember(componentId)) // just in case filter out invalid component IDs
			.collect(Collectors.toSet());
		
		final Multimap<Class<? extends SnomedDocument>, String> componentIdsByType = HashMultimap.create();
		for (String requiredComponentId : requiredComponentIds) {
			if (!Strings.isNullOrEmpty(requiredComponentId)) {
				componentIdsByType.put(this.getDocType(requiredComponentId), requiredComponentId);
			}
		}
		
		// collect all deleted IDs as well
		deletions.build()
			.stream()
			.map(DeleteRequest::getComponentId)
			.forEach(componentId -> componentIdsByType.put(getDocType(componentId), componentId));
		
		try {
			for (final Entry<Class<? extends SnomedDocument>, Collection<String>> idsForType : componentIdsByType.asMap().entrySet()) {
				context.lookup(idsForType.getValue(), idsForType.getKey());	
			}
		} catch (final ComponentNotFoundException e) {
			throw e.toBadRequestException();
		}
		
		// bind additional caches to the context
		TransactionContext newContext = context.inject()
			.bind(SnomedOWLExpressionConverter.class, new SnomedOWLExpressionConverter(context))
			.build();
		
		return next(newContext);
	}

	private boolean isMember(String componentId) {
		try {
			UUID.fromString(componentId);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
	
	private Class<? extends SnomedDocument> getDocType(String componentId) {
		switch (SnomedComponent.getTypeSafe(componentId)) {
			case SnomedConcept.TYPE: return SnomedConceptDocument.class;
			case SnomedDescription.TYPE: return SnomedDescriptionIndexEntry.class;
			case SnomedRelationship.TYPE: return SnomedRelationshipIndexEntry.class;
			default: {
				if (!isMember(componentId)) {
					throw new BadRequestException("Incorrect SNOMED CT identifier '%s'.", componentId);
				}
				return SnomedRefSetMemberIndexEntry.class;
			}
		}
	}
	
	private static void collectNestedRequests(Request<?, ?> root, ImmutableList.Builder<SnomedComponentRequest<?>> requests, ImmutableList.Builder<DeleteRequest> deletions) {
		if (root instanceof DeleteRequest) {
			deletions.add((DeleteRequest) root);
		} else if (root instanceof SnomedComponentRequest<?>) {
			requests.add((SnomedComponentRequest<?>) root);
		} else if (root instanceof DelegatingRequest<?, ?, ?>) {
			collectNestedRequests(((DelegatingRequest<?, ?, ?>) root).next(), requests, deletions);
		} else if (root instanceof BulkRequest<?>) {
			((BulkRequest<?>) root).getRequests().forEach(req -> {
				collectNestedRequests(req, requests, deletions);
			});
		}
	}
	
}
