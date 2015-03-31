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
package com.b2international.snowowl.snomed.api.impl;

import java.util.List;

import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;

import com.b2international.commons.ClassUtils;
import com.b2international.snowowl.api.domain.IComponentList;
import com.b2international.snowowl.api.domain.IComponentRef;
import com.b2international.snowowl.api.exception.ComponentNotFoundException;
import com.b2international.snowowl.api.impl.domain.InternalComponentRef;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.CommonIndexConstants;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.api.ISnomedStatementBrowserService;
import com.b2international.snowowl.snomed.api.domain.ISnomedRelationship;
import com.b2international.snowowl.snomed.api.impl.domain.SnomedRelationshipList;
import com.b2international.snowowl.snomed.datastore.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.SnomedRelationshipIndexQueryAdapter;
import com.b2international.snowowl.snomed.datastore.services.SnomedBranchRefSetMembershipLookupService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class SnomedStatementBrowserServiceImpl implements ISnomedStatementBrowserService {

	private static final class SortedRelationshipAdapter extends SnomedRelationshipIndexQueryAdapter {

		private static final long serialVersionUID = 1L;

		private SortedRelationshipAdapter(final String conceptId, final int searchFlags) {
			super(conceptId, searchFlags);
		}

		@Override
		protected Sort createSort() {
			return new Sort(new SortField(CommonIndexConstants.COMPONENT_ID, Type.LONG));
		}
	}

	private static SnomedIndexService getIndexService() {
		return ApplicationContext.getServiceForClass(SnomedIndexService.class);
	}

	@Override
	public IComponentList<ISnomedRelationship> getInboundEdges(final IComponentRef nodeRef, final int offset, final int limit) {
		final InternalComponentRef internalRef = ClassUtils.checkAndCast(nodeRef, InternalComponentRef.class);
		final SnomedRelationshipIndexQueryAdapter queryAdapter = new SortedRelationshipAdapter(nodeRef.getComponentId(), SnomedRelationshipIndexQueryAdapter.SEARCH_DESTINATION_ID);
		return toEdgeList(internalRef, offset, limit, queryAdapter);
	}

	@Override
	public IComponentList<ISnomedRelationship> getOutboundEdges(final IComponentRef nodeRef, final int offset, final int limit) {
		final InternalComponentRef internalRef = ClassUtils.checkAndCast(nodeRef, InternalComponentRef.class);
		final SnomedRelationshipIndexQueryAdapter queryAdapter = new SortedRelationshipAdapter(nodeRef.getComponentId(), SnomedRelationshipIndexQueryAdapter.SEARCH_SOURCE_ID);
		return toEdgeList(internalRef, offset, limit, queryAdapter);
	}

	private IComponentList<ISnomedRelationship> toEdgeList(final InternalComponentRef internalRef, final int offset, final int limit, final SnomedRelationshipIndexQueryAdapter queryAdapter) {
		
		try {
			Long.parseLong(internalRef.getComponentId());
		} catch (NumberFormatException e) {
			throw new ComponentNotFoundException(ComponentCategory.CONCEPT, internalRef.getComponentId());
		}
		
		final SnomedRelationshipList result = new SnomedRelationshipList();
		final IBranchPath branchPath = internalRef.getBranchPath();
		result.setTotalMembers(getIndexService().getHitCount(branchPath, queryAdapter));

		final List<SnomedRelationshipIndexEntry> indexEntries = getIndexService().search(branchPath, queryAdapter, offset, limit);
		final List<ISnomedRelationship> relationships = Lists.transform(indexEntries, createConverter(branchPath));
		result.setMembers(ImmutableList.copyOf(relationships));

		return result;
	}

	private SnomedRelationshipConverter createConverter(final IBranchPath branchPath) {
		return new SnomedRelationshipConverter(new SnomedBranchRefSetMembershipLookupService(branchPath));
	}
}
