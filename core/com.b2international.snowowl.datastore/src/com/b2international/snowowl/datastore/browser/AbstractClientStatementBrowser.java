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
package com.b2international.snowowl.datastore.browser;

import java.util.List;

import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.IStatement;
import com.b2international.snowowl.core.api.browser.IClientStatementBrowser;
import com.b2international.snowowl.core.api.browser.IStatementBrowser;
import com.b2international.snowowl.datastore.BranchPathAwareService;
import com.google.common.base.Preconditions;

/**
 * Abstract implementation of {@link IClientStatementBrowser} that delegates to a branch-aware {@link IStatementBrowser}.
 */
public abstract class AbstractClientStatementBrowser<C extends IComponent<K>, S extends IStatement<K>, K> implements IClientStatementBrowser<C, S, K>, BranchPathAwareService {

	private final IStatementBrowser<C, S, K> delegateBrowser;

	public AbstractClientStatementBrowser(final IStatementBrowser<C, S, K> delegateBrowser) {
		this.delegateBrowser = Preconditions.checkNotNull(delegateBrowser, "Statement browser argument cannot be null.");
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientStatementBrowser#getInboundStatements(java.lang.Object)
	 */
	@Override
	public List<S> getInboundStatements(final C concept) {
		return delegateBrowser.getInboundStatements(getBranchPath(), concept);
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientStatementBrowser#getOutboundStatements(java.lang.Object)
	 */
	@Override
	public List<S> getOutboundStatements(final C concept) {
		return delegateBrowser.getOutboundStatements(getBranchPath(), concept);
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientStatementBrowser#getStatements(java.lang.Object)
	 */
	@Override
	public List<S> getStatements(final C concept) {
		return delegateBrowser.getStatements(getBranchPath(), concept);
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientStatementBrowser#getAllStatements()
	 */
	@Override
	public List<S> getAllStatements() {
		return delegateBrowser.getAllStatements(getBranchPath());
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientStatementBrowser#getStatement(java.lang.Object)
	 */
	@Override
	public S getStatement(final K id) {
		return delegateBrowser.getStatement(getBranchPath(), id);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientStatementBrowser#getStatementsById(java.lang.Object)
	 */
	@Override
	public List<S> getStatementsById(final K conceptId) {
		return delegateBrowser.getStatementsById(getBranchPath(), conceptId);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientStatementBrowser#getInboundStatementsById(java.lang.Object)
	 */
	@Override
	public List<S> getInboundStatementsById(final K conceptId) {
		return delegateBrowser.getInboundStatementsById(getBranchPath(), conceptId);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientStatementBrowser#getOutboundStatementsById(java.lang.Object)
	 */
	@Override
	public List<S> getOutboundStatementsById(final K conceptId) {
		return delegateBrowser.getOutboundStatementsById(getBranchPath(), conceptId);
	}
	
	/**
	 * @return the delegateBrowser
	 */
	public IStatementBrowser<C, S, K> getDelegateBrowser() {
		return delegateBrowser;
	}
}