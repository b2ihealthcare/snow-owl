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
package com.b2international.snowowl.core.api.browser;

import java.io.Serializable;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponent;

/**
 *
 */
public interface IClientTerminologyBrowserFactory<K extends Serializable, C extends IComponent<K>, T extends IClientTerminologyBrowser<C, K>> {

	/**
	 * Returns a terminology browser for the active branch.
	 * 
	 * @return the terminology browser for the active branch
	 */
	T getTerminologyBrowser();
	
	/**
	 * Returns a branch specific {@link IClientTerminologyBrowser client terminology browser}.
	 * 
	 * @param branchPath the branch
	 * @return the branch specific client terminology browser
	 */
	IClientTerminologyBrowser<C, K> getTerminologyBrowser(IBranchPath branchPath);
}