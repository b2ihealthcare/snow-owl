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
package com.b2international.snowowl.datastore.server.snomed.escg;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import com.b2international.collections.longs.LongCollection;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.RevisionIndexRead;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.escg.ConceptIdQueryEvaluator2;
import com.b2international.snowowl.snomed.datastore.escg.EscgRewriter;
import com.b2international.snowowl.snomed.datastore.escg.IEscgQueryEvaluatorService;

public class EscgQueryEvaluatorService implements IEscgQueryEvaluatorService {

	private final EscgRewriter rewriter;

	public EscgQueryEvaluatorService(EscgRewriter rewriter) {
		this.rewriter = rewriter;
	}
	
	@Override
	public LongCollection evaluateConceptIds(final IBranchPath branchPath, final String queryExpression) {
		checkNotNull(queryExpression, "ESCG query expression wrapper argument cannot be null.");
		return getIndex().read(branchPath.getPath(), new RevisionIndexRead<LongCollection>() {
					@Override
					public LongCollection execute(RevisionSearcher index) throws IOException {
						final ConceptIdQueryEvaluator2 delegate = new ConceptIdQueryEvaluator2(index);
						return delegate.evaluate(rewriter.parseRewrite(queryExpression));
					}
				});
	}

	private RevisionIndex getIndex() {
		return ApplicationContext.getInstance().getService(RepositoryManager.class)
				.get(SnomedDatastoreActivator.REPOSITORY_UUID)
				.service(RevisionIndex.class);
	}
}