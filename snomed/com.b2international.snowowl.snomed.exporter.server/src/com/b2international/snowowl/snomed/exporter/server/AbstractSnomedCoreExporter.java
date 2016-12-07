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
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import com.b2international.commons.BooleanUtils;
import com.b2international.commons.CompareUtils;
import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
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
	private static final int PAGE_SIZE = 100000;
	
	private int currentIndex;
	private int currentOffset;
	private Hits<T> hits;
	
	private Class<T> clazz;
	private SnomedExportContext exportContext;
	private RevisionSearcher revisionSearcher;

	protected AbstractSnomedCoreExporter(final SnomedExportContext exportContext, final Class<T> clazz, final RevisionSearcher revisionSearcher) {
		this.exportContext = checkNotNull(exportContext, "exportContext");
		this.clazz = checkNotNull(clazz, "clazz");
		this.revisionSearcher = checkNotNull(revisionSearcher, "revisionSearcher");
		
		this.hits = new Hits<T>(Collections.<T>emptyList(), 0, 0, -1);
		this.currentIndex = 0;
		this.currentOffset = 0;
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
	public Iterator<String> iterator() {
		return this;
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean hasNext() {
		
		if (currentIndex == hits.getHits().size() && currentOffset != hits.getTotal()) {
			
			try {
				
				final Query<T> exportQuery = Query.select(clazz).where(getQueryExpression()).offset(currentOffset).limit(PAGE_SIZE).build();
				hits = revisionSearcher.search(exportQuery);
				
				currentIndex = 0;
				currentOffset += hits.getHits().size();
				
			} catch (IOException e) {
				throw new SnowowlRuntimeException(e);
			}
			
		}
		
		return hits.getHits().size() > 0 && currentIndex < hits.getHits().size();
	}
	
	@Override
	public String next() {
		return convertToString(hits.getHits().get(currentIndex++));
	}
	
	@Override
	public SnomedExportContext getExportContext() {
		return exportContext;
	}

	public RevisionSearcher getRevisionSearcher() {
		return revisionSearcher;
	}

	@Override
	public void execute() throws IOException {
		new SnomedExportExecutor(this).execute();
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

	private Expression getQueryExpression() {
		
		ExpressionBuilder builder = Expressions.builder();
		
		// effective time constraint
		appendEffectiveTimeConstraint(builder);
		
		// module constraint
		Set<String> modulesToExport = exportContext.getModulesToExport();
		if (!CompareUtils.isEmpty(modulesToExport)) {
			builder.must(SnomedDocument.Expressions.modules(modulesToExport));
		}
		
		// add whatever else subclasses would add
		appendExpressionConstraint(builder);
		
		return builder.build();
	}

	private void appendEffectiveTimeConstraint(ExpressionBuilder builder) {
		
		if (exportContext.isUnpublishedExport()) {
			
			builder.must(SnomedDocument.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME));
			
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
				builder.must(expression);
			}
			
		}
	}

}
