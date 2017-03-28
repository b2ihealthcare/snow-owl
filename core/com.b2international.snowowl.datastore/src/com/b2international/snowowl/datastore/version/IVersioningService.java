/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.version;

import java.util.Collection;
import java.util.Date;

import javax.annotation.Nullable;

import org.eclipse.core.runtime.IStatus;

import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.ICodeSystemVersion;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;


/**
 * Represents a terminology independent service responsible for collecting and creating
 * versions for its concrete implementations. 
 * @deprecated - use {@link CodeSystemRequests#prepareNewCodeSystemVersion()} instead
 */
public interface IVersioningService {
	
	/**Returns with a collection of already existing versions for a given tooling feature.*/
	Collection<ICodeSystemVersion> getExistingVersions(final String toolingId);
	
	/**Configures and applies the given version on the service.*/
	IStatus configureNewVersionId(final String versionId, final boolean ignoreValidation);
	
	/**Configures and applies the given effective time on the service.*/
	IStatus configureEffectiveTime(final Date effectiveTime);
	
	/**
	 * Configures the parent branch path for the versioning.
	 */
	IStatus configureParentBranchPath(String parentBranchPath);
	
	/**
	 * Configures the code system short name for the for the versioning. 
	 */
	IStatus configureCodeSystemShortName(String codeSystemShortName);
	
	/**Applies the given description on the stateful service.*/
	IStatus configureDescription(@Nullable final String description);
	
	/**Performs the tagging. Returns with a {@link IStatus status} indicating the outcome of the process.*/
	IStatus tag();
	
	/**Returns with the version ID for the current service. Could return with {@code null} if not configured yet.*/
	@Nullable String getVersionId();

	/**Acquires a lock on the server-side to ensure data consistency while versioning is running.*/
	void acquireLock() throws SnowowlServiceException;
	
	/**Releases the previously acquired lock from the server-side.*/
	void releaseLock() throws SnowowlServiceException;
	
	/**Checks whether caller could create a new version or not. The result is returned as a {@link IStatus status}.*/
	IStatus canCreateNewVersion();
	
}