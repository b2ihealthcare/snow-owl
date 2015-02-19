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

import java.io.Externalizable;
import java.util.Iterator;

import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * Iterable aggregating CDO transactions. Also provides a UUID to group transactions
 * by any arbitrary logic. Clients may add element via  {@link #add(CDOTransaction)}.
 *
 */
public interface ICDOTransactionAggregator extends Iterable<CDOTransaction>, Externalizable, AutoCloseable {

	/**
	 * Returns with a version 4 UUID aggregating 
	 * a bunch of {@link CDOTransaction transaction} by any custom logic.
	 * <br>Never {@code null}.
	 * @return the UUID of the aggregator.
	 */
	String getUuid();
	
	/**Adds a new transaction to the current aggregator. Duplicates are not allowed.*/
	void add(final CDOTransaction transaction);
	
	@Override 
	public Iterator<CDOTransaction> iterator();
	
	@Override
	public void close();
	
}