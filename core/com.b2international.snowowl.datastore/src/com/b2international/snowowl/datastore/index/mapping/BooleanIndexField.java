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

import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.util.BytesRef;

/**
 * @since 4.3
 */
public class BooleanIndexField extends IndexFieldBase<Boolean> {

	public BooleanIndexField(String fieldName) {
		this(fieldName, false);
	}
	
	public BooleanIndexField(String fieldName, boolean stored) {
		super(fieldName, stored);
	}

	@Override
	protected Boolean getValue(IndexableField field) {
		return convertFromString(field.stringValue());
	}

	@Override
	protected BytesRef toBytesRef(Boolean value) {
		return new BytesRef(convertToString(value));
	}

	@Override
	protected IndexableField toField(Boolean value) {
		return new StringField(fieldName(), convertToString(value), Store.YES);
	}

	@Override
	protected Type getSortFieldType() {
		return Type.STRING;
	}
	
	private String convertToString(Boolean value) {
		return value ? "1" : "0";
	}
	
	private Boolean convertFromString(String value) {
		return "1".equals(value);
	}

}
