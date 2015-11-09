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
package com.b2international.snowowl.datastore.store;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.util.BytesRef;

import com.b2international.commons.ReflectionUtils;
import com.b2international.snowowl.datastore.store.query.Clause;
import com.b2international.snowowl.datastore.store.query.EqualsWhere;
import com.b2international.snowowl.datastore.store.query.LessThanWhere;
import com.b2international.snowowl.datastore.store.query.PrefixWhere;
import com.b2international.snowowl.datastore.store.query.Where;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * @since 4.1
 */
public class IndexStore<T> extends SingleDirectoryIndexImpl implements Store<T> {

	private static final String ID_FIELD = "id";
	private static final String SOURCE_FIELD = "source";
	private ObjectMapper objectMapper;
	private Class<T> clazz;
	private Collection<String> additionalSearchableFields = newHashSet();

	/**
	 * Creates a new Index based {@link Store} implementation working on the given directory.
	 * 
	 * @param directory
	 *            - the directory to use for this index based store.
	 * @param type
	 *            - the value's type
	 */
	public IndexStore(File directory, Class<T> type) {
		this(directory, new ObjectMapper(), type);
	}
	
	public IndexStore(File directory, ObjectMapper objectMapper, Class<T> type) {
		super(directory);
		this.objectMapper = checkNotNull(objectMapper, "objectMapper");
		this.clazz = checkNotNull(type, "type");
	}

	@Override
	public void put(String key, T value) {
		try {
			doUpdate(key, value);
		} catch (IOException e) {
			throw new StoreException("Failed to store value '%s' in key '%s'", value, key, e);
		}
	}

	@Override
	public T get(String key) {
		try {
			final Query query = matchKeyQuery(key);
			return Iterables.getOnlyElement(searchIndex(query), null);
		} catch (IOException e) {
			throw new StoreException(e.getMessage(), e);
		}
	}

	@Override
	public T remove(String key) {
		try {
			final T t = get(key);
			deleteDoc(key);
			commit();
			return t;
		} catch (IOException e) {
			throw new StoreException(e.getMessage(), e);
		}
	}

	@Override
	public boolean replace(String key, T oldValue, T newValue) {
		checkNotNull(oldValue, "oldValue");
		checkNotNull(newValue, "newValue");
		if (oldValue.equals(newValue) || !oldValue.equals(get(key))) {
			return false;
		} else {
			try {
				doUpdate(key, newValue);
				return true;
			} catch (IOException e) {
				throw new StoreException("Failed to replace key '%s' with value '%s' in store '%s'", key, newValue, getDirectory(), e);
			}
		}
	}

	private void doUpdate(String key, T newValue) throws IOException {
		updateDoc(key, newValue);
		commit();
	}

	@Override
	public Collection<T> values() {
		try {
			return searchIndex(matchAllQuery());
		} catch (IOException e) {
			throw new StoreException("Failed to retrieve values from store '%s'.", getDirectory(), e);
		}
	}
	
	@Override
	public Collection<T> search(com.b2international.snowowl.datastore.store.query.Query query) {
		return search(query, 0, Integer.MAX_VALUE);
	}
	
	@Override
	public Collection<T> search(com.b2international.snowowl.datastore.store.query.Query query, int offset, int limit) {
		try {
			return searchIndex(convert(query), offset, limit);
		} catch (IOException e) {
			throw new StoreException("Failed to execute query '%s' on store '%s'.", query, getDirectory(), e);
		}
	}

	private Query convert(com.b2international.snowowl.datastore.store.query.Query query) {
		final BooleanQuery result = new BooleanQuery();
		for (Clause clause : query.clauses()) {
			if (clause instanceof Where) {
				final String property = ((Where) clause).property();
				final String value = ((Where) clause).value();
				if (clause instanceof EqualsWhere) {
					result.add(new TermQuery(new Term(property, value)), Occur.MUST);
				} else if (clause instanceof PrefixWhere) {
					result.add(new PrefixQuery(new Term(property, value)), Occur.MUST);
				} else if (clause instanceof LessThanWhere) {
					result.add(new TermRangeQuery(property, null, new BytesRef(value), false, false), Occur.MUST);
				}
			}
		}
		return result;
	}

	@Override
	public void clear() {
		try {
			writer.deleteAll();
			commit();
		} catch (IOException e) {
			throw new StoreException("Failed to clear store '%s'", getDirectory(), e);
		}
	}
	
	@Override
	public void configureSearchable(String property) {
		this.additionalSearchableFields.add(checkNotNull(property));
	}

	@Override
	public boolean containsKey(String key) {
		IndexSearcher searcher = null;

		try {

			try {
				searcher = manager.acquire();

				final TotalHitCountCollector collector = new TotalHitCountCollector();
				searcher.search(matchKeyQuery(key), collector);
				return collector.getTotalHits() > 0;

			} finally {
				if (null != searcher) {
					manager.release(searcher);
				}
			}

		} catch (IOException e) {
			throw new StoreException("Failed to check the existence of key '%s'", key, e);
		} 
	}

	private void updateDoc(String key, T value) throws IOException {
		final Document doc = createDoc(key, value);
		writer.updateDocument(new Term(ID_FIELD, key), doc);
	}

	private Document createDoc(String key, T value) throws IOException {
		final Document doc = new Document();
		doc.add(new StringField(ID_FIELD, key, Field.Store.NO));
		doc.add(new StoredField(SOURCE_FIELD, serialize(value)));
		for (String property : newHashSet(this.additionalSearchableFields)) {
			doc.add(new StringField(property, String.valueOf(ReflectionUtils.getGetterValue(value, property)), Field.Store.NO));
		}
		return doc;
	}
	
	private void deleteDoc(String key) throws IOException {
		writer.deleteDocuments(matchKeyQuery(key));
	}

	private String serialize(T value) throws IOException {
		return objectMapper.writeValueAsString(value);
	}

	private T deserialize(String source) throws IOException {
		return objectMapper.readValue(source, clazz);
	}
	
	private List<T> searchIndex(final Query query) throws IOException {
		return searchIndex(query, Integer.MAX_VALUE);
	}

	private List<T> searchIndex(final Query query, final int limit) throws IOException {
		return searchIndex(query, 0, limit);
	}
	
	private List<T> searchIndex(final Query query, final int offset, final int limit) throws IOException {
		IndexSearcher searcher = null;
		try {
			searcher = manager.acquire();

			final TopDocs docs = searcher.search(query, null, offset + limit, Sort.INDEXORDER, false, false);
			final ScoreDoc[] scoreDocs = docs.scoreDocs;
			final ImmutableList.Builder<T> resultBuilder = ImmutableList.builder();

			for (int i = offset; i < offset + limit && i < scoreDocs.length; i++) {
				final Document sourceDocument = searcher.doc(scoreDocs[i].doc, ImmutableSet.of(SOURCE_FIELD));
				final String source = sourceDocument.get(SOURCE_FIELD);
				final T deserializedSource = deserialize(source);
				resultBuilder.add(deserializedSource);
			}

			return resultBuilder.build();
		} finally {
			if (null != searcher) {
				manager.release(searcher);
			}
		}
	}
	
	private static Query matchAllQuery() {
		return new MatchAllDocsQuery();
	}
	
	private static BooleanQuery matchKeyQuery(String key) {
		final BooleanQuery query = new BooleanQuery();
		query.add(new TermQuery(new Term(ID_FIELD, key)), Occur.MUST);
		return query;
	}
}
