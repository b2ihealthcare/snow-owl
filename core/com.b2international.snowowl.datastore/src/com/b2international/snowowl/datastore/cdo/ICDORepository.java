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
package com.b2international.snowowl.datastore.cdo;

import java.sql.Connection;
import java.util.Set;

import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.db.IDBStore;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Service interface for wrapping a CDO specific {@link IRepository repository} and a {@link IDBStore DB store}.
 */
public interface ICDORepository extends ICDOManagedItem<ICDORepository> {

	/**Extension point ID for repository implementations.*/
	String REPOSITORY_EXT_ID = "com.b2international.snowowl.datastore.repository";
	
	/**Returns with the wrapped {@link IDBStore DB store} instance.*/
	IDBStore getDbStore();
	
	/**Returns with the wrapped {@link IDBStore DB store} instance.*/
	IRepository getRepository();
	
	/**Returns with the pooled SQL connection to the underlying {@link IDBStore DB store}.*/
	Connection getConnection();
	
	void setReaderPoolCapacity(int capacity);
	
	void setWriterPoolCapacity(int capacity);
	
	/**
	 * User ID predicate class.
	 * @see ICDORepository#lock(IBranchPath, Predicate)
	 */
	public static final class AllowedUserIdPredicate implements Predicate<String> {
		
		private Set<String> userIds;

		/**
		 * Creates a new predicate with one or more user IDs.
		 * @param userId the mandatory user IDs.
		 * @param userIds other user IDs.
		 */
		public AllowedUserIdPredicate(final String userId, final String... userIds) {
			this(Lists.asList(Preconditions.checkNotNull(userId), userIds));
		}

		/**
		 * Creates a new predicate instance that will an iterable of user IDs.  
		 * @param userIds the user IDs.
		 */
		public AllowedUserIdPredicate(final Iterable<String> userIds) {
			this.userIds = Sets.newHashSet(Preconditions.checkNotNull(userIds));
		}
		
		/* (non-Javadoc)
		 * @see com.google.common.base.Predicate#apply(java.lang.Object)
		 */
		@Override
		public boolean apply(final String input) {
			return !userIds.contains(input);
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return CompareUtils.isEmpty(userIds) 
					? "" 
					: "Allowed user ID" + (userIds.size() > 1 ? "s: " : ": ") + userIds;
		}
		
	}
	
}