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
package com.b2international.snowowl.snomed.exporter.server;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Set;
import java.util.function.Consumer;

import com.b2international.commons.BooleanUtils;
import com.b2international.commons.CompareUtils;
import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.common.ContentSubType;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.exporter.server.rf2.SnomedExporter;

/**
 * Base exporter for SNOMED CT concepts, descriptions and relationships.
 * Export is executed based on the requested branchpath where only artefacts visible
 * from the branchpath are exported.
 */
public abstract class AbstractSnomedCoreExporter<T extends SnomedDocument> implements SnomedExporter {

	// scroll page size for the query
	protected static final int PAGE_SIZE = 10000;
	
	private final SnomedExportContext exportContext;
	private final RevisionSearcher searcher;
	private final Class<T> clazz;

	protected AbstractSnomedCoreExporter(final SnomedExportContext exportContext, final Class<T> clazz, final RevisionSearcher revisionSearcher) {
		this.clazz = clazz;
		this.exportContext = checkNotNull(exportContext, "exportContext");
		this.searcher = checkNotNull(revisionSearcher, "revisionSearcher");
	}
	
	protected final RevisionSearcher getSearcher() {
		return searcher;
	}
	
	/**
	 * Transforms the SNOMED CT document index representation argument into a serialized line of attributes as specified in the RF1 or RF2 format.
	 * 
	 * @param snomedDocument
	 *            SNOMED CT document to transform
	 * @return a string as a serialized line in the export file
	 */
	public abstract String convertToString(final T snomedDocument);

	@Override
	public final SnomedExportContext getExportContext() {
		return exportContext;
	}
	
	@Override
	public final void writeLines(Consumer<String> lineProcessor) throws IOException {
		final Query<T> exportQuery = Query.select(clazz).where(getQueryExpression()).scroll().limit(PAGE_SIZE).build();
		for (Hits<T> hits : searcher.scroll(exportQuery)) {
			for (T hit : filter(hits)) {
				lineProcessor.accept(convertToString(hit));
			}
		}		
	}
	
	protected Hits<T> filter(Hits<T> hits) throws IOException {
		return hits;
	}

	protected void appendExpressionConstraint(ExpressionBuilder builder) {
		//do nothing
	}
	
	protected final String formatEffectiveTime(final Long effectiveTime) {
		return EffectiveTimes.format(effectiveTime, DateFormats.SHORT, exportContext.getUnsetEffectiveTimeLabel());
	}
	
	protected final String formatStatus(boolean isActive) {
		return BooleanUtils.toString(isActive);
	}

	private final Expression getQueryExpression() {
		
		ExpressionBuilder builder = Expressions.builder();
		
		// effective time constraint
		appendEffectiveTimeConstraint(builder);
		
		// module constraint
		Set<String> modulesToExport = exportContext.getModulesToExport();
		if (!CompareUtils.isEmpty(modulesToExport)) {
			builder.filter(SnomedDocument.Expressions.modules(modulesToExport));
		}
		
		// add whatever else subclasses would add
		appendExpressionConstraint(builder);
		
		return builder.build();
	}

	private void appendEffectiveTimeConstraint(ExpressionBuilder builder) {
		
		if (exportContext.isUnpublishedExport()) {
			
			builder.filter(SnomedDocument.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME));
			
		} else { // DELTA and SNAPSHOT might have effective time constraints, FULL will programmatically have those
			
			long startTime = exportContext.getStartEffectiveTime() == null ? 0L : exportContext.getStartEffectiveTime().getTime();
			long endTime = exportContext.getEndEffectiveTime() == null ? Long.MAX_VALUE : exportContext.getEndEffectiveTime().getTime();
			
			Expression expression = null;
			
			if (exportContext.getContentSubType() == ContentSubType.FULL) {
				expression = SnomedDocument.Expressions.effectiveTime(startTime, endTime, false, true);
			} else {
				expression = SnomedDocument.Expressions.effectiveTime(startTime, endTime);
			}
			
			if (expression != null) {
				builder.filter(expression);
			}
			
		}
	}

}
