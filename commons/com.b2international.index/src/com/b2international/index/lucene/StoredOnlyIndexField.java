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
import org.apache.lucene.document.StoredField;

/**
 * @since 4.3 
 * @param <T> - the type of the stored only field
 */
public final class StoredOnlyIndexField<T> extends IndexFieldDelegate<T> implements StoredIndexField<T> {
	
	public StoredOnlyIndexField(IndexField<T> delegate) {
		super(delegate);
	}
	
	@Override
	public void addTo(Document doc, T value) {
		if (value instanceof String) {
			doc.add(new StoredField(fieldName(), (String) value));
		} else if (value instanceof Long) {
			doc.add(new StoredField(fieldName(), (Long) value));
		} else if (value instanceof Integer) {
			doc.add(new StoredField(fieldName(), (Integer) value));
		} else if (value instanceof Float) {
			doc.add(new StoredField(fieldName(), (Float) value));
		}
	}
	
}
