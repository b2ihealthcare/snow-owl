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
package com.b2international.snowowl.datastore.server.snomed.index;

import org.apache.lucene.search.Query;

import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.core.api.index.IIndexService;
import com.b2international.snowowl.datastore.server.index.AbstractIndexBrowser;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.google.common.base.Preconditions;

/**
 * SNOMED&nbsp;CT specific {@link AbstractIndexBrowser index browser} implementation.
 *
 */
public abstract class AbstractSnomedIndexBrowser<E extends IIndexEntry> extends AbstractIndexBrowser<E> {

	protected AbstractSnomedIndexBrowser(final IIndexService<?> service) {
		super(Preconditions.checkNotNull(service, "Index service argument cannot be null."));
	}
	
	@Override
	protected Query getComponentIdQuery(String componentId) {
		return SnomedMappings.newQuery().id(componentId).matchAll();
	}
}
