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
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.unmodifiableCollection;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

import com.b2international.snowowl.core.api.component.LabelProvider;
import com.google.common.base.Predicate;

/**
 * {@link NodeChange Node change} implementation.
 *
 */
public class NodeChangeImpl implements NodeChange, Collection<NodeDelta>, RandomAccess, Serializable, LabelProvider {

	private static final long serialVersionUID = 4122477334445479064L;
	
	private final List<NodeDelta> deltas;
	private final String nodeId;
	private final String label;

	
	public NodeChangeImpl(final String nodeId, final String label, final Iterable<? extends NodeDelta> deltas) {
		this.nodeId = checkNotNull(nodeId, "nodeId");
		this.label = checkNotNull(label, "label");
		this.deltas = newArrayList(filter(filter(checkNotNull(deltas, "deltas"), NON_EMPTY_NODE_PREDICATE), NodeDelta.class));
	}
	
	@Override
	public Collection<NodeDelta> getDeltas() {
		return unmodifiableCollection(this);
	}

	@Override
	public int size() {
		return deltas.size();
	}

	@Override
	public boolean isEmpty() {
		return deltas.isEmpty();
	}

	@Override
	public boolean contains(final Object o) {
		return deltas.contains(o);
	}

	@Override
	public Iterator<NodeDelta> iterator() {
		return deltas.iterator();
	}

	@Override
	public Object[] toArray() {
		return deltas.toArray();
	}

	@Override
	public <T> T[] toArray(final T[] a) {
		return deltas.toArray(a);
	}

	@Override
	public boolean add(final NodeDelta e) {
		return isEmptryOrNull(e) ? false : deltas.add(e);
	}

	@Override
	public boolean remove(final Object o) {
		return deltas.remove(o);
	}

	@Override
	public boolean containsAll(final Collection<?> c) {
		return deltas.containsAll(c);
	}

	@Override
	public boolean addAll(final Collection<? extends NodeDelta> c) {
		return deltas.addAll(filter(c, NON_EMPTY_NODE_PREDICATE));
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		return deltas.removeAll(c);
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
		return deltas.retainAll(c);
	}

	@Override
	public void clear() {
		deltas.clear();
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getNodeId() {
		return nodeId;
	}
	
	@Override
	public boolean equals(final Object o) {
		return deltas.equals(o);
	}

	@Override
	public int hashCode() {
		return deltas.hashCode();
	}
	
	/**
	 * (non-API)
	 * Sorts the underlying list of deltas with the comparator argument.
	 * {@code null} comparator is ignored.
	 */
	public void sort(final Comparator<NodeDelta> comparator) {
		if (null != comparator) {
			Collections.sort(deltas, comparator);
		}
	}
	
	private static boolean isEmptryOrNull(final NodeDelta e) {
		return NodeDelta.NULL_IMPL.equals(e) || null == e;
	}

	private static final Predicate<NodeDelta> NON_EMPTY_NODE_PREDICATE = new NonEmptyNodePredicate();
	
	private static final class NonEmptyNodePredicate implements Predicate<NodeDelta>, Serializable {
		private static final long serialVersionUID = 5758841729507698075L;
		public boolean apply(final NodeDelta node) {
			return !isEmptryOrNull(node);
		}
		
	} 
	
}