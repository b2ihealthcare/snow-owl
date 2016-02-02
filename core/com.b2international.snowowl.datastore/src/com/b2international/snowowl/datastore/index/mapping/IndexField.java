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

import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.TermFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.BytesRef;

/**
 * @since 4.3
 * @param <T>
 *            - the type of the value to store in this index field
 */
public interface IndexField<T> {

	String fieldName();

	T getValue(Document doc);

	String getValueAsString(Document doc);

	List<T> getValues(Document doc);

	List<String> getValuesAsStringList(Document doc);

	Set<String> getValuesAsStringSet(Document doc);

	TermQuery toQuery(T value);

	PrefixQuery toExistsQuery();

	Term toTerm(T value);

	TermFilter toTermFilter(T value);

	void addTo(Document doc, T value);

	void removeAll(Document doc);

	<D extends DocumentBuilderBase<D>> void removeAll(D doc);

	Sort createSort();

	/**
	 * Creates a {@link Filter} accepting documents where any of the specified values appear in the indexed content for this field.
	 * <p>
	 * If the specified {@code Iterable} is empty, the returned filter will match no documents.
	 * 
	 * @param values the values to use (may not be {@code null})
	 * @return the {@code Filter} accepting any of the specified values for this field
	 */
	Filter createTermsFilter(Iterable<T> values);

	/**
	 * <i>Expert</i>: creates a {@link Filter} accepting documents where any of the specified {@link BytesRef}s appear in the indexed content for this
	 * field (term values need to be converted by the caller).
	 * <p>
	 * If the specified {@code Iterable} is empty, the returned filter will match no documents.
	 * 
	 * @param bytesRefs the {@code BytesRef}s to use (may not be {@code null})
	 * @return the {@code Filter} accepting any of the specified values for this field
	 */
	Filter createBytesRefFilter(Iterable<BytesRef> bytesRefs);

	/**
	 * Reads the value from the given source {@link Document} and adds it to the target {@link Document}.
	 * 
	 * @param source
	 * @param target
	 */
	void copyTo(Document source, Document target);
}
