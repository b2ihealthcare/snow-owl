/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.api.info;

import java.util.List;

import com.b2international.snowowl.api.admin.exception.RepositoryNotFoundException;
import com.b2international.snowowl.api.info.domain.IRepositoryInfo;
import com.b2international.snowowl.core.exceptions.BadRequestException;

/**
 * Implementations of this service allow browsing runtime information of the Terminology Server. 
 * @since 5.8
 */
public interface IRepositoryInfoService {

	/**
	 * 
	 * Retrieves metadata / runtime information about the repository identified by the repositoryId, if it exists. 
	 * 
	 * @param repositoryId the id by which the repository is identified by.
	 * 
	 * @return the state for the requested repository
	 * 
	 * @throws RepositoryNotFoundException if no repository is identified by the given repositoryId.
	 * 
	 * @throws BadRequestException if the given repository exists, but does not support runtime information about itself.
	 */
	IRepositoryInfo getRepositoryInfo(String repositoryId);
	
	/**
	 * Retrieves
	 * @return
	 */
	List<IRepositoryInfo> getAllRepositoryInformation();
	
	/**
	 * Recalculates and updates the state for the all available repositories.
	 */
	void updateAllRespositoryInformation();
}
