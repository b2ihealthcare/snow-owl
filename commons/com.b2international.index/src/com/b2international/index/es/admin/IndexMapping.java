/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.es.admin;

import java.util.*;
import java.util.stream.Collectors;

import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.query.Query;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterables;

/**
 * Contains information about the actual indices present in the Elasticsearch and their current {@link DocumentMapping}.
 * 
 * @since 9.0
 */
public final class IndexMapping {

	private Mappings mappings;
	private final BiMap<String, DocumentMapping> mappingByIndex;
	
	public IndexMapping(Mappings mappings) {
		this.mappings = Objects.requireNonNull(mappings);
		this.mappingByIndex = HashBiMap.create(mappings.size());
	}

	/**
	 * Registering the mapping and indexName as a created and ready to use index. Previously registered index names will be removed, but it is up to
	 * the caller to actually delete and remove the index from the underlying Elasticsearch instance.
	 * 
	 * @param mapping
	 *            - the mapping of the index
	 * @param indexName
	 *            - the name of the index that should be associated with the mapping
	 */
	public void register(DocumentMapping mapping, String indexName) {
		Objects.requireNonNull(mapping, "mapping may not be null");
		Objects.requireNonNull(indexName, "indexName may not be null");
		
		Preconditions.checkState(mappings.getMapping(mapping.type()) != null, "Mapping configuration is missing for type '%s' in this index mapping.", mapping.type());
		Preconditions.checkState(mappings.getMapping(mapping.type()) == mapping, "Type '%s' incorrectly has two mapping configurations", mapping.type());
		
		this.mappingByIndex.put(indexName, mapping);
	}
	
	/**
	 * Remove the currently configured indexName link for the given mapping so it is no longer available for search and write operations.
	 * 
	 * @param mapping - the mapping to clear from the index
	 */
	public void unregister(DocumentMapping mapping) {
		Objects.requireNonNull(mapping, "mapping may not be null");
		Preconditions.checkState(mappings.getMapping(mapping.type()) != null, "Mapping configuration is missing for type '%s' in this index mapping.", mapping.type());
		
		this.mappingByIndex.inverse().remove(mapping);
	}
	
	public Mappings getMappings() {
		return this.mappings;
	}
	
	public DocumentMapping getMapping(Class<?> type) {
		return mappings.getMapping(type);
	}
	
	public List<DocumentMapping> getDocumentMapping(Query<?> query) {
		if (query.getSelection().getParentScope() != null) {
			return List.of(getMapping(query.getSelection().getParentScope()).getNestedMapping(Iterables.getOnlyElement(query.getSelection().getFrom())));
		} else {
			return query.getSelection().getFrom().stream().map(this::getMapping).collect(Collectors.toList());
		}
	}
	
	public Set<String> updateMappings(Mappings mappings) {
		// override mappings
		this.mappings = mappings;
		
		// remove all unknown index registrations and return them for deletion
		final Set<String> removedIndexes = new HashSet<>();
		for (final DocumentMapping previousMapping : Set.copyOf(this.mappingByIndex.inverse().keySet())) {
			if (!this.mappings.getDocumentMappings().contains(previousMapping)) {
				removedIndexes.add(this.mappingByIndex.inverse().remove(previousMapping));
			}
		}
		return removedIndexes;
	}
	
	/**
	 * @return the current set of index names available for any kind of operation (they exist and are at least in yellow state)
	 */
	public String[] indices() {
		return mappingByIndex.keySet().stream().sorted().toArray(String[]::new);
	}
	
	/**
	 * Returns the actual index name for the given {@link DocumentMapping}.
	 * 
	 * @param mapping
	 * @return
	 */
	public String getTypeIndex(DocumentMapping mapping) {
		Preconditions.checkState(mappingByIndex.containsValue(mapping), "Mapping '%s' does not exist in this index mapping configuration", mapping.type());
		return mappingByIndex.inverse().get(mapping);
	}
	
	/**
	 * @param type - the type for which the current index name needs to be returned
	 * @return the current index name where content for this type can be found, never <code>null</code>
	 */
	public String getTypeIndex(Class<?> type) {
		return getTypeIndex(mappings.getMapping(type));
	}
	
	/**
	 * Returns the actual index name for the given List of {@link DocumentMapping mappings}.
	 * 
	 * @param mappings
	 * @return
	 */
	public List<String> getTypeIndexes(List<DocumentMapping> mappings) {
		return mappings.stream().map(this::getTypeIndex).collect(Collectors.toList());
	}
	
	public Map<String, DocumentMapping> getMappingByIndex() {
		return Map.copyOf(mappingByIndex);
	}

}
