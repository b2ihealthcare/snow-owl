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
package com.b2international.snowowl.datastore.exception;

import org.eclipse.emf.cdo.common.id.CDOID;

import com.b2international.snowowl.datastore.cdo.ICDOEditingContextMerger;

/**
 * Checked exception indicating that merge failed due to stale reference 
 * triggered by remote deletion in the store.
 * <br>This exception should be thrown when merging CDO editing contexts' 
 * content via {@link ICDOEditingContextMerger merger}. 
 * @see ICDOEditingContextMerger
 */
public class MergeFailedWithCDOIDException extends MergeFailedException {

	private static final long serialVersionUID = 6679622086917704800L;
	
	private final CDOID cdoId;

	/**
	 * Creates a new exception instance with the unique CDO ID of the missing object.
	 * @param cdoId the CDO ID of the missing object.
	 */
	public MergeFailedWithCDOIDException(final CDOID cdoId) {
		this.cdoId = cdoId;
	}
	
	/**
	 * Returns with the CDO ID of the missing object.
	 * @return the CDO ID.
	 */
	public CDOID getCdoId() {
		return cdoId;
	}
	
	@Override
	public String toString() {
		return getClass().getCanonicalName() + ": Merge failed due to stale reference to " + cdoId + ".";
	}
	
}