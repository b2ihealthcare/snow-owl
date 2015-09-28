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

import org.apache.lucene.search.BooleanClause.Occur;

import com.b2international.snowowl.snomed.exporter.server.ComponentExportType;

/**
 * RF2 exporter for all SNOMED CT relationships except stated ones.
 */
public class SnomedRelationshipExporter extends AbstractSnomedRelationshipExporter {

	public SnomedRelationshipExporter(final SnomedExportConfiguration configuration) {
		super(configuration, Occur.MUST_NOT);
	}
	
	@Override
	public ComponentExportType getType() {
		return ComponentExportType.RELATIONSHIP;
	}
}
