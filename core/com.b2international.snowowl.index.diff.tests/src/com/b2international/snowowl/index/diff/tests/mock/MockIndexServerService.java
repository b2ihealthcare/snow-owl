/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.index.diff.tests.mock;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.server.index.ICommitTimeProvider;
import com.b2international.snowowl.datastore.server.index.IDirectoryManager;
import com.b2international.snowowl.datastore.server.index.IIndexAccessUpdater;
import com.b2international.snowowl.datastore.server.index.IIndexPostProcessor;
import com.b2international.snowowl.datastore.server.index.IndexServerService;
import com.b2international.snowowl.datastore.server.index.RAMDirectoryManager;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

/**
 * @since 4.3
 */
public class MockIndexServerService extends IndexServerService<MockIndexEntry> {

	private AtomicLong clock = new AtomicLong(0L);
	private static final String ID_FIELD = "id";
	private static final String LABEL_FIELD = "label";
	
	private static final String COMPARE_FIELD = "component_compare_unique_key";
	private static final String IGNORE_COMPARE_FIELD = "component_ignore_compare_unique_key"; 
	
	private IDirectoryManager directoryManager;

	public MockIndexServerService() {
		directoryManager = new RAMDirectoryManager();
	}
	
	@Override
	public String getRepositoryUuid() {
		return MockIndexServerService.class.getName();
	}

	@Override
	protected IIndexPostProcessor getIndexPostProcessor() {
		return IIndexPostProcessor.NOOP;
	}

	@Override
	protected IDirectoryManager getDirectoryManager() {
		return directoryManager;
	}
		
	@Override
	protected ICommitTimeProvider getCommitTimeProvider() {
		return ICommitTimeProvider.DEFAULT;
	}
	
	@Override
	protected IIndexAccessUpdater getIndexAccessUpdater() {
		return IIndexAccessUpdater.NOOP;
	}
	
	public void deleteDocs(IBranchPath branch, String...ids) {
		for (String id : ids) {
			delete(branch, createIdTerm(id));
		}
		commit(branch);
	}

	public void tag(String tag) {
		final IBranchPath version = BranchPathUtils.createVersionPath(tag);
		final String[] segments = version.getPath().split("/");
		final int[] cdoBranchPath = new int[segments.length];
		for (int i = 0; i < segments.length ; i++) {
			cdoBranchPath[i] = i;
		}
		reopen(version, cdoBranchPath, clock.incrementAndGet());
	}
	
	public void indexIrrelevantDocs(IBranchPath branch, String...ids) {
		indexIrrelevantDocs(branch, toDocs(ids));
	}
	
	public void indexIrrelevantDocs(IBranchPath branch, Iterable<IdLabelPair> docs) {
		indexDocs(branch, docs, new IrrelevantMappingStrategy());
	}
	
	public void indexRelevantDocs(IBranchPath branch, String...ids) {
		indexRelevantDocs(branch, toDocs(ids));
	}
	
	public void indexRelevantDocs(IBranchPath branch, Iterable<IdLabelPair> docs) {
		indexDocs(branch, docs, new RelevantMappingStrategy());
	}

	private Collection<IdLabelPair> toDocs(String...ids) {
		final Collection<IdLabelPair> docs = newHashSet();
		for (String id : ids) {
			docs.add(new IdLabelPair(id, id + "_default_label"));
		}
		return docs;
	}
	
	public void indexRelevantDocs(IBranchPath branch, Map<String, String> idLabelPairs) {
		indexRelevantDocs(branch, FluentIterable.from(idLabelPairs.entrySet()).transform(new Function<Entry<String, String>, IdLabelPair>() {
			@Override
			public IdLabelPair apply(Entry<String, String> input) {
				return new IdLabelPair(input.getKey(), input.getValue());
			}
		}));
	}
	
	public int getAllDocsCount(IBranchPath branch) {
		return getHitCount(branch, getAllDocsQuery(), null);
	} 
	
	public Collection<Document> getAllDocs(IBranchPath branch) {
		final int count = getAllDocsCount(branch);
		if (count > 0) {
			final Collection<Document> docs = newArrayList();
			for (ScoreDoc sd : search(branch, getAllDocsQuery(), count).scoreDocs) {
				docs.add(document(branch, sd.doc, null));
			}
			return docs;
		}
		return Collections.emptySet();
	}
	
	public Map<String, IdLabelPair> getAllDocsMap(IBranchPath branch) {
		return FluentIterable.from(getAllDocs(branch)).transform(new Function<Document, IdLabelPair>() {
			@Override
			public IdLabelPair apply(Document input) {
				return new IdLabelPair(input.get(ID_FIELD), input.get(LABEL_FIELD));
			}
		}).uniqueIndex(new Function<IdLabelPair, String>() {
			@Override
			public String apply(IdLabelPair input) {
				return input.getId();
			}
		});
	}
	
	private Query getAllDocsQuery() {
		return new PrefixQuery(new Term(ID_FIELD));
	}
	
	private void indexDocs(IBranchPath branch, Iterable<IdLabelPair> docs, MappingStrategy strategy) {
		for (IdLabelPair doc : docs) {
			index(branch, strategy.createDoc(doc), createIdTerm(doc.getId()));
		}
		commit(branch);
	}

	private Term createIdTerm(String id) {
		return new Term(ID_FIELD, id);
	}
	
	private interface MappingStrategy {
		
		Document createDoc(IdLabelPair obj);
		
	}
	
	private class RelevantMappingStrategy implements MappingStrategy {

		@Override
		public Document createDoc(IdLabelPair obj) {
			final Document d = createBaseDoc(obj);
			d.add(new NumericDocValuesField(COMPARE_FIELD, Long.valueOf(obj.getId())));
			return d;
		}
		
	}
	
	private class IrrelevantMappingStrategy implements MappingStrategy {
		
		@Override
		public Document createDoc(IdLabelPair obj) {
			final Document d = createBaseDoc(obj);
			d.add(new NumericDocValuesField(COMPARE_FIELD, -1L));
			d.add(new NumericDocValuesField(IGNORE_COMPARE_FIELD, Long.valueOf(obj.getId())));
			return d;
		}
		
	}
	
	private static Document createBaseDoc(IdLabelPair doc) {
		final Document d = new Document();
		d.add(createIdField(doc.getId()));
		d.add(createLabelField(doc.getLabel()));
		return d;
	}

	private static Field createIdField(String id) {
		return createField(ID_FIELD, id);
	}
	
	private static Field createLabelField(String label) {
		return createField(LABEL_FIELD, label);
	}
	
	private static Field createField(String name, String value) {
		return new StringField(name, value, org.apache.lucene.document.Field.Store.YES);
	}

}
