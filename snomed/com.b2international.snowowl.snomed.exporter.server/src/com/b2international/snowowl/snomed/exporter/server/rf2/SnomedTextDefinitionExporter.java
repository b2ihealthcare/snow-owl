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

import java.io.IOException;

import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.exporter.server.ComponentExportType;
import com.b2international.snowowl.snomed.exporter.server.SnomedExportContext;

/**
 *
 */
public class SnomedTextDefinitionExporter extends SnomedRf2DescriptionExporter {

	public SnomedTextDefinitionExporter(final SnomedExportContext exportContext, final RevisionSearcher revisionSearcher, final String languageCode) {
		super(exportContext, revisionSearcher, languageCode);
	}
	
	@Override
	public ComponentExportType getType() {
		return ComponentExportType.TEXT_DEFINITION;
	}
	
	@Override
	protected void appendExpressionConstraint(final ExpressionBuilder builder) {
		builder
			.filter(SnomedDescriptionIndexEntry.Expressions.type(Concepts.TEXT_DEFINITION))
			.filter(SnomedDescriptionIndexEntry.Expressions.languageCode(getLanguageCode()));
	}
	
	@Override
	protected void executeAdditionalExporters() throws IOException {/* Overriding with empty implementation, to avoid language members to be written twice to lang refset file*/}

}
