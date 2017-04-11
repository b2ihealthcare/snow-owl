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

import com.b2international.index.lucene.Fields;
import com.b2international.index.lucene.IndexField;
import com.b2international.index.mapping.DocumentMapping;

/**
 * @since 4.7
 */
public class JsonDocumentMapping {

	public static IndexField<String> _id() {
		return Fields.stringField(DocumentMapping._ID);
	}
	
	public static IndexField<String> _uid() {
		return Fields.searchOnlyStringField(DocumentMapping._UID);
	}

	public static IndexField<String> _type() {
		return Fields.searchOnlyStringField(DocumentMapping._TYPE);
	}

	public static Filter filterByType(String type) {
		return _type().createTermsFilter(Collections.singleton(type));
	}

	public static IndexField<String> _hash() {
		return Fields.searchOnlyStringField(DocumentMapping._HASH);
	}

}
