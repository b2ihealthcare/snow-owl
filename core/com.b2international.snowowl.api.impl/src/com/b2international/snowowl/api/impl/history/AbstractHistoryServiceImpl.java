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
package com.b2international.snowowl.api.impl.history;

import java.util.Collection;
import java.util.List;

import com.b2international.commons.ChangeKind;
import com.b2international.snowowl.api.impl.history.domain.HistoryInfo;
import com.b2international.snowowl.api.impl.history.domain.HistoryInfoDetails;
import com.b2international.snowowl.api.impl.history.domain.HistoryVersion;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.core.history.IHistoryService;
import com.b2international.snowowl.core.history.domain.ChangeType;
import com.b2international.snowowl.core.history.domain.IHistoryInfo;
import com.b2international.snowowl.core.history.domain.IHistoryInfoDetails;
import com.b2international.snowowl.core.history.domain.IHistoryVersion;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.CDOCommitInfoUtils;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.history.HistoryInfoConfiguration;
import com.b2international.snowowl.datastore.history.HistoryInfoConfigurationImpl;
import com.b2international.snowowl.datastore.history.HistoryService;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 */
public abstract class AbstractHistoryServiceImpl implements IHistoryService {

	private static final Function<com.b2international.snowowl.core.api.IHistoryInfoDetails, IHistoryInfoDetails> DETAIL_CONVERTER = 
			new Function<com.b2international.snowowl.core.api.IHistoryInfoDetails, IHistoryInfoDetails>() {

		@Override
		public IHistoryInfoDetails apply(final com.b2international.snowowl.core.api.IHistoryInfoDetails input) {
			final ChangeType convertedChangeType = convertChangeType(input.getChangeType());
			final IHistoryInfoDetails convertedDetails = new HistoryInfoDetails(input.getComponentType(), input.getDescription(), convertedChangeType);
			return convertedDetails;
		}

		private ChangeType convertChangeType(final ChangeKind changeKind) {
			switch (changeKind) {
				case ADDED:
					return ChangeType.NEW;
				case DELETED:
					return ChangeType.DETACHED;
				case UPDATED:
					return ChangeType.CHANGED;
				case UNCHANGED: // This should not be returned when asking for component history, fall through to default. 
				default:
					throw new IllegalStateException("Unexpected change kind: " + changeKind);
			}
		}
	};

	private static final Function<com.b2international.snowowl.core.api.IHistoryInfo, IHistoryInfo> CONVERTER = 
			new Function<com.b2international.snowowl.core.api.IHistoryInfo, IHistoryInfo>() {

		@Override
		public IHistoryInfo apply(final com.b2international.snowowl.core.api.IHistoryInfo input) {
			final List<com.b2international.snowowl.core.api.IHistoryInfoDetails> inputDetails = input.getDetails();
			final List<IHistoryInfoDetails> convertedDetails = ImmutableList.copyOf(Lists.transform(inputDetails, DETAIL_CONVERTER));
			final IHistoryVersion convertedVersion = new HistoryVersion(input.getVersion().getMajorVersion(), input.getVersion().getMinorVersion());
			final String trimmedComments = CDOCommitInfoUtils.removeUuidPrefix(input.getComments());
			final IHistoryInfo convertedInfo = new HistoryInfo(convertedVersion, input.getTimeStamp(), input.getAuthor(), trimmedComments, convertedDetails);
			return convertedInfo;
		}
	};

	private static HistoryService getHistoryService() {
		return ApplicationContext.getServiceForClass(HistoryService.class);
	}

	protected final String handledRepositoryUuid;
	protected final ComponentCategory handledCategory;
	private final short terminologyComponentId;

	protected AbstractHistoryServiceImpl(final String handledRepositoryUuid, final ComponentCategory handledCategory, final short terminologyComponentId) {
		this.handledRepositoryUuid = handledRepositoryUuid;
		this.handledCategory = handledCategory;
		this.terminologyComponentId = terminologyComponentId;
	}

	@Override
	public List<IHistoryInfo> getHistory(final String branchPath, final String componentId) {
		final IBranchPath branch = BranchPathUtils.createPath(branchPath);
		final long storageKey = getStorageKey(branch, componentId);
		if (!CDOIDUtils.checkId(storageKey)) {
			throw new ComponentNotFoundException(handledCategory, componentId);
		}

		final HistoryInfoConfiguration configuration = HistoryInfoConfigurationImpl.create(branch, storageKey, ComponentIdentifier.of(terminologyComponentId, componentId));
		final Collection<com.b2international.snowowl.core.api.IHistoryInfo> sourceHistoryInfos = getHistoryService().getHistory(configuration);			
		final Collection<IHistoryInfo> targetHistoryInfos = Collections2.transform(sourceHistoryInfos, CONVERTER);

		return ImmutableList.copyOf(targetHistoryInfos);
	}

	protected abstract long getStorageKey(IBranchPath branchPath, final String componentId);
	
}