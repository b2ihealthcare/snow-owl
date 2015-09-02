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

import java.util.List;

import org.apache.lucene.document.Document;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

/**
 * @since 4.3 
 * @param <T> - the type of the {@link IndexField} to delegate to
 */
public class FilteredIndexField<T> extends IndexFieldDelegate<T> {

	private Predicate<? super T> predicate;

	public FilteredIndexField(IndexField<T> delegate, Predicate<? super T> predicate) {
		super(delegate);
		this.predicate = checkNotNull(predicate, "predicate");
	}

	@Override
	public List<T> getValues(Document doc) {
		return FluentIterable.from(super.getValues(doc)).filter(predicate).toList();
	}
	
	protected final Predicate<? super T> getPredicate() {
		return predicate;
	}
	
}
