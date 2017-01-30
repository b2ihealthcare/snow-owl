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
package com.b2international.snowowl.datastore.store;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptyList;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.lucene.document.BinaryDocValuesField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.TermsFilter;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
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
import com.b2international.snowowl.datastore.store.query.SortBy;
import com.b2international.snowowl.datastore.store.query.SortBy.SortByField;
import com.b2international.snowowl.datastore.store.query.Where;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @since 4.1
 */
public class IndexStore<T> extends SingleDirectoryIndexImpl implements Store<T> {

	private static final String ID_FIELD = "id";
	private static final String SOURCE_FIELD = "source";
	private ObjectMapper objectMapper;
	private Class<T> clazz;
	private Set<String> searchableFields = newHashSet();
	private Set<String> sortFields = newHashSet();
	
	private LoadingCache<String, Method> propertyToMethodCache = CacheBuilder.newBuilder().build(CacheLoader.<String, Method>from(new Function<String, Method>() {
		@Override
		public Method apply(String input) {
			return ReflectionUtils.getGetter(clazz, input);
		}
	}));

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
	public void putAll(Map<String, T> map) {
		try {
			doUpdate(map);
		} catch (IOException e) {
			throw new StoreException("Failed to store values.", e);
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
	public Collection<T> get(Collection<String> keys) {
		
		if (keys.isEmpty()) {
			return emptyList();
		}
		
		try {

			List<BytesRef> uniqueBytesRefs = FluentIterable.from(ImmutableSet.copyOf(keys)).transform(new Function<String, BytesRef>() {
				@Override
				public BytesRef apply(String input) {
					return new BytesRef(input);
				}
			}).copyInto(Lists.<BytesRef>newArrayList());
			
			return searchIndex(matchAllQuery(), new TermsFilter(ID_FIELD, uniqueBytesRefs));
			
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
	public Collection<T> removeAll(Collection<String> keys) {
		try {
			final Collection<T> values = Lists.newArrayList();
			
			for (final String key : keys) {
				values.add(get(key));
				deleteDoc(key);
			}
			
			commit();
			return values;
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
	
	private void doUpdate(Map<String, T> map) throws IOException {
		for (final Entry<String, T> entry : map.entrySet()) {
			updateDoc(entry.getKey(), entry.getValue());
		}
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
			return searchIndex(convertQuery(query), null, offset, limit, convertSortBy(query));
		} catch (IOException e) {
			throw new StoreException("Failed to execute query '%s' on store '%s'.", query, getDirectory(), e);
		}
	}

	private Query convertQuery(com.b2international.snowowl.datastore.store.query.Query query) {
		final BooleanQuery result = new BooleanQuery();
		for (Clause clause : query.clauses()) {
			if (clause instanceof Where) {
				final Where<?> where = (Where<?>) clause;
				final String property = where.property();
				final Object propertyValue = where.value();
				final Class<?> propertyClass = propertyValue.getClass();
				
				if (clause instanceof EqualsWhere) {
					if (Long.class.isAssignableFrom(propertyClass) || long.class.isAssignableFrom(propertyClass)) {
						result.add(NumericRangeQuery.newLongRange(property, (Long) propertyValue, (Long) propertyValue, true, true), Occur.MUST);
					} else {
						result.add(new TermQuery(new Term(property, String.valueOf(propertyValue))), Occur.MUST);
					}
				} else if (clause instanceof PrefixWhere) {
					result.add(new PrefixQuery(new Term(property, String.valueOf(propertyValue))), Occur.MUST);
				} else if (clause instanceof LessThanWhere) {
					if (Long.class.isAssignableFrom(propertyClass) || long.class.isAssignableFrom(propertyClass)) {
						result.add(NumericRangeQuery.newLongRange(property, null, (Long) propertyValue, false, false), Occur.MUST);
					} else {
						result.add(TermRangeQuery.newStringRange(property, null, String.valueOf(propertyValue), false, false), Occur.MUST);
					}
				}
			}
		}
		return result;
	}

	private Sort convertSortBy(com.b2international.snowowl.datastore.store.query.Query query) {
		SortBy sortBy = query.sortBy();
		
		if (sortBy == SortBy.INDEX_ORDER) {
			return Sort.INDEXORDER;
		} else if (sortBy instanceof SortBy.SortByField) {
			SortBy.SortByField sortByField = (SortByField) sortBy;
			return new Sort(new SortField(sortByField.property(), sortByField.isNumeric() ? Type.LONG : Type.STRING_VAL, !sortByField.isAscending()));
		} else {
			throw new IllegalArgumentException("Unsupported sort type " + sortBy + ".");
		}
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
		this.searchableFields.add(checkNotNull(property));
		propertyToMethodCache.put(property, ReflectionUtils.getGetter(clazz, property));
	}
	
	@Override
	public void configureSortable(String property) {
		this.sortFields.add(checkNotNull(property));
		propertyToMethodCache.put(property, ReflectionUtils.getGetter(clazz, property));
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
		
		for (String property : newHashSet(this.searchableFields)) {
			Object propertyValue = ReflectionUtils.invokeSafe(value, propertyToMethodCache.getUnchecked(property));
			Class<?> propertyClass = propertyValue.getClass();
			
			if (Long.class.isAssignableFrom(propertyClass) || long.class.isAssignableFrom(propertyClass)) {
				doc.add(new LongField(property, (long) propertyValue, Field.Store.NO));
			} else {
				doc.add(new StringField(property, String.valueOf(propertyValue), Field.Store.NO));
			}
		}

		for (String property : this.sortFields) {
			Object propertyValue = ReflectionUtils.invokeSafe(value, propertyToMethodCache.getUnchecked(property));
			Class<?> propertyClass = propertyValue.getClass();
			
			if (Long.class.isAssignableFrom(propertyClass) || long.class.isAssignableFrom(propertyClass)) {
				doc.add(new NumericDocValuesField(property, (long) propertyValue));
			} else {
				doc.add(new BinaryDocValuesField(property, new BytesRef(String.valueOf(propertyValue))));
			}
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
		return searchIndex(query, null, Integer.MAX_VALUE);
	}
	
	private List<T> searchIndex(final Query query, Filter filter) throws IOException {
		return searchIndex(query, filter, Integer.MAX_VALUE);
	}

	private List<T> searchIndex(final Query query, Filter filter, final int limit) throws IOException {
		return searchIndex(query, filter, 0, limit, Sort.INDEXORDER);
	}
	
	private List<T> searchIndex(final Query query, Filter filter, final int offset, final int limit, final Sort sort) throws IOException {
		IndexSearcher searcher = null;
		try {
			searcher = manager.acquire();

			final TopDocs docs = searcher.search(query, filter, offset + limit, sort, false, false);
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
