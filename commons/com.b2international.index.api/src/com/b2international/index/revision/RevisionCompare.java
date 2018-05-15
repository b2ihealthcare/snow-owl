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
package com.b2international.index.revision;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.ints.IntValueMap;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongSet;
import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.google.common.collect.ImmutableSet;

/**
 * @since 5.0
 */
public final class RevisionCompare {

	static Builder builder(InternalRevisionIndex index, RevisionBranchRef base, RevisionBranchRef compare, int limit) {
		return new Builder(index, base, compare, limit);
	}
	
	static class Builder {
		
		private final InternalRevisionIndex index;
		private final RevisionBranchRef base;
		private final RevisionBranchRef compare;
		private final int limit;
	
		private final Map<Class<? extends Revision>, LongSet> newComponents = newHashMap();
		private final Map<Class<? extends Revision>, LongSet> changedComponents = newHashMap();
		private final Map<Class<? extends Revision>, LongSet> deletedComponents = newHashMap();
		
		private final IntValueMap<Class<? extends Revision>> newTotals = PrimitiveMaps.newObjectKeyIntOpenHashMap();
		private final IntValueMap<Class<? extends Revision>> changedTotals = PrimitiveMaps.newObjectKeyIntOpenHashMap();
		private final IntValueMap<Class<? extends Revision>> deletedTotals = PrimitiveMaps.newObjectKeyIntOpenHashMap();

		Builder(InternalRevisionIndex index, RevisionBranchRef base, RevisionBranchRef compare, int limit) {
			this.index = index;
			this.base = base;
			this.compare = compare;
			this.limit = limit;
		}
		
		public Builder newRevision(Class<? extends Revision> type, long storageKey) {
			if (!newComponents.containsKey(type)) {
				newComponents.put(type, PrimitiveSets.newLongOpenHashSet());
			}
			
			if (newComponents.get(type).size() < limit) {
				newComponents.get(type).add(storageKey);
			}
			
			newTotals.put(type, newTotals.get(type) + 1);
			return this;
		}
		
		public Builder changedRevision(Class<? extends Revision> type, long storageKey) {
			if (!changedComponents.containsKey(type)) {
				changedComponents.put(type, PrimitiveSets.newLongOpenHashSet());
			}
			
			if (changedComponents.get(type).size() < limit) {
				changedComponents.get(type).add(storageKey);
			}
			
			changedTotals.put(type, changedTotals.get(type) + 1);
			return this;
		}
		
		public Builder deletedRevision(Class<? extends Revision> type, long storageKey) {
			if (!deletedComponents.containsKey(type)) {
				deletedComponents.put(type, PrimitiveSets.newLongOpenHashSet());
			}
			
			if (deletedComponents.get(type).size() < limit) {
				deletedComponents.get(type).add(storageKey);
			}
			
			deletedTotals.put(type, deletedTotals.get(type) + 1);
			return this;
		}
		
		public RevisionCompare build() {
			return new RevisionCompare(index, 
					base, 
					compare, 
					newComponents, 
					changedComponents, 
					deletedComponents,
					newTotals,
					changedTotals,
					deletedTotals);
		}
		
	}
	
	private final InternalRevisionIndex index;
	private final RevisionBranchRef base;
	private final RevisionBranchRef compare;

	private final Map<Class<? extends Revision>, LongSet> newComponents;
	private final Map<Class<? extends Revision>, LongSet> changedComponents;
	private final Map<Class<? extends Revision>, LongSet> deletedComponents;

	private final IntValueMap<Class<? extends Revision>> newTotals;
	private final IntValueMap<Class<? extends Revision>> changedTotals;
	private final IntValueMap<Class<? extends Revision>> deletedTotals;
	
	private RevisionCompare(InternalRevisionIndex index, 
			RevisionBranchRef base, 
			RevisionBranchRef compare,
			Map<Class<? extends Revision>, LongSet> newComponents,
			Map<Class<? extends Revision>, LongSet> changedComponents,
			Map<Class<? extends Revision>, LongSet> deletedComponents,
			IntValueMap<Class<? extends Revision>> newTotals,
			IntValueMap<Class<? extends Revision>> changedTotals,
			IntValueMap<Class<? extends Revision>> deletedTotals) {
		
		this.index = index;
		this.base = base;
		this.compare = compare;
		this.newComponents = newComponents;
		this.changedComponents = changedComponents;
		this.deletedComponents = deletedComponents;
		this.newTotals = newTotals;
		this.changedTotals = changedTotals;
		this.deletedTotals = deletedTotals;
	}
	
	public Collection<Class<? extends Revision>> getNewRevisionTypes() {
		return ImmutableSet.copyOf(newComponents.keySet());
	}
	
	public Collection<Class<? extends Revision>> getChangedRevisionTypes() {
		return ImmutableSet.copyOf(changedComponents.keySet());
	}
	
	public Collection<Class<? extends Revision>> getDeletedRevisionTypes() {
		return ImmutableSet.copyOf(deletedComponents.keySet());
	}
	
	public int getNewTotals(Class<? extends Revision> type) {
		return newTotals.get(type);
	}
	
	public int getChangedTotals(Class<? extends Revision> type) {
		return changedTotals.get(type);
	}
	
	public int getDeletedTotals(Class<? extends Revision> type) {
		return deletedTotals.get(type);
	}
	
	LongSet getNewComponents(Class<? extends Revision> type) {
		return newComponents.get(type);
	}
	
	Map<Class<? extends Revision>, LongSet> getNewComponents() {
		return newComponents;
	}
	
	LongSet getChangedComponents(Class<? extends Revision> type) {
		return changedComponents.get(type);
	}
	
	Map<Class<? extends Revision>, LongSet> getChangedComponents() {
		return changedComponents;
	}
	
	LongSet getDeletedComponents(Class<? extends Revision> type) {
		return deletedComponents.get(type);
	}
	
	Map<Class<? extends Revision>, LongSet> getDeletedComponents() {
		return deletedComponents;
	}
	
	public <T> Hits<T> searchNew(final Query<T> query) {
		return index.read(compare, new RevisionIndexRead<Hits<T>>() {
			@Override
			public Hits<T> execute(RevisionSearcher searcher) throws IOException {
				return searcher.search(rewrite(query, newComponents));
			}
		});
	}
	
	public <T> Hits<T> searchChanged(final Query<T> query) {
		return index.read(compare, new RevisionIndexRead<Hits<T>>() {
			@Override
			public Hits<T> execute(RevisionSearcher searcher) throws IOException {
				return searcher.search(rewrite(query, changedComponents));
			}
		});
	}
	
	public <T> Hits<T> searchDeleted(final Query<T> query) {
		return index.read(base, new RevisionIndexRead<Hits<T>>() {
			@Override
			public Hits<T> execute(RevisionSearcher searcher) throws IOException {
				return searcher.search(rewrite(query, deletedComponents));
			}
		});
	}
	
	private <T> Query<T> rewrite(Query<T> query, Map<Class<? extends Revision>, LongSet> storageKeysByType) {
		if (query.getParentType() != null) {
			throw new UnsupportedOperationException("Nested query are not supported");
		}
		final Class<?> revisionType = query.getFrom();
		final LongIterator queryStorageKeys = storageKeysByType.get(revisionType).iterator();
		final Set<Long> storageKeys = newHashSet();
		while (queryStorageKeys.hasNext()) {
			storageKeys.add(queryStorageKeys.next());
		}
		return Query.select(query.getSelect())
				.from(query.getFrom())
				.fields(query.getFields())
				.where(Expressions.builder()
						.must(query.getWhere())
						.filter(Expressions.matchAnyLong(Revision.STORAGE_KEY, storageKeys))
						.build())
				.limit(Math.min(query.getLimit(), storageKeys.size())) // Allow retrieving a subset of these keys, using the query limit
				.build();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(base, compare, newComponents, changedComponents, deletedComponents);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!getClass().equals(obj.getClass())) return false;
		RevisionCompare other = (RevisionCompare) obj;
		return Objects.equals(newComponents, other.newComponents)
				&& Objects.equals(changedComponents, other.changedComponents)
				&& Objects.equals(deletedComponents, other.deletedComponents)
				&& Objects.equals(base, other.base)
				&& Objects.equals(compare, other.compare);
	}

}
