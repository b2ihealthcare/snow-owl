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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import com.b2international.snowowl.datastore.index.diff.CompareResult;
import com.b2international.snowowl.datastore.index.diff.CompareResultImpl;
import com.b2international.snowowl.datastore.index.diff.NodeDiff;
import com.google.common.base.Predicate;

/**
 * Default {@link CompareResultFilter} implementation.
 *
 */
public class DefaultCompareResultFilter implements CompareResultFilter {

	public static final CompareResultFilter DEFAULT = new DefaultCompareResultFilter(); 
	
	@Override
	public CompareResult filter(final CompareResult unfiltered, final Predicate<NodeDiff> predicate) {
		checkNotNull(unfiltered, "unfiltered");
		checkNotNull(predicate, "predicate");
		
		final List<NodeDiff> unfilteredDiffs = newArrayList(unfiltered.getChanges().getAllElements());
		getDiffFilter().filter(unfilteredDiffs, predicate);
		return new CompareResultImpl(unfiltered, unfilteredDiffs);
	}
	
	/**
	 * Returns with the {@link NodeDiffFilter} for excluding a subset of {@link NodeDiff}
	 * wrapped by the {@link CompareResult}.
	 * @return the node difference filter used by the current compare result filter.
	 */
	protected NodeDiffFilter getDiffFilter() {
		return DefaultNodeDiffFilter.DEFAULT;
	}
	
	private DefaultCompareResultFilter() { /*suppressed constructor*/ }

}