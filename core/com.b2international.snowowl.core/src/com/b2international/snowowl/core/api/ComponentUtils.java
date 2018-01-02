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
package com.b2international.snowowl.core.api;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.api.component.IdProvider;
import com.b2international.snowowl.core.api.component.LabelProvider;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

/**
 * Utility class for terminology independent {@link IComponent components}.
 * @see IComponent
 */
public final class ComponentUtils {

	/*
	 * Default alphabetical label ordering
	 */
	private static final Ordering<LabelProvider> LABEL_ORDERING = Ordering
			.from(String.CASE_INSENSITIVE_ORDER) // use case insensitive label ordering on Strings
			.nullsFirst() // allow null Strings
			.onResultOf(getLabelFunction()) // get labels by extracting them from IComponent<?> instances
			.nullsFirst(); // allow null values of IComponent<?>

	private static final class IdFunction<K extends Comparable<K>> implements Function<IdProvider<K>, K> {
		@Override
		public K apply(final IdProvider<K> input) {
			return input.getId();
		}
	}
	
	private enum LabelFunction implements Function<LabelProvider, String> {
		INSTANCE;
		
		@Override
		public String apply(final LabelProvider input) {
			return input.getLabel(); // XXX: should this be the label sort key instead?
		}
	}
	
	private ComponentUtils() {
		// Suppress instantiation 
	}

	public static <K extends Comparable<K>> Function<IdProvider<K>, K> getIdFunction() {
		return new IdFunction<K>();
	}
	
	public static Function<LabelProvider, String> getLabelFunction() {
		return LabelFunction.INSTANCE;
	}
	
	public static <K extends Comparable<K>, T extends IdProvider<K>> Ordering<T> getIdOrdering() {
		return Ordering
				.natural() // use the natural ordering of type K
				.nullsFirst() // allow null values of K
				.onResultOf(ComponentUtils.<K>getIdFunction()) // get values of K by extracting IDs from IComponent<K>
				.nullsFirst(); // allow null values of IComponent<K>
	}
	
	public static Ordering<LabelProvider> getLabelOrdering() {
		return LABEL_ORDERING;
	}
	
	/**
	 * Returns the unique identifier of the passed in terminology independent components.
	 * @param components the terminology independent components as an {@link Iterable iterable}.
	 * Should not be {@code null}.
	 * @return an {@link Iterable iterable} of unique identifiers.
	 * @param <K> type of the unique identifier.
	 */
	public static <K extends Comparable<K>> Iterable<K> getIds(final Iterable<? extends IComponent<K>> components) {
		checkNotNull(components, "Components argument cannot be null");
		return Iterables.transform(components, ComponentUtils.<K>getIdFunction());
	}
	
	/**
	 * Returns with a set of the unique identifiers of a bunch of terminology independent components.
	 * @param components the terminology independent components. Cannot be {@code null}. 
	 * @return a set of the unique identifiers of the components.
	 * @param <K> type of the unique component identifier.
	 */
	public static <K extends Comparable<K>> Set<K> getIdSet(final Iterable<? extends IComponent<K>> components) {
		checkNotNull(components, "Components argument cannot be null.");
		return CompareUtils.isEmpty(components) ? Collections.<K>emptySet() : ImmutableSet.copyOf(getIds(components));
	}
	
	/**
	 * Returns with the human readable label of the passed in terminology independent components.
	 * @param components the terminology independent components as an {@link Iterable iterable}.
	 * Should not be {@code null}.
	 * @return an {@link Iterable iterable} of human readable label.
	 * @param <K> type of the unique identifier.
	 */
	public static <K> Iterable<String> getLabels(final Iterable<? extends IComponent<K>> components) {
		checkNotNull(components, "Components argument cannot be null.");
		return Iterables.transform(components, getLabelFunction());
	} 
	
	/**
	 * Returns with a list of terminology independent components sorted by their labels.
	 * @param components an iterable of the terminology independent components to sort.
	 * @return the a list of components sorted by their labels.
	 * @param <K> type of the unique identifier of the terminology independent component.
	 * @param <T> type of the terminology independent component. 
	 */
	public static <K, T extends IComponent<K>> List<T> sortByLabel(final Iterable<T> components) {
		checkNotNull(components, "Components argument cannot be null");
		return getLabelOrdering().sortedCopy(components);
	}
	
	/**
	 * Returns with a list of terminology independent components sorted by their ids. 
	 * The id component must be a subClass of {@link Comparable}.
	 * @param components
	 * @return
	 */
	public static <K extends Comparable<K>, T extends IComponent<K>> List<T> sortById(final Iterable<T> components) {
		checkNotNull(components, "Components argument cannot be null.");
		return ComponentUtils.<K, T>getIdOrdering().sortedCopy(components);
	}
	
	/**
	 * Compares the two specified terminology independent {@link IComponent
	 * component}'s label lexicographically, ignoring case differences. The sign
	 * of the value returned is the same as that of
	 * {@code c1.getLabel().compareToIgnoreCase(c2.getLabel())}.
	 * 
	 * @param c1
	 *            the first {@code IComponent} to compare
	 * @param c2
	 *            the second {@code IComponent} to compare
	 * @return a negative integer, zero, or a positive integer as the specified
	 *         label is greater than, equal to, or less than this component
	 *         label, ignoring case considerations.
	 */
	public static int compareByLabel(final IComponent<?> c1, final IComponent<?> c2) {
		return getLabelOrdering().compare(c1, c2);
	}
	
	public static <K extends Comparable<K>, T extends IComponent<K>> int compareById(final T c1, final T c2) {
		return ComponentUtils.<K, T>getIdOrdering().compare(c1, c2);
	}
}