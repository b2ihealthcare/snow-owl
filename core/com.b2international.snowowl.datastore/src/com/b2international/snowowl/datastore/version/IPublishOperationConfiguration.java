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
package com.b2international.snowowl.datastore.version;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

/**
 * Represents a serializable configuration to perform a publication operation.
 */
public interface IPublishOperationConfiguration extends Serializable, ToolingIdIterable {

	/**
	 * @return the identifier of the publishing remote job
	 */
	UUID getRemoteJobId();
	
	/**
	 * Returns with the version ID.
	 * @return the version ID.
	 */
	String getVersionId();
	
	/**
	 * Returns the parent branch path
	 * @return
	 */
	String getParentBranchPath();
	
	/**
	 * Returns with the configured effective time.
	 * @return the effective time that has to be used for the publication process.
	 */
	Date getEffectiveTime();
	
	String getPrimaryToolingId();
	
	/**
	 * Returns with the tooling IDs representing the contents that has to be published.
	 */
	Collection<String> getToolingIds();
	
	/**
	 * Returns with the performer user's ID.
	 */
	String getUserId(); 
	
	/**
	 * Returns with the description. 
	 */
	String getDescription();
	
	/**
	 * The short name of the code system for the version.
	 */
	String getCodeSystemShortName();
	
}