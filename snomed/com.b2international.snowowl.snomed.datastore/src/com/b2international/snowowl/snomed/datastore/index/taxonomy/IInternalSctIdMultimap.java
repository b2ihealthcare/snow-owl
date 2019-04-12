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
import com.b2international.collections.longs.LongSet;

/**
 * @since 6.11
 */
public interface IInternalSctIdMultimap {

	IInternalSctIdMultimap EMPTY = new IInternalSctIdMultimap() {
		
		@Override
		public LongSet keySet() {
			return LongCollections.emptySet();
		}
		
		@Override
		public boolean isEmpty() {
			return true;
		}
		
		@Override
		public LongSet get(long key) {
			return LongCollections.emptySet();
		}
		
		@Override
		public LongSet get(String key) {
			return LongCollections.emptySet();
		}
	};
	
	LongSet get(String key);

	LongSet get(long key);

	LongSet keySet();

	boolean isEmpty();
}
