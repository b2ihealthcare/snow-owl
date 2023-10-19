/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Set;

import com.b2international.commons.collections.Collections3;

/**
 * @since 9.0.0
 */
public final class RevisionCompareOptions {

	private static final int COMPARE_DEFAULT_LIMIT = 100_000;
	public static final RevisionCompareOptions DEFAULT = builder().build();

	/**
	 * @since 9.0.0
	 */
	public static final class Builder {
		
		private int limit = COMPARE_DEFAULT_LIMIT;
		private boolean includeComponentChanges = false;
		private boolean includeDerivedComponentChanges = false;
		private Set<String> ids;
		private Set<String> types;
		
		public Builder limit(int limit) {
			this.limit = limit;
			return this;
		}
		
		public Builder includeComponentChanges(boolean includeComponentChanges) {
			this.includeComponentChanges = includeComponentChanges;
			return this;
		}
		
		public Builder includeDerivedComponentChanges(boolean includeDerivedComponentChanges) {
			this.includeDerivedComponentChanges = includeDerivedComponentChanges;
			return this;
		}
		
		public Builder types(Iterable<String> types) {
			this.types = types == null ? null : Collections3.toImmutableSet(types);
			return this;
		}
		
		public Builder ids(Iterable<String> ids) {
			this.ids = ids == null ? null : Collections3.toImmutableSet(ids);
			return this;
		}
		
		public RevisionCompareOptions build() {
			return new RevisionCompareOptions(limit, includeComponentChanges, includeDerivedComponentChanges, types, ids);
		}
 		
	} 
	
	private final int limit;
	private final boolean includeComponentChanges;
	private boolean includeDerivedComponentChanges;
	private final Set<String> types;
	private final Set<String> ids;
	
	private RevisionCompareOptions(
			final int limit,
			final boolean includeComponentChanges,
			final boolean includeDerivedComponentChanges,
			final Set<String> types,
			final Set<String> ids) {
		this.limit = limit;
		this.includeComponentChanges = includeComponentChanges;
		this.includeDerivedComponentChanges = includeDerivedComponentChanges;
		this.types = types;
		this.ids = ids;
	}
	
	public int getLimit() {
		return limit;
	}

	public boolean isIncludeComponentChanges() {
		return includeComponentChanges;
	}
	
	public boolean isIncludeDerivedComponentChanges() {
		return includeDerivedComponentChanges;
	}
	
	public Set<String> getTypes() {
		return types;
	}
	
	public Set<String> getIds() {
		return ids;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
}
