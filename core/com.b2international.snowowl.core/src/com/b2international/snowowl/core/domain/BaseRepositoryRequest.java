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
package com.b2international.snowowl.core.domain;

import com.b2international.snowowl.core.events.BaseEvent;

/**
 * @since 4.5
 */
public abstract class BaseRepositoryRequest<B> extends BaseEvent implements RepositoryRequest<B> {

	private String codeSystemShortName;
	private String branchPath;

	@Override
	public String getCodeSystemShortName() {
		return codeSystemShortName;
	}

	@Override
	public String getBranchPath() {
		return branchPath;
	}

	public void setCodeSystemShortName(String codeSystemShortName) {
		this.codeSystemShortName = codeSystemShortName;
	}

	public void setBranchPath(String branchPath) {
		this.branchPath = branchPath;
	}
	
	@Override
	protected String getAddress() {
		throw new UnsupportedOperationException();
	}

}