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
package com.b2international.snowowl.core.request;

import java.io.File;
import java.util.List;
import java.util.Set;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.compare.ConceptMapCompareChangeKind;
import com.b2international.snowowl.core.compare.ConceptMapCompareResultItem;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 7.13
 */
public final class ConceptMapCompareDsvExportRequestBuilder extends BaseRequestBuilder<ConceptMapCompareDsvExportRequestBuilder, ServiceProvider, File> {

	private final List<ConceptMapCompareResultItem> items;
	private final String filePath;

	private Character delimiter;
	private Set<ConceptMapCompareChangeKind> changeKids;
	
	public ConceptMapCompareDsvExportRequestBuilder(List<ConceptMapCompareResultItem> items, String filePath) {
		this.filePath = filePath;
		this.items = items;
	}

	public ConceptMapCompareDsvExportRequestBuilder delimiter(final Character delimiter) {
		this.delimiter = delimiter;
		return getSelf();
	}
	
	public ConceptMapCompareDsvExportRequestBuilder changeKids(final Set<ConceptMapCompareChangeKind> changeKids) {
		this.changeKids = changeKids;
		return getSelf();
	}
	
	@Override
	protected Request<ServiceProvider, File> doBuild() {
		final ConceptMapCompareDsvExportRequest req = new ConceptMapCompareDsvExportRequest();
		req.setItems(items);
		req.setFilePath(filePath);
		req.setChangeKinds(changeKids);
		req.setDelimiter(delimiter);
		return req;
	}

}
