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
package com.b2international.snowowl.snomed.exporter.server.exporter;

import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.FULLY_DEFINED;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.PRIMITIVE;
import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.exporter.server.ComponentExportType;
import com.b2international.snowowl.snomed.exporter.server.SnomedExportContext;

/**
 * RF2 exporter for SNOMED&nbsp;CT concepts.
 *
 */
public class SnomedConceptExporter extends SnomedCoreExporter<SnomedConceptDocument> {

	public SnomedConceptExporter(final SnomedExportContext configuration, final RevisionSearcher revisionSearcher, final boolean unpublished) {
		super(checkNotNull(configuration, "configuration"), SnomedConceptDocument.class, revisionSearcher, unpublished);
	}

	@Override
	public String transform(final SnomedConceptDocument doc) {
		final StringBuilder sb = new StringBuilder();
		sb.append(doc.getId());
		sb.append(HT);
		sb.append(formatEffectiveTime(doc.getEffectiveTime()));
		sb.append(HT);
		sb.append(doc.isActive() ? "1" : "0");
		sb.append(HT);
		sb.append(doc.getModuleId());
		sb.append(HT);
		sb.append(doc.isPrimitive() ? PRIMITIVE : FULLY_DEFINED);
		return sb.toString();
	}

	@Override
	public ComponentExportType getType() {
		return ComponentExportType.CONCEPT;
	}

	@Override
	public String[] getColumnHeaders() {
		return SnomedRf2Headers.CONCEPT_HEADER;
	}

}
