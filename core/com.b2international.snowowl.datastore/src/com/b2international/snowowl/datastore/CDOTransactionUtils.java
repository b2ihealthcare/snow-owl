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
package com.b2international.snowowl.datastore;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

/**
 * Utility class for CDO transactions and for CDO views.
 */
public abstract class CDOTransactionUtils {

	/**
	 * Private constructor.
	 */
	private CDOTransactionUtils() { /*suppress instantiation*/ }
	
	/**
	 * Returns with the underlying {@link CDOTransaction} for the specified CDO object or {@code null} if the 
	 * transaction is not active or was opened in a <i>READ ONLY</i> mode.
	 * <br><br><b>Note: </b>callers should <b>NOT</b> close the the returning transaction explicitly 
	 * as it was opened and managed by other.   
	 * @param object the CDO object.
	 * @param <T> - type of the CDO object.
	 * @return the underlying transaction or {@code null} if the underlying transaction was opened in <i>READ ONLY</i> mode or 
	 * not active.
	 */
	public static <T extends CDOObject> CDOTransaction getUnderlyingTransaction(final T object) {
		final CDOView cdoView = object.cdoView();
		if (checkTransaction(cdoView)) {
			return (CDOTransaction) (cdoView instanceof CDOTransaction ? cdoView : null);
		}
		return null;
	}
	
	/**
	 * Returns {@code true} if the specified CDO view is not {@code null} and not closed. Otherwise it returns with {@code false}. 
	 * @param cdoView the CDO view instance to check.
	 * @return {@code true} if the CDO view is active. Otherwise {@code false}.
	 */
	public static boolean checkTransaction(final CDOView cdoView) {
		return !cdoView.isClosed();
	}
	
}