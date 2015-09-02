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

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;

import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

import com.google.common.base.Predicate;

/**
 * @since 4.3
 */
public class FilteredLongIndexField extends FilteredIndexField<Long> implements LongCollectionIndexField {

	public FilteredLongIndexField(LongIndexField field, Predicate<? super Long> predicate) {
		super(field, predicate);
	}

	@Override
	public LongSet getValueAsLongSet(Document doc) {
		final IndexableField[] fields = getDelegate().getFields(doc);
		final LongSet longIds = new LongOpenHashSet(fields.length + 1);
		for (final IndexableField field : fields) {
			final Long value = getDelegate().getValue(field);
			if (getPredicate().apply(value)) {
				longIds.add(value);
			}
		}
		return longIds;
	}
	
	@Override
	protected LongIndexField getDelegate() {
		return (LongIndexField) super.getDelegate();
	}
	
}
