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
package com.b2international.snowowl.datastore.version;

import com.b2international.snowowl.datastore.index.diff.NodeDiff;
import com.google.common.base.Predicate;

/**
 * Represents a filter for {@link NodeDiff} instances.
 *
 */
public interface NodeDiffFilter {

	/**
	 * Filters the given iterable of {@link NodeDiff} instances with the predicate argument.
	 * If the predicate provides a {@code true} value after applying it on a {@link NodeDiff}
	 * then the {@link NodeDiff} instance will be excluded from the result set.
	 * <p>Clients should create copy of the given iterable if would like to save its current state
	 * as implementations of this class may directly modify the content, hence the state of the iterable.
	 * <p>Clients should also note, that not only the content of the unfiltered iterable could change,
	 * but the state of the node differences as well. 
	 * @param unfiltered the unfiltered iterable.
	 * @param predicate the predicate used for the filtering. Predicate is evaluated as {@code true} on a node to exclude/filter.
	 * @return the filtered iterable of {@link NodeDiff} instances.
	 */
	<T extends NodeDiff> Iterable<T> filter(final Iterable<T> unfiltered, final Predicate<T> predicate);
	
}