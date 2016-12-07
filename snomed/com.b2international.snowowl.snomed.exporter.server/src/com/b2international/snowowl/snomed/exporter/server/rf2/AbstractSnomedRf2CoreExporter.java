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
package com.b2international.snowowl.snomed.exporter.server.rf2;

import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.exporter.server.AbstractSnomedCoreExporter;
import com.b2international.snowowl.snomed.exporter.server.SnomedExportContext;
import com.b2international.snowowl.snomed.exporter.server.SnomedRfFileNameBuilder;

/**
 *
 */
public abstract class AbstractSnomedRf2CoreExporter<T extends SnomedDocument> extends AbstractSnomedCoreExporter<T> {

	/**
	 * @param exportContext
	 * @param clazz
	 * @param revisionSearcher
	 * @param unpublished
	 */
	protected AbstractSnomedRf2CoreExporter(SnomedExportContext exportContext, Class<T> clazz, RevisionSearcher revisionSearcher) {
		super(exportContext, clazz, revisionSearcher);
	}

	@Override
	public String getRelativeDirectory() {
		return RF2_CORE_RELATIVE_DIRECTORY;
	}
	
	@Override
	public String getFileName() {
		return SnomedRfFileNameBuilder.buildCoreRf2FileName(getType(), getExportContext());
	}

}
