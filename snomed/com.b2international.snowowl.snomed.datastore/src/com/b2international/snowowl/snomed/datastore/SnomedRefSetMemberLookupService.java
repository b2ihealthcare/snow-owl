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
package com.b2international.snowowl.snomed.datastore;

import java.util.List;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.view.CDOQuery;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EPackage;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.AbstractLookupService;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.CDOQueryUtils;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.utils.ComponentUtils2;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * Lookup service implementation for SNOMED CT reference set members.
 * @deprecated - UNSUPPORTED API, only exist for compatibility reasons, use {@link SnomedRequests} where possible
 */
public class SnomedRefSetMemberLookupService extends AbstractLookupService<String, SnomedRefSetMember, CDOView> {

	/**
	 * Table names for all available reference set members, ordered by frequency of use (it is likely that most of the reference
	 * set members in a system will be simple, for example).
	 */
	private static final Iterable<String> TABLE_NAMES = ImmutableList.<String>of(
			"SNOMEDREFSET_SNOMEDREFSETMEMBER",
			"SNOMEDREFSET_SNOMEDATTRIBUTEVALUEREFSETMEMBER",
			"SNOMEDREFSET_SNOMEDSIMPLEMAPREFSETMEMBER",
			"SNOMEDREFSET_SNOMEDLANGUAGEREFSETMEMBER",
			"SNOMEDREFSET_SNOMEDCONCRETEDATATYPEREFSETMEMBER",
			"SNOMEDREFSET_SNOMEDQUERYREFSETMEMBER");

	@Override
	public SnomedRefSetMember getComponent(final String uuid, final CDOView view) {

		final IBranchPath branch = BranchPathUtils.createPath(view);
		final long memberStorageKey = getStorageKey(branch, uuid);
		CDOObject cdoObject = null;

		//second attempt to lookup reference set member in transaction
		notFoundMemberLoop: 

			if (CDOUtils.NO_STORAGE_KEY == memberStorageKey) {

				for (final SnomedRefSetMember newMember : ComponentUtils2.getNewObjects(view, SnomedRefSetMember.class)) {
					if (uuid.equals(newMember.getUuid())) {
						cdoObject = newMember;
						break notFoundMemberLoop;
					}
				}

				//check for deleted reference set members via SQL queries
				for (final String tableName : TABLE_NAMES) {

					final String sqlGetRefSetMember = String.format(SnomedTerminologyQueries.SQL_GET_REFSET_MEMBER_BY_UUID, tableName);
					final CDOQuery query = view.createQuery("sql", sqlGetRefSetMember);
					query.setParameter("uuid", uuid);

					final List<SnomedRefSetMember> result = CDOQueryUtils.getViewResult(query, SnomedRefSetMember.class);

					if (!CompareUtils.isEmpty(result)) {
						return Iterables.getOnlyElement(result);
					}
				}

				return null;
			}

		if (null == cdoObject) {
			cdoObject = CDOUtils.getObjectIfExists(view, memberStorageKey);
		}

		if (null == cdoObject) {
			return null;
		}

		return (SnomedRefSetMember) cdoObject;
	}

	@Override
	public SnomedRefSetMemberIndexEntry getComponent(final IBranchPath branchPath, final String uuid) {
		return SnomedRequests.prepareSearchMember()
				.setLimit(2)
				.filterById(uuid)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.then(new Function<SnomedReferenceSetMembers, SnomedRefSetMemberIndexEntry>() {
					@Override
					public SnomedRefSetMemberIndexEntry apply(SnomedReferenceSetMembers input) {
						final SnomedReferenceSetMember member = Iterables.getOnlyElement(input, null);
						return member == null ? null : SnomedRefSetMemberIndexEntry.builder(member).build();
					}
				})
				.getSync();
	}

	@Override
	public long getStorageKey(final IBranchPath branchPath, final String id) {
		final SnomedRefSetMemberIndexEntry component = getComponent(branchPath, id);
		return component != null ? component.getStorageKey() : CDOUtils.NO_STORAGE_KEY;
	}

	@Override
	protected EPackage getEPackage() {
		return SnomedPackage.eINSTANCE;
	}
}
