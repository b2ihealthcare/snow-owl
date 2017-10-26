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

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FloatPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.util.BytesRef;

/**
 * @since 4.3
 */
public class FloatIndexField extends IndexFieldBase<Float> {

	public FloatIndexField(String fieldName) {
		this(fieldName, true);
	}
	
	public FloatIndexField(String fieldName, boolean store) {
		super(fieldName, store);
	}

	@Override
	protected Float getValue(IndexableField field) {
		return field.numericValue().floatValue();
	}

	@Override
	protected BytesRef toBytesRef(Float value) {
		return null;
	}

	@Override
	public void addTo(Document doc, Float value) {
		super.addTo(doc, value);
		if (Store.YES == isStored()) {
			doc.add(new StoredField(fieldName(), value));
		}
	}
	
	@Override
	protected IndexableField toField(Float value) {
		return new FloatPoint(fieldName(), value);
	}

	@Override
	protected Type getSortFieldType() {
		return Type.FLOAT;
	}

}
