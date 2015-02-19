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
package com.b2international.snowowl.datastore.server.index;

import org.apache.lucene.store.Directory;

/**
 * Fired when a directory is being created on the very first time.
 *
 */
public interface IndexDirectoryFirstStartupCallback {

	/**
	 * Fires when the {@link Directory} is first initialized on the MAIN branch
	 * for the {@link IndexBranchService} argument.
	 * @param service the service which is initialized first time.
	 */
	void fireFirstStartup(IndexBranchService service);
	
}