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
package com.b2international.index.json;

import static com.google.common.collect.Lists.newArrayList;

import java.io.IOException;
import java.util.Collection;

import org.apache.lucene.index.IndexWriter;

import com.b2international.index.BulkUpdate;
import com.b2international.index.Searcher;
import com.b2international.index.WithId;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.query.Query;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 5.0
 */
public final class BulkUpdateOperation<T extends WithId> implements Operation {

	private final ObjectMapper mapper;
	private final Mappings mappings;
	private final BulkUpdate<T> update;
	private final Collection<Index> updates = newArrayList();

	public BulkUpdateOperation(BulkUpdate<T> update, ObjectMapper mapper, Mappings mappings) {
		this.update = update;
		this.mapper = mapper;
		this.mappings = mappings;
	}
	
	@Override
	public void execute(IndexWriter writer, Searcher searcher) throws IOException {
		final DocumentMapping mapping = mappings.getMapping(update.getType());
		final Query<? extends T> query = Query.select(update.getType()).where(update.getFilter()).limit(Integer.MAX_VALUE).build();
		for (T hit : searcher.search(query)) {
			final T changed = update.getUpdate().apply(hit);
			Index op = new Index(changed._id(), changed, mapper, mapping);
			op.execute(writer, searcher);
			updates.add(op);
		}
	}
	
	public Collection<Index> updates() {
		return updates;
	}
	
}
