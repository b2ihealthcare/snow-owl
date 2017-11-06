/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request.rf2;

import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mapdb.DB;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongKeyMap;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.graph.LongTarjan;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.domain.TransactionContextProvider;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;

/**
 * @since 6.0
 */
final class Rf2EffectiveTimeSlice {

	private static final int BATCH_SIZE = 5000;

	private final String effectiveTime;
	private final LongKeyMap<Set<String>> componentsByContainer;
	private final LongKeyMap<LongSet> dependenciesByComponent;
	
	// tmp map to quickly collect batch of items before flushing it to disk
	private final Map<String, String[]> tmpComponentsById;
	private final HTreeMap<String, String[]> componentsById;

	
	public Rf2EffectiveTimeSlice(DB db, String effectiveTime) {
		this.effectiveTime = effectiveTime;
		this.componentsById = db.hashMap(effectiveTime, Serializer.STRING, Serializer.ELSA).create();
		this.tmpComponentsById = newHashMapWithExpectedSize(BATCH_SIZE);
		this.dependenciesByComponent = PrimitiveMaps.newLongKeyOpenHashMap();
		this.componentsByContainer = PrimitiveMaps.newLongKeyOpenHashMap();
	}

	public Collection<SnomedComponent> getComponentsByContainer(long containerId) {
		final ImmutableList.Builder<SnomedComponent> components = ImmutableList.builder();
		for (String componentId : componentsByContainer.get(containerId)) {
			components.add(getComponent(componentId));
		}
		return components.build();
	}

	private SnomedComponent getComponent(String componentId) {
		final String[] valuesWithType = componentsById.get(componentId);

		// skip non-RF2 componentIds
		if (valuesWithType == null) {
			return null;
		}
		
		for (Rf2ContentType<?> resolver : Rf2Format.getContentTypes()) {
			if (valuesWithType[0].equals(resolver.getType())) {
				String[] values = new String[valuesWithType.length - 1];
				System.arraycopy(valuesWithType, 1, values, 0, valuesWithType.length - 1);
				return resolver.resolve(values);
			}
		}
		
		throw new IllegalArgumentException("Unrecognized RF2 component: " + componentId + " - " + valuesWithType);
	}

	public void register(String containerId, String type, String[] values) {
		String[] valuesWithType = new String[values.length + 1];
		valuesWithType[0] = type;
		System.arraycopy(values, 0, valuesWithType, 1, values.length);

		final String componentId = values[0];
		final long containerIdL = Long.parseLong(containerId);
		// XXX do not register concepts in componentsByContainer map
		if (!IComponent.ROOT_ID.equals(containerId)) {
			if (!componentsByContainer.containsKey(containerIdL)) {
				componentsByContainer.put(containerIdL, newHashSet());
			}
			componentsByContainer.get(containerIdL).add(componentId);
		}
		tmpComponentsById.put(componentId, valuesWithType);
		if (tmpComponentsById.size() >= BATCH_SIZE) {
			flush();
		}
	}
	
	public void registerDependencies(long componentId, LongSet dependencies) {
		if (!dependenciesByComponent.containsKey(componentId)) {
			dependenciesByComponent.put(componentId, dependencies);
		} else {
			dependenciesByComponent.get(componentId).addAll(dependencies);
		}
	}

	public void flush() {
		if (!tmpComponentsById.isEmpty()) {
			componentsById.putAll(tmpComponentsById);
		}
		tmpComponentsById.clear();
	}

	private List<LongSet> getImportPlan() {
		return new LongTarjan(50000, dependenciesByComponent::get).run(dependenciesByComponent.keySet());
	}

	public void doImport(BranchContext context) throws Exception {
		Stopwatch w = Stopwatch.createStarted();
		System.err.println("Importing components from " + effectiveTime);
		try (Rf2TransactionContext tx = new Rf2TransactionContext(context.service(TransactionContextProvider.class).get(context))) {
			for (LongSet componentsToImportInBatch : getImportPlan()) {
				LongIterator it = componentsToImportInBatch.iterator();
				final Collection<SnomedComponent> componentsToImport = newArrayListWithExpectedSize(componentsToImportInBatch.size());
				while (it.hasNext()) {
					long componentToImportL = it.next();
					String componentToImport = Long.toString(componentToImportL);
					SnomedComponent component = getComponent(componentToImport);
					if (component != null) {
						// TODO attach all members of this component
						componentsToImport.add(component);
					}
				}
				tx.add(componentsToImport);
				// TODO consider moving preCommit into commit(userId, commitComment)
				tx.preCommit();
				tx.commit("info@b2international.com", "Imported components from " + effectiveTime, DatastoreLockContextDescriptions.ROOT);
			}
		}
		System.err.println("Imported components from " + effectiveTime + " in " + w);
	}

}