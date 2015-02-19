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
package com.b2international.snowowl.snomed.importer.rf2.util;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.emf.cdo.transaction.CDOTransaction;

import com.b2international.commons.collections.MutableSupplier;
import com.b2international.snowowl.datastore.cdo.CDOTransactionAggregator;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.cdo.ICDOTransactionAggregator;
import com.google.common.base.Strings;

/**
 * Supplies {@link CDOTransactionAggregator transaction aggregator} based on SNOMED CT specific effective times.
 */
public class EffectiveTimeBaseTransactionAggregatorSupplier extends MutableSupplier<String, ICDOTransactionAggregator> {


	private ICDOTransactionAggregator aggregator;

	public EffectiveTimeBaseTransactionAggregatorSupplier(final CDOTransaction transaction) {
		super(new AtomicReference<String>());
		aggregator = CDOTransactionAggregator.create(CDOUtils.check(transaction));
	}

	/* (non-Javadoc)
	 * @see com.b2international.commons.collections.ISupplier2#get(java.lang.Object)
	 */
	@Override
	public ICDOTransactionAggregator get(final String from) {
		
		if (Strings.nullToEmpty(getValue()).equals(from)) {
			
			return aggregator; 
			
		} else {
			
			set(from);
			aggregator = CDOTransactionAggregator.create(aggregator);
			return aggregator;
			
		}
		
	}

}