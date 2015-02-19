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

import java.io.Serializable;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser;
import com.b2international.snowowl.core.api.browser.IClientTerminologyBrowserFactory;
import com.b2international.snowowl.core.api.browser.ITerminologyBrowser;

public class AbstractClientTerminologyBrowserFactory<K extends Serializable, 
		C extends IComponent<K>, 
		T extends IClientTerminologyBrowser<C, K>> implements IClientTerminologyBrowserFactory<K, C, T> {

	private final Class<T> clientTerminologyBrowserClass;
	
	protected AbstractClientTerminologyBrowserFactory(final Class<T> clientTerminologyBrowserClass) {
		this.clientTerminologyBrowserClass = clientTerminologyBrowserClass;
	}
	
	@Override
	public T getTerminologyBrowser() {
		return ApplicationContext.getInstance().getService(clientTerminologyBrowserClass);
	}
	
	public IClientTerminologyBrowser<C, K> getTerminologyBrowser(IBranchPath branchPath) {
		ITerminologyBrowser<C, K> wrappedBrowser = ((AbstractClientTerminologyBrowser<C, K>)getTerminologyBrowser()).getWrappedBrowser();
		return new BranchSpecificClientTerminologyBrowser<C, K>(wrappedBrowser, branchPath);
	}
}