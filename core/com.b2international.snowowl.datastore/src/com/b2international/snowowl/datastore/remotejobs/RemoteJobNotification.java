/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.remotejobs;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.b2international.snowowl.core.events.SystemNotification;

/**
 * @since 5.7
 */
public abstract class RemoteJobNotification extends SystemNotification {
	private static final long serialVersionUID = 1L;
	
	private final Set<String> jobIds;
	
	public RemoteJobNotification(Set<String> jobIds) {
		this.jobIds = jobIds;
	}

	public Collection<String> getJobIds() {
		return jobIds;
	}
	
	private static class Added extends RemoteJobNotification {
		private static final long serialVersionUID = 1L;

		public Added(Set<String> jobIds) {
			super(jobIds);
		}
	}
	
	private static class Changed extends RemoteJobNotification {
		private static final long serialVersionUID = 1L;
		
		public Changed(Set<String> jobIds) {
			super(jobIds);
		}
	}
	
	private static class Removed extends RemoteJobNotification {
		private static final long serialVersionUID = 1L;
		
		public Removed(Set<String> jobIds) {
			super(jobIds);
		}
	}
	
	public static RemoteJobNotification added(String jobId) {
		return added(Collections.singleton(jobId));
	}
	
	public static RemoteJobNotification added(Set<String> jobIds) {
		return new Added(jobIds);
	}
	
	public static RemoteJobNotification changed(String jobId) {
		return changed(Collections.singleton(jobId));
	}
	
	public static RemoteJobNotification changed(Set<String> jobIds) {
		return new Changed(jobIds);
	}
	
	public static RemoteJobNotification removed(String jobId) {
		return removed(Collections.singleton(jobId));
	}
	
	public static RemoteJobNotification removed(Set<String> jobIds) {
		return new Removed(jobIds);
	}
	
	public static boolean isAdded(RemoteJobNotification n) {
		return n instanceof Added;
	}
	
	public static boolean isChanged(RemoteJobNotification n) {
		return n instanceof Changed;
	}

	public static boolean isRemoved(RemoteJobNotification n) {
		return n instanceof Removed;
	}

}
