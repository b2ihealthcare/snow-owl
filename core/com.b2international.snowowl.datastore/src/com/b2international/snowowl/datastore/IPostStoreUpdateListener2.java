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
package com.b2international.snowowl.datastore;

import com.b2international.snowowl.datastore.cdo.ICDOManagedItem;

/**
 * Extended {@link IPostStoreUpdateListener post store update listener} with an additional
 * {@link #getRepositoryUuid() repository UUID} information for identifying the repository the
 * current listener listening for changes.
 * @see IPostStoreUpdateListener
 */
public interface IPostStoreUpdateListener2 extends IPostStoreUpdateListener {

	/**
	 * Returns with the unique ID of the repository what the current listeners listens for. 
	 * @return the repository UUID.
	 * @see ICDOManagedItem#getUuid()
	 */
	String getRepositoryUuid();
	
}