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
package com.b2international.snowowl.datastore.index.mapping;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.TermFilter;
import org.apache.lucene.queries.TermsFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.BytesRef;

import com.b2international.commons.CompareUtils;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;

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
	public final TermQuery toQuery(T value) {
		return new TermQuery(toTerm(value));
	}
	
	@Override
	public final PrefixQuery toExistsQuery() {
		return new PrefixQuery(new Term(fieldName()));
	}
	
	@Override
	public final Term toTerm(T value) {
		return new Term(fieldName(), toBytesRef(value));
	}
	
	@Override
	public final TermFilter toTermFilter(T value) {
		return new TermFilter(toTerm(value));
	}

	@Override
	public void addTo(Document doc, T value) {
		doc.add(toField(value));
	}

	@Override
	public final void removeAll(Document doc) {
		doc.removeFields(fieldName());
	}
	
	@Override
	public <D extends DocumentBuilderBase<D>> void removeAll(D doc) {
		// FIXME referencing internal field via build method
		removeAll(doc.build());
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
	
	@Override
	public final Filter createTermsFilter(final Iterable<T> values) {
		if (CompareUtils.isEmpty(values)) {
			return null;
		} else {
			final Set<T> uniqueValues = ImmutableSet.copyOf(values);
			final Set<BytesRef> uniqueBytesRefs = FluentIterable.from(uniqueValues)
					.transform(new Function<T, BytesRef>() {
						@Override public BytesRef apply(T input) { return toBytesRef(input); }
					})
					.toSet();
			
			return createBytesRefFilter(uniqueBytesRefs);
		}
	}

	@Override
	public final Filter createBytesRefFilter(final Iterable<BytesRef> bytesRefs) {
		final Set<BytesRef> uniqueBytesRefs = ImmutableSet.copyOf(bytesRefs);
		return new TermsFilter(fieldName(), newArrayList(uniqueBytesRefs)); // TermsFilter wants to sort the list in-place
	}
}
