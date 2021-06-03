/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.compare;

import java.util.List;

import com.b2international.snowowl.core.domain.ListCollectionResource;
/**
 * @since 7.8
 */
public final class ConceptMapCompareResult extends ListCollectionResource<ConceptMapCompareResultItem> {
	
	private static final long serialVersionUID = 1L;
	
	private final int limit;
	
	private final int totalAdded;
	private final int totalRemoved;
	private final int totalChanged;
	private final int totalUnchanged;

	public ConceptMapCompareResult(List<ConceptMapCompareResultItem> items, int totalAdded, int totalRemoved, int totalChanged, int totalUnchanged, int limit) {
		super(items);
		this.totalAdded = totalAdded;
		this.totalRemoved = totalRemoved;
		this.totalChanged = totalChanged;
		this.totalUnchanged = totalUnchanged;
		this.limit = limit;
	}

	public int getTotalAdded() {
		return totalAdded;
	}
	
	public int getTotalRemoved() {
		return totalRemoved;
	}
	
	public int getTotalChanged() {
		return totalChanged;
	}
	
	public int getTotalUnchanged() {
		return totalUnchanged;
	}
	
	public int getLimit() {
		return limit;
	}
	
}
