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
package com.b2international.snowowl.datastore.request;

/**
 * @since 4.5
 */
public final class RepositoryRequests {

	private RepositoryRequests() {}
	
	public static Branching branching() {
		return new Branching();
	}
	
	public static Merging merging() {
		return new Merging();
	}
	
	public static Reviews reviews() {
		return new Reviews();
	}
	
	public static CommitInfoRequests commitInfos() {
		return new CommitInfoRequests();
	}
	
	public static RepositoryBulkReadRequestBuilder prepareBulkRead() {
		return new RepositoryBulkReadRequestBuilder();
	}

}
