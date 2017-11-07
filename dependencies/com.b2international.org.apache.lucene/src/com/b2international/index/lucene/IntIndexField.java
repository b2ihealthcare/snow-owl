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

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.NumericUtils;

import com.google.common.collect.ImmutableSet;

/**
 * @since 4.3
 */
public class IntIndexField extends IndexFieldBase<Integer> {

	public IntIndexField(String fieldName) {
		this(fieldName, true);
	}
	
	public IntIndexField(String fieldName, boolean store) {
		super(fieldName, store);
	}
	
	@Override
	public Query toQuery(Integer value) {
		return IntPoint.newExactQuery(fieldName(), value);
	}
	
	@Override
	protected Query toSetQuery(Iterable<Integer> values) {
		return IntPoint.newSetQuery(fieldName(), ImmutableSet.copyOf(values));
	}
	
	@Override
	public void addTo(Document doc, Integer value) {
		super.addTo(doc, value);
		if (Store.YES == isStored()) {
			doc.add(new StoredField(fieldName(), value));
		}
	}
	
	@Override
	protected IndexableField toField(Integer value) {
		return new IntPoint(fieldName(), value);
	}
	
	@Override
	protected BytesRef toBytesRef(Integer value) {
		byte[] packed = new byte[Integer.BYTES];
		NumericUtils.intToSortableBytes(value, packed, 0);
	    return new BytesRef(packed);
	}
	
	@Override
	protected Type getSortFieldType() {
		return Type.INT;
	}

	@Override
	public Integer getValue(IndexableField field) {
		return getNumber(field).intValue();
	}

	public short getShortValue(Document doc) {
		return getNumber(getField(doc)).shortValue();
	}
	
	private Number getNumber(IndexableField field) {
		return checkNotNull(field.numericValue(), "Cannot get numeric value from field '%s'");
	}
	
}
