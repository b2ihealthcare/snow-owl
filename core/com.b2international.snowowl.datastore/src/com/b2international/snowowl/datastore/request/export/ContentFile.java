/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.request.export;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.domain.PageableCollectionResource;
import com.b2international.snowowl.core.request.SearchResourceRequestBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * @since 6.3
 */
public final class ContentFile extends ContentEntry {

	private final List<String> columns;
	private final String script;
	private final Map<String, Object> scriptParams;
	private final Map<String, SearchResourceRequestBuilder<?, BranchContext, ? extends PageableCollectionResource<? extends IComponent>>> requestBuilderMap;

	public ContentFile(final String name,
			final List<String> columns,
			final String script,
			final Map<String, Object> scriptParams,
			final ImmutableMap<String, SearchResourceRequestBuilder<?, BranchContext, ? extends PageableCollectionResource<? extends IComponent>>> requestBuilderMap) {

		super(name);
		this.columns = ImmutableList.copyOf(checkNotNull(columns, "columns"));
		this.script = checkNotNull(script, "script");
		this.scriptParams = ImmutableMap.copyOf(checkNotNull(scriptParams, "scriptParams"));
		this.requestBuilderMap = checkNotNull(requestBuilderMap, "requestBuilderMap");
	}

	public List<String> getColumns() {
		return columns;
	}

	public String getScript() {
		return script;
	}

	public Map<String, Object> getScriptParams() {
		return scriptParams;
	}

	public Map<String, SearchResourceRequestBuilder<?, BranchContext, ? extends PageableCollectionResource<? extends IComponent>>> getRequestBuilderMap() {
		return requestBuilderMap;
	}
}
