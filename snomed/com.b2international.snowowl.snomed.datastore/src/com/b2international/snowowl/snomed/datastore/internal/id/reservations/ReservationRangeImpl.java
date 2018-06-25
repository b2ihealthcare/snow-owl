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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifier;
import com.b2international.snowowl.snomed.datastore.id.reservations.Reservation;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Range;

/**
 * @since 4.0
 */
public class ReservationRangeImpl implements Reservation {

	private Range<Long> itemIdRange;
	private String namespace;
	private Collection<ComponentCategory> components;
	
	public ReservationRangeImpl(long itemIdMin, long itemIdMax, String namespace, Collection<ComponentCategory> components) {
		checkArgument(components.size() >= 1, "At least one ComponentNature must be defined");
		final int minItemIdMin = Strings.isNullOrEmpty(namespace) ? 100 : 1;
		checkArgument(itemIdMin >= minItemIdMin, "ItemIdMin should be greater than or equal to %s", minItemIdMin);
		checkArgument(itemIdMax >= itemIdMin, "ItemIdMax should be greater than or equal to ItemIdMin");
		this.itemIdRange = Range.closed(itemIdMin, itemIdMax);
		this.namespace = Strings.nullToEmpty(namespace);
		this.components = components;
	}
	
	private long getItemIdMin() {
		return itemIdRange.lowerEndpoint();
	}

	private long getItemIdMax() {
		return itemIdRange.upperEndpoint();
	}

	private String getNamespace() {
		return namespace;
	}

	private Collection<ComponentCategory> getComponents() {
		return components;
	}
	
	public boolean affects(String namespace, ComponentCategory category) {
		return Objects.equal(namespace, getNamespace()) && getComponents().contains(category);
	}
	
	public Range<Long> getItemIdRange() {
		return itemIdRange;
	}

	@Override
	public Set<SnomedIdentifier> intersection(Set<SnomedIdentifier> identifiers) {
		return identifiers.stream()
				.filter(identifier -> affects(identifier.getNamespace(), identifier.getComponentCategory()))
				.filter(identifier -> itemIdRange.contains(identifier.getItemId()))
				.collect(Collectors.toSet());
	}
	
	@Override
	public String toString() {
		return String.format("Reservation[%s-%s, namespace=%s, components=%s]", getItemIdMin(), getItemIdMax(), getNamespace(), getComponents());
	}

}
