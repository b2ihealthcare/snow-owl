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

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.lucene.search.Query;

import com.b2international.index.Doc;
import com.b2international.index.mapping.IndexField;
import com.b2international.index.mapping.Mappings;
import com.google.common.base.Strings;

/**
 * @since 4.7
 */
public class JsonDocumentMapping {

	public static Query matchIdAndType(Class<?> type, String key) {
		final String docType = getType(type);
		return Mappings.newQuery().and(_id().toQuery(key)).and(_type().toQuery(docType)).matchAll();
	}
	
	public static IndexField<String> _id() {
		return Mappings.stringField("_id");
	}

	public static IndexField<String> _type() {
		return Mappings.stringField("_type");
	}

	public static String getType(Object object) {
		return getType(object.getClass());
	}
	
	public static String getType(Class<?> type) {
		checkArgument(type.isAnnotationPresent(Doc.class), "Doc annotation must be present on types need to be indexed as separated documents");
		final Doc annotation = type.getAnnotation(Doc.class);
		final String docType = Strings.isNullOrEmpty(annotation.type()) ? type.getSimpleName().toLowerCase() : annotation.type();
		checkArgument(!Strings.isNullOrEmpty(docType), "Document type should not be null or empty on class %s", type.getName());
		return docType;
	}
	
}
