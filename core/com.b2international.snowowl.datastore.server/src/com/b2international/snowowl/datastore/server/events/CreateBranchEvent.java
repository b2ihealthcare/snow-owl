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

	private String repository;
	private String parent;
	private String name;
	private Metadata metadata;
	
	public CreateBranchEvent(String repository, String parent, String name, Metadata metadata) {
		super(path(parent, name));
		this.repository = repository;
		this.parent = parent;
		this.name = name;
		this.metadata = metadata;
	}
	
	private static String path(String parent, String name) {
		return parent + Branch.SEPARATOR + name;
	}

	public String getParent() {
		return parent;
	}
	
	public String getName() {
		return name;
	}
	
	public String getRepository() {
		return repository;
	}
	
	public Metadata getMetadata() {
		return metadata;
	}
	
}
