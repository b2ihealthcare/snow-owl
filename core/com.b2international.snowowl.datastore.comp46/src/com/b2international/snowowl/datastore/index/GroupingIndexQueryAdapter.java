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
package com.b2international.snowowl.datastore.index;

import static com.b2international.commons.ClassUtils.checkAndCast;

import java.util.Set;

import org.apache.lucene.util.BytesRef;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.IGroupingIndexQueryAdapter;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.core.api.index.IIndexService;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.google.common.base.Function;
import com.google.common.collect.Multimap;

/**
 */
public abstract class GroupingIndexQueryAdapter<E extends IIndexEntry, G> extends QueryDslIndexQueryAdapter<E> implements IGroupingIndexQueryAdapter<E, G> {

	private static final long serialVersionUID = 1L;

	private final String groupField;
	private final Set<String> valueFields;
	
	public GroupingIndexQueryAdapter(String searchString, int searchFlags, String[] componentIds, String groupField) {
		this(searchString, searchFlags, componentIds, groupField, Mappings.fieldsToLoad().id().build());
	}
	public GroupingIndexQueryAdapter(String searchString, int searchFlags, String[] componentIds, String groupField, Set<String> valueFields) {
		super(searchString, searchFlags, componentIds);
		this.groupField = groupField;
		this.valueFields = valueFields;
	}

	protected abstract Function<BytesRef, G> createGroupFieldConverter();
	
	@Override
	public Multimap<G, String> searchUnsortedIdGroups(final IIndexService<? super E> indexService, final IBranchPath branchPath) {
		final AbstractIndexService<?> abstractIndexService = checkAndCast(indexService, AbstractIndexService.class);
		return abstractIndexService.searchUnorderedIdGroups(branchPath, createQuery(), createFilter(), groupField, valueFields, createGroupFieldConverter());
	}
}