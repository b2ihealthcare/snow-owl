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
 * Checked exception indicating the merging the content of two CDO audit view
 * failed due to deletion.
 * <br>Also contains the conflicting user's unique identifier and a human readable information about 
 * the deleted component.
 * @see MergeFailedException
 * @see ICDOEditingContextMerger
 */
public class MergeFailedWithDetailsException extends MergeFailedWithCDOIDException {

	private static final long serialVersionUID = 4176230959114924996L;
	
	private final String componentDescription;
	private final String userId;

	/**
	 * Creates a new exception instance with the CDO ID of the missing object, the unique user ID
	 * of the conflicting user and a human readable description about the missing component.
	 * @param userId the unique ID of the user.
	 * @param componentDescription the description about the missing component.
	 * @param cdoId the unique ID of the missing/deleted component.
	 */
	public MergeFailedWithDetailsException(final String userId, final String componentDescription, final CDOID cdoId) {
		super(cdoId);
		this.userId = userId;
		this.componentDescription = componentDescription;
	}

	/**
	 * Returns with the unique identifier of the conflicting user.
	 * @return the unique ID of the conflicting user.
	 */
	public String getUserId() {
		return userId;
	}
	
	/**
	 * Returns with the human readable description about the stale object.
	 * @return a description about the deleted component.
	 */
	public String getComponentDescription() {
		return componentDescription;
	}
	
	@Override
	public String toString() {
		return getClass().getCanonicalName() + ": Merge failed when processing changes made by " + userId + ": \"" + componentDescription + "\".";
	}
	
}