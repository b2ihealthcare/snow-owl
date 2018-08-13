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
package com.b2international.snowowl.datastore.net4j;

import javax.annotation.Nullable;

import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.BranchPathUtils;

/**
 * Abstract import indication responsible for deserializing a user ID and a {@link IBranchPath branch path}.
 *
 */
public abstract class AbstractUserAndBranchAwareImportIndication extends AbstractImportIndication {

	private String userId;
	private IBranchPath branchPath;

	protected AbstractUserAndBranchAwareImportIndication(final SignalProtocol<?> protocol, final short importSignal) {
		super(protocol, importSignal);
	}

	/* 
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.server.net4j.AbstractImportIndication#postFileIndicating(org.eclipse.net4j.util.io.ExtendedDataInputStream)
	 */
	@Override
	protected void postFileIndicating(final ExtendedDataInputStream in) throws Exception {
		
		userId = in.readUTF();
		
		final String branchPathString = in.readUTF();
		
		if (StringUtils.isEmpty(branchPathString)) {
			
			throw new SnowowlServiceException("Null or empty branch path is prohibited.");
			
		}
		
		try {
			
			branchPath = BranchPathUtils.createPath(branchPathString);
			
		} catch (final Throwable t) {
			
			throw new SnowowlServiceException("Failed to perform Value domain import, due to incorrect branch path.");
			
		}
		
	}
	
	/**
	 * Returns with the user ID. Could be {@code null}.
	 */
	@Nullable protected String getUserId() {
		return userId;
	}
	
	/**
	 * Returns with the branch path. Could be {@code null}.
	 */
	@Nullable protected IBranchPath getBranchPath() {
		return branchPath;
	}
}