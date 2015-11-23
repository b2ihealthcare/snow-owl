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
package com.b2international.snowowl.datastore.request;

import java.io.IOException;

import org.apache.lucene.search.IndexSearcher;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.DelegatingBranchContext;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.index.IndexRead;
import com.b2international.snowowl.datastore.index.IndexTransactionProvider;

/**
 * A subclass of {@link DelegatingRequest} that:
 * <ul>
 * <li>opens an index read transaction using {@link IndexTransactionProvider};
 * <li>executes the delegate with a {@link BranchContext} that allows access to an {@link IndexSearcher} from the read
 * transaction.
 * </ul>
 * 
 * @since 4.5
 */
public final class IndexReadRequest<B> extends DelegatingRequest<BranchContext, BranchContext, B> {

	IndexReadRequest(Request<BranchContext, B> next) {
		super(next);
	}
	
	@Override
	public B execute(final BranchContext context) {
		final IBranchPath branchPath = context.branch().branchPath();
		return context.service(IndexTransactionProvider.class).executeReadTransaction(branchPath, new IndexRead<B>() {
			@Override
			public B execute(IndexSearcher index) throws IOException {
				return wrapAndExecute(context, index);
			}
		});
	}
	
	private B wrapAndExecute(final BranchContext context, final IndexSearcher index) {
		final BranchContext decoratedContext = new DelegatingBranchContext(context) {
			@Override
			public <T> T service(Class<T> type) {
				if (type.isAssignableFrom(IndexSearcher.class)) {
					return type.cast(index);
				} else {
					return super.service(type);
				}
			}
		};

		return next(decoratedContext);
	}
}
