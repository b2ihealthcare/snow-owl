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

import java.io.IOException;
import java.util.Collection;
import java.util.NoSuchElementException;

import org.eclipse.emf.common.util.EList;

import com.b2international.index.Hits;
import com.b2international.index.Searcher;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.query.Query.QueryBuilder;
import com.b2international.index.revision.Revision;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.CodeSystemService;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.exporter.server.SnomedRf2Exporter;
import com.b2international.snowowl.terminologymetadata.CodeSystemVersion;
import com.b2international.snowowl.terminologymetadata.CodeSystemVersionGroup;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;

/**
 * Base exporter for SNOMED CT concepts, descriptions and relationships.
 * Export is executed based on the requested branchpath where only artefacts visible
 * from the branchpath are exported.
 */
public abstract class SnomedCoreExporter<T extends SnomedDocument> extends SnomedCompositeExporter<T> implements SnomedRf2Exporter {

	//never been queried
	private int totalSize = -1;
	
	private int currentOffset;
	private int currentIndex;
	private Hits<T> conceptHits;
	private Class<T> clazz;

	private SnomedEditingContext editingContext;

	private final static int PAGE_SIZE = 100;

	private Collection<Long> commitTimes;
	
	protected SnomedCoreExporter(final SnomedExportConfiguration configuration, final Class<T> clazz) {
		super(checkNotNull(configuration, "configuration"));
		this.clazz = checkNotNull(clazz, "clazz");
		IBranchPath currentBranchPath = getConfiguration().getCurrentBranchPath();
		ApplicationContext.getServiceForClass(CodeSystemService.class).getAllTagsWithHead(SnomedDatastoreActivator.REPOSITORY_UUID);
		editingContext = new SnomedEditingContext(currentBranchPath);
		CodeSystemVersionGroup codeSystemVersionGroup = editingContext.getCodeSystemVersionGroup();
		EList<CodeSystemVersion> codeSystemVersions = codeSystemVersionGroup.getCodeSystemVersions();
		commitTimes = Collections2.transform(codeSystemVersions, new Function<CodeSystemVersion, Long>() {

			@Override
			public Long apply(CodeSystemVersion codeSystemVersion) {
				return codeSystemVersion.getLastUpdateDate().getTime();
			}
		});
	}
	
	@Override
	public boolean hasNext() {
		
		if (totalSize == -1 || (currentIndex >= conceptHits.getHits().size()) && currentOffset < totalSize ) {
			try {
				
				//traverse back from the current branchpath and find all the concepts that have the commit times from the versions visible from the branch path
				Searcher searcher = getConfiguration().getSearcher();
				
				QueryBuilder<T> builder = Query.builder(clazz);
				ExpressionBuilder commitTimeConditionBuilder = Expressions.builder();
				
				//Select * from table where commitTimes in(,,,)
				Expression commitExpression = Expressions.matchAnyLong(Revision.COMMIT_TIMESTAMP, commitTimes);
				
				//conditionally add unpublished concepts as well (from everywhere? or the last branch?)
				if (getConfiguration().includeUnpublished()) {
					Expression unpublishedExpression = Expressions.builder()
							.must(Expressions.exactMatch(Revision.BRANCH_PATH, getConfiguration().getCurrentBranchPath().getPath()))
							.must(SnomedDocument.Expressions.unreleased()).build();
					commitExpression = Expressions.or(commitExpression, unpublishedExpression);
					
				}
				commitTimeConditionBuilder.must(commitExpression);
				Query<T> query = builder.selectAll().where(commitTimeConditionBuilder.build()).limit(PAGE_SIZE).offset(currentOffset).build();
				//here are the results to export
				conceptHits = searcher.search(query);
				
				//to avoid getting the size every time
				if (totalSize == -1) {
					totalSize = conceptHits.getTotal();
				}
				currentIndex = 0;
				currentOffset += conceptHits.getHits().size();
			} catch (IOException e) {
				e.printStackTrace();
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
		return transform(revisionIndexEntry);
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedCompositeExporter#close()
	 */
	@Override
	public void close() throws Exception {
		editingContext.close();
		super.close();
	}
}
