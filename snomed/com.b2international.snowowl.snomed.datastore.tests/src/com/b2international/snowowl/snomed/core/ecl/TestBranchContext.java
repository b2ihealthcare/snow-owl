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
package com.b2international.snowowl.snomed.core.ecl;

import org.mockito.Mockito;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.DelegatingServiceProvider;

/**
 * @since 5.4
 */
public final class TestBranchContext extends DelegatingServiceProvider implements BranchContext {

	private final Branch branch;

	private TestBranchContext(Branch branch) {
		super(ServiceProvider.EMPTY);
		this.branch = branch;
	}
	
	@Override
	public Branch branch() {
		return branch;
	}
	
	@Override
	public SnowOwlConfiguration config() {
		return service(SnowOwlConfiguration.class);
	}
	
	@Override
	public String id() {
		throw new UnsupportedOperationException();
	}
	
	public static TestBranchContext.Builder on(String branch) {
		return new TestBranchContext.Builder(branch);
	}
	
	public static class Builder {
		
		private TestBranchContext context;
		
		Builder(String branch) {
			final Branch mockBranch = Mockito.mock(Branch.class);
			context = new TestBranchContext(mockBranch);
		}
		
		public <T> Builder with(Class<T> type, T object) {
			context.bind(type, object);
			return this;
		}
		
		public BranchContext build() {
			return context;
		}
		
	}
	
}
