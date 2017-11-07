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
package com.b2international.index.lucene;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.MatchNoDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.util.BytesRef;

import com.google.common.base.Functions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterables;

/**
 * @since 4.3
 */
public abstract class IndexFieldBase<T> implements IndexField<T> {

	private String fieldName;
	private Store store;

	public IndexFieldBase(String fieldName, boolean store) {
		this.fieldName = fieldName;
		this.store = store ? Store.YES : Store.NO;
	}
	
	public final Store isStored() {
		return store;
	}
	
	@Override
	public void copyTo(Document source, Document target) {
		for (T t : getValues(source)) {
			addTo(target, t);
		}
	}
	
	protected abstract T getValue(IndexableField field);
	
	protected abstract BytesRef toBytesRef(T value);
	
	protected abstract IndexableField toField(T value);
	
	protected abstract Type getSortFieldType();
	
	@Override
	public final String fieldName() {
		return fieldName;
	}
	
	@Override
	public final T getValue(Document doc) {
		return getValue(checkNotNull(getField(doc), "Missing field %s from doc [%s]", fieldName(), doc));
	}
	
	@Override
	public final String getValueAsString(Document doc) {
		return getValue(doc).toString();
	}
	
	@Override
	public final List<T> getValues(Document doc) {
		final Builder<T> values = ImmutableList.builder();
		for (IndexableField field : getFields(doc)) {
			values.add(getValue(field));
		}
		return values.build();
	}
	
	@Override
	public final List<String> getValuesAsStringList(Document doc) {
		return FluentIterable.from(getValues(doc)).transform(Functions.toStringFunction()).toList();
	}
	
	@Override
	public final Set<String> getValuesAsStringSet(Document doc) {
		return FluentIterable.from(getValues(doc)).transform(Functions.toStringFunction()).toSet();
	}
	
	@Override
	public abstract Query toQuery(T value);
	
	@Override
	public final Query toQuery(Iterable<T> values) {
		if (values == null || Iterables.isEmpty(values)) {
			return new MatchNoDocsQuery(); 
		} else {
			return toSetQuery(values);
		}
	}
	
	protected abstract Query toSetQuery(Iterable<T> values);

	@Override
	public final Query toExistsQuery() {
		return new TermRangeQuery(fieldName(), null, null, true, true);
	}
	
	@Override
	public final Term toTerm(T value) {
		return new Term(fieldName(), toBytesRef(value));
	}

	@Override
	public void addTo(Document doc, T value) {
		doc.add(toField(value));
	}

	@Override
	public final void removeAll(Document doc) {
		doc.removeFields(fieldName());
	}
	
	public final IndexableField getField(Document doc) {
		return doc.getField(fieldName());
	}
	
	public final IndexableField[] getFields(Document doc) {
		return doc.getFields(fieldName());
	}
	
	@Override
	public final Sort createSort() {
		return new Sort(new SortField(fieldName(), getSortFieldType()));
	}
}
