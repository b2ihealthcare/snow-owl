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
package com.b2international.commons.collections;

import java.util.Set;

/**
 * Representation of a difference between two sets.
 * @param <E> type of the element
 */
public interface SetDifference<E> {

	/**
	 * Returns with {@code true}if there are no difference between the two maps. The maps are equals.
	 * @return
	 */
	boolean areEqual();

	/**
	 * Returns a set containing the entries from the left set whose
	 * elements are not present in the right set.
	 */
	Set<E> entriesOnlyOnLeft();

	/**
	 * Returns a set containing the entries from the right set whose
	 * elements are not present in the left one.
	 */
	Set<E> entriesOnlyOnRight();

	/**
	 * Returns an unmodifiable set containing the entries that appear in both
	 * sets. That is, the intersection of the two sets.
	 */
	Set<E> entriesInCommon();
}