/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.internal.id.reservations;

import java.util.Collection;
import java.util.Set;

import com.b2international.snowowl.core.domain.PageableCollectionResource;
import com.b2international.snowowl.core.exceptions.NotImplementedException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifier;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.id.reservations.Reservation;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.request.SnomedSearchRequestBuilder;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.inject.Provider;

/**
 * Includes all IDs which are currently in used on the terminology store's MAIN branch (computed dynamically). 
 * 
 * @since 4.0
 */
public class UniqueInStoreReservation implements Reservation {

	private final Provider<IEventBus> bus;

	public UniqueInStoreReservation(Provider<IEventBus> bus) {
		this.bus = bus;
	}
	
	@Override
	public Set<SnomedIdentifier> intersection(Set<SnomedIdentifier> identifiers) {
		final Multimap<ComponentCategory, String> identifiersByCategory = HashMultimap.create();
		identifiers.forEach(identifier -> {
			identifiersByCategory.put(identifier.getComponentCategory(), identifier.toString());
		});
		
		final ImmutableSet.Builder<SnomedIdentifier> intersection = ImmutableSet.builder();
		
		for (ComponentCategory category : identifiersByCategory.keySet()) {
			final SnomedSearchRequestBuilder<?, ? extends PageableCollectionResource<?>> searchRequest;
			final Collection<String> identifiersToCheck = identifiersByCategory.get(category);
			
			switch (category) {
			case CONCEPT:
				searchRequest = SnomedRequests.prepareSearchConcept();
				break;
			case DESCRIPTION:
				searchRequest = SnomedRequests.prepareSearchDescription();
				break;
			case RELATIONSHIP:
				searchRequest = SnomedRequests.prepareSearchRelationship();
				break;
			default: 
				throw new NotImplementedException("Cannot check whether components of type '%s' are unique.", category);
			}
			
			final PageableCollectionResource<?> results = searchRequest
				.all()
				.filterByIds(identifiersToCheck)
				.setFields(SnomedComponentDocument.Fields.ID)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, BranchPathUtils.createMainPath().getPath())
				.execute(bus.get())
				.getSync();
		
			results.stream()
				.filter(SnomedComponent.class::isInstance)
				.map(SnomedComponent.class::cast)
				.map(SnomedComponent::getId)
				.map(SnomedIdentifiers::create)
				.forEach(intersection::add);
		}
		
		return intersection.build();
	}
	
}
