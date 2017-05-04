/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newLinkedHashSet;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.VerhoeffCheck;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.store.MemStore;
import com.b2international.snowowl.datastore.store.Store;
import com.b2international.snowowl.snomed.datastore.config.SnomedIdentifierConfiguration;
import com.b2international.snowowl.snomed.datastore.id.AbstractSnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifier;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.id.cis.IdentifierStatus;
import com.b2international.snowowl.snomed.datastore.id.cis.SctId;
import com.b2international.snowowl.snomed.datastore.id.gen.ItemIdGenerationStrategy;
import com.b2international.snowowl.snomed.datastore.id.reservations.ISnomedIdentiferReservationService;
import com.b2international.snowowl.snomed.datastore.internal.id.reservations.SnomedIdentifierReservationServiceImpl;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * {@link Store} based implementation of the identifier service.
 * 
 * @since 4.5
 */
public class DefaultSnomedIdentifierService extends AbstractSnomedIdentifierService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSnomedIdentifierService.class);

	private final Store<SctId> store;
	private final ItemIdGenerationStrategy generationStrategy;

	/*
	 * Tests only
	 */
	DefaultSnomedIdentifierService(final ItemIdGenerationStrategy generationStrategy) {
		this(new MemStore<SctId>(), generationStrategy, new SnomedIdentifierReservationServiceImpl(), new SnomedIdentifierConfiguration());
	}
	
	public DefaultSnomedIdentifierService(final Store<SctId> store, final ItemIdGenerationStrategy generationStrategy,
			final ISnomedIdentiferReservationService reservationService, final SnomedIdentifierConfiguration config) {
		super(reservationService, config);
		
		store.configureSearchable(SctId.Fields.NAMESPACE);
		store.configureSearchable(SctId.Fields.PARTITION_ID);
		store.configureSearchable(SctId.Fields.SEQUENCE);
		store.configureSortable(SctId.Fields.SEQUENCE);
		
		this.store = store;
		this.generationStrategy = generationStrategy;
	}

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

	@Override
	public String generate(final String namespace, final ComponentCategory category) {
		checkNotNull(category, "Component category must not be null.");
		checkCategory(category);

		LOGGER.debug("Generating component ID for category {}.", category.getDisplayName());

		final String componentId = generateId(namespace, category);
		final SctId sctId = buildSctId(componentId, IdentifierStatus.ASSIGNED);
		store.put(componentId, sctId);

		return componentId;
	}

	@Override
	public void register(final String componentId) {
		LOGGER.debug("Registering component ID {}.", componentId);

		final SctId sctId = getSctId(componentId);
		if (!sctId.matches(IdentifierStatus.AVAILABLE, IdentifierStatus.RESERVED)) {
			LOGGER.warn("Cannot register ID {} as it is already present with status {}.", componentId, sctId.getStatus());
		} else {
			sctId.setStatus(IdentifierStatus.ASSIGNED.getSerializedName());
			store.put(componentId, sctId);
		}
	}

	@Override
	public String reserve(final String namespace, final ComponentCategory category) {
		checkNotNull(category, "Component category must not be null.");
		checkCategory(category);

		LOGGER.debug("Reserving component ID for category {}.", category.getDisplayName());

		final String componentId = generateId(namespace, category);
		final SctId sctId = buildSctId(componentId, IdentifierStatus.RESERVED);
		store.put(componentId, sctId);

		return componentId;
	}

	@Override
	public void deprecate(final String componentId) {
		final SctId sctId = getSctId(componentId);
		if (sctId.matches(IdentifierStatus.ASSIGNED, IdentifierStatus.PUBLISHED)) {
			LOGGER.debug("Deprecating component ID {}.", componentId);

			sctId.setStatus(IdentifierStatus.DEPRECATED.getSerializedName());
			store.put(componentId, sctId);
		} else if (!sctId.isDeprecated()) {
			throw new BadRequestException("Cannot deprecate ID in state %s.", sctId.getStatus());
		}
	}

	@Override
	public void release(final String componentId) {
		final SctId sctId = getSctId(componentId);
		if (sctId.matches(IdentifierStatus.ASSIGNED, IdentifierStatus.RESERVED)) {
			LOGGER.debug("Releasing component ID {}.", componentId);
			store.remove(componentId);
		} else if (!sctId.isAvailable()) {
			throw new BadRequestException("Cannot release ID in state %s.", sctId.getStatus());
		}
	}

	@Override
	public void publish(final String componentId) {
		final SctId sctId = getSctId(componentId);
		if (sctId.isAssigned()) {
			LOGGER.debug("Publishing component ID {}.", componentId);
			sctId.setStatus(IdentifierStatus.PUBLISHED.getSerializedName());
			store.put(componentId, sctId);
		} else if (!sctId.isPublished()) {
			throw new BadRequestException("Cannot publish ID '%s' in state %s.", sctId.getSctid(), sctId.getStatus());
		}
	}

	@Override
	public Collection<String> generate(final String namespace, final ComponentCategory category, final int quantity) {
		checkNotNull(category, "Component category must not be null.");
		checkCategory(category);

		LOGGER.debug("Generating {} component IDs for category {}.", quantity, category.getDisplayName());

		final Map<String, SctId> sctIds = Maps.newHashMap();
		final Collection<String> componentIds = generateIds(namespace, category, quantity);

		for (final String componentId : componentIds) {
			final SctId sctId = buildSctId(componentId, IdentifierStatus.ASSIGNED);
			sctIds.put(componentId, sctId);
		}

		store.putAll(sctIds);
		return componentIds;
	}

	@Override
	public void register(final Collection<String> componentIds) {
		
		final Map<String, SctId> sctIds = Maps.newHashMap();
		final Collection<String> registeredComponentIds = Lists.newArrayList();

		LOGGER.debug("Registering {} component IDs.", componentIds.size());

		try {
			
			for (SctId sctId : getSctIds(componentIds)) {
				
				if (!sctId.matches(IdentifierStatus.AVAILABLE, IdentifierStatus.RESERVED)) {
					LOGGER.warn("Cannot register ID {} as it is already present with status {}.", sctId.getSctid(), sctId.getStatus());
				} else {
					sctId.setStatus(IdentifierStatus.ASSIGNED.getSerializedName());
					sctIds.put(sctId.getSctid(), sctId);
					registeredComponentIds.add(sctId.getSctid());
				}
			}

			store.putAll(sctIds);
			
		} catch (Exception e) {
			// remove the registered component IDs
			store.removeAll(registeredComponentIds);
			throw e;
		}
	}

	@Override
	public Collection<String> reserve(final String namespace, final ComponentCategory category, final int quantity) {
		checkNotNull(category, "Component category must not be null.");
		checkCategory(category);

		LOGGER.debug("Reserving {} component IDs for category {}.", quantity, category.getDisplayName());

		final Map<String, SctId> sctIds = Maps.newHashMap();
		final Collection<String> componentIds = generateIds(namespace, category, quantity);

		for (final String componentId : componentIds) {
			final SctId sctId = buildSctId(componentId, IdentifierStatus.RESERVED);
			sctIds.put(componentId, sctId);
		}

		store.putAll(sctIds);
		return componentIds;
	}

	@Override
	public void deprecate(final Collection<String> componentIds) {
		final Map<String, SctId> deprecatedSctIds = Maps.newHashMap();

		LOGGER.debug("Deprecating {} component IDs.", componentIds.size());

		for (SctId sctId : getSctIds(componentIds)) {

			if (sctId.matches(IdentifierStatus.ASSIGNED, IdentifierStatus.PUBLISHED)) {
				sctId.setStatus(IdentifierStatus.DEPRECATED.getSerializedName());
				deprecatedSctIds.put(sctId.getSctid(), sctId);
			} else if (!sctId.isDeprecated()) {
				throw new BadRequestException("Cannot deprecate ID in state %s.", sctId.getStatus());
			}
			
		}
		
		store.putAll(deprecatedSctIds);
	}

	@Override
	public void release(final Collection<String> componentIds) {
		final Collection<String> releasedComponentIds = Lists.newArrayList();

		LOGGER.debug("Releasing {} component IDs.", componentIds.size());

		for (SctId sctId : getSctIds(componentIds)) {
			if (sctId.matches(IdentifierStatus.ASSIGNED, IdentifierStatus.RESERVED)) {
				releasedComponentIds.add(sctId.getSctid());
			} else if (!sctId.isAvailable()) {
				throw new BadRequestException("Cannot release ID in state %s.", sctId.getStatus());
			}
		}
		
		store.removeAll(releasedComponentIds);
	}

	@Override
	public void publish(final Collection<String> componentIds) {
		LOGGER.debug("Publishing {} component IDs.", componentIds.size());
		
		final Collection<SctId> sctIds = getSctIds(componentIds);
		final Collection<SctId> modifiedSctIds = newHashSet();
		final Collection<SctId> problemSctIds = newHashSet();
		
		for (final SctId sctId : sctIds) {
			if (sctId.isAssigned()) {
				sctId.setStatus(IdentifierStatus.PUBLISHED.getSerializedName());
				modifiedSctIds.add(sctId);
			} else if (!sctId.isPublished()) {
				problemSctIds.add(sctId);
			}
		}
		
		store.putAll(Maps.uniqueIndex(modifiedSctIds, new Function<SctId, String>() {
			@Override
			public String apply(SctId id) {
				return id.getSctid();
			}
		}));
		
		if (!problemSctIds.isEmpty()) {
			LOGGER.warn("Could not publish component IDs ({}), because they are not assigned nor already published", problemSctIds);
		}
	}

	@Override
	public Collection<SctId> getSctIds(final Collection<String> componentIds) {
		
		Set<String> uniqueComponentIds = newHashSet(componentIds);
		
		Collection<SctId> existingSctIds = store.get(componentIds);
		
		ImmutableSet<String> existingIds = FluentIterable.from(existingSctIds).transform(new Function<SctId, String>() {
			@Override
			public String apply(SctId input) {
				return input.getSctid();
			}
		}).toSet();
		
		if (existingIds.equals(uniqueComponentIds)) {
			return existingSctIds;
		}
			
		ImmutableList<SctId> newSctIds = FluentIterable.from(Sets.difference(uniqueComponentIds, existingIds)).transform(new Function<String, SctId>() {
			@Override
			public SctId apply(String input) {
				return buildSctId(input, IdentifierStatus.AVAILABLE);
			}
		}).toList();
		
		List<SctId> sctIds = newArrayList(existingSctIds);
		
		sctIds.addAll(newSctIds);
		
		return sctIds;
	}

	@Override
	public boolean importSupported() {
		return true;
	}

	private String generateId(final String namespace, final ComponentCategory category) {
		return generateIds(namespace, category, 1).iterator().next();
	}

	private Collection<String> generateIds(final String namespace, final ComponentCategory category, final int quantity) {
		
		final Set<String> componentIds = newLinkedHashSet();

		while (componentIds.size() < quantity) {
			
			String componentId = generateComponentId(namespace, category);
			
			int i = 1;
			while (isDisallowed(componentId, componentIds)) {
				
				if (i == getConfig().getMaxIdGenerationAttempts()) {
					throw new BadRequestException("Couldn't generate identifier in %s number of attempts", getConfig().getMaxIdGenerationAttempts());
				}
				
				componentId = generateComponentId(namespace, category);
				i++;
			}

			componentIds.add(componentId);
		}

		return componentIds;
	}
	
	private boolean isDisallowed(String componentId, Set<String> componentIds) {
		return componentIds.contains(componentId) || getReservationService().isReserved(componentId) || store.containsKey(componentId);
	}

	private String generateComponentId(final String namespace, final ComponentCategory category) {
		final String selectedNamespace = selectNamespace(namespace);
		final StringBuilder builder = new StringBuilder();

		// generate the SCT Item ID (value can be a function of component category and namespace)
		builder.append(generationStrategy.generateItemId(selectedNamespace, category));

		// append namespace and the first part of the partition-identifier
		if (Strings.isNullOrEmpty(selectedNamespace)) {
			builder.append('0');
		} else {
			builder.append(selectedNamespace);
			builder.append('1');
		}

		// append the second part of the partition-identifier
		builder.append(category.ordinal());

		// calc check-digit
		builder.append(VerhoeffCheck.calculateChecksum(builder, false));

		return builder.toString();
	}

	private SctId buildSctId(final String componentId, final IdentifierStatus status) {
		final SnomedIdentifier identifier = SnomedIdentifiers.create(componentId);
		final SctId sctId = new SctId();
		sctId.setSctid(componentId);
		sctId.setStatus(status.getSerializedName());
		sctId.setSequence(identifier.getItemId());
		sctId.setNamespace(identifier.getNamespace());
		sctId.setPartitionId(String.format("%s%s", identifier.getFormatIdentifier(), identifier.getComponentIdentifier()));
		sctId.setCheckDigit(identifier.getCheckDigit());

		// TODO set remaining attributes
		return sctId;
	}

}
