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
package com.b2international.snowowl.datastore.index.diff;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.sort;
import static java.util.Collections.unmodifiableCollection;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.b2international.commons.hierarchy.Derivation;
import com.b2international.commons.hierarchy.PreorderIterator;

/**
 * Derivation of {@link NodeDiff node diffs}.
 */
public class NodeDiffDerivation implements Derivation<NodeDiff>, Serializable {

	private static final long serialVersionUID = 8850012580697897618L;

	private final Collection<NodeDiff> diffs;
	private final Collection<NodeDiff> roots;

	public NodeDiffDerivation(final Collection<NodeDiff> diffs) {
		this(checkNotNull(diffs, "diffs"), AllEquslsComparator.ALL_EQUAL_COMPARATOR);
	}
	
	public NodeDiffDerivation(final Collection<NodeDiff> diffs, final Comparator<NodeDiff> comparator) {
		this.diffs = unmodifiableCollection(newHashSet(checkNotNull(diffs, "diffs")));
		roots = unmodifiableCollection(getRoots(this.diffs, checkNotNull(comparator, "comparator")));
	}
	
	@Override
	public Collection<NodeDiff> getRoots() {
		return roots;
	}

	@Override
	public Collection<NodeDiff> getAllElements() {
		return diffs;
	}

	@Override
	public Iterator<NodeDiff> iterator() {
		return new PreorderIterator<NodeDiff>(getRoots());
	}

	@Override
	public int size() {
		return diffs.size();
	}

	private Collection<NodeDiff> getRoots(final Iterable<NodeDiff> diffs, final Comparator<NodeDiff> comparator) {
		final List<NodeDiff> roots = newArrayList();
		for (final NodeDiff diff : diffs) {
			if (null == diff.getParent()) {
				roots.add(diff);
			}
		}
		sort(roots, comparator);
		return roots;
	}
	
	private static final class AllEquslsComparator implements Comparator<NodeDiff>, Serializable {
		
		private static final long serialVersionUID = 6908322922393431669L;
		
		private static Comparator<NodeDiff> ALL_EQUAL_COMPARATOR = new AllEquslsComparator();
		@Override public int compare(final NodeDiff o1, final NodeDiff o2) {
			return 0;
		}
	};
	
}