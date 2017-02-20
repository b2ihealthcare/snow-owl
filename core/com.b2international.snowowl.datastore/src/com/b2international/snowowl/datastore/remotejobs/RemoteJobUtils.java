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

import java.util.UUID;

import javax.annotation.Nullable;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.jobs.Job;

import com.b2international.commons.TokenReplacer;
import com.b2international.snowowl.datastore.DatastoreActivator;

/**
 */
public abstract class RemoteJobUtils {

	private RemoteJobUtils() { }
	
	private static final QualifiedName REMOTE_JOB_ID_KEY = new QualifiedName(DatastoreActivator.PLUGIN_ID, "remoteJobId");
	private static final QualifiedName REQUESTING_USER_ID_KEY = new QualifiedName(DatastoreActivator.PLUGIN_ID, "requestingUserId");
	private static final QualifiedName CUSTOM_COMMAND_ID_KEY = new QualifiedName(DatastoreActivator.PLUGIN_ID, "customCommandId");
	
	public static String getJobSpecificAddress(final String addressTemplate, final UUID id) {
		return new TokenReplacer().register("uuid", id.toString()).substitute(addressTemplate);
	}
	
	public static UUID getRemoteJobId(final Job job) {
		return (UUID) job.getProperty(REMOTE_JOB_ID_KEY);
	}
	
	public static String getRemoteJobDescription(final Job job) {
		return job.getName();
	}
	
	public static String getRequestingUserId(final Job job) {
		return (String) job.getProperty(REQUESTING_USER_ID_KEY);
	}
	
	public static @Nullable String getCustomCommandId(final Job job) {
		return (String) job.getProperty(CUSTOM_COMMAND_ID_KEY);
	}
	
	public static Job configureProperties(final Job job, final String requestingUserId, @Nullable final String customCommandId, @Nullable final UUID remoteJobId) {
		job.setSystem(true);
		job.setProperty(REMOTE_JOB_ID_KEY, getOrCreateId(remoteJobId));
		job.setProperty(REQUESTING_USER_ID_KEY, requestingUserId);
		job.setProperty(CUSTOM_COMMAND_ID_KEY, customCommandId);
		return job;
	}

	private static UUID getOrCreateId(@Nullable final UUID remoteJobId) {
		return (null != remoteJobId) ? remoteJobId : UUID.randomUUID();
	}
}