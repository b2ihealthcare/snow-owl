/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Set;

import com.b2international.index.Doc;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * @since 
 */
public enum DocumentMappingRegistry {

	INSTANCE;
	
	private final BiMap<Class<?>, String> docTypeCache = HashBiMap.create();
	
	/**
	 * For testing purposes only.
	 */
	@VisibleForTesting
	/*package*/ boolean enableRuntimeMappingOverrides = false; 

	public String getType(Class<?> type) {
		if (!docTypeCache.containsKey(type)) {
			final Doc annotation = getDocAnnotation(type);
			checkArgument(annotation != null, "Doc annotation must be present on type '%s' or on its class hierarchy", type);
			final String docType = Strings.isNullOrEmpty(annotation.type()) ? type.getSimpleName().toLowerCase() : annotation.type();
			checkArgument(!Strings.isNullOrEmpty(docType), "Document type should not be null or empty on class %s", type.getName());
			// let other classes override
			if (docTypeCache.containsValue(docType)) {
				if (enableRuntimeMappingOverrides) {
					docTypeCache.inverse().remove(docType);
				} else {
					Class<?> existingClassMapping = docTypeCache.inverse().get(docType);
					throw new IllegalArgumentException(String.format("Another class '%s' already uses the same index name '%s' as this class '%s'.", existingClassMapping.getName(), docType, type.getName()));
				}
			}
			docTypeCache.put(type, docType);
		}
		return docTypeCache.get(type);
	}

	public Collection<Class<?>> getTypes() {
		return Set.copyOf(docTypeCache.keySet());
	}

	public Class<?> getClass(String type) {
		return checkNotNull(docTypeCache.inverse().get(type), "Missing doc class for key '%s'. Populate the doc type cache via #getType(Class<?>) method before using this method.", type);
	}
	
	public static Doc getDocAnnotation(Class<?> type) {
		if (type.isAnnotationPresent(Doc.class)) {
			return type.getAnnotation(Doc.class);
		} else {
			if (type.getSuperclass() != null) {
				final Doc doc = getDocAnnotation(type.getSuperclass());
				if (doc != null) {
					return doc;
				}
			}
			
			for (Class<?> iface : type.getInterfaces()) {
				final Doc doc = getDocAnnotation(iface);
				if (doc != null) {
					return doc;
				}
			}
			return null;
		}
	}
	
}
