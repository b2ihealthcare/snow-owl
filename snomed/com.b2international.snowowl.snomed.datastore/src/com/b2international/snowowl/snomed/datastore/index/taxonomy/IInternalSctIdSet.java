/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.index.taxonomy;

import com.b2international.collections.longs.LongCollections;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongList;

/**
 * @since 6.11
 */
public interface IInternalSctIdSet {

	IInternalSctIdSet EMPTY = new IInternalSctIdSet() {
		
		@Override
		public LongList toLongList() {
			return LongCollections.emptyList();
		}
		
		@Override
		public int size() {
			return 0;
		}
		
		@Override
		public LongIterator iterator() {
			return LongCollections.emptyIterator();
		}
		
		@Override
		public boolean isEmpty() {
			return true;
		}
		
		@Override
		public boolean contains(long sctId) {
			return false;
		}
		
		@Override
		public boolean contains(String sctId) {
			return false;
		}
	};
	
	boolean contains(String sctId);

	boolean contains(long sctId);

	LongList toLongList();

	LongIterator iterator();

	int size();

	boolean isEmpty();
}
