/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.history;

import java.io.Serializable;

import com.b2international.snowowl.datastore.cdo.CDOUtils;

import bak.pcj.map.LongKeyLongMap;
import bak.pcj.map.LongKeyLongOpenHashMap;

/**
 * @since 4.6.7
 */
public interface StorageKeyCache extends Serializable {
	
	LongKeyLongMap getCache();
	
	boolean containsId(long id);
	
	long getStorageKey(long id);
	
	
	StorageKeyCache NOOP = new StorageKeyCache() {
		@Override
		public LongKeyLongMap getCache() {
			return new LongKeyLongOpenHashMap();
		}

		@Override
		public boolean containsId(long id) {
			return false;
		}

		@Override
		public long getStorageKey(long id) {
			return CDOUtils.NO_STORAGE_KEY;
		}
	};
}