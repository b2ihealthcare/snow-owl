/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.datastore.request.compare.BranchCompareRequestBuilder;

/**
 * Central branching class with access to branching features.
 * 
 * @since 4.5
 */
public final class Branching {

	Branching() {}
	
	public BranchCreateRequestBuilder prepareCreate() {
		return new BranchCreateRequestBuilder();
	}
	
	public BranchSearchRequestBuilder prepareSearch() {
		return new BranchSearchRequestBuilder();
	}
	
	public BranchGetRequestBuilder prepareGet(String path) {
		return new BranchGetRequestBuilder(path);
	}
	
	public BranchDeleteRequestBuilder prepareDelete(String branchPath) {
		return new BranchDeleteRequestBuilder(branchPath);
	}

	public BranchReopenRequestBuilder prepareReopen(String branchPath) {
		return new BranchReopenRequestBuilder(branchPath);
	}

	public BranchUpdateRequestBuilder prepareUpdate(String branchPath) {
		return new BranchUpdateRequestBuilder(branchPath);
	}
	
	public BranchCompareRequestBuilder prepareCompare() {
		return new BranchCompareRequestBuilder();
	}
	
}
