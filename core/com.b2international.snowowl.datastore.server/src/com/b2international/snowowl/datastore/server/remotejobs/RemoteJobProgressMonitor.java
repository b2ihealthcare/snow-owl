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
package com.b2international.snowowl.datastore.server.remotejobs;

import java.util.UUID;

import org.eclipse.core.runtime.NullProgressMonitor;

import com.google.common.base.Preconditions;

/**
 */
public class RemoteJobProgressMonitor extends NullProgressMonitor {

	private final RemoteJobManager remoteJobManager;

	private final UUID remoteJobId;

	private int totalWork;
	
	private double unitsWorked;
	
	public RemoteJobProgressMonitor(RemoteJobManager remoteJobManager, UUID remoteJobId) {
		Preconditions.checkNotNull(remoteJobManager, "Remote job management service reference may not be null.");
		Preconditions.checkNotNull(remoteJobId, "Remote job identifier may not be null.");
		this.remoteJobManager = remoteJobManager;
		this.remoteJobId = remoteJobId;
	}
	
	@Override
	public void beginTask(String name, int totalWork) {
		this.totalWork = totalWork;
		this.unitsWorked = 0.0;
		super.beginTask(name, totalWork);
	}
	
	@Override
	public void worked(int work) {
		doInternalWorked(work);
		super.worked(work);
	}
	
	@Override
	public void internalWorked(double work) {
		doInternalWorked(work);
		super.internalWorked(work);
	}

	@Override
	public void done() {
		workRemainingUnits();
		super.done();
	}

	private void workRemainingUnits() {
		double remaining = Math.max((double) totalWork - unitsWorked, 0.0);
		doInternalWorked(remaining);
	}

	private void doInternalWorked(double work) {
		double oldUnits = Math.min(unitsWorked, totalWork);
		unitsWorked += Math.max(work, 0.0);
		double newUnits = Math.min(unitsWorked, totalWork);
		
		int oldProgress = getProgress(oldUnits);
		int newProgress = getProgress(newUnits);
		if (newProgress > oldProgress) {
			remoteJobManager.updateUnits(remoteJobId, newProgress);
		}
	}

	private int getProgress(double units) {
		return (0 == totalWork) ? 0 : (int) (units / totalWork * 100.0);
	}
}