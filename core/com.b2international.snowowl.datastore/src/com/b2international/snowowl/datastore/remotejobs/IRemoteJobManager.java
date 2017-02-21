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
package com.b2international.snowowl.datastore.remotejobs;

import java.util.Set;
import java.util.UUID;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.snowowl.rpc.Async;

/**
 * The service interface for remote job management.
 * 
 */
public interface IRemoteJobManager {

	String JOB_ID_PARAMETER = "jobId";
	
	String ADDRESS_REMOTE_JOB_CHANGED = "RemoteJob_changed";
	String ADDRESS_REMOTE_JOB_COMPLETED = "RemoteJob_{uuid}_completed";
	
	/**
	 * Retrieves the list of all remote jobs on the server.
	 */
	Set<RemoteJobEntry> getAllRemoteJobs();

	/**
	 * Retrieves the list of all remote jobs on the server scheduled by the specified user identifier.
	 * 
	 * @param userId the user identifier to use when filtering remote jobs (may not be {@code null})
	 */
	Set<RemoteJobEntry> getRemoteJobsByUser(String userId);

	/**
	 * Signals the remote job with the specified identifier to cancel all running computations. The running job can
	 * ignore the request, or react to it at any later time.
	 * 
	 * @param id the remote job identifier (may not be {@code null})
	 */
	@Async
	void cancelRemoteJob(UUID id);

	/**
	 * Removes entries for all remote jobs which are in {@link RemoteJobState#FINISHED} state (not requiring further
	 * interactions with the requesting user).
	 */
	@Async
	void removeFinishedRemoteJobs();

	/**
	 * Removes entries for all remote jobs of a particular user which are in {@link RemoteJobState#FINISHED} state (not requiring further
	 * interactions).
	 * 
	 * @param userId the user identifier to use when removing remote jobs (may not be {@code null})
	 */
	@Async
	void removeFinishedRemoteJobsByUser(String userId);
}