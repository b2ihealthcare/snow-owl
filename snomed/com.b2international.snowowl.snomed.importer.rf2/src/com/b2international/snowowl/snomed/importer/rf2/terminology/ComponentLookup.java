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
package com.b2international.snowowl.snomed.importer.rf2.terminology;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.longs.LongKeyLongMap;
import com.b2international.commons.ClassUtils;
import com.b2international.index.Hits;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.RevisionIndexRead;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.CDOEditingContext;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.b2international.snowowl.snomed.importer.rf2.util.ImportUtil;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;

/**
 * Maps SNOMED CT component identifiers to CDO storage keys in a compact
 * in-memory map.
 * 
 * 
 * @param <C> the component type (must be either a subtype of {@link Component}
 * or a subtype of {@link SnomedRefSet})
 */
public class ComponentLookup<C extends CDOObject> {

	private static final int EXPECTED_COMPONENT_SIZE = 50000;

	private final LongKeyLongMap componentIdMap = PrimitiveMaps.newLongKeyLongOpenHashMapWithExpectedSize(EXPECTED_COMPONENT_SIZE);
	private final CDOEditingContext editingContext;
	private final EnumSet<ComponentImportType> initializedComponents = EnumSet.noneOf(ComponentImportType.class);
	private final Map<String, C> newComponents = Maps.newHashMap();
	private final Class<? extends C> clazz;
	private final RevisionIndex index;
	
	public ComponentLookup(final RevisionIndex index, final SnomedEditingContext editingContext, Class<? extends C> clazz) {
		this.index = index;
		this.editingContext = editingContext;
		this.clazz = clazz;
	}

	public void addNewComponent(final C component, final String id) {
		newComponents.put(id, component);
	}

	@SuppressWarnings("unchecked")
	public C getComponent(final String componentId) {
		final C c = newComponents.get(componentId);
		
		if (null != c) {
			return c;
		}
		
		long storageKey = getComponentStorageKey(componentId);
		
		if (storageKey > 0L) {
			return (C) editingContext.lookup(getComponentStorageKey(componentId));
		}
		
		return null;
	}

	public long getComponentStorageKey(final String componentId) {
		final long componentIdLong = ImportUtil.parseLong(componentId);
		if (!componentIdMap.containsKey(componentIdLong)) {
			try {
				final long storageKey = getStorageKey(componentId, SnomedIdentifiers.getComponentCategory(componentId));
				if (CDOUtils.NO_STORAGE_KEY != storageKey) {
					registerComponentStorageKey(componentId, storageKey);
				}
			} catch (Exception e) {
				throw new SnowowlRuntimeException(e);
			}
		}
		return componentIdMap.get(componentIdLong);
	}

	private long getStorageKey(final String componentId, final ComponentCategory componentCategory) throws IOException {
		return index.read(editingContext.getBranch(), new RevisionIndexRead<Long>() {
			@Override
			public Long execute(RevisionSearcher index) throws IOException {
				final Class<? extends SnomedDocument> type = SnomedDocument.getType(componentCategory);
				final Query<? extends SnomedDocument> query = Query.select(type).where(SnomedDocument.Expressions.id(componentId)).limit(2).build();
				final Hits<? extends SnomedDocument> hits = index.search(query);
				if (SnomedRefSet.class.isAssignableFrom(clazz)) {
					return hits.getTotal() > 0 ? ClassUtils.checkAndCast(Iterables.getOnlyElement(hits), SnomedConceptDocument.class).getRefSetStorageKey() : CDOUtils.NO_STORAGE_KEY;
				} else {
					return hits.getTotal() > 0 ? Iterables.getOnlyElement(hits).getStorageKey() : CDOUtils.NO_STORAGE_KEY;
				}
			}
		});
	}

	public boolean isInitialized(final ComponentImportType importType) {
		return initializedComponents.contains(importType);
	}

	public void registerComponentStorageKey(final String componentId, final long storageKey) {
		final long existingKey = componentIdMap.put(ImportUtil.parseLong(componentId), storageKey);
		
		if (existingKey > 0L && existingKey != storageKey) {
			throw new IllegalStateException(
					MessageFormat.format("Storage key re-registered for component with ID ''{0}''. Old key: {1}, new key: {2}",
							componentId, existingKey, storageKey));
		}
	}
	
	public void registerNewComponents() {
		// Consume each element while it is being registered
		for (final Iterator<Entry<String, C>> itr = Iterators.consumingIterator(newComponents.entrySet().iterator()); itr.hasNext();) {
			final Entry<String, C> newComponent = itr.next();
			registerComponentStorageKey(newComponent.getKey(), CDOIDUtil.getLong(newComponent.getValue().cdoID()));
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
			
			newComponents.clear();
			
		}
		
		if (null != componentIdMap) {
			
			componentIdMap.clear();
			
		}
		
		if (null != initializedComponents) {
			
			initializedComponents.clear();
			
		}
		
	}
}