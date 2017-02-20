/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.remotejobs;

import com.b2international.index.Hits;
import com.b2international.index.Index;
import com.b2international.index.query.Query;

/**
 * @since 5.7
 */
public final class RemoteJobStore {

	private final Index index;

	public RemoteJobStore(Index index) {
		this.index = index;
	}
	
	public RemoteJobs search(Query<RemoteJobEntry> query) {
		return index.read(searcher -> {
			final Hits<RemoteJobEntry> hits = searcher.search(query);
			return new RemoteJobs(hits.getHits(), hits.getOffset(), hits.getLimit(), hits.getTotal());
		});
	}
	
	public RemoteJobEntry get(String id) {
		return index.read(searcher -> searcher.get(RemoteJobEntry.class, id));
	}
	
	public void put(String id, RemoteJobEntry job) {
		index.write(writer -> {
			writer.put(id, job);
			return null;
		});
	}
	
	public void delete(String id) {
		index.write(writer -> {
			writer.remove(RemoteJobEntry.class, id);
			return null;
		});
	}
	
}
