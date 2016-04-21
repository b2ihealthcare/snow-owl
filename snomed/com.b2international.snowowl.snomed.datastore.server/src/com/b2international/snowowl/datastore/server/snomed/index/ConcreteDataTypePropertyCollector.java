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
package com.b2international.snowowl.datastore.server.snomed.index;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.NumericDocValues;

import com.b2international.collections.longs.LongCollection;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;

/**
 * Class for collecting module identifiers on concrete domains and their containers (concept or relationship).
 */
public class ConcreteDataTypePropertyCollector extends ComponentPropertyCollector {

	private NumericDocValues moduleIds;
	private NumericDocValues referencedComponentId;

	public ConcreteDataTypePropertyCollector(final LongCollection acceptedIds) {
		super(checkNotNull(acceptedIds, "acceptedIds"));
	}

	@Override
	protected void initDocValues(final AtomicReader leafReader) throws IOException {
		super.initDocValues(leafReader);
		moduleIds = SnomedMappings.module().getDocValues(leafReader);
		referencedComponentId = SnomedMappings.memberReferencedComponentId().getDocValues(leafReader);
	}

	@Override
	protected boolean isLeafCollectible() {
		return super.isLeafCollectible() 
				&& moduleIds != null 
				&& referencedComponentId != null;
	}

	@Override
	protected long[] collectProperties(final int docId) {
		return new long[] { 
				moduleIds.get(docId), 
				referencedComponentId.get(docId) 
		};
	}
}
