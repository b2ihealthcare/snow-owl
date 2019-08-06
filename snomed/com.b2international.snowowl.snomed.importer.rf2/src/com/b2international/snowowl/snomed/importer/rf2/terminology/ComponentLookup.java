/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.importer.rf2.terminology;

import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongKeyLongMap;
import com.b2international.collections.longs.LongSet;
import com.b2international.collections.longs.LongValueMap;
import com.b2international.index.Hits;
import com.b2international.index.query.Query;
import com.b2international.index.revision.Revision;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.RevisionIndexRead;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.datastore.CDOEditingContext;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.b2international.snowowl.snomed.importer.rf2.util.ImportUtil;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

/**
 * Maps SNOMED CT component identifiers to CDO storage keys in a compact
 * in-memory map.
 * 
 * @param <C> the component type (must be either a subtype of {@link Component}
 * or a subtype of {@link SnomedRefSet})
 */
public final class ComponentLookup<C extends CDOObject> {

	public static final int EXPECTED_COMPONENT_SIZE = 50000;
	
	private final CDOEditingContext editingContext;
	private final EnumSet<ComponentImportType> initializedComponents = EnumSet.noneOf(ComponentImportType.class);
	private final Class<? extends C> clazz;
	private final RevisionIndex index;
	
	private LongKeyLongMap componentIdMap;
	private Map<String, C> newComponents;
	
	public ComponentLookup(final RevisionIndex index, final SnomedEditingContext editingContext, Class<? extends C> clazz) {
		this.index = index;
		this.editingContext = editingContext;
		this.clazz = clazz;
	}

	public void addNewComponent(final C component, final String id) {
		if (newComponents == null) {
			newComponents = Maps.newHashMap();
		}
		C prev = newComponents.put(id, component);
		if (prev != null && component != prev) {
			throw new IllegalStateException("Reregistering a new component with ID " + id);
		}
	}

	public C getNewComponent(String componentId) {
		return newComponents == null ? null : newComponents.get(componentId);
	}
	
	/**
	 * Fetch CDOObject for the given component IDs.
	 * @param componentIds 
	 * @return
	 */
	public Collection<C> getComponents(Collection<String> componentIds) {
		final Collection<C> components = Sets.newHashSetWithExpectedSize(componentIds.size());
		final Set<String> missingComponentIds = Sets.newHashSet();
		
		for (String componentId : componentIds) {
			final C component = getNewComponent(componentId);
			if (component != null) {
				components.add(component);
			} else {
				missingComponentIds.add(componentId);
			}
		}
		
		if (missingComponentIds.isEmpty()) {
			return components;
		}
		
		LongIterator storageKeys = getComponentStorageKeys(missingComponentIds).iterator();
		
		while (storageKeys.hasNext()) {
			final long storageKey = storageKeys.next();
			components.add((C) editingContext.lookup(storageKey));
		}
		
		return components;
	}

	public long getComponentStorageKey(final String componentId) {
		LongIterator it = getComponentStorageKeys(Collections.singleton(componentId)).iterator();
		return it.hasNext() ? it.next() : CDOUtils.NO_STORAGE_KEY;
	}

	public LongSet getComponentStorageKeys(final Collection<String> componentIds) {
		final LongSet storageKeys = PrimitiveSets.newLongOpenHashSetWithExpectedSize(componentIds.size());
		final Set<String> missingStorageKeyComponentIds = newHashSet();
		for (String componentId : componentIds) {
			final long componentIdLong = ImportUtil.parseLong(componentId);
			if (componentIdMap != null && componentIdMap.containsKey(componentIdLong)) {
				storageKeys.add(componentIdMap.get(componentIdLong));
			} else {
				missingStorageKeyComponentIds.add(componentId);
			}
		}
		
		if (!missingStorageKeyComponentIds.isEmpty()) {
			try {
				LongValueMap<String> missingStorageKeys = getStorageKeys(missingStorageKeyComponentIds);
				for (String missingStorageKeyComponentId : missingStorageKeyComponentIds) {
					if (missingStorageKeys.containsKey(missingStorageKeyComponentId)) {
						final long missingStorageKey = missingStorageKeys.get(missingStorageKeyComponentId);
						if (missingStorageKey > CDOUtils.NO_STORAGE_KEY) {
							storageKeys.add(missingStorageKey);
							registerComponentStorageKey(missingStorageKeyComponentId, missingStorageKey);
						}
					}
				} 
			} catch (IOException e) {
				throw new SnowowlRuntimeException(e);
			}
		}
		
		return storageKeys;
	}
	
	private LongValueMap<String> getStorageKeys(final Collection<String> componentIds) throws IOException {
		return index.read(editingContext.getBranch(), new RevisionIndexRead<LongValueMap<String>>() {
			@Override
			public LongValueMap<String> execute(RevisionSearcher index) throws IOException {
				final LongValueMap<String> map = PrimitiveMaps.newObjectKeyLongOpenHashMapWithExpectedSize(componentIds.size());
				// index componentIds by their category
				final Multimap<Class<? extends SnomedDocument>, String> idsByType = Multimaps.index(componentIds, id -> SnomedDocument.getType(SnomedIdentifiers.getComponentCategory(id)));
				for (Class<? extends SnomedDocument> type : idsByType.keySet()) {
					// execute queries for each type and based on the current clazz extract either refset or concept storage keys
					ImmutableList.Builder<String> fields = ImmutableList.builder();
					fields.add(SnomedDocument.Fields.ID);
					if (SnomedConceptDocument.class.isAssignableFrom(type) && SnomedRefSet.class.isAssignableFrom(clazz)) {
						fields.add(SnomedConceptDocument.Fields.REFSET_STORAGEKEY);
					} else {
						fields.add(Revision.STORAGE_KEY);
					}
					final Query<String[]> query = Query.select(String[].class)
							.from(type)
							.fields(fields.build())
							.where(SnomedDocument.Expressions.ids(componentIds))
							.limit(componentIds.size())
							.build();
					final Hits<String[]> hits = index.search(query);
					for (String[] doc : hits) {
						map.put(doc[0], Long.parseLong(doc[1]));
					}
				}
				return map;
			}
		});
	}

	public boolean isInitialized(final ComponentImportType importType) {
		return initializedComponents.contains(importType);
	}

	private void registerComponentStorageKey(final String componentId, final long storageKey) {
		if (componentIdMap == null) {
			componentIdMap = PrimitiveMaps.newLongKeyLongOpenHashMapWithExpectedSize(EXPECTED_COMPONENT_SIZE);
		}
		final long existingKey = componentIdMap.put(ImportUtil.parseLong(componentId), storageKey);
		
		if (existingKey > 0L && existingKey != storageKey) {
			throw new IllegalStateException(
					MessageFormat.format("Storage key re-registered for component with ID ''{0}''. Old key: {1}, new key: {2}",
							componentId, existingKey, storageKey));
		}
	}
	
	public void registerNewComponentStorageKeys() {
		// Consume each element while it is being registered
		if (newComponents != null) {
			for (final Entry<String, C> newComponent : Iterables.consumingIterable(newComponents.entrySet())) {
				registerComponentStorageKey(newComponent.getKey(), CDOIDUtil.getLong(newComponent.getValue().cdoID()));
			}
			newComponents = null;
		}
	}
	
	public void setInitialized(final ComponentImportType importType) {
		initializedComponents.add(importType);
	}
	
	/**
	 * Clears the underlying caches.
	 */
	public void clear() {
		
		if (null != newComponents) {
			newComponents = null;
		}
		
		if (null != componentIdMap) {
			componentIdMap = null;
		}
		
		if (null != initializedComponents) {
			initializedComponents.clear();
		}
		
	}

}