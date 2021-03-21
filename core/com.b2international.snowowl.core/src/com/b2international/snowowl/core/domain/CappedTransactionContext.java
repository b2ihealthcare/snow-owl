/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.domain;

import com.b2international.index.revision.Revision;
import com.b2international.index.revision.StagingArea;

/**
 * @since 7.16
 */
public final class CappedTransactionContext extends DelegatingTransactionContext {

	// 0 or less disables this feature, 1 or more allows partial commits for any transaction
	private final int commitThreshold;

	public CappedTransactionContext(TransactionContext context, int commitThreshold) {
		super(context);
		this.commitThreshold = commitThreshold;
	}

	@Override
	public String add(Object obj) {
		String id = super.add(obj);
		commitIfAboveThreshold();
		return id;
	}
	
	@Override
	public void update(Revision oldVersion, Revision newVersion) {
		// apply update first
		super.update(oldVersion, newVersion);
		commitIfAboveThreshold();
	}
	
	@Override
	public void delete(Object obj) {
		super.delete(obj);
		commitIfAboveThreshold();
	}
	
	@Override
	public void delete(Object obj, boolean force) {
		super.delete(obj, force);
		commitIfAboveThreshold();
	}
	
	@Override
	public void clearContents() {
		throw new UnsupportedOperationException("clearContents is not supported in capped transaction scenarios");
	}
	
	@Override
	public void close() throws Exception {
		// if there is anything left in the staging area on close, then commit it before close (dirty check already included in commit implementation)
		commit();
		super.close();
	}
	
	private void commitIfAboveThreshold() {
		// check if staged objects collection reaches the threshold and commit if it does
		if (commitThreshold > 0 && service(StagingArea.class).getNumberOfStagedObjects() >= commitThreshold) {
			commit();
		}
	}
	
}
