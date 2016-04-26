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
package com.b2international.snowowl.datastore.server.snomed.index;

import java.io.IOException;

import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.NumericDocValues;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.longs.LongKeyLongMap;
import com.b2international.snowowl.datastore.index.AbstractDocsOutOfOrderCollector;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;

/**
 * @since SO 4.4.1
 */
public class ComponentModuleCollector extends AbstractDocsOutOfOrderCollector {
	
	private NumericDocValues componentId;
	private NumericDocValues moduleId;
	private LongKeyLongMap idToModuleMap;

	public ComponentModuleCollector() {
		idToModuleMap = PrimitiveMaps.newLongKeyLongOpenHashMap();
	}
	
	@Override
	protected void initDocValues(AtomicReader leafReader) throws IOException {
		componentId = SnomedMappings.id().getDocValues(leafReader);
		moduleId = SnomedMappings.module().getDocValues(leafReader);
	}

	@Override
	protected boolean isLeafCollectible() {
		return componentId != null && moduleId != null;
	}

	@Override
	public void collect(int doc) throws IOException {
		idToModuleMap.put(componentId.get(doc), moduleId.get(doc));
	}

	public LongKeyLongMap getIdToModuleMap() {
		return idToModuleMap;
	}
}
