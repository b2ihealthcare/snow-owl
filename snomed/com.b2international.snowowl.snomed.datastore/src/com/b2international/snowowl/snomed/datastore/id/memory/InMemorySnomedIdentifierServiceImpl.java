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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;

import com.b2international.commons.VerhoeffCheck;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.store.MemStore;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.id.AbstractSnomedIdentifierServiceImpl;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifier;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.id.cis.IdentifierStatus;
import com.b2international.snowowl.snomed.datastore.id.cis.SctId;
import com.b2international.snowowl.snomed.datastore.id.gen.ItemIdGenerationStrategy;
import com.b2international.snowowl.snomed.datastore.id.reservations.ISnomedIdentiferReservationService;
import com.google.common.base.Strings;
import com.google.inject.Provider;

/**
 * In memory implementation of the identifier service.
 * 
 * @since 4.5
 */
public class InMemorySnomedIdentifierServiceImpl extends AbstractSnomedIdentifierServiceImpl {

	private ISnomedIdentiferReservationService reservationService;
	private ItemIdGenerationStrategy generationStrategy;

	private MemStore<SctId> store = new MemStore<SctId>();

	public SctId getSctId(final String componentId) {
		final SctId storedSctId = store.get(componentId);

		if (null != storedSctId) {
			return storedSctId;
		} else {
			return buildSctId(componentId, IdentifierStatus.AVAILABLE);
		}
	}

	public Collection<SctId> getSctIds() {
		return store.values();
	}

	public InMemorySnomedIdentifierServiceImpl(final ItemIdGenerationStrategy generationStrategy,
			final Provider<SnomedTerminologyBrowser> provider) {
		super(provider);
		this.generationStrategy = generationStrategy;
	}

	@Override
	public boolean includes(final SnomedIdentifier identifier) {
		return super.includes(identifier) || getSctId(identifier.toString()).getStatus() != IdentifierStatus.AVAILABLE.getSerializedName();
	}

	@Override
	public String generate(final String namespace, final ComponentCategory category) {
		checkNotNull(category, "Component category must not be null.");
		checkCategory(category);

		final String componentId = generateId(namespace, category);
		final SctId sctId = buildSctId(componentId, IdentifierStatus.ASSIGNED);
		store.put(componentId, sctId);

		return componentId;
	}

	@Override
	public void register(final String componentId) {
		if (reservationService.isReserved(componentId)) {
			// TODO change exception
			throw new RuntimeException("Component ID is already registered.");
		} else {
			final SctId sctId = buildSctId(componentId, IdentifierStatus.ASSIGNED);
			store.put(componentId, sctId);
		}
	}

	@Override
	public String reserve(final String namespace, final ComponentCategory category) {
		checkNotNull(category, "Component category must not be null.");
		checkCategory(category);

		final String componentId = generateId(namespace, category);
		final SctId sctId = buildSctId(componentId, IdentifierStatus.RESERVED);
		store.put(componentId, sctId);

		return componentId;
	}

	@Override
	public void deprecate(final String componentId) {
		final SctId sctId = getSctId(componentId);
		if (hasStatus(sctId, IdentifierStatus.ASSIGNED, IdentifierStatus.PUBLISHED)) {
			sctId.setStatus(IdentifierStatus.DEPRECATED.getSerializedName());
			store.put(componentId, sctId);
		} else {
			throw new BadRequestException(String.format("Cannot deprecate ID in state %s.", sctId.getStatus()));
		}
	}

	@Override
	public void release(final String componentId) {
		final SctId sctId = getSctId(componentId);
		if (hasStatus(sctId, IdentifierStatus.ASSIGNED, IdentifierStatus.RESERVED)) {
			store.remove(componentId);
		} else {
			throw new BadRequestException(String.format("Cannot release ID in state %s.", sctId.getStatus()));
		}
	}

	@Override
	public void publish(final String componentId) {
		final SctId sctId = getSctId(componentId);
		if (hasStatus(sctId, IdentifierStatus.ASSIGNED)) {
			sctId.setStatus(IdentifierStatus.PUBLISHED.getSerializedName());
			store.put(componentId, sctId);
		} else {
			throw new BadRequestException(String.format("Cannot publish ID in state %s.", sctId.getStatus()));
		}
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

	private SctId buildSctId(final String componentId, final IdentifierStatus status) {
		final SnomedIdentifier identifier = SnomedIdentifiers.of(componentId);
		final SctId sctId = new SctId();
		sctId.setSctid(componentId);
		sctId.setStatus(status.getSerializedName());
		sctId.setNamespace(Integer.valueOf(identifier.getNamespace()));
		sctId.setPartitionId(String.valueOf(identifier.getPartitionIdentifier()));
		sctId.setCheckDigit(identifier.getCheckDigit());
		
		// TODO set remaining attributes?

		return sctId;
	}

	private boolean hasStatus(final SctId sctId, final IdentifierStatus... status) {
		for (final IdentifierStatus s : status) {
			if (s.getSerializedName().equals(sctId.getStatus())) {
				return true;
			}

		}

		return false;
	}

}
