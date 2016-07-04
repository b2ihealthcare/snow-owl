/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.revision.compare;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongSet;
import com.b2international.index.revision.Revision;

/**
 * @since 5.0
 */
public final class RevisionCompare {

	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private Map<Class<? extends Revision>, LongSet> newComponents = newHashMap();
		private Map<Class<? extends Revision>, LongSet> changedComponents = newHashMap();
		private Map<Class<? extends Revision>, LongSet> deletedComponents = newHashMap();
		
		Builder() {
		}
		
		public Builder newRevision(Class<? extends Revision> type, long storageKey) {
			if (!newComponents.containsKey(type)) {
				newComponents.put(type, PrimitiveSets.newLongOpenHashSet());
			}
			newComponents.get(type).add(storageKey);
			return this;
		}
		
		public Builder changedRevision(Class<? extends Revision> type, long storageKey) {
			if (!changedComponents.containsKey(type)) {
				changedComponents.put(type, PrimitiveSets.newLongOpenHashSet());
			}
			changedComponents.get(type).add(storageKey);
			return this;
		}
		
		public Builder deletedRevision(Class<? extends Revision> type, long storageKey) {
			if (!deletedComponents.containsKey(type)) {
				deletedComponents.put(type, PrimitiveSets.newLongOpenHashSet());
			}
			deletedComponents.get(type).add(storageKey);
			return this;
		}
		
		public RevisionCompare build() {
			return new RevisionCompare(newComponents, changedComponents, deletedComponents);
		}
		
	}
	
	private final Map<Class<? extends Revision>, LongSet> newComponents;
	private final Map<Class<? extends Revision>, LongSet> changedComponents;
	private final Map<Class<? extends Revision>, LongSet> deletedComponents;

	private RevisionCompare(Map<Class<? extends Revision>, LongSet> newComponents,
			Map<Class<? extends Revision>, LongSet> changedComponents,
			Map<Class<? extends Revision>, LongSet> deletedComponents) {
		this.newComponents = newComponents;
		this.changedComponents = changedComponents;
		this.deletedComponents = deletedComponents;
	}
	
	public LongSet getNewComponents(Class<? extends Revision> type) {
		return newComponents.get(type);
	}
	
	public Map<Class<? extends Revision>, LongSet> getNewComponents() {
		return newComponents;
	}
	
	public LongSet getChangedComponents(Class<? extends Revision> type) {
		return changedComponents.get(type);
	}
	
	public Map<Class<? extends Revision>, LongSet> getChangedComponents() {
		return changedComponents;
	}
	
	public LongSet getDeletedComponents(Class<? extends Revision> type) {
		return deletedComponents.get(type);
	}
	
	public Map<Class<? extends Revision>, LongSet> getDeletedComponents() {
		return deletedComponents;
	}
	
}
