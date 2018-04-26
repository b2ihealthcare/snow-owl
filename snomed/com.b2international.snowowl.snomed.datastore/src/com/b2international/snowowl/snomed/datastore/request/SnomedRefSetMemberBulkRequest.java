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

import org.eclipse.emf.cdo.CDOObject;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.events.bulk.BulkResponse;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Multimap;

/**
 * @since 6.4
 */
final class SnomedRefSetMemberBulkRequest implements Request<TransactionContext, BulkResponse> {

	private final BulkRequest<TransactionContext> bulkRequest;

	public SnomedRefSetMemberBulkRequest(final BulkRequest<TransactionContext> bulkRequest) {
		this.bulkRequest = bulkRequest;
	}

	@Override
	public BulkResponse execute(final TransactionContext context) {
		
		// Prefetch all component IDs mentioned in reference set member creation requests, abort if any of them can not be found
		final Set<String> requiredComponentIds = FluentIterable.from(bulkRequest.getRequests())
			.filter(SnomedRefSetMemberCreateRequest.class)
			.transformAndConcat(createRequest -> createRequest.getRequiredComponentIds(context))
			.toSet();
		
		final Multimap<Class<? extends CDOObject>, String> componentIdsByType = FluentIterable.from(requiredComponentIds)
			.index(componentId -> {
				switch (SnomedIdentifiers.getComponentCategory(componentId)) {
					case CONCEPT: return Concept.class;
					case DESCRIPTION: return Description.class;
					case RELATIONSHIP: return Relationship.class;
					default: throw new UnsupportedOperationException("Cannot determine CDO class from component ID '" + componentId + "'.");
				}
			});
		
		try {
			
			for (final Entry<Class<? extends CDOObject>, Collection<String>> idsForType : componentIdsByType.asMap().entrySet()) {
				context.lookup(idsForType.getValue(), idsForType.getKey());	
			}
			
		} catch (final ComponentNotFoundException e) {
			throw e.toBadRequestException();
		}
		
		return bulkRequest.execute(context);
	}
}
