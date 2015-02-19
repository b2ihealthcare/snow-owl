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
package com.b2international.snowowl.snomed.datastore;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.view.CDOQuery;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.ComponentIdAndLabel;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.AbstractLookupService;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.CDOQueryUtils;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.utils.ComponentUtils2;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMembershipIndexQueryAdapter;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * Lookup service for the SNOMED CT reference set members.
 * 
 */
public class SnomedRefSetMemberLookupService extends AbstractLookupService<String, SnomedRefSetMember, CDOView> {

	/**
	 * Table names for all supported reference set members. Consider order of the collection in respect of
	 * performance before modifying it.
	 */
	private static final Iterable<String> TABLE_NAMES = ImmutableList.<String>of(
			"SNOMEDREFSET_SNOMEDREFSETMEMBER",
			"SNOMEDREFSET_SNOMEDATTRIBUTEVALUEREFSETMEMBER",
			"SNOMEDREFSET_SNOMEDSIMPLEMAPREFSETMEMBER",
			"SNOMEDREFSET_SNOMEDLANGUAGEREFSETMEMBER",
			"SNOMEDREFSET_SNOMEDCONCRETEDATATYPEREFSETMEMBER",
			"SNOMEDREFSET_SNOMEDQUERYREFSETMEMBER");

	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedRefSetMemberLookupService.class);

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.ILookupService#getComponent(java.io.Serializable, java.lang.Object)
	 */
	@Override
	public SnomedRefSetMember getComponent(final String uuid, final CDOView view) {

		final long memberStorageKey = getRefSetBrowser().getMemberStorageKey(BranchPathUtils.createPath(view), uuid);
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

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.ILookupService#getComponent(com.b2international.snowowl.core.api.IBranchPath, java.io.Serializable)
	 */
	@Override
	public SnomedRefSetMemberIndexEntry getComponent(final IBranchPath branchPath, final String uuid) {
		checkNotNull(branchPath, "The branch path cannot be null.");
		checkNotNull(uuid, "The identifier of the SNOMED CT reference set member cannot be null.");

		final SnomedIndexService service = getIndexService();

		if (null == service) {
			LOGGER.warn("Lucene index lookup service for SNOMED CT reference set members was not available.");
			return null;
		}

		final List<SnomedRefSetMemberIndexEntry> result = service.search(branchPath, SnomedRefSetMembershipIndexQueryAdapter.createFindByUuidQuery(uuid), 1);

		if (null == result) {
			LOGGER.warn("Lucene index lookup service for SNOMED CT reference set members returned with null.");
			return null;
		}

		return Iterables.getOnlyElement(result, null);
	}

	/* 
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.ILookupService#getStorageKey(com.b2international.snowowl.core.api.IBranchPath, java.io.Serializable)
	 */
	@Override
	public long getStorageKey(final IBranchPath branchPath, final String id) {
		return getRefSetBrowser().getMemberStorageKey(branchPath, id);
	}

	/* 
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.ILookupService#getComponentIdAndLabel(com.b2international.snowowl.core.api.IBranchPath, long)
	 */
	@Override
	public ComponentIdAndLabel getComponentIdAndLabel(final IBranchPath branchPath, final long storageKey) {
		return getTerminologyBrowser().getComponentIdAndLabel(branchPath, storageKey);
	}

	private SnomedIndexService getIndexService() {
		return ApplicationContext.getInstance().getService(SnomedIndexService.class);
	}

	private SnomedRefSetBrowser getRefSetBrowser() {
		return ApplicationContext.getInstance().getService(SnomedRefSetBrowser.class);
	}

	private SnomedTerminologyBrowser getTerminologyBrowser() {
		return ApplicationContext.getInstance().getService(SnomedTerminologyBrowser.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.AbstractLookupService#getEPackage()
	 */
	@Override
	protected EPackage getEPackage() {
		return SnomedPackage.eINSTANCE;
	}
}