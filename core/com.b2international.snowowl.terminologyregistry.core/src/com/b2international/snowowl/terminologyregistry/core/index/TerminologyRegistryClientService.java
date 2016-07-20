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
package com.b2international.snowowl.terminologyregistry.core.index;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;

import java.util.Collection;

import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.ICodeSystem;
import com.b2international.snowowl.datastore.ICodeSystemVersion;
import com.b2international.snowowl.datastore.TerminologyRegistryService;
import com.b2international.snowowl.datastore.tasks.TaskManager;

/**
 * A terminology metadata service implementation for clients. 
 */
public final class TerminologyRegistryClientService {

	private final TerminologyRegistryService delegate;

	public TerminologyRegistryClientService(final TerminologyRegistryService wrappedService) {
		delegate = wrappedService;
	}


	public Collection<ICodeSystem> getCodeSystems() {
		return delegate.getCodeSystems(getBranchPathMap());
	}

	public Collection<ICodeSystemVersion> getCodeSystemVersions(final String codeSystemShortName) {
		return delegate.getCodeSystemVersions(getBranchPathMap(), codeSystemShortName);
	}

	public ICodeSystem getCodeSystemByShortName(final String codeSystemShortName) {
		return delegate.getCodeSystemByShortName(getBranchPathMap(), codeSystemShortName);
	}

	public ICodeSystem getCodeSystemByOid(final String codeSystemOID) {
		return delegate.getCodeSystemByOid(getBranchPathMap(), codeSystemOID);
	}

	public String getTerminologyComponentIdByShortName(final String codeSystemShortName) {
		return delegate.getTerminologyComponentIdByShortName(getBranchPathMap(), codeSystemShortName);
	}

	public String getVersionId(final ICodeSystem codeSystem) {
		return delegate.getVersionId(getBranchPathMap(), codeSystem);
	}

	private IBranchPathMap getBranchPathMap() {
		return getServiceForClass(TaskManager.class).getBranchPathMap();
	}

}