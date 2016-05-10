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
package com.b2international.index.mapping;

import static com.google.common.base.Preconditions.checkArgument;

import com.b2international.index.Doc;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.google.common.base.Strings;

/**
 * @since 4.7
 */
public class DocumentMapping {

	public static final String _ID = "_id";
	public static final String _UID = "_uid";
	public static final String _TYPE = "_type";

	private DocumentMapping() {}
	
	public static Expression matchType(Class<?> type) {
		return Expressions.exactMatch(_TYPE, getType(type));
	}

	public static Expression matchId(String id) {
		return Expressions.exactMatch(_ID, id);
	}
	
	public static String toUid(Class<?> type, String key) {
		return String.format("%s#%s", getType(type), key);
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
	
	public static boolean isNestedDoc(Class<?> fieldType) {
		return fieldType.isAnnotationPresent(Doc.class) && fieldType.getAnnotation(Doc.class).nested();
	}
	
}
