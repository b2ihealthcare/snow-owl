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
package com.b2international.snowowl.datastore.server.internal.merge;

import java.util.List;
import java.util.UUID;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.merge.Merge;
import com.b2international.snowowl.core.merge.MergeCollection;
import com.b2international.snowowl.core.merge.MergeService;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.FluentIterable;

/**
 * @since 4.6
 */
public class MergeServiceImpl implements MergeService {

	private final Repository repository;
	private final Cache<UUID, AbstractBranchChangeRemoteJob> merges;
	
	public MergeServiceImpl(Repository repository, int mergeMaxResults) {
		this.repository = repository;
		this.merges = CacheBuilder.newBuilder()
				.maximumSize(mergeMaxResults)
				.build();
	}

	@Override
	public Merge enqueue(String source, String target, String commitMessage, String reviewId) {
		AbstractBranchChangeRemoteJob job = AbstractBranchChangeRemoteJob.create(repository, source, target, commitMessage, reviewId);
		merges.put(job.getMerge().getId(), job);
		job.schedule();
		return job.getMerge();
	}

	@Override
	public Merge getMerge(UUID id) {
		AbstractBranchChangeRemoteJob job = merges.getIfPresent(id);
		
		if (job != null) {
			return job.getMerge();
		} else {
			throw new NotFoundException("Merge queue entry", id.toString());
		}
	}

	@Override
	public MergeCollection search(Predicate<Merge> query) {
		List<Merge> results = FluentIterable
				.from(merges.asMap().values())
				.transform(new Function<AbstractBranchChangeRemoteJob, Merge>() {
					@Override
					public Merge apply(AbstractBranchChangeRemoteJob input) {
						return input.getMerge();
					}
				})
				.filter(query)
				.toList();
		
		return new MergeCollection(results);
	}

	@Override
	public void deleteMerge(UUID id) {
		AbstractBranchChangeRemoteJob job = merges.asMap().remove(id);
		
		if (job != null) {
			job.cancel();
		} else {
			throw new NotFoundException("Merge queue entry", id.toString());
		}		
	}
}
