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

import java.util.concurrent.atomic.AtomicBoolean;

import com.b2international.index.BulkUpdate;
import com.b2international.index.Hits;
import com.b2international.index.Index;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Query;
import com.b2international.snowowl.core.IDisposableService;
import com.google.common.base.Function;

/**
 * @since 5.7
 */
public final class RemoteJobStore implements IDisposableService {

	private final AtomicBoolean disposed = new AtomicBoolean(false);
	private final Index index;

	public RemoteJobStore(Index index) {
		this.index = index;
		this.index.admin().create();
	}
	
	public RemoteJobs search(Expression query, int offset, int limit) {
		return index.read(searcher -> {
			final Hits<RemoteJobEntry> hits = searcher.search(
					Query.select(RemoteJobEntry.class)
						.where(query)
						.offset(offset)
						.limit(limit)
						.build()
					);
			return new RemoteJobs(hits.getHits(), hits.getOffset(), hits.getLimit(), hits.getTotal());
		});
	}
	
	public RemoteJobEntry get(String id) {
		return index.read(searcher -> searcher.get(RemoteJobEntry.class, id));
	}
	
	public void put(String id, RemoteJobEntry job) {
		index.write(writer -> {
			writer.put(id, job);
			writer.commit();
			return null;
		});
	}
	
	public void delete(String id) {
		index.write(writer -> {
			writer.remove(RemoteJobEntry.class, id);
			writer.commit();
			return null;
		});
	}
	
	public void update(String id, Function<RemoteJobEntry, RemoteJobEntry> mutator) {
		index.write(writer -> {
			writer.bulkUpdate(new BulkUpdate<>(RemoteJobEntry.class, DocumentMapping.matchId(id), RemoteJobEntry::getId, mutator));
			writer.commit();
			return null;
		});
	}

	@Override
	public void dispose() {
		if (disposed.compareAndSet(false, true)) {
			this.index.admin().close();
		}
	}

	@Override
	public boolean isDisposed() {
		return disposed.get();
	}
	
}
