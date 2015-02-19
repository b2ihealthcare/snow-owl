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

import com.b2international.snowowl.datastore.index.diff.CompareResult;
import com.b2international.snowowl.datastore.index.diff.NodeDiff;
import com.google.common.base.Predicate;

/**
 * Represents a filter to exclude elements from a {@link CompareResult compare result}.
 *
 */
public interface CompareResultFilter {

	/**
	 * Filters the given {@link CompareResult} argument with the given predicate. Each element
	 * of the given result will be excluded from the returning filtered result if the predicate
	 * is evaluated to {@code false} after applying it onto the element. If the predicate provides
	 * {@code true} value for an element, element will no be filtered out and remains in the result.
	 * @param unfiltered the unfiltered compare result.
	 * @param predicate the predicate to use for the filter.
	 * @return the new filtered compare result instance.
	 */
	CompareResult filter(final CompareResult unfiltered, final Predicate<NodeDiff> predicate);
	
}