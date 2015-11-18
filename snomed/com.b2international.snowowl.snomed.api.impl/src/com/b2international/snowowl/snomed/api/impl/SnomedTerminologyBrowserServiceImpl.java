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

import static com.b2international.commons.pcj.LongSets.toStringSet;

import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.apache.lucene.search.Sort;

import com.b2international.commons.ClassUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.IComponentList;
import com.b2international.snowowl.core.domain.IComponentRef;
import com.b2international.snowowl.core.domain.IStorageRef;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.index.IndexQueryBuilder;
import com.b2international.snowowl.datastore.server.domain.InternalComponentRef;
import com.b2international.snowowl.datastore.server.domain.InternalStorageRef;
import com.b2international.snowowl.snomed.api.ISnomedConceptService;
import com.b2international.snowowl.snomed.api.ISnomedTerminologyBrowserService;
import com.b2international.snowowl.snomed.api.impl.domain.SnomedConceptList;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.SnomedConceptIndexQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedConceptConverter;
import com.b2international.snowowl.snomed.datastore.services.SnomedBranchRefSetMembershipLookupService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 */
public class SnomedTerminologyBrowserServiceImpl implements ISnomedTerminologyBrowserService {

	private ISnomedConceptService conceptService;

	@Resource
	public void setConceptService(final ISnomedConceptService conceptService) {
		this.conceptService = conceptService;
	}

	private static SnomedIndexService getIndexService() {
		return ApplicationContext.getServiceForClass(SnomedIndexService.class);
	}

	private static SnomedTerminologyBrowser getTerminologyBrowser() {
		return ApplicationContext.getServiceForClass(SnomedTerminologyBrowser.class);
	}

	private static final class SortedTerminologyConceptAdapter extends SnomedConceptIndexQueryAdapter {

		private static final long serialVersionUID = 1L;

		public static final int SEARCH_ROOTS = 1 << 7;
		public static final int SEARCH_PARENT = 1 << 8;
		public static final int SEARCH_ANCESTOR = 1 << 9;

		public SortedTerminologyConceptAdapter(final String conceptId, final int searchFlags, final String[] conceptIds) {
			super(conceptId, searchFlags | SEARCH_ACTIVE_CONCEPTS, conceptIds);
		}

		@Override
		protected IndexQueryBuilder createIndexQueryBuilder() {
			return super.createIndexQueryBuilder()
					.requireIf(allFlagsSet(SEARCH_ROOTS), SnomedMappings.newQuery().parent(SnomedMappings.ROOT_ID).matchAll())
					.require(new IndexQueryBuilder()
						.matchIf(allFlagsSet(SEARCH_PARENT), SnomedMappings.newQuery().parent(searchString).matchAll())
						.matchIf(allFlagsSet(SEARCH_ANCESTOR), SnomedMappings.newQuery().ancestor(searchString).matchAll())
					);
		}

		@Override
		public Sort createSort() {
			return SnomedMappings.id().createSort();
		}
	}

	@Override
	public List<ISnomedConcept> getRootNodes(final IStorageRef ref) {
		final InternalStorageRef internalRef = ClassUtils.checkAndCast(ref, InternalStorageRef.class);
		internalRef.checkStorageExists();

		final IBranchPath branch = internalRef.getBranch().branchPath();
		final SortedTerminologyConceptAdapter queryAdapter = new SortedTerminologyConceptAdapter(null, SortedTerminologyConceptAdapter.SEARCH_ROOTS, null);
		final List<SnomedConceptIndexEntry> entries = getIndexService().search(branch, queryAdapter);
		return convertEntries(branch, entries);
	}

	@Override
	public ISnomedConcept getNode(final IComponentRef nodeRef) {
		return conceptService.read(nodeRef);
	}

	@Override
	public IComponentList<ISnomedConcept> getDescendants(final IComponentRef nodeRef, final boolean direct, final int offset, final int limit) {

		final InternalComponentRef internalRef = ClassUtils.checkAndCast(nodeRef, InternalComponentRef.class);
		internalRef.checkStorageExists();

		final IBranchPath branch = internalRef.getBranch().branchPath();
		final String componentId = nodeRef.getComponentId();
		checkConceptExists(branch, componentId);

		final int flags = direct ? SortedTerminologyConceptAdapter.SEARCH_PARENT : SortedTerminologyConceptAdapter.SEARCH_PARENT | SortedTerminologyConceptAdapter.SEARCH_ANCESTOR;
		final SortedTerminologyConceptAdapter queryAdapter = new SortedTerminologyConceptAdapter(componentId, flags, null);
		return toComponentList(offset, limit, branch, queryAdapter);
	}

	@Override
	public IComponentList<ISnomedConcept> getAncestors(final IComponentRef nodeRef, final boolean direct, final int offset, final int limit) {

		final InternalComponentRef internalRef = ClassUtils.checkAndCast(nodeRef, InternalComponentRef.class);
		internalRef.checkStorageExists();

		final IBranchPath branch = internalRef.getBranch().branchPath();
		final String componentId = nodeRef.getComponentId();
		checkConceptExists(branch, componentId);

		final Collection<String> ancestorIds;

		if (direct) {
			ancestorIds = getTerminologyBrowser().getSuperTypeIds(branch, internalRef.getComponentId()); 
		} else {
			ancestorIds = toStringSet(getTerminologyBrowser().getAllSuperTypeIds(branch, Long.valueOf(internalRef.getComponentId())));
		}

		if (ancestorIds.isEmpty()) {
			return emptyComponentList();
		}

		final String[] ancestorIdArray = ancestorIds.toArray(new String[ancestorIds.size()]);
		final SortedTerminologyConceptAdapter queryAdapter = new SortedTerminologyConceptAdapter(null, SortedTerminologyConceptAdapter.SEARCH_ACTIVE_CONCEPTS, ancestorIdArray);
		return toComponentList(offset, limit, branch, queryAdapter);
	}

	private void checkConceptExists(final IBranchPath branchPath, final String componentId) {
		if (!getTerminologyBrowser().exists(branchPath, componentId)) {
			throw new ComponentNotFoundException(ComponentCategory.CONCEPT, componentId);
		}
	}

	private IComponentList<ISnomedConcept> toComponentList(final int offset, final int limit, final IBranchPath branchPath, final SortedTerminologyConceptAdapter queryAdapter) {
		final int totalMembers = getIndexService().getHitCount(branchPath, queryAdapter);
		final List<SnomedConceptIndexEntry> entries = getIndexService().search(branchPath, queryAdapter, offset, limit);
		final List<ISnomedConcept> concepts = convertEntries(branchPath, entries);

		final SnomedConceptList result = new SnomedConceptList();
		result.setTotalMembers(totalMembers);
		result.setMembers(concepts);
		return result;
	}

	private List<ISnomedConcept> convertEntries(final IBranchPath branchPath, final List<SnomedConceptIndexEntry> entries) {
		final SnomedConceptConverter converter = new SnomedConceptConverter(new SnomedBranchRefSetMembershipLookupService(branchPath));
		return ImmutableList.copyOf(Lists.transform(entries, converter));
	}

	private IComponentList<ISnomedConcept> emptyComponentList() {
		final SnomedConceptList result = new SnomedConceptList();
		result.setMembers(ImmutableList.<ISnomedConcept>of());
		return result;
	}
}