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

import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;


/**
 * @since 4.3 
 * @param <T> - the type of the field
 */
public class IndexFieldDelegate<T> implements IndexField<T> {

	private IndexField<T> delegate;

	public IndexFieldDelegate(IndexField<T> delegate) {
		this.delegate = delegate;
	}

	@Override
	public void copyTo(Document source, Document target) {
		this.delegate.copyTo(source, target);
	}
	
	@Override
	public final String fieldName() {
		return delegate.fieldName();
	}

	@Override
	public T getValue(Document doc) {
		return delegate.getValue(doc);
	}

	@Override
	public List<T> getValues(Document doc) {
		return delegate.getValues(doc);
	}
	
	@Override
	public List<String> getValuesAsStringList(Document doc) {
		return delegate.getValuesAsStringList(doc);
	}
	
	@Override
	public Set<String> getValuesAsStringSet(Document doc) {
		return delegate.getValuesAsStringSet(doc);
	}
	
	@Override
	public String getValueAsString(Document doc) {
		return delegate.getValueAsString(doc);
	}

	@Override
	public final Query toQuery(T value) {
		return delegate.toQuery(value);
	}
	
	@Override
	public Query toQuery(Iterable<T> values) {
		return delegate.toQuery(values);
	}

	@Override
	public final Query toExistsQuery() {
		return delegate.toExistsQuery();
	}

	@Override
	public final Term toTerm(T value) {
		return delegate.toTerm(value);
	}

	@Override
	public void addTo(Document doc, T value) {
		delegate.addTo(doc, value);
	}

	@Override
	public final void removeAll(Document doc) {
		delegate.removeAll(doc);
	}
	
	@Override
	public final Sort createSort() {
		return delegate.createSort();
	}
	
	protected IndexField<T> getDelegate() {
		return delegate;
	}
	
}
