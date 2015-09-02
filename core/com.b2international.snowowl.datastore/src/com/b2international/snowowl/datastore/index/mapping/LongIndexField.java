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
import org.apache.lucene.document.LongField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.util.BytesRef;

import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

import com.b2international.snowowl.datastore.index.IndexUtils;

/**
 * @since 4.3
 */
public class LongIndexField extends IndexFieldBase<Long> {

	public LongIndexField(String fieldName) {
		this(fieldName, true);
	}
	
	public LongIndexField(String fieldName, boolean stored) {
		super(fieldName, stored);
	}

	@Override
	protected IndexableField toField(Long value) {
		return new LongField(fieldName(), value, isStored());
	}

	@Override
	protected BytesRef toBytesRef(Long value) {
		return _toBytesRef(value);
	}
	
	@Override
	protected Type getSortFieldType() {
		return Type.LONG;
	}
	
	@Override
	public Long getValue(IndexableField field) {
		return field.numericValue().longValue();
	}
	
	public final LongSet getValueAsLongSet(Document doc) {
		final IndexableField[] fields = getFields(doc);
		final LongSet longIds = new LongOpenHashSet(fields.length + 1);
		for (final IndexableField field : fields) {
			longIds.add(getValue(field));
		}
		return longIds;
	}

	/**
	 * @param value
	 * @return
	 * @deprecated - if possible don't use this API, use {@link Mappings} or {@link #LongIndexField(String) constructor} instead
	 */
	public static BytesRef _toBytesRef(Long value) {
		return IndexUtils.longToPrefixCoded(value);
	}

}
