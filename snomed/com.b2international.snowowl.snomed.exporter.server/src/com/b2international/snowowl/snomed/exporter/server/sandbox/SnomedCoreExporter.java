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
package com.b2international.snowowl.snomed.exporter.server.sandbox;

import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.lucene.search.Query;

import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.exporter.server.SnomedRf2Exporter;

/**
 * Base exporter for SNOMED CT concepts, descriptions and relationships.
 */
public abstract class SnomedCoreExporter extends SnomedCompositeExporter implements SnomedRf2Exporter {

	protected SnomedCoreExporter(final SnomedExportConfiguration configuration) {
		super(checkNotNull(configuration, "configuration"));
	}

	protected abstract int getTerminologyComponentType();
	
	@Override
	protected Query getSnapshotQuery() {
		return SnomedMappings.newQuery().type(getTerminologyComponentType()).matchAll();
	}
}
