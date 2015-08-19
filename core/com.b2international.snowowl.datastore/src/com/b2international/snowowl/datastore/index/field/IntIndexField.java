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

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.util.BytesRef;

import com.b2international.snowowl.datastore.index.IndexUtils;

/**
 * @since 4.3
 */
public class IntIndexField extends IndexField {

	private int value;
	
	public IntIndexField(String fieldName, short value) {
		this(fieldName, (int) value);
	}
	
	public IntIndexField(String fieldName, int value) {
		super(fieldName);
		this.value = value;
	}
	
	@Override
	protected BytesRef toBytesRef() {
		return IndexUtils.intToPrefixCoded(value);
	}
	
	@Override
	protected IndexableField toField() {
		return new IntField(getFieldName(), value, Store.YES);
	}

	public static int getInt(Document doc, String fieldName) {
		return IndexUtils.getIntValue(doc.getField(fieldName));
	}

	public static short getShort(Document doc, String fieldName) {
		return IndexUtils.getShortValue(doc.getField(fieldName));
	}
	
}
