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

import com.b2international.snowowl.core.api.IBranchPath;

/**
 * Represents the required information to perform a tagging in the repository.
 * @see Serializable
 */
public interface ITagConfiguration extends Serializable {

	/**Returns with the requesting user identifier.*/
	String getUserId();

	/**Returns with the unique version ID for the tag.*/
	String getVersionId();
	
	/**Returns with the destination branch path where the that has to be performed.*/
	IBranchPath getBranchPath();
	
	/**The UUID of the repository where the tag has to be created.*/
	String getRepositoryUuid();
	
	/**The parent lock context description, as a part of which tagging should be carried out*/
	String getParentContextDescription();
}
