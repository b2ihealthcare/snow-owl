/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.compare.ConceptMapCompareChangeKind;
import com.b2international.snowowl.core.compare.ConceptMapCompareResultItem;

/**
 * @since 7.13
 */
public final class ConceptMapCompareDsvExportRequestBuilder extends ExportRequestBuilder<ConceptMapCompareDsvExportRequestBuilder, ServiceProvider> {

	@NotNull
	private List<ConceptMapCompareResultItem> items;
	
	@NotNull
	private List<ConceptMapCompareChangeKind> changeKinds;

	@Nullable
	private Set<String> columns;

	public ConceptMapCompareDsvExportRequestBuilder(final List<ConceptMapCompareResultItem> items) {
		this.items = items;
	}
	
	public ConceptMapCompareDsvExportRequestBuilder changeKinds(final List<ConceptMapCompareChangeKind> changeKinds) {
		this.changeKinds = changeKinds;
		return this;
	}

	public ConceptMapCompareDsvExportRequestBuilder changeKinds(final Set<String> columns) {
		this.columns = columns;
		return this;
	}

	@Override
	protected ExportRequest<ServiceProvider> create() {
		final ConceptMapCompareDsvExportRequest request = new ConceptMapCompareDsvExportRequest();
		request.setItems(items);
		request.setChangeKinds(changeKinds);
		request.setColumns(columns);
		return request;
	}
}
