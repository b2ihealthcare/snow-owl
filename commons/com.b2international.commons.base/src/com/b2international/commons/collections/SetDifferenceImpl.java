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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.isEmpty;
import static java.util.Collections.unmodifiableSet;

import java.io.Serializable;
import java.util.Set;

import com.google.common.base.MoreObjects;

/**
 * Serializable {@link SetDifference} implementation.
 *
 */
public class SetDifferenceImpl<E> implements SetDifference<E>, Serializable {

	private static final long serialVersionUID = 4522527459256388320L;
	
	private final Set<E> left;
	private final Set<E> right;
	private final Set<E> intersection;
	private final boolean equal;
	
	public SetDifferenceImpl(final Set<E> left, final Set<E> right, final Set<E> intersection) {
		this.left = checkNotNull(left, "left");
		this.right = checkNotNull(right, "right");
		this.intersection = checkNotNull(intersection, "intersection");
		this.equal = isEmpty(this.left) && isEmpty(this.right);
	}

	@Override
	public boolean areEqual() {
		return equal;
	}

	@Override
	public Set<E> entriesOnlyOnLeft() {
		return unmodifiableSet(left);
	}

	@Override
	public Set<E> entriesOnlyOnRight() {
		return unmodifiableSet(right);
	}

	@Override
	public Set<E> entriesInCommon() {
		return unmodifiableSet(intersection);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("left", left)
			.add("right", right)
			.add("intersection", intersection)
			.add("equal", equal)
			.toString();
	}
	
}