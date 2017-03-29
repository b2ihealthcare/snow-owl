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
package com.b2international.snowowl.api.impl.info;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.b2international.snowowl.api.admin.exception.RepositoryNotFoundException;
import com.b2international.snowowl.api.impl.info.domain.RepositoryInfo;
import com.b2international.snowowl.api.impl.info.domain.RepositoryInfo.Builder;
import com.b2international.snowowl.api.info.IRepositoryInfoService;
import com.b2international.snowowl.api.info.domain.IRepositoryInfo;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.server.internal.InternalRepository;
import com.b2international.snowowl.datastore.server.internal.RepositoryMetadata;
import com.google.common.collect.Lists;

/**
 * @since 5.8 
 */
public class RepositoryInfoServiceImpl implements IRepositoryInfoService {

	@Override
	public IRepositoryInfo getRepositoryInfo(String repositoryId) {
		assertAvailableRepository(repositoryId);
		
		Repository repository = getRepositoryManager().get(repositoryId);
		RepositoryMetadata repositoryMetadata = assertInfoSupport(repository);
		
		Builder infoBuilder = RepositoryInfo.builder()
							.headTimestampForDatabase(repositoryMetadata.getHeadTimestampForDatabase())
							.headTimestampForIndex(repositoryMetadata.getHeadTimestampForIndex())
							.repositoryId(repository.id())
							.state(repositoryMetadata.getHealth());
		
		return infoBuilder.build();
	}

	@Override
	public List<IRepositoryInfo> getAllRepositoryInformation() {
		List<IRepositoryInfo> results = Lists.newArrayList();
		for (String repositoryId : getRepositoryIds()) {
			results.add(getRepositoryInfo(repositoryId));
		}
		return results;
	}

	@Override
	public void updateAllRespositoryInformation() {
		getRepositoryIds().stream()
			.map(repositoryId -> getRepository(repositoryId))
			.filter(InternalRepository.class::isInstance).map(InternalRepository.class::cast)
			.forEach(repository -> repository.updateHealth());
	}

	private Repository getRepository(String repositoryId) {
		return getRepositoryManager().get(repositoryId);
	}
	
	private Collection<String> getRepositoryIds() {
		return getRepositoryManager()
					.repositories().stream()
					.map(repository -> repository.id())
					.collect(Collectors.toList());
	}

	private RepositoryManager getRepositoryManager() {
		return ApplicationContext.getServiceForClass(RepositoryManager.class);
	}

	private void assertAvailableRepository(String repositoryId) {
		if (!getRepositoryIds().contains(repositoryId)) {
			throw new RepositoryNotFoundException(repositoryId);
		}
	}

	private RepositoryMetadata assertInfoSupport(Repository repository) throws BadRequestException {
		if (!(repository instanceof RepositoryMetadata)) {
			throw new BadRequestException("Repository {} does not provide runtime information", repository.id());
		}
		return (RepositoryMetadata) repository;
	}
}
