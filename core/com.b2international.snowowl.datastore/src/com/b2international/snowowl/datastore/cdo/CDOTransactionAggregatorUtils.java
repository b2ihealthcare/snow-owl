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

import org.eclipse.emf.cdo.transaction.CDOTransaction;

import com.b2international.commons.CompareUtils;
import com.google.common.base.Preconditions;

/**
 * Utility class for {@link ICDOTransactionAggregator transaction aggregator}s.
 */
public abstract class CDOTransactionAggregatorUtils {

	/**
	 * Checks the {@link ICDOTransactionAggregator aggregator} argument, and returns with the given argument
	 * if can be referenced (not {@code null}) and wraps at least on {@link CDOTransaction transaction}.
	 * @param aggregator the aggregator to check.
	 * @return the argument.
	 */
	public static <T extends ICDOTransactionAggregator> T check(final T aggregator) {
		Preconditions.checkNotNull(aggregator, "CDO transaction aggregator argument cannot be null.");
		if (CompareUtils.isEmpty(aggregator)) {
			throw new EmptyTransactionAggregatorException();
		}
		return aggregator;
	} 
	
	private CDOTransactionAggregatorUtils() {
		//private
	}
	
}