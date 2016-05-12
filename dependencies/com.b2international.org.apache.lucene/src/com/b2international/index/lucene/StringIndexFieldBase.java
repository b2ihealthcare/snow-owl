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
package com.b2international.index.lucene;

import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.util.BytesRef;


/**
 * @since 4.3
 */
public abstract class StringIndexFieldBase extends IndexFieldBase<String> {

	public StringIndexFieldBase(String fieldName) {
		this(fieldName, true);
	}
	
	public StringIndexFieldBase(String fieldName, boolean stored) {
		super(fieldName, stored);
	}
	
	@Override
	protected BytesRef toBytesRef(String value) {
		return new BytesRef(value);
	}
	
	@Override
	protected Type getSortFieldType() {
		return Type.STRING;
	}
	
	@Override
	public String getValue(IndexableField field) {
		return field.stringValue();
	}

}
