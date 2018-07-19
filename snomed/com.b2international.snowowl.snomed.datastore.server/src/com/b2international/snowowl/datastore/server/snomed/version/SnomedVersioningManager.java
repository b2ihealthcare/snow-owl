/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.snomed.version;

import com.b2international.snowowl.datastore.server.snomed.ISnomedVersioningManager;
import com.b2international.snowowl.datastore.version.IPublishManager;
import com.b2international.snowowl.datastore.version.VersioningManager;

/**
 * {@link ISnomedUnversionedComponentProvider} implementation.
 *
 */
public class SnomedVersioningManager extends VersioningManager implements ISnomedVersioningManager {
	
//	private SnomedPublishManager publishManager;

	@Override
	protected IPublishManager getPublishManager() {
		return null;
//		if (null == publishManager)
//			this.publishManager = new SnomedPublishManager();
//		return publishManager;
	}
	
	@Override
	public void postCommit() {
//		getPublishManager().postCommit();
	}
	
}