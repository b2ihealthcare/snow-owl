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
import java.util.Iterator;
import java.util.NoSuchElementException;
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
	
	private int totalSize = -1;
	private int currentOffset;
	private int currentIndex;
	private Hits<T> conceptHits;
	private Class<T> clazz;
	private SnomedExportContext exportContext;
	private RevisionSearcher revisionSearcher;
	private boolean onlyUnpublished;

	protected AbstractSnomedCoreExporter(final SnomedExportContext exportContext, final Class<T> clazz, final RevisionSearcher revisionSearcher, 
			final boolean onlyUnpublished) {
		this.exportContext = checkNotNull(exportContext, "exportContext");
		this.clazz = checkNotNull(clazz, "clazz");
		this.revisionSearcher = checkNotNull(revisionSearcher, "revisionSearcher");
		this.onlyUnpublished = onlyUnpublished;
	}
	
	/**
	 * Transforms the SNOMED CT document index representation argument into a serialized line of 
	 * attributes as specified in the RF1 or RF2 format.
	 * @param the SNOMED CT document to transform.
	 * @return a string as a serialized line in the export file.
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
		
		if (totalSize == -1 || (currentIndex >= conceptHits.getHits().size()) && currentOffset < totalSize ) {
			try {
				//traverse back from the current branchpath and find all the concepts that have the commit times from the versions visible from the branch path
				final Query<T> exportQuery = Query.select(clazz).where(getQueryExpression()).offset(currentOffset).limit(PAGE_SIZE).build();
				conceptHits = revisionSearcher.search(exportQuery);
				
				//to avoid getting the size every time
				if (totalSize == -1) {
					totalSize = conceptHits.getTotal();
				}
				currentIndex = 0;
				currentOffset += conceptHits.getHits().size();
			} catch (IOException e) {
				throw new SnowowlRuntimeException(e);
			}
		}
		return conceptHits != null && conceptHits.getHits().size() > 0 && currentIndex < conceptHits.getHits().size();
	}
	
	@Override
	public String next() {
		
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		
		T revisionIndexEntry = conceptHits.getHits().get(currentIndex++);
		return convertToString(revisionIndexEntry);
	}
	
	@Override
	public SnomedExportContext getExportContext() {
		return exportContext;
	}

	/**
	 * Returns the revision searcher used by this exporter.
	 * @return
	 */
	public RevisionSearcher getRevisionSearcher() {
		return revisionSearcher;
	}

	@Override
	public void close() throws Exception {
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
		
		if (onlyUnpublished) {
			
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
