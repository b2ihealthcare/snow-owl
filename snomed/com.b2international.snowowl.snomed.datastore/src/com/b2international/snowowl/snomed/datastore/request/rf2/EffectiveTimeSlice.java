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

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

import java.util.Collection;
import java.util.Map;

import org.mapdb.DB;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

/**
 * @since 6.0
 */
final class EffectiveTimeSlice {

	private static final int BATCH_SIZE = 5000;

	private final Multimap<String, String> componentsByContainer;

	// tmp map to quickly collect batch of items before flushing it to disk
	private final Map<String, String[]> tmpComponentsById;
	private final HTreeMap<String, String[]> componentsById;

	public EffectiveTimeSlice(DB db, String effectiveTime) {
		this.componentsByContainer = HashMultimap.create();
		this.tmpComponentsById = newHashMapWithExpectedSize(BATCH_SIZE);
		this.componentsById = db.hashMap(effectiveTime, Serializer.STRING, Serializer.ELSA).create();
	}

	public Collection<SnomedComponent> getComponentsByContainer(String containerId) {
		final ImmutableList.Builder<SnomedComponent> components = ImmutableList.builder();
		for (String componentId : componentsByContainer.get(containerId)) {
			final String[] valuesWithType = componentsById.get(componentId);

			for (Rf2ContentType resolver : Rf2Format.getContentTypes()) {
				if (valuesWithType[0].equals(resolver.getType())) {
					String[] values = new String[valuesWithType.length - 1];
					System.arraycopy(valuesWithType, 1, values, 0, valuesWithType.length - 1);
					components.add(resolver.resolve(values));
					break;
				}
			}
		}
		return components.build();
	}

	public void register(String containerId, String type, String[] values) {
		String[] valuesWithType = new String[values.length + 1];
		valuesWithType[0] = type;
		System.arraycopy(values, 0, valuesWithType, 1, values.length);

		final String componentId = values[0];
		componentsByContainer.get(containerId).add(componentId);
		tmpComponentsById.put(componentId, valuesWithType);
		if (tmpComponentsById.size() >= BATCH_SIZE) {
			flush();
		}
	}

	public void flush() {
		if (!tmpComponentsById.isEmpty()) {
			componentsById.putAll(tmpComponentsById);
		}
		tmpComponentsById.clear();
	}

}