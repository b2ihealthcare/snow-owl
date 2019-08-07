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
package com.b2international.snowowl.datastore.oplock;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.index.Hits;
import com.b2international.index.Index;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.query.SortBy;
import com.google.common.collect.ImmutableList;

/**
 * @since 7.1.0
 */
public class DatastoreLockIndex {
	
	private static final Logger LOG = LoggerFactory.getLogger("locks");

	private final Index index;

	public DatastoreLockIndex(final Index index) {
		this.index = index;
		this.index.admin().create();
	}
	
	public DatastoreLocks search(Expression query, int limit) {
		return search(query, ImmutableList.of(), SortBy.DOC_ID, limit); 
	}
	
	private DatastoreLocks search(Expression query, List<String> fields, SortBy sortBy, int limit) {
		final Hits<DatastoreLockEntry> hits = searchHits(query, fields, sortBy, limit);
		return new DatastoreLocks(hits.getHits(), null, null, hits.getLimit(), hits.getTotal());
	}
	
	private Hits<DatastoreLockEntry> searchHits(Expression query, List<String> fields, SortBy sortBy, int limit) {
		return index.read(searcher -> {
			return searcher.search(
					Query.select(DatastoreLockEntry.class)
					.fields(fields)
					.where(Expressions.builder()
							.filter(query)
							.build())
					.sortBy(sortBy)
					.limit(limit)
					.build()
					);
		});
	}
	
	
	public DatastoreLockEntry get(String lockId) {
		return index.read(searcher -> searcher.get(DatastoreLockEntry.class, lockId));
	}
	
	void put(String lockId, DatastoreLockEntry lock) {
		index.write(writer -> {
			writer.put(lockId, lock);
			writer.commit();
			return null;
		});
	}
	
}
