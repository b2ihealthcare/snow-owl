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
package com.b2international.snowowl.core.api;

import java.io.Serializable;


/**
 * Represents a {@link #getTimestamp() point in time} on a particular {@link IBranchPath branch}.
 */
public interface IBranchPoint extends Serializable {

	/**Unspecified point in time. Value: {@value}*/
	public static final long UNSPECIFIED_TIME = -1L;
	
	/**
	 * Returns with the {@link IBranchPath branch path} of the branch point.
	 */
	IBranchPath getBranchPath();

	/**
	 * Returns with the timestamp for the branch point. 
	 */
	long getTimestamp();
	
	/**
	 * Returns with the UUID of the CDO connection which is used by the current branch point.
	 * @return the connection UUID.
	 */
	String getUuid();

	/**
	 * Represents a *NON* existent {@link NullBranchPoint#INSTANCE branch point} with an
	 * {@link IBranchPoint#UNSPECIFIED_TIME unspecified point in time}.
	 * @see #INSTANCE
	 * @see IBranchPoint
	 */
	public static enum NullBranchPoint implements IBranchPoint {
		
		/**
   	 * The NULL instance.
	   * @see NullBranchPoint
		 */
		INSTANCE;

		/* (non-Javadoc)
		 * @see com.b2international.snowowl.core.api.IBranchPoint#getBranchPath()
		 */
		@Override
		public IBranchPath getBranchPath() {
			return NullBranchPath.INSTANCE;
		}

		/* (non-Javadoc)
		 * @see com.b2international.snowowl.core.api.IBranchPoint#getTimestamp()
		 */
		@Override
		public long getTimestamp() {
			return IBranchPoint.UNSPECIFIED_TIME;
		}
		
		/**
		 * Always returns with {@code null}.<p>
		 * {@inheritDoc}
		 */
		/* (non-Javadoc)
		 * @see com.b2international.snowowl.datastore.IBranchPoint#getUuid()
		 */
		@Override
		public String getUuid() {
			return null;
		}
		
	}
	
}