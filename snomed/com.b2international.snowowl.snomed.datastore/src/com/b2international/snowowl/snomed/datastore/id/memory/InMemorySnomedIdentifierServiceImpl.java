/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.id.memory;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.b2international.commons.VerhoeffCheck;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifier;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.id.gen.ItemIdGenerationStrategy;
import com.b2international.snowowl.snomed.datastore.id.reservations.ISnomedIdentiferReservationService;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;

/**
 * In memory implementation of the identifier service.
 * 
 * @since 4.5
 */
public class InMemorySnomedIdentifierServiceImpl implements ISnomedIdentifierService {

	private ISnomedIdentiferReservationService reservationService;
	private ItemIdGenerationStrategy generationStrategy;

	// TODO update that this contains only IDs during transactions
	private Set<String> reservedComponentIds = Sets.newHashSet();

	public InMemorySnomedIdentifierServiceImpl(final ISnomedIdentiferReservationService reservationService,
			final ItemIdGenerationStrategy generationStrategy) {
		this.reservationService = reservationService;
		this.generationStrategy = generationStrategy;
	}

	@Override
	public boolean includes(final SnomedIdentifier identifier) {
		return reservedComponentIds.contains(identifier.toString());
	}

	@Override
	public SnomedIdentifier generate(final String namespace, final ComponentCategory category) {
		checkNotNull(category, "Component category must not be null.");
		checkCategory(category);

		final String componentId = generateId(namespace, category);
		reservedComponentIds.add(componentId);

		return SnomedIdentifiers.of(componentId);
	}

	@Override
	public void register(final SnomedIdentifier identifier) {
		if (reservationService.isReserved(identifier.toString())) {
			// TODO change exception
			throw new RuntimeException("Component ID is already registered.");
		} else {
			reservedComponentIds.add(identifier.toString());
		}
	}

	@Override
	public SnomedIdentifier reserve(final String namespace, final ComponentCategory category) {
		checkNotNull(category, "Component category must not be null.");
		checkCategory(category);

		final String componentId = generateId(namespace, category);
		reservedComponentIds.add(componentId);

		return SnomedIdentifiers.of(componentId);
	}

	@Override
	public void deprecate(final SnomedIdentifier identifier) {
		// TODO what to do in memory implementation?
		// do nothing for now
	}

	@Override
	public void release(final SnomedIdentifier identifier) {
		reservedComponentIds.remove(identifier.toString());
	}

	@Override
	public void publish(final SnomedIdentifier identifier) {
		// TODO what to do in memory implementation?
		// do nothing for now
	}

	private void checkCategory(ComponentCategory category) {
		checkArgument(category == ComponentCategory.CONCEPT || category == ComponentCategory.DESCRIPTION
				|| category == ComponentCategory.RELATIONSHIP, "Cannot generate ID for componentCategory %s", category);
	}

	private String generateId(final String namespace, final ComponentCategory category) {
		String componentId = createComponentId(namespace, category);
		while (reservationService.isReserved(componentId)) {
			componentId = createComponentId(namespace, category);
		}

		return componentId;
	}

	private String createComponentId(final String namespace, final ComponentCategory category) {
		final StringBuilder builder = new StringBuilder();
		// generate the SCT Item ID
		builder.append(generationStrategy.generateItemId());

		// append namespace and the first part of the partition-identifier
		if (Strings.isNullOrEmpty(namespace)) {
			builder.append('0');
		} else {
			builder.append(namespace);
			builder.append('1');
		}

		// append the second part of the partition-identifier
		builder.append(category.ordinal());

		// calc check-digit
		builder.append(VerhoeffCheck.calculateChecksum(builder, false));

		return builder.toString();
	}

}
