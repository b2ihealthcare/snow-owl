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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newLinkedHashSet;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.emf.cdo.spi.server.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.VerhoeffCheck;
import com.b2international.index.DocSearcher;
import com.b2international.index.Hits;
import com.b2international.index.Index;
import com.b2international.index.IndexRead;
import com.b2international.index.IndexWrite;
import com.b2international.index.Writer;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.datastore.config.SnomedIdentifierConfiguration;
import com.b2international.snowowl.snomed.datastore.id.AbstractSnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.id.domain.IdentifierStatus;
import com.b2international.snowowl.snomed.datastore.id.domain.SctId;
import com.b2international.snowowl.snomed.datastore.id.gen.ItemIdGenerationStrategy;
import com.b2international.snowowl.snomed.datastore.id.reservations.ISnomedIdentiferReservationService;
import com.b2international.snowowl.snomed.datastore.internal.id.reservations.SnomedIdentifierReservationServiceImpl;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Provider;

/**
 * {@link Store} based implementation of the identifier service.
 * 
 * @since 4.5
 */
public class DefaultSnomedIdentifierService extends AbstractSnomedIdentifierService implements IDisposableService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSnomedIdentifierService.class);

	private final Index store;
	private final ItemIdGenerationStrategy generationStrategy;
	private final AtomicBoolean disposed = new AtomicBoolean(false);

	/*
	 * Tests only
	 */
	DefaultSnomedIdentifierService(final Provider<Index> store, final ItemIdGenerationStrategy generationStrategy) {
		this(store.get(), generationStrategy, new SnomedIdentifierReservationServiceImpl(), new SnomedIdentifierConfiguration());
	}
	
	public DefaultSnomedIdentifierService(final Index store, final ItemIdGenerationStrategy generationStrategy,
			final ISnomedIdentiferReservationService reservationService, final SnomedIdentifierConfiguration config) {
		super(reservationService, config);
		this.store = store;
		this.generationStrategy = generationStrategy;
	}

	@Override
	public Set<String> generate(final String namespace, final ComponentCategory category, final int quantity) {
		checkNotNull(category, "Component category must not be null.");
		checkArgument(quantity > 0, "Number of requested IDs should be non-negative.");
		checkCategory(category);

		LOGGER.debug("Generating {} component IDs for category {}.", quantity, category.getDisplayName());

		final Set<String> componentIds = generateIds(namespace, category, quantity);
		final Map<String, SctId> sctIds = FluentIterable.from(componentIds).toMap(componentId -> buildSctId(componentId, IdentifierStatus.ASSIGNED));
		putSctIds(sctIds);
		return componentIds;
	}

	@Override
	public void register(final Set<String> componentIds) {
		if (CompareUtils.isEmpty(componentIds)) {
			return;
		}
		LOGGER.debug(String.format("Registering {} component IDs.", componentIds.size()));

		final Map<String, SctId> sctIds = getSctIds(componentIds);
		final Map<String, SctId> problemSctIds = ImmutableMap.copyOf(Maps.filterValues(sctIds, Predicates.<SctId>not(Predicates.or(
				SctId::isAvailable, 
				SctId::isReserved, 
				SctId::isAssigned))));
		
		if (!problemSctIds.isEmpty()) {
			throw new SctIdStatusException("Cannot register %s component IDs because they are not available, reserved, or already assigned.", problemSctIds);
		}

		final Map<String, SctId> availableOrReservedSctIds = ImmutableMap.copyOf(Maps.filterValues(sctIds, Predicates.or(
				SctId::isAvailable, 
				SctId::isReserved)));
		
		for (final SctId sctId : availableOrReservedSctIds.values()) {
			sctId.setStatus(IdentifierStatus.ASSIGNED.getSerializedName());
		}
		
		putSctIds(availableOrReservedSctIds);
	}

	@Override
	public Set<String> reserve(final String namespace, final ComponentCategory category, final int quantity) {
		checkNotNull(category, "Component category must not be null.");
		checkArgument(quantity > 0, "Number of requested IDs should be greater than zero.");
		checkCategory(category);

		LOGGER.debug("Reserving {} component IDs for category {}.", quantity, category.getDisplayName());

		final Set<String> componentIds = generateIds(namespace, category, quantity);
		final Map<String, SctId> sctIds = FluentIterable.from(componentIds).toMap(componentId -> buildSctId(componentId, IdentifierStatus.RESERVED));
		putSctIds(sctIds);
		return componentIds;
	}

	@Override
	public void release(final Set<String> componentIds) {
		LOGGER.debug("Releasing {} component IDs.", componentIds.size());

		final Map<String, SctId> sctIds = getSctIds(componentIds);
		final Map<String, SctId> problemSctIds = ImmutableMap.copyOf(Maps.filterValues(sctIds, Predicates.<SctId>not(Predicates.or(
				SctId::isAssigned, 
				SctId::isReserved, 
				SctId::isAvailable))));

		if (!problemSctIds.isEmpty()) {
			throw new SctIdStatusException("Cannot release %s component IDs because they are not assigned, reserved, or already available.", problemSctIds);
		}

		final Map<String, SctId> assignedOrReservedSctIds = ImmutableMap.copyOf(Maps.filterValues(sctIds, Predicates.or(
				SctId::isAssigned, 
				SctId::isReserved)));
	
		// XXX: It might be better to keep the last state change recorded in the index on these SctIds, but for now we remove them entirely
		removeSctIds(assignedOrReservedSctIds.keySet());
	}

	@Override
	public void deprecate(final Set<String> componentIds) {
		LOGGER.debug("Deprecating {} component IDs.", componentIds.size());

		final Map<String, SctId> sctIds = getSctIds(componentIds);
		final Map<String, SctId> problemSctIds = ImmutableMap.copyOf(Maps.filterValues(sctIds, Predicates.<SctId>not(Predicates.or(
				SctId::isAssigned, 
				SctId::isPublished, 
				SctId::isDeprecated))));
		
		if (!problemSctIds.isEmpty()) {
			throw new SctIdStatusException("Cannot deprecate %s component IDs because they are not assigned, published, or already deprecated.", problemSctIds);
		}

		final Map<String, SctId> assignedOrPublishedSctIds = ImmutableMap.copyOf(Maps.filterValues(sctIds, Predicates.or(
				SctId::isAssigned, 
				SctId::isPublished)));
		
		for (final SctId sctId : assignedOrPublishedSctIds.values()) {
			sctId.setStatus(IdentifierStatus.DEPRECATED.getSerializedName());
		}
		
		putSctIds(assignedOrPublishedSctIds);
	}

	@Override
	public void publish(final Set<String> componentIds) {
		LOGGER.debug("Publishing {} component IDs.", componentIds.size());
		
		final Map<String, SctId> sctIds = getSctIds(componentIds);
		final Map<String, SctId> problemSctIds = ImmutableMap.copyOf(Maps.filterValues(sctIds, Predicates.<SctId>not(Predicates.or(
				SctId::isAssigned, 
				SctId::isPublished))));
		
		final Map<String, SctId> assignedSctIds = ImmutableMap.copyOf(Maps.filterValues(sctIds, SctId::isAssigned));
		
		for (final SctId sctId : assignedSctIds.values()) {
			sctId.setStatus(IdentifierStatus.PUBLISHED.getSerializedName());
		}
		
		putSctIds(assignedSctIds);
		
		if (!problemSctIds.isEmpty()) {
			LOGGER.warn("Cannot publish the following component IDs because they are not assigned or already published: {}", problemSctIds);
		}
	}

	@Override
	public Map<String, SctId> getSctIds(final Set<String> componentIds) {
		final Query<SctId> getSctIdsQuery = Query.select(SctId.class)
				.where(Expressions.matchAny(DocumentMapping._ID, componentIds))
				.limit(componentIds.size())
				.build();
		
		final Hits<SctId> existingIds = store.read(index -> index.search(getSctIdsQuery));
		final Map<String, SctId> existingIdsMap = Maps.uniqueIndex(existingIds, SctId::getSctid);
		
		if (existingIdsMap.size() == componentIds.size()) {
			return existingIdsMap;
		} else {
			final Set<String> knownComponentIds = existingIdsMap.keySet();
			final Set<String> difference = ImmutableSet.copyOf(Sets.difference(componentIds, knownComponentIds));
			
			final ImmutableMap.Builder<String, SctId> resultBuilder = ImmutableMap.builder();
			resultBuilder.putAll(existingIdsMap);
			
			for (final String componentId : difference) {
				resultBuilder.put(componentId, buildSctId(componentId, IdentifierStatus.AVAILABLE));
			}
			
			return resultBuilder.build();
		}
	}

	@Override
	public boolean importSupported() {
		return true;
	}

	private Set<String> generateIds(final String namespace, final ComponentCategory category, final int quantity) {
		final Set<String> componentIds = newLinkedHashSet(); // important to keep order of generated ids
		final int maxAttempts = getConfig().getMaxIdGenerationAttempts();

		for (int i = 0; i < quantity; i++) {
			final String componentId = generateId(namespace, category, maxAttempts, componentIds);
			componentIds.add(componentId);
		}

		return ImmutableSet.<String>copyOf(componentIds);
	}

	private String generateId(final String namespace, final ComponentCategory category, final int maxAttempts, Set<String> componentIds) {
		for (int attempt = 1; attempt <= maxAttempts; attempt++) {
			final String componentId = generateId(namespace, category, attempt);
			if (!isGeneratedIdDisallowed(componentId, componentIds)) {
				return componentId;
			}
		}
		
		throw new BadRequestException("Couldn't generate identifier in maximum (%s) number of attempts", maxAttempts);
	}
	
	private String generateId(final String namespace, final ComponentCategory category, final int attempt) {
		final StringBuilder builder = new StringBuilder();
	
		// generate the item identifier (value can be a function of component category and namespace)
		builder.append(generationStrategy.generateItemId(namespace, category, attempt));
	
		// append namespace and the first digit of the partition-identifier
		if (Strings.isNullOrEmpty(namespace)) {
			builder.append('0');
		} else {
			builder.append(namespace);
			builder.append('1');
		}
	
		// append the second digit of the partition-identifier
		builder.append(category.ordinal());
	
		// add Verhoeff check digit last
		builder.append(VerhoeffCheck.calculateChecksum(builder, false));
	
		return builder.toString();
	}

	private boolean isGeneratedIdDisallowed(String componentId, Set<String> componentIds) {
		
		if (componentIds.contains(componentId)) {
			return true;
		}
		
		if (getReservationService().isReserved(componentId)) {
			return true;
		}
		
		final SctId sctId = readSctId(componentId);
		
		if (sctId == null) {
			return false;
		} else {
			return !sctId.isAvailable();
		}
	}

	private SctId buildSctId(final String componentId, final IdentifierStatus status) {
		final SctId sctId = new SctId();
		
		sctId.setSctid(componentId);
		sctId.setStatus(status.getSerializedName());
		sctId.setSequence(SnomedIdentifiers.getItemId(componentId));
		sctId.setNamespace(SnomedIdentifiers.getNamespace(componentId));
		sctId.setPartitionId(SnomedIdentifiers.getPartitionId(componentId));
		sctId.setCheckDigit(SnomedIdentifiers.getCheckDigit(componentId));

		// TODO: Other attributes of SctId could also be set here
		return sctId;
	}
	
	private void putSctIds(final Map<String, SctId> ids) {
		store.write(new IndexWrite<Void>() {
			@Override
			public Void execute(Writer index) throws IOException {
				index.putAll(ids);
				index.commit();
				return null;
			}
		});
	}
	
	private void removeSctIds(final Set<String> ids) {
		store.write(new IndexWrite<Void>() {
			@Override
			public Void execute(Writer index) throws IOException {
				index.removeAll(ImmutableMap.<Class<?>, Set<String>>of(SctId.class, ids));
				index.commit();
				return null;
			}
		});
	}
	
	private SctId readSctId(final String id) {
		return store.read(new IndexRead<SctId>() {
			@Override
			public SctId execute(DocSearcher index) throws IOException {
				return index.get(SctId.class, id);
			}
		});
	}
	
	@Override
	public void dispose() {
		if (disposed.compareAndSet(false, true)) {
			store.admin().close();
		}
	}
	
	@Override
	public boolean isDisposed() {
		return disposed.get();
	}
	
}
