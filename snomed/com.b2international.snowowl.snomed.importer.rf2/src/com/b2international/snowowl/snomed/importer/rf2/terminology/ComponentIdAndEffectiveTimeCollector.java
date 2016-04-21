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
package com.b2international.snowowl.snomed.importer.rf2.terminology;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;

import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.NumericDocValues;

import com.b2international.snowowl.datastore.index.AbstractDocsOutOfOrderCollector;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;

import bak.pcj.map.ObjectKeyLongMap;
import bak.pcj.map.ObjectKeyLongOpenHashMap;

/**
 * @since 4.7
 */
public final class ComponentIdAndEffectiveTimeCollector extends AbstractDocsOutOfOrderCollector {

	private final ObjectKeyLongMap availableComponents;
	
	private NumericDocValues componentIdValues;
	private NumericDocValues effectiveTimeValues;

	public ComponentIdAndEffectiveTimeCollector(int expectedSize) {
		checkArgument(expectedSize > 0, "Expected size must be greater than zero");
		this.availableComponents = new ObjectKeyLongOpenHashMap(expectedSize);
	}
	
	@Override
	public void collect(int doc) throws IOException {
		final String id = Long.toString(componentIdValues.get(doc));
		availableComponents.put(id, effectiveTimeValues.get(doc));
	}

	@Override
	protected boolean isLeafCollectible() {
		return componentIdValues != null && effectiveTimeValues != null;
	}

	@Override
	protected void initDocValues(AtomicReader leafReader) throws IOException {
		componentIdValues = SnomedMappings.id().getDocValues(leafReader);
		effectiveTimeValues = SnomedMappings.effectiveTime().getDocValues(leafReader);
	}
	
	public ObjectKeyLongMap getAvailableComponents() {
		return availableComponents;
	}
	
}
