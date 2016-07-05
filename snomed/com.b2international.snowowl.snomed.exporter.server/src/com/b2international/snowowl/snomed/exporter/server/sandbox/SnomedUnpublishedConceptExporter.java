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

import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query.QueryBuilder;
import com.b2international.index.revision.Revision;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;

/**
 * Exporter for unpublished concepts
 */
public class SnomedUnpublishedConceptExporter extends SnomedConceptExporter {

	/**
	 * @param configuration
	 */
	public SnomedUnpublishedConceptExporter(SnomedExportContext configuration) {
		super(configuration);
	}
	
	/**
	 * Returns the query expression for the snapshot export
	 * @return
	 */
	protected Query<SnomedConceptDocument> getSnapshotQuery() {
		return null;
	}
	
	/**
	 * Returns the query expression for the delta export
	 * @return
	 */
	protected Query<SnomedConceptDocument> getDeltaQuery() {
		return null;
	}
	
	/**
	 * @returns the query for the full export
	 */
	protected Query<SnomedConceptDocument> getFullQuery() {
		QueryBuilder<SnomedConceptDocument> builder = Query.select(SnomedConceptDocument.class);
		ExpressionBuilder commitTimeConditionBuilder = Expressions.builder();
		
		//Select * from table where commitTimes in(,,,)
		Expression commitExpression = Expressions.matchAll();
		
		Expression unpublishedExpression = Expressions.builder()
				.must(Expressions.exactMatch(Revision.BRANCH_PATH, getExportContext().getCurrentBranchPath().getPath()))
				.must(SnomedDocument.Expressions.unreleased()).build();
		commitExpression = Expressions.builder().should(commitExpression).should(unpublishedExpression).build();
			
		commitTimeConditionBuilder.must(commitExpression);
		Query<SnomedConceptDocument> query = builder.where(commitTimeConditionBuilder.build()).limit(getPageSize()).offset(getCurrentOffset()).build();
		return query;
	}

}
