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
package com.b2international.index.translog;

import static com.google.common.collect.Lists.newArrayList;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.elasticsearch.cache.recycler.PageCacheRecycler;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.util.BigArrays;
import org.elasticsearch.index.engine.Engine;
import org.elasticsearch.index.mapper.ParseContext.Document;
import org.elasticsearch.index.mapper.ParsedDocument;
import org.elasticsearch.index.shard.ShardId;
import org.elasticsearch.index.translog.Translog;
import org.elasticsearch.index.translog.Translog.Snapshot;
import org.elasticsearch.index.translog.Translog.TranslogGeneration;
import org.elasticsearch.index.translog.TranslogConfig;

import com.b2international.index.json.BulkUpdateOperation;
import com.b2international.index.json.Delete;
import com.b2international.index.json.Index;
import com.b2international.index.json.JsonDocumentMapping;
import com.b2international.index.json.JsonDocumentSearcher;
import com.b2international.index.json.Operation;
import com.b2international.index.mapping.Mappings;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

/**
 * @since 5.0
 */
public class EsTransactionLog implements TransactionLog {
	
	private final ObjectMapper mapper;
	private final Mappings mappings;
	private final Translog translog;

	public EsTransactionLog(final String indexName, final Path translogPath, final ObjectMapper mapper, final Mappings mappings, final Map<String, String> commitData) throws IOException {
		this.mapper = mapper;
		this.mappings = mappings;
		
		final ShardId shardId = new ShardId(indexName, 0);
		
		final Settings indexSettings = Settings.builder()
			.put(TranslogConfig.INDEX_TRANSLOG_SYNC_INTERVAL, 0)
			.put(PageCacheRecycler.TYPE, PageCacheRecycler.Type.NONE.name())
			.build();
		
		final TranslogConfig translogConfig = new TranslogConfig(shardId, translogPath, indexSettings, Translog.Durabilty.REQUEST,
				BigArrays.NON_RECYCLING_INSTANCE, null);
		
		if (commitData.containsKey(Translog.TRANSLOG_GENERATION_KEY)) {
			final long generation = Long.parseLong(commitData.get(Translog.TRANSLOG_GENERATION_KEY));
			translogConfig.setTranslogGeneration(new TranslogGeneration(commitData.get(Translog.TRANSLOG_UUID_KEY), generation));
		}
		
		this.translog = new Translog(translogConfig); 
	}

	@Override
	public void close() throws IOException {
		translog.close();
	}
	
	@Override
	public void commit(final IndexWriter writer) throws IOException {
		if (writer.hasUncommittedChanges()) {
			translog.prepareCommit();
			commitWriter(writer);
			translog.commit();
		}
	}

	@Override
	public void commitWriter(final IndexWriter writer) throws IOException {
		final Map<String, String> commitData = Maps.newHashMap();
		commitData.put(Translog.TRANSLOG_GENERATION_KEY, Long.toString(translog.getGeneration().translogFileGeneration));
		commitData.put(Translog.TRANSLOG_UUID_KEY, translog.getGeneration().translogUUID);
		
		writer.setCommitData(commitData);
		writer.commit();
	}
	
	@Override
	public void addOperation(final Operation operation) throws IOException {
		final Collection<Translog.Operation> translogOperations = toTranslogOperation(operation);
		for (Translog.Operation op : translogOperations) {
			translog.add(op);
		}
	}

	private Collection<Translog.Operation> toTranslogOperation(final Operation operation) {
		if (operation instanceof Index) {
			final Index op = (Index) operation;
			final String uid = op.uid();

			final ParsedDocument document = new ParsedDocument(new StringField("_uid", uid, Store.NO),
					new LongField("_version", 0, Store.NO), op.key(), op.mapping().type().getName(), null, 0, 0, Collections.<Document> emptyList(),
					new BytesArray(op.source()), null);
			org.elasticsearch.index.engine.Engine.Index index = new Engine.Index(new Term(uid), document);

			return Collections.<Translog.Operation>singleton(new org.elasticsearch.index.translog.Translog.Index(index));
		} else if (operation instanceof Delete) { 
			final Delete op = (Delete) operation;
			final org.elasticsearch.index.engine.Engine.Delete delete = new Engine.Delete(null, null, JsonDocumentMapping._uid().toTerm(op.uid()));
			
			return Collections.<Translog.Operation>singleton(new org.elasticsearch.index.translog.Translog.Delete(delete));
		} else if (operation instanceof BulkUpdateOperation<?>){
			final Collection<Index> updates = ((BulkUpdateOperation<?>) operation).updates();
			final Collection<Translog.Operation> ops = newArrayList();
			for (Index update : updates) {
				ops.addAll(toTranslogOperation(update));
			}
			return ops;
		} else {
			throw new IllegalArgumentException(String.format("Unhandled operation type %s.", operation));
		}
	}
	
	@Override
	public void recoverFromTranslog(final IndexWriter writer, final JsonDocumentSearcher searcher) throws IOException {
		final Snapshot snapshot = translog.newSnapshot();
		org.elasticsearch.index.translog.Translog.Operation translogOperation = null;
		
		while ((translogOperation = snapshot.next()) != null) {
			final Operation operation = toOperation(translogOperation);
			operation.execute(writer, searcher);
		}
		
		if (snapshot.estimatedTotalOperations() != 0) {
			commit(writer);
		}
	}

	private Operation toOperation(final org.elasticsearch.index.translog.Translog.Operation op) {
		if (op instanceof Translog.Index) {
			final Translog.Index index = (org.elasticsearch.index.translog.Translog.Index) op;
			return new Index(index.id(), index.source().toBytes(), mapper, mappings.getByType(index.type()));
		} else if (op instanceof Translog.Delete) {
			final Translog.Delete delete = (org.elasticsearch.index.translog.Translog.Delete) op;
			return new Delete(delete.uid().text());
		} else {
			throw new IllegalArgumentException(String.format("Unhandled operation type %s.", op));
		}
	}
	
}
