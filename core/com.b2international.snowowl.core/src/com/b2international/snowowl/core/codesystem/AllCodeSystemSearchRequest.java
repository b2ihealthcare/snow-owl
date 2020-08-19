/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.codesystem;

import java.util.Collection;
import java.util.List;

import com.b2international.snowowl.core.Repositories;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.google.common.collect.Lists;

/**
 * @since 7.8
 */
final class AllCodeSystemSearchRequest implements Request<ServiceProvider, CodeSystems> {

	private static final long serialVersionUID = 1L;
	
	private final Collection<String> ids;
	private final List<String> fields;
	private final String expand;
	private final Iterable<String> toolingIds;
	
	AllCodeSystemSearchRequest(final Collection<String> ids, final List<String> fields, final String expand, final Iterable<String> toolingIds) {
		this.ids = ids;
		this.fields = fields;
		this.expand = expand;
		this.toolingIds= toolingIds;
	}

	@Override
	public CodeSystems execute(ServiceProvider context) {
		final List<CodeSystem> codeSystemList = Lists.newArrayList();

		final Repositories repositories = RepositoryRequests.prepareSearch()
				.all()
				.build()
				.execute(context);
		
		repositories.forEach(repositoryInfo -> {
			CodeSystems css = CodeSystemRequests.prepareSearchCodeSystem()
					.all()
					.filterByIds(ids)
					.filterByToolingIds(toolingIds)
					.setFields(fields)
					.setExpand(expand)
					.build(repositoryInfo.id())
					.getRequest()
					.execute(context);
			
			codeSystemList.addAll(css.getItems());
		});
		
		return new CodeSystems(codeSystemList, null, codeSystemList.size(), codeSystemList.size());
	}
	
}
