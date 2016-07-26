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

import java.io.File;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.importer.TerminologyImportType;

/**
 * Abstract import request with additional user ID and {@link IBranchPath branch path} properties.
 *
 */
public abstract class AbstractUserAndBranchAwareImportRequest extends ImportRequest {

	private final IBranchPath branchPath;

	protected AbstractUserAndBranchAwareImportRequest(final SignalProtocol<?> protocol, final short importSignal, final IBranchPath branchPath, final File sourceDir, final TerminologyImportType importType) {
		super(protocol, importSignal, sourceDir, importType);
		this.branchPath = branchPath;
		
	}
	
	protected abstract EPackage getEPackage();

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.net4j.ImportRequest#postFileRequesting(org.eclipse.net4j.util.io.ExtendedDataOutputStream)
	 */
	@Override
	protected void postFileRequesting(final ExtendedDataOutputStream out) throws Exception {
		out.writeUTF(getUserId()); //user ID
		out.writeUTF(branchPath.getPath()); //branch path as string
	}

	/*returns with the user ID from the underlying session*/
	private String getUserId() {
		return getConnection().getUserId();
	}
	
	/*returns with the connection manager*/
	private ICDOConnectionManager getConnection() {
		return ApplicationContext.getInstance().getService(ICDOConnectionManager.class);
	}

}