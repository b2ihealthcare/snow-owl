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
package com.b2international.snowowl.datastore.server.events;

import com.b2international.snowowl.core.Metadata;
import com.b2international.snowowl.datastore.server.branch.Branch;


/**
 * @since 4.1
 */
public class CreateBranchEvent extends BranchEvent {

	private final String parent;
	private final String name;
	private final Metadata metadata;

	public CreateBranchEvent(final String repositoryId, final String parent, final String name, final Metadata metadata) {
		super(repositoryId, path(parent, name));
		this.parent = parent;
		this.name = name;
		this.metadata = metadata;
	}

	private static String path(final String parent, final String name) {
		return parent + Branch.SEPARATOR + name;
	}

	public String getParent() {
		return parent;
	}

	public String getName() {
		return name;
	}

	public Metadata getMetadata() {
		return metadata;
	}
}
