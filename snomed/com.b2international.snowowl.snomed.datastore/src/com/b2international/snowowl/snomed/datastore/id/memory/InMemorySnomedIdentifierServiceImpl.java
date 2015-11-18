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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.google.common.collect.Lists;
import com.google.inject.Provider;

/**
 * In memory implementation of the identifier service.
 * 
 * @since 4.5
 */
public class InMemorySnomedIdentifierServiceImpl extends AbstractSnomedIdentifierServiceImpl {

	private static final Logger LOGGER = LoggerFactory.getLogger(InMemorySnomedIdentifierServiceImpl.class);

	private ItemIdGenerationStrategy generationStrategy;

	private MemStore<SctId> store = new MemStore<SctId>();

	@Override
	public SctId getSctId(final String componentId) {
		final SctId storedSctId = store.get(componentId);

		if (null != storedSctId) {
			return storedSctId;
		} else {
			return buildSctId(componentId, IdentifierStatus.AVAILABLE);
		}
	}

	@Override
	public Collection<SctId> getSctIds() {
		return store.values();
	}

	public InMemorySnomedIdentifierServiceImpl(final ItemIdGenerationStrategy generationStrategy,
			final Provider<SnomedTerminologyBrowser> provider, final ISnomedIdentiferReservationService reservationService) {
		super(provider, reservationService);
		this.generationStrategy = generationStrategy;
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
		if (contains(componentId)) {
			final SctId sctId = getSctId(componentId);
			if (hasStatus(sctId, IdentifierStatus.AVAILABLE, IdentifierStatus.RESERVED)) {
				sctId.setStatus(IdentifierStatus.ASSIGNED.getSerializedName());
				store.put(componentId, sctId);
			} else {
				LOGGER.warn(
						String.format("Cannot register ID %s as it is already present with status %s.", componentId, sctId.getStatus()));
			}
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
		} else if (hasStatus(sctId, IdentifierStatus.AVAILABLE)) {
			// do nothing
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

	@Override
	public boolean contains(final String componentId) {
		return store.containsKey(componentId);
	}

	@Override
	public Collection<String> bulkGenerate(final String namespace, final ComponentCategory category, final int quantity) {
		checkNotNull(category, "Component category must not be null.");
		checkCategory(category);

		final Collection<String> componentIds = generateIds(namespace, category, quantity);
		for (final String componentId : componentIds) {
			final SctId sctId = buildSctId(componentId, IdentifierStatus.ASSIGNED);
			store.put(componentId, sctId);
		}

		return componentIds;
	}

	@Override
	public void bulkRegister(final Collection<String> componentIds) {
		final Collection<String> registeredComponentIds = Lists.newArrayList();

		try {
			for (final String componentId : componentIds) {
				if (contains(componentId)) {
					final SctId sctId = getSctId(componentId);
					if (hasStatus(sctId, IdentifierStatus.AVAILABLE, IdentifierStatus.RESERVED)) {
						sctId.setStatus(IdentifierStatus.ASSIGNED.getSerializedName());
						store.put(componentId, sctId);
						registeredComponentIds.add(componentId);
					} else {
						LOGGER.warn(String.format("Cannot register ID %s as it is already present with status %s.", componentId,
								sctId.getStatus()));
					}
				} else {
					final SctId sctId = buildSctId(componentId, IdentifierStatus.ASSIGNED);
					store.put(componentId, sctId);
					registeredComponentIds.add(componentId);
				}
			}
		} catch (Exception e) {
			// remove the registered component IDs
			for (final String componentId : registeredComponentIds) {
				store.remove(componentId);
			}

			throw e;
		}
	}

	@Override
	public Collection<String> bulkReserve(final String namespace, final ComponentCategory category, final int quantity) {
		checkNotNull(category, "Component category must not be null.");
		checkCategory(category);

		final Collection<String> componentIds = generateIds(namespace, category, quantity);
		for (final String componentId : componentIds) {
			final SctId sctId = buildSctId(componentId, IdentifierStatus.RESERVED);
			store.put(componentId, sctId);
		}

		return componentIds;
	}

	@Override
	public void bulkDeprecate(final Collection<String> componentIds) {
		final Collection<SctId> deprecatedSctIds = Lists.newArrayList();

		for (final String componentId : componentIds) {
			final SctId sctId = getSctId(componentId);

			if (hasStatus(sctId, IdentifierStatus.ASSIGNED, IdentifierStatus.PUBLISHED)) {
				sctId.setStatus(IdentifierStatus.DEPRECATED.getSerializedName());
				deprecatedSctIds.add(sctId);
			} else {
				throw new BadRequestException(String.format("Cannot deprecate ID in state %s.", sctId.getStatus()));
			}
		}

		putAll(deprecatedSctIds);
	}

	@Override
	public void bulkRelease(final Collection<String> componentIds) {
		final Collection<String> releasedComponentIds = Lists.newArrayList();

		for (final String componentId : componentIds) {
			final SctId sctId = getSctId(componentId);

			if (hasStatus(sctId, IdentifierStatus.ASSIGNED, IdentifierStatus.RESERVED)) {
				releasedComponentIds.add(componentId);
			} else if (hasStatus(sctId, IdentifierStatus.AVAILABLE)) {
				// do nothing
			} else {
				throw new BadRequestException(String.format("Cannot release ID in state %s.", sctId.getStatus()));
			}
		}

		for (final String componentId : releasedComponentIds) {
			store.remove(componentId);
		}
	}

	@Override
	public void bulkPublish(final Collection<String> componentIds) {
		final Collection<SctId> publishedSctIds = Lists.newArrayList();

		for (final String componentId : componentIds) {
			final SctId sctId = getSctId(componentId);

			if (hasStatus(sctId, IdentifierStatus.ASSIGNED)) {
				sctId.setStatus(IdentifierStatus.PUBLISHED.getSerializedName());
				publishedSctIds.add(sctId);
			} else {
				throw new BadRequestException(String.format("Cannot publish ID in state %s.", sctId.getStatus()));
			}
		}

		putAll(publishedSctIds);
	}

	@Override
	public Collection<SctId> getSctIds(final Collection<String> componentIds) {
		final Collection<SctId> sctIds = Lists.newArrayList();
		for (final String componentId : componentIds) {
			sctIds.add(getSctId(componentId));
		}

		return sctIds;
	}
	
	private String generateId(final String namespace, final ComponentCategory category) {
		return generateIds(namespace, category, 1).iterator().next();
	}

	private Collection<String> generateIds(final String namespace, final ComponentCategory category, final int quantity) {
		int i = 0;
		final Collection<String> componentIds = Lists.newArrayList();

		while (i < quantity) {
			i++;

			String componentId = createComponentId(namespace, category);
			while (reservationService.isReserved(componentId) && !contains(componentId)) {
				componentId = createComponentId(namespace, category);
			}

			componentIds.add(componentId);
		}

		return componentIds;
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
		sctId.setNamespace(null == identifier.getNamespace() ? 0 : Integer.valueOf(identifier.getNamespace()));
		sctId.setPartitionId(String.valueOf(identifier.getPartitionIdentifier()));
		sctId.setCheckDigit(identifier.getCheckDigit());

		// TODO set remaining attributes?

		return sctId;
	}

	private void putAll(final Collection<SctId> sctIds) {
		for (final SctId sctId : sctIds) {
			store.put(sctId.getSctid(), sctId);
		}
	}

}
