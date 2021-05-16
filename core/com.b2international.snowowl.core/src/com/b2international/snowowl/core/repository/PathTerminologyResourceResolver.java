/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.repository;

import java.util.List;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.branch.BranchPathUtils;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.google.common.primitives.Ints;

/**
 * @since 7.5
 */
@FunctionalInterface
public interface PathTerminologyResourceResolver {

	/**
	 * Returns the closest relative {@link TerminologyResource} for the given reference branch.
	 * 
	 * @param context - the context to use for the resolution of the resource
	 * @param toolingId - the tooling identifier of the requested resource, may not be <code>null</code>
	 * @param referenceBranch - the reference branch, may not be <code>null</code>
	 * 
	 * @return the closest relative {@link TerminologyResource}
	 * 
	 * @throws BadRequestException - if there is no relative {@link TerminologyResource} can be found for the given reference branch
	 */
	<T extends TerminologyResource> T resolve(ServiceProvider context, String toolingId, String referenceBranch);
	
	/**
	 * @since 8.0
	 */
	class Default implements PathTerminologyResourceResolver {
		
		@Override
		public <T extends TerminologyResource> T resolve(ServiceProvider context, String toolingId, String referenceBranch) {
			final List<String> ancestorsAndSelf = BranchPathUtils.getAllPaths(referenceBranch);
			return ResourceRequests.prepareSearch()
				.all()
				.filterByToolingId(toolingId)
				.filterByBranches(ancestorsAndSelf)
				.buildAsync()
				.getRequest()
				.execute(context)
				.stream()
				.filter(TerminologyResource.class::isInstance)
				.map(t -> (T) t)
				.sorted((t1, t2) -> Ints.compare(ancestorsAndSelf.indexOf(t1.getBranchPath()), ancestorsAndSelf.indexOf(t2.getBranchPath())))
				.findFirst()
				.orElseThrow(() -> new NotFoundException("Terminology Resource", referenceBranch));
		}
		
	}
	
}
