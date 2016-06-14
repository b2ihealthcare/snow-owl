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
package com.b2international.snowowl.datastore;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;

/**
 * Representation of a global client specific, task aware
 * version configuration.
 *
 */
public interface VersionConfiguration {

	/**
	 * Updates the state of the configuration with the code system version argument.
	 * @param version the code system version with the new state to 
	 * update the configuration.
	 * @return returns with a status representing the outcome of the operation. 
	 */
	IStatus update(final ICodeSystemVersion version);
	
	/**
	 * Returns with the internal state of the configuration as a map where the
	 * keys are {@link ICodeSystem}s and the values are the currently configured {@link ICodeSystemVersion}.
	 * @return the version configuration as a map of repository IDs and the {@link ICodeSystemVersion version}s.
	 */
	Map<ICodeSystem, ICodeSystemVersion> getConfiguration();
	
	/**
	 * Returns with the configuration as a branch path map.
	 * @return returns with the state of the configuration as a branch path map.
	 */
	IBranchPathMap getConfigurationAsBranchPathMap();
	
	/**
	 * Returns with {@code true} if the code system version is locked, hence it cannot be altered 
	 * @param version the code system to check whether it is locked or not.
	 * @return {@code true} if locked, otherwise {@code false}.
	 */
	boolean isLocked(final ICodeSystemVersion version);
	
	/**
	 * Returns with {@code true} if the code system argument is the one and only version in the repository.
	 * Versions representing the {@link ICodeSystemVersion#INITIAL_STATE 'init'} do not count when invoking this method.
	 * In other words if a repository has the {@link ICodeSystemVersion#INITIAL_STATE 'init'} state an any other
	 * regular version, this method will return with {@code false}.
	 * @param version the version to check whether it is a singleton or not.
	 * @return {@code true} if singleton, otherwise {@code false}.
	 */
	boolean isSingleton(final ICodeSystemVersion version);
	
	/**
	 * Returns with {@code true} if the state of the configuration has been changed
	 * since it has been created. Otherwise {@code false}.
	 * @return {@code true} if changed/dirty, otherwise {@code false}.
	 */
	boolean isDirty();
	
	/**
	 * Returns with all versions from a repository where the argument belongs to.
	 * <br>Version representing the {@link ICodeSystemVersion#INITIAL_STATE 'init'} state will be excluded 
	 * from the returning list. 
	 * @param version the version to check. 
	 * @return all versions in the repository.
	 */
	List<ICodeSystemVersion> getAllVersionsForRepository(final ICodeSystemVersion version);
	
}