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
package com.b2international.snowowl.datastore.index.field;

import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FieldCacheTermsFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.BytesRef;

import com.b2international.commons.CompareUtils;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

public abstract class ComponentIdField {

	public static final String COMPONENT_ID = "component_id";

	public static Query existsQuery() {
		return new PrefixQuery(new Term(COMPONENT_ID));
	}

	public static String getString(Document doc) {
		 return doc.get(COMPONENT_ID);
	}
	
	protected static Sort createSort(Type type) {
		return new Sort(new SortField(COMPONENT_ID, type));
	}

	protected static Filter createFilter(Function<String, BytesRef> toBytesRefFunction, String... componentIds) {
		if (CompareUtils.isEmpty(componentIds)) {
			return null;
		} else {
			return createFilter(FluentIterable.from(ImmutableList.copyOf(componentIds)).transform(toBytesRefFunction).toList());
		}
	}

	public static Filter createFilter(final List<BytesRef> bytesRefs) {
		return new FieldCacheTermsFilter(COMPONENT_ID, bytesRefs.toArray(new BytesRef[bytesRefs.size()]));
	}

	public void addTo(Document doc) {
		doc.add(toField());
	}

	public TermQuery toQuery() {
		return new TermQuery(new Term(COMPONENT_ID, toBytesRef()));
	}

	protected abstract IndexableField toField();

	protected abstract BytesRef toBytesRef();
}
