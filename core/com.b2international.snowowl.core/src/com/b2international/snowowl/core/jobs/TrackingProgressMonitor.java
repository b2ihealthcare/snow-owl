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
package com.b2international.snowowl.core.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ProgressMonitorWrapper;

/**
 */
public class TrackingProgressMonitor extends ProgressMonitorWrapper {

	private volatile boolean doneTracking = false;
	
	public TrackingProgressMonitor(IProgressMonitor delegate) {
		super(delegate);
	}
	
	@Override
	public void done() {
		setDoneTracking();
		super.done();
	}

	@Override
	public void setCanceled(boolean canceled) {
		if (canceled) {
			setDoneTracking();
		}
		super.setCanceled(canceled);
	}

	public boolean isDoneTracking() {
		return doneTracking || isCanceled();
	}
	
	private void setDoneTracking() {
		doneTracking = true;
	}
}