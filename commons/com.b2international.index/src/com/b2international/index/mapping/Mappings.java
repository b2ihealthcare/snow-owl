/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static com.google.common.collect.Maps.newHashMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.index.query.Query;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.*;
import com.google.common.collect.Multiset.Entry;

/**
 * @since 4.7
 */
public final class Mappings {

	private final BiMap<Class<?>, String> docTypeCache = HashBiMap.create();
	private final Map<Class<?>, DocumentMapping> mappingsByType = newHashMap(); 
	
	public Mappings(Class<?>...types) {
		this(Set.of(types));
	}
	
	public Mappings(Collection<Class<?>> types) {
		final Multiset<String> duplicates = HashMultiset.create();
		for (Class<?> type : Set.copyOf(types)) {
			// XXX register only root mappings, nested mappings should be looked up via the parent/ancestor mapping
			DocumentMapping mapping = putMapping(type);
			duplicates.add(mapping.typeAsString());
		}
		for (Entry<String> duplicate : duplicates.entrySet()) {
			if (duplicate.getCount() > 1) {
				throw new IllegalArgumentException("Multiple Java types with the same document name: " + duplicate.getElement());
			}
		}
	}
	
	public Collection<Class<?>> getTypes() {
		return Set.copyOf(mappingsByType.keySet());
	}
	
	public DocumentMapping putMapping(Class<?> type) {
		final DocumentMapping mapping = new DocumentMapping(type);
		mappingsByType.put(type, mapping);
		// init cache by calculating docType
		getType(type);
		return mapping;
	}

	public Collection<DocumentMapping> getMappings() {
		return ImmutableList.copyOf(mappingsByType.values());
	}
	
	public DocumentMapping getMapping(Class<?> type) {
		checkArgument(mappingsByType.containsKey(type), "Dynamic mapping is not supported: %s", type);
		return mappingsByType.get(type);
	}
	
	public DocumentMapping getByType(String className) {
		final Collection<DocumentMapping> mappings = Lists.newArrayList();
		for (DocumentMapping mapping : mappingsByType.values()) {
			if (mapping.type().getName().equals(className)) {
				mappings.add(mapping);
			}
		}
		return Iterables.getOnlyElement(mappings);
	}
	
	public List<DocumentMapping> getDocumentMapping(Query<?> query) {
		if (query.getSelection().getParentScope() != null) {
			return List.of(getMapping(query.getSelection().getParentScope()).getNestedMapping(Iterables.getOnlyElement(query.getSelection().getFrom())));
		} else {
			return query.getSelection().getFrom().stream().map(this::getMapping).collect(Collectors.toList());
		}
	}
	
	/**
	 * For testing purposes only.
	 */
	@VisibleForTesting
	/*package*/ boolean enableRuntimeMappingOverrides = false; 

	public String getType(Class<?> type) {
		checkNotNull(type, "Type argument may not be null");
		if (!docTypeCache.containsKey(type)) {
			final String docType = DocumentMapping.getDocType(type);
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

	public Class<?> getClass(String type) {
		return checkNotNull(docTypeCache.inverse().get(type), "Missing doc class for key '%s'. Populate the doc type cache via #getType(Class<?>) method before using this method.", type);
	}
	
}
