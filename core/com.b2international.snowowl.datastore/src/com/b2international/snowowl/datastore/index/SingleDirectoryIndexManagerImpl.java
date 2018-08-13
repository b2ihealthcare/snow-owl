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
package com.b2international.snowowl.datastore.index;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

import com.b2international.index.compat.SingleDirectoryIndex;
import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Ordering;

public class SingleDirectoryIndexManagerImpl implements SingleDirectoryIndexManager {

	private ConcurrentMap<String, SingleDirectoryIndex> services = new MapMaker().makeMap();
	
	@Override
	public void registerIndex(SingleDirectoryIndex index) {
		services.put(index.getIndexPath(), index);
	}

	@Override
	public void unregisterIndex(SingleDirectoryIndex index) {
		services.remove(index.getIndexPath(), index);
	}
	
	@Override
	public List<String> getServiceIds() {
		return Ordering.natural().immutableSortedCopy(services.keySet());
	}

	@Override
	public SingleDirectoryIndex getService(final String serviceId) {
		Preconditions.checkNotNull(serviceId, "Service identifier may not be null.");
		return services.get(serviceId);
	}
	
}