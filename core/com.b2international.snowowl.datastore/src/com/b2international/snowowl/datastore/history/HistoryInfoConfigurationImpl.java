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
package com.b2international.snowowl.datastore.history;

import static com.b2international.commons.Pair.of;
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.cdo.CDOIDUtils.checkId;
import static com.b2international.snowowl.datastore.cdo.CDOUtils.apply;
import static com.b2international.snowowl.datastore.cdo.CDOUtils.getObjectIfExists;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.commons.Pair;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.cdo.CDOViewFunction;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.tasks.TaskManager;

import bak.pcj.map.LongKeyLongMap;

/**
 * {@link HistoryInfoConfiguration} implementation. 
 */
public class HistoryInfoConfigurationImpl implements HistoryInfoConfiguration, Serializable {

	private static final long serialVersionUID = 737924958133045175L;
	
	private final long storageKey;
	private final String terminologyComponentId;
	private final IBranchPath branchPath;
	private final String componentId;
	private final StorageKeyCache storageKeyCache;
	
	public static HistoryInfoConfiguration create(final long storageKey) {
		return create(storageKey, getActiveBranchPath(storageKey));
	}
	
	public static HistoryInfoConfiguration create(final long storageKey, final IBranchPath branchPath) {
		checkArgument(checkId(storageKey), "Invalid storage key of: " + storageKey);
		checkNotNull(branchPath, "branchPath");
		
		final ICDOConnection connection = getConnection(storageKey);
		final Pair<String, String> idPair = apply(new CDOViewFunction<Pair<String, String>, CDOView>(connection, branchPath) {
			protected Pair<String, String> apply(final CDOView view) {
				final CDOObject object = getObjectIfExists(view, storageKey);
				return null == object ? null : of(getComponentId(object), getTerminologyComponentId(object));
			}
		});
		
		if (idPair == null) {
			return NullHistoryInfoConfiguration.INSTANCE;
		} else {
			return new HistoryInfoConfigurationImpl(storageKey, idPair.getA(), idPair.getB(), branchPath);
		}
	}

	private HistoryInfoConfigurationImpl(final long storageKey, final String componentId, final String terminologyComponentId, final IBranchPath branchPath, final StorageKeyCache cache) {
		checkArgument(checkId(storageKey), "Invalid storage key of: " + storageKey);
		this.storageKey = storageKey;
		this.componentId = checkNotNull(componentId, "componentId");
		this.terminologyComponentId = checkNotNull(terminologyComponentId, "terminologyComponentId");
		this.branchPath = checkNotNull(branchPath, "branchPath");
		this.storageKeyCache = cache;
	}
	
	public HistoryInfoConfigurationImpl(final long storageKey, final String componentId, final String terminologyComponentId, final IBranchPath branchPath) {
		this(storageKey, componentId, terminologyComponentId, branchPath, StorageKeyCache.NOOP);
	}
	
	public HistoryInfoConfigurationImpl(final long storageKey, final String componentId, final String terminologyComponentId, final IBranchPath branchPath, LongKeyLongMap conceptIdToStorageKeyMap) {
		this(storageKey, componentId, terminologyComponentId, branchPath, new StorageKeyCacheImp(conceptIdToStorageKeyMap));
	}

	@Override
	public long getStorageKey() {
		return storageKey;
	}

	@Override
	public String getComponentId() {
		return componentId;
	}
	
	@Override
	public String getTerminologyComponentId() {
		return terminologyComponentId;
	}
	
	@Override
	public IBranchPath getBranchPath() {
		return branchPath;
	}
	
	private static IBranchPath getActiveBranchPath(final long storageKey) {
		final String repositoryUuid = getRepositoryUuid(storageKey);
		return getServiceForClass(TaskManager.class).getActiveBranch(repositoryUuid);
	}

	private static String getRepositoryUuid(final long storageKey) {
		return getConnection(storageKey).getUuid();
	}

	private static ICDOConnection getConnection(final long storageKey) {
		return getServiceForClass(ICDOConnectionManager.class).get(storageKey);
	}
	
	private static String getTerminologyComponentId(final CDOObject object) {
		return CoreTerminologyBroker.getInstance().getTerminologyComponentId(object);
	}
	
	private static String getComponentId(final CDOObject object) {
		return String.valueOf(CoreTerminologyBroker.getInstance().adapt(object).getId());
	}

	@Override
	public StorageKeyCache getStorageKeyCache() {
		return storageKeyCache;
	}
}