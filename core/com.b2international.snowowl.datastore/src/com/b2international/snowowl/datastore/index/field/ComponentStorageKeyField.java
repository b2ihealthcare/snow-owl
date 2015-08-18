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

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.util.BytesRef;

import com.b2international.snowowl.datastore.index.IndexUtils;

/**
 * @since 4.3
 */
public class ComponentStorageKeyField extends IndexField {

	public static final String COMPONENT_STORAGE_KEY = "component_storage_key";
	public static final Set<String> FIELDS_TO_LOAD = Collections.singleton(COMPONENT_STORAGE_KEY);
	
	private long storageKey;
	
	public ComponentStorageKeyField(long storageKey) {
		this.storageKey = storageKey;
	}
	
	@Override
	protected IndexableField toField() {
		return new LongField(COMPONENT_STORAGE_KEY, storageKey, Store.YES);
	}

	@Override
	protected BytesRef toBytesRef() {
		return IndexUtils.longToPrefixCoded(storageKey);
	}

	@Override
	protected String getFieldName() {
		return COMPONENT_STORAGE_KEY;
	}
	
	public static long getLong(Document doc) {
		return IndexUtils.getLongValue(doc.getField(COMPONENT_STORAGE_KEY));
	}

	public static NumericDocValues getNumericDocValues(AtomicReader leafReader) throws IOException {
		return leafReader.getNumericDocValues(COMPONENT_STORAGE_KEY);
	}

}
