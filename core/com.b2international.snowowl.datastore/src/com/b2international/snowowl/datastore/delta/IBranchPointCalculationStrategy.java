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
package com.b2international.snowowl.datastore.delta;

import java.io.Serializable;

import com.b2international.snowowl.core.api.IBranchPoint;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * Interface for determining source and target branch {@link IBranchPoint points} 
 * as a pair of {@link IBranchPoint#getBranchPath() branch path} and the {@link IBranchPoint#getTimestamp() point in time}.
 *
 */
public interface IBranchPointCalculationStrategy extends Serializable {

	/**
	 * Returns with the source {@link IBranchPoint branch point}.
	 */
	IBranchPoint getSourceBranchPoint();
	
	/**
	 * Returns with the target {@link IBranchPoint branch point}.
	 */
	IBranchPoint getTargetBranchPoint();

	/**
	 * Utility class for {@link IBranchPointCalculationStrategy}.
	 */
	public static final class Utils {
		
		/**
		 * Returns a human readable representation of a {@link IBranchPointCalculationStrategy strategy}.
		 */
		public static String toString(final IBranchPointCalculationStrategy strategy) {

			Preconditions.checkNotNull(strategy, "Strategy argument cannot be null.");
			Preconditions.checkNotNull(strategy.getSourceBranchPoint(), "Source branch point cannot be null.");
			Preconditions.checkNotNull(strategy.getTargetBranchPoint(), "Target branch point cannot be null.");
			
			return Objects.toStringHelper(strategy)
					.add("Source", strategy.getSourceBranchPoint())
					.add("Target", strategy.getTargetBranchPoint())
					.toString();
			
		}
		
		private Utils() { /*suppress instantiation*/ }
		
	}
	
}