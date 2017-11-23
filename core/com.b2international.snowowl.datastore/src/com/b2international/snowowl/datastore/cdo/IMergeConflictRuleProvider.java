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
package com.b2international.snowowl.datastore.cdo;

import static java.util.Collections.emptySet;

import java.util.Collection;

/**
 * @since 4.7
 */
public interface IMergeConflictRuleProvider {

	String EXTENSION_ID = "com.b2international.snowowl.datastore.server.mergeConflictRuleProvider";
	
	/**
	 * Returns the unique repository identifier this rule provider is associated with.
	 * 
	 * @return
	 */
	String getRepositoryUUID();
	
	/**
	 * Returns a collection of {@link IMergeConflictRule}s.
	 * 
	 * @return
	 */
	Collection<IMergeConflictRule> getRules();

	/**
	 * NOOP implementation of {@link IMergeConflictRuleProvider}
	 * 
	 * @since 4.7
	 */
	public class NullImpl implements IMergeConflictRuleProvider {
		
		private String repositoryUUID;

		public NullImpl(final String repositoryUUID) {
			this.repositoryUUID = repositoryUUID;
		}
		
		@Override
		public String getRepositoryUUID() {
			return repositoryUUID;
		}
		
		@Override
		public Collection<IMergeConflictRule> getRules() {
			return emptySet();
		}
		
	}
	
}
