/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.cis.memory;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newLinkedHashSet;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.VerhoeffCheck;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.index.Hits;
import com.b2international.index.Index;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.cis.AbstractSnomedIdentifierService;
import com.b2international.snowowl.snomed.cis.SnomedIdentifierConfiguration;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.cis.domain.IdentifierStatus;
import com.b2international.snowowl.snomed.cis.domain.SctId;
import com.b2international.snowowl.snomed.cis.gen.ItemIdGenerationStrategy;
import com.b2international.snowowl.snomed.cis.internal.reservations.SnomedIdentifierReservationServiceImpl;
import com.b2international.snowowl.snomed.cis.reservations.ISnomedIdentifierReservationService;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Provider;

/**
 * {@link Index} based implementation of the identifier service.
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
	@VisibleForTesting
	public DefaultSnomedIdentifierService(final Provider<Index> store, final ItemIdGenerationStrategy generationStrategy) {
		this(store.get(), generationStrategy, new SnomedIdentifierReservationServiceImpl(), new SnomedIdentifierConfiguration());
	}
	
	public DefaultSnomedIdentifierService(final Index store, final ItemIdGenerationStrategy generationStrategy,
			final ISnomedIdentifierReservationService reservationService, final SnomedIdentifierConfiguration config) {
		super(reservationService, config);
		this.store = store;
		this.generationStrategy = generationStrategy;
	}

	@Override
	public Set<String> generate(final String namespace, final ComponentCategory category, final int quantity) {
		return ImmutableSet.copyOf(generateSctIds(namespace, category, quantity).keySet());
	}
	
	@Override
	public Map<String, SctId> generateSctIds(String namespace, ComponentCategory category, int quantity) {
		checkNotNull(category, "Component category must not be null.");
		checkArgument(quantity > 0, "Number of requested IDs should be non-negative.");
		checkCategory(category);

		LOGGER.debug("Generating {} component IDs for category {}.", quantity, category.getDisplayName());

		final Set<String> componentIds = generateIds(namespace, category, quantity);
		final Map<String, SctId> sctIds = FluentIterable.from(componentIds).toMap(componentId -> buildSctId(componentId, IdentifierStatus.ASSIGNED));
		putSctIds(sctIds);
		return sctIds;
	}

	@Override
	public Map<String, SctId> register(final Set<String> componentIds) {
		if (CompareUtils.isEmpty(componentIds)) {
			return Collections.emptyMap();
		}
		LOGGER.debug("Registering {} component IDs.", componentIds.size());

		final Map<String, SctId> sctIds = getSctIds(componentIds);

		final Map<String, SctId> availableOrReservedSctIds = ImmutableMap.copyOf(Maps.filterValues(sctIds, Predicates.or(
				SctId::isAvailable, 
				SctId::isReserved)));
		
		for (final SctId sctId : availableOrReservedSctIds.values()) {
			sctId.setStatus(IdentifierStatus.ASSIGNED.getSerializedName());
		}
		
		putSctIds(availableOrReservedSctIds);
		
		return ImmutableMap.copyOf(sctIds);
	}

	@Override
	public Set<String> reserve(final String namespace, final ComponentCategory category, final int quantity) {
		return reserveSctIds(namespace, category, quantity).values().stream().map(SctId::getSctid).collect(Collectors.toSet());
	}
	
	@Override
	public Map<String, SctId> reserveSctIds(String namespace, ComponentCategory category, int quantity) {
		checkNotNull(category, "Component category must not be null.");
		checkArgument(quantity > 0, "Number of requested IDs should be greater than zero.");
		checkCategory(category);

		LOGGER.debug("Reserving {} component IDs for category {}.", quantity, category.getDisplayName());

		final Set<String> componentIds = generateIds(namespace, category, quantity);
		final Map<String, SctId> sctIds = FluentIterable.from(componentIds).toMap(componentId -> buildSctId(componentId, IdentifierStatus.RESERVED));
		putSctIds(sctIds);
		return ImmutableMap.copyOf(sctIds);
	}

	@Override
	public Map<String, SctId> release(final Set<String> componentIds) {
		LOGGER.debug("Releasing {} component IDs.", componentIds.size());

		final Map<String, SctId> sctIds = newHashMap(getSctIds(componentIds));

		final Map<String, SctId> assignedOrReservedSctIds = ImmutableMap.copyOf(Maps.filterValues(sctIds, Predicates.or(
				SctId::isAssigned, 
				SctId::isReserved)));
	
		// XXX: It might be better to keep the last state change recorded in the index on these SctIds, but for now we remove them entirely
		removeSctIds(assignedOrReservedSctIds.keySet());
		
		return getSctIds(componentIds);
	}

	@Override
	public Map<String, SctId> deprecate(final Set<String> componentIds) {
		LOGGER.debug("Deprecating {} component IDs.", componentIds.size());

		final Map<String, SctId> sctIds = getSctIds(componentIds);

		final Map<String, SctId> assignedOrPublishedSctIds = ImmutableMap.copyOf(Maps.filterValues(sctIds, Predicates.or(
				SctId::isAssigned, 
				SctId::isPublished)));
		
		for (final SctId sctId : assignedOrPublishedSctIds.values()) {
			sctId.setStatus(IdentifierStatus.DEPRECATED.getSerializedName());
		}
		
		putSctIds(assignedOrPublishedSctIds);
		
		return ImmutableMap.copyOf(sctIds);
	}

	@Override
	public Map<String, SctId> publish(final Set<String> componentIds) {
		LOGGER.debug("Publishing {} component IDs.", componentIds.size());
		
		final Map<String, SctId> sctIds = getSctIds(componentIds);
		
		final Map<String, SctId> sctIdsToPublish = ImmutableMap.copyOf(Maps.filterValues(sctIds, Predicates.not(SctId::isPublished)));
		
		for (final SctId sctId : sctIdsToPublish.values()) {
			sctId.setStatus(IdentifierStatus.PUBLISHED.getSerializedName());
		}
		
		putSctIds(sctIdsToPublish);
		
		return ImmutableMap.copyOf(sctIds);
	}

	@Override
	public Map<String, SctId> getSctIds(final Set<String> componentIds) {
		if (CompareUtils.isEmpty(componentIds)) {
			return Collections.emptyMap();
		}
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
				if (!SnomedIdentifiers.isValid(componentId)) {
					throw new BadRequestException("Not valid SCTID: %s", componentId);
				}
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
		final Set<String> generatedComponentIds = newLinkedHashSet(); // important to keep order of generated ids
		final int maxAttempts = getConfig().getMaxIdGenerationAttempts();
		
		for (int attempt = 1; attempt <= maxAttempts && generatedComponentIds.size() != quantity; attempt++) {
			final int remainingQuantity = quantity - generatedComponentIds.size();
			Set<String> newGeneratedComponentIds = doGenerateIds(namespace, category, remainingQuantity, attempt);
			// check this newly generated set for reservation
			Set<String> reservedIds = isReserved(newGeneratedComponentIds, generatedComponentIds);
			// remove IDs that are reserved from the newly generated set
			newGeneratedComponentIds.removeAll(reservedIds);
			// add new generated IDs to the results
			generatedComponentIds.addAll(newGeneratedComponentIds);
		}
		
		if (generatedComponentIds.size() != quantity) {
			final String namespaceValue = Strings.isNullOrEmpty(namespace) ? SnomedIdentifiers.INT_NAMESPACE : namespace;
			throw new BadRequestException("Couldn't generate %s identifiers [%s, %s] in maximum (%s) number of attempts", quantity, category, namespaceValue, maxAttempts);
		} else {
			return ImmutableSet.<String>copyOf(generatedComponentIds);
		}
	}

	private Set<String> doGenerateIds(final String namespace, final ComponentCategory category, final int quantity, final int attempt) {
		// generate the item identifier (value can be a function of component category and namespace)
		return generationStrategy.generateItemIds(namespace, category, quantity, attempt)
			.stream()
			.map(itemId -> {
				final StringBuilder builder = new StringBuilder();
				builder.append(itemId);
				
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
			})
			.collect(Collectors.toCollection(Sets::newLinkedHashSet));
	}

	private Set<String> isReserved(Set<String> ids, Set<String> currentGeneratedIds) {
		Set<String> remainingIdsToCheck = newHashSet(ids);
		final ImmutableSet.Builder<String> reservedIds = ImmutableSet.builder();

		// check already generated set of IDs first
		Set<String> reservedByCurrentSet = Sets.intersection(remainingIdsToCheck, currentGeneratedIds);
		if (!reservedByCurrentSet.isEmpty()) {
			reservedIds.addAll(reservedByCurrentSet);
			remainingIdsToCheck.removeAll(reservedByCurrentSet);
		}

		// check local reservation service
		Set<String> reservedByService = getReservationService().isReserved(remainingIdsToCheck);
		if (!reservedByService.isEmpty()) {
			reservedIds.addAll(reservedByService);
			remainingIdsToCheck.removeAll(reservedByService);
		}
		
		// check the ID index to verify state of remaining IDs
		if (!remainingIdsToCheck.isEmpty()) {
			getSctIds(remainingIdsToCheck).forEach((id, sctId) -> {
				if (!sctId.isAvailable()) {
					reservedIds.add(id);
				}
			});
		}
		
		return reservedIds.build();
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
		store.write(index -> {
			index.putAll(ids);
			index.commit();
			return null;
		});
	}
	
	private void removeSctIds(final Set<String> ids) {
		store.write(index -> {
			index.removeAll(ImmutableMap.<Class<?>, Set<String>>of(SctId.class, ids));
			index.commit();
			return null;
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
