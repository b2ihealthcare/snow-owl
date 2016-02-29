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
package com.b2international.snowowl.datastore.server.internal.merge;

import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.server.remotejobs.RemoteJobKey;

/**
 * @since 4.6
 */
public class BranchChangeRemoteJobKey extends RemoteJobKey {
	
	public BranchChangeRemoteJobKey(final String repositoryId, final String path) {
		super(repositoryId, BranchPathUtils.createPath(path));
	}
}
