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
package com.b2international.index.json;

import java.util.Collections;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;

import com.b2international.index.lucene.Fields;
import com.b2international.index.lucene.IndexField;
import com.b2international.index.mapping.DocumentMapping;

/**
 * @since 4.7
 */
public class JsonDocumentMapping {

	public static Query matchIdAndType(Class<?> type, String key) {
		final String docType = DocumentMapping.getType(type);
		return Fields.newQuery().and(_id().toQuery(key)).and(_type().toQuery(docType)).matchAll();
	}
	
	public static IndexField<String> _id() {
		return Fields.stringField(DocumentMapping._ID);
	}
	
	public static IndexField<String> _uid() {
		return Fields.stringField(DocumentMapping._UID);
	}

	public static IndexField<String> _type() {
		return Fields.stringField(DocumentMapping._TYPE);
	}

	public static Filter filterByType(Class<?> type) {
		return _type().createTermsFilter(Collections.singleton(DocumentMapping.getType(type)));
	}

}
