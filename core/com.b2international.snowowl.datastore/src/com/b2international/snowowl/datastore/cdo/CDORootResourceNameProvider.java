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
package com.b2international.snowowl.datastore.cdo;

import java.util.Collection;

/**
 * Representation of a CDO root resource name provider.
 *
 */
public interface CDORootResourceNameProvider {

	/**Unique ID of the CDO root resource name provider extension point. {@code cdoRootResourceNameProvider}
	 *<p>ID: {@value}*/
	String ROOT_RESOURCE_NAMEPROVIDER_EXTENSION_POINT_ID = //
			"com.b2international.snowowl.datastore.cdoRootResourceNameProvider";
	
	/**
	 * Returns with a collection of root resource names for a given application specific repository. 
	 * @return a collection of CDO root resource names.
	 */
	Collection<String> getRootResourceNames();
	
	/**
	 * Returns with the associated repository UUID.
	 * @return the UUID of the repository.
	 */
	String getRepositoryUuid();
	
	/**
	 * Returns with {@code true} if the root CDO resource given with the unique resource name
	 * argument is a meta root resource, hence the {@link CodeSystemVersionGroup code system version group} should
	 * be initialized when the container resource being created.
	 * @param rootResourceName the unique root resource name to check.
	 * @return {@code true} if the root resource given with its name is a meta root resource, otherwise {@code false}.
	 */
	boolean isMetaRootResource(final String rootResourceName);

	
}