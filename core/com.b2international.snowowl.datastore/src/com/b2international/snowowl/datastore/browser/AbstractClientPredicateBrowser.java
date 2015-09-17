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

import java.util.Collection;

import com.b2international.snowowl.core.api.browser.IClientPredicateBrowser;
import com.b2international.snowowl.core.api.browser.IPredicateBrowser;
import com.b2international.snowowl.datastore.ActiveBranchPathAwareService;
import com.google.common.base.Preconditions;

/**
 * Abstract implementation of {@link IClientPredicateBrowser} that delegates to a branch-aware {@link IPredicateBrowser}.
 */
public abstract class AbstractClientPredicateBrowser<P> extends ActiveBranchPathAwareService implements IClientPredicateBrowser<P> {

	private final IPredicateBrowser<P> delegateBrowser;

	public AbstractClientPredicateBrowser(final IPredicateBrowser<P> delegateBrowser) {
		this.delegateBrowser = Preconditions.checkNotNull(delegateBrowser, "Predicate browser argument cannot be null.");
	}

	@Override
	public Collection<P> getPredicate(final long... storageKeys) {
		return delegateBrowser.getPredicate(getBranchPath(), storageKeys);
	}

	@Override
	public Collection<P> getAllPredicates() {
		return delegateBrowser.getAllPredicates(getBranchPath());
	}

	@Override
	public Collection<P> getPredicates(String conceptId, String ruleRefSetId) {
		return delegateBrowser.getPredicates(getBranchPath(), conceptId, ruleRefSetId);
	}
	
	@Override
	public Collection<P> getPredicates(Iterable<String> ruleParentIds, String ruleRefSetId) {
		return delegateBrowser.getPredicates(getBranchPath(), ruleParentIds, ruleRefSetId);
	}

	@Override
	public String getDataTypePredicateLabel(final String dataTypeName) {
		return delegateBrowser.getDataTypePredicateLabel(getBranchPath(), dataTypeName);
	}

	@Override
	public String getDataTypePredicateLabel(final String dataTypeName, final String conceptId) {
		return delegateBrowser.getDataTypePredicateLabel(getBranchPath(), dataTypeName, conceptId);
	}
}