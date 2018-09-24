/*
 * Copyright 2016-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

public enum EmptyIndexField implements IndexField<Object> {
	
	INSTANCE;
	
	@Override
	public Term toTerm(Object value) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Query toQuery(Object value) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Query toQuery(Iterable<Object> values) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Query toExistsQuery() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void removeAll(Document doc) {
	}
	
	@Override
	public Set<String> getValuesAsStringSet(Document doc) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public List<String> getValuesAsStringList(Document doc) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public List<Object> getValues(Document doc) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String getValueAsString(Document doc) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Object getValue(Document doc) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String fieldName() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Sort createSort() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void copyTo(Document source, Document target) {
	}
	
	@Override
	public void addTo(Document doc, Object value) {
	}	
}
