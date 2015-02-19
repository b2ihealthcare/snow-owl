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
package com.b2international.snowowl.snomed.reasoner.server.classification;

import java.util.Timer;
import java.util.TimerTask;

import com.b2international.snowowl.core.api.IBranchPath;

/**
 */
public class CollectingServiceReference<S> {
	
	private IBranchPath branchPath;
	private boolean shared;
	private S service;

	private volatile TimerTask collectingTask;
	
	public void init(final IBranchPath branchPath, final boolean shared, final S service) {
		this.branchPath = branchPath;
		this.shared = shared;
		this.service = service;
	}
	
	public IBranchPath getBranchPath() {
		return branchPath;
	}
	
	public boolean isShared() {
		return shared;
	}
	
	public S getService() {
		return service;
	}
	
	public void scheduleTaskOnTimer(final Timer timer, final TimerTask collectingTask, final long delay) {
		timer.schedule(collectingTask, delay);
		this.collectingTask = collectingTask;
	}
	
	public void cancelCollectingTask() {
		collectingTask.cancel();
	}
}