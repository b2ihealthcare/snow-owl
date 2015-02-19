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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.b2international.snowowl.datastore.exception.MergeFailedException;

/**
 * Status indicating a failed merge.
 * <br>Intentionally has {@link IStatus#CANCEL cancel} severity to avoid raising
 * error dialog from job framework. 
 * @see MergeFailedException
 */
public class MergeFailedStatus extends Status {

	private final MergeFailedException exception;

	/**
	 * Creates a new status instance indicating a failed merge.
	 * @param exception the outcome of the failed merge operation.
	 */
	public MergeFailedStatus(final MergeFailedException exception) {
		super(IStatus.CANCEL, DatastoreActivator.PLUGIN_ID, exception.getMessage());
		this.exception = exception;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.Status#getException()
	 */
	@Override
	public Throwable getException() {
		return exception;
	}
	
	
}