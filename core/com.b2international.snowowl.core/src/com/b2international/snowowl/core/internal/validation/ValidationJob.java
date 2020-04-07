/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.internal.validation;

import java.util.Objects;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.google.common.base.Preconditions;

/**
 * @since 6.6
 */
public class ValidationJob extends Job {

	private static final String VALIDATION_JOB_NAME = "validationJob";
	
	private final Object family;
	private final Runnable runnable;
	
	public ValidationJob(Object family, Runnable runnable) {
		super(VALIDATION_JOB_NAME);
		Preconditions.checkNotNull(family);
		Preconditions.checkNotNull(runnable);
		
		this.family = family;
		this.runnable = runnable;
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			runnable.run();
		} catch (OperationCanceledException e) {
			return Status.CANCEL_STATUS;
		}
		
		if (monitor.isCanceled()) {
			return Status.CANCEL_STATUS;
				
		}
		return Status.OK_STATUS;
	}
	
	@Override
	public boolean belongsTo(Object family) {
		return Objects.equals(this.family, family);
	}
	
}
