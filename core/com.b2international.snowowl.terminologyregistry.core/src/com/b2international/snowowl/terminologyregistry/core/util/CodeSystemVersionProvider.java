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
package com.b2international.snowowl.terminologyregistry.core.util;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.BranchPathUtils.isMain;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EPackage;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.AbstractLookupService;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.BranchPointUtils;
import com.b2international.snowowl.datastore.CodeSystemService;
import com.b2international.snowowl.datastore.CodeSystemUtils;
import com.b2international.snowowl.datastore.ICodeSystemVersion;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.cdo.CDOViewFunction;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

/**
 * Component container code system version provider to pin down the version
 * string retrieving logic.
 * 
 * @since 3.1.0
 */
public abstract class CodeSystemVersionProvider implements ICodeSystemVersionProvider {

	@Override
	public String getVersion(String terminologyComponentId, String componentId, IBranchPath branchPath) {
		List<ICodeSystemVersion> allTagsWithHead = getAllTagsWithHead(terminologyComponentId);

		if (existsOnlyOnMain(allTagsWithHead)) {
			return ICodeSystemVersion.UNVERSIONED;
		} else {
			String lastVersionId = getLastVersionId(branchPath, allTagsWithHead);
			CDOObject lastVersion = tryLoadSpecificVersion(componentId, branchPath, lastVersionId);
			if (isCreatedAfterLastVersion(lastVersion) || hasChangedSinceLastVersion(componentId, branchPath, lastVersion)) {
				return ICodeSystemVersion.UNVERSIONED;
			} else {
				return lastVersionId;
			}
		}
	}
	
	@Override
	public Map<String, String> getVersions(final String terminologyComponentId, final Collection<String> componentIds,
			final IBranchPath branchPath) {
		final List<ICodeSystemVersion> allTagsWithHead = getAllTagsWithHead(terminologyComponentId);
		final Map<String, String> result = Maps.newHashMap();
		
		if (existsOnlyOnMain(allTagsWithHead)) {
			for (final String componentId : componentIds) {
				result.put(componentId, ICodeSystemVersion.UNVERSIONED);
			}
		} else {
			final String lastVersionId = getLastVersionId(branchPath, allTagsWithHead);
			for (final String componentId : componentIds) {
				final CDOObject lastVersion = tryLoadSpecificVersion(componentId, branchPath, lastVersionId);
				if (isCreatedAfterLastVersion(lastVersion) || hasChangedSinceLastVersion(componentId, branchPath, lastVersion)) {
					result.put(componentId, ICodeSystemVersion.UNVERSIONED);
				} else {
					result.put(componentId, lastVersionId);
				}
			}
		}
		
		return result;
	}

	private List<ICodeSystemVersion> getAllTagsWithHead(String terminologyComponentId) {
		String repositoryUuid = CodeSystemUtils.getRepositoryUuid(terminologyComponentId);
		return getServiceForClass(CodeSystemService.class).getAllTagsWithHead(repositoryUuid);
	}

	private String getLastVersionId(IBranchPath branchPath, List<ICodeSystemVersion> allTagsWithHead) {
		Preconditions.checkArgument(allTagsWithHead.size() >= 2, "Not available any previous version");
		if (isMain(branchPath)) {
			// ordered list: the second version is the latest
			ICodeSystemVersion lastVersion = allTagsWithHead.get(1);
			return lastVersion.getVersionId();
		} else {
			return CodeSystemUtils.findMatchingVersion(branchPath, allTagsWithHead).getVersionId();
		}
	}

	/* Returns true if the component exists only on MAIN and does not have any other version. */
	private boolean existsOnlyOnMain(List<ICodeSystemVersion> allTagsWithHead) {
		if (allTagsWithHead.size() == 1) {
			return true;
		}
		return false;
	}

	/* Returns true if the component was not existing in it's container code system's last version. */
	private boolean isCreatedAfterLastVersion(CDOObject lastVersion) {
		return lastVersion == null;
	}

	/* Returns true if the component was already existing in it's container code system's last version AND has been changed since. */
	private boolean hasChangedSinceLastVersion(final String componentId, final IBranchPath branchPath, CDOObject lastVersion){
		Preconditions.checkArgument(lastVersion!=null, "CDO Object representing the last version must not be null");

		CDOObject headVersion = CDOUtils.apply(new CDOViewFunction<CDOObject, CDOView>(getEPackage(), branchPath) {
			@Override
			protected CDOObject apply(CDOView view) {
				return CDOUtils.getObjectIfExists(view, getCdoId(componentId, branchPath));
			}
		});

		return isHeadChanged(headVersion, lastVersion);
	}

	/**
	 * Returns {@code true} if head version has any change since the specified version
	 * 
	 * @param headVersion
	 * @param specifiedVersion
	 * @return
	 */
	protected boolean isHeadChanged(CDOObject headVersion, CDOObject specifiedVersion) {
		return (getTimeStamp(headVersion) > getTimeStamp(specifiedVersion));
	}

	protected abstract EPackage getEPackage();

	protected final long getTimeStamp(CDOObject cdoObject){
		return cdoObject.cdoRevision().getTimeStamp();
	}

	protected CDOID getCdoId(String componentId, IBranchPath branchPath) {
		AbstractLookupService<String, ?, CDOView> lookupService = getLookupService();
		long storageKey = lookupService.getStorageKey(branchPath, componentId);
		return CDOIDUtil.createLong(storageKey);
	}

	protected abstract AbstractLookupService<String, ?, CDOView> getLookupService();

	private CDOObject tryLoadSpecificVersion(String componentId, IBranchPath branchPath, String version) {
		CDOID cdoId = getCdoId(componentId, branchPath);
		IBranchPath versionBranch = BranchPathUtils.createPath(branchPath, version);
		ICDOConnection connection = ApplicationContext.getInstance().getService(ICDOConnectionManager.class).get(cdoId);
		CDOBranch oldestBranch = connection.getOldestBranch(versionBranch);
		long timeStamp = oldestBranch.getBase().getTimeStamp();
		return getObjectIfExists(cdoId, versionBranch, timeStamp);
	}

	private CDOObject getObjectIfExists(final CDOID cdoId, IBranchPath branchPath, long timeStamp) {
		ICDOConnection connection = ApplicationContext.getInstance().getService(ICDOConnectionManager.class).get(cdoId);
		return CDOUtils.apply(new CDOViewFunction<CDOObject, CDOView>(BranchPointUtils.create(connection, branchPath, timeStamp)) {
			@Override
			protected CDOObject apply(CDOView view) {
				return CDOUtils.getObjectIfExists(view, cdoId);
			}
		});
	}
}
