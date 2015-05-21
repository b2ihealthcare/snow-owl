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
package com.b2international.snowowl.datastore.server.session;

import static com.google.common.collect.Maps.newHashMap;

import java.text.MessageFormat;
import java.util.Map;

import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.osgi.service.prefs.PreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.UserBranchPathMap;
import com.b2international.snowowl.datastore.cdo.ICDORepository;
import com.b2international.snowowl.datastore.cdo.ICDORepositoryManager;
import com.b2international.snowowl.datastore.session.IApplicationSessionManager;
import com.b2international.snowowl.rpc.RpcSession;

/**
 * 
 */
public class VersionProcessor extends SessionEventListener {

	public static final String KEY_USER_BRANCH_PATH_MAP = "lastUsedVersion";

	private static final Logger LOGGER = LoggerFactory.getLogger(VersionProcessor.class);
	
	private final PreferencesService preferencesService;
	
	public VersionProcessor() {
		this(ApplicationContext.getInstance().getService(PreferencesService.class));
	}

	public VersionProcessor(final PreferencesService preferencesService) {
		this.preferencesService = preferencesService;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.server.session.SessionEventListener#onLogin(com.b2international.snowowl.datastore.session.IApplicationSessionManager, 
	 * com.b2international.snowowl.rpc.RpcSession)
	 */
	@Override
	protected void onLogin(final IApplicationSessionManager manager, final RpcSession session) {

		final String userId = (String) session.get(IApplicationSessionManager.KEY_USER_ID);
		final Preferences userPreferences = preferencesService.getUserPreferences(userId);
		final UserBranchPathMap userBranchPathMap = loadLastUsedVersion(userPreferences, userId);
		
		session.put(KEY_USER_BRANCH_PATH_MAP, userBranchPathMap);
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.server.session.SessionEventListener#onLogout(com.b2international.snowowl.datastore.session.IApplicationSessionManager, 
	 * com.b2international.snowowl.rpc.RpcSession)
	 */
	@Override
	protected void onLogout(final IApplicationSessionManager manager, final RpcSession session) {

		final String userId = (String) session.get(IApplicationSessionManager.KEY_USER_ID);
		final Preferences userPreferences = preferencesService.getUserPreferences(userId);
		final UserBranchPathMap userBranchPathMap = (UserBranchPathMap) session.get(KEY_USER_BRANCH_PATH_MAP);
		
		saveLastUsedVersion(userPreferences, userBranchPathMap, userId);
	}

	private UserBranchPathMap loadLastUsedVersion(final Preferences userPreferences, final String userId) {
		
		final Preferences userBranchPathMapNode = userPreferences.node(KEY_USER_BRANCH_PATH_MAP);
		final Map<String, IBranchPath> sourceMap = newHashMap();
		
		try {
			
			if (userBranchPathMapNode.keys().length < 1) {
				return new UserBranchPathMap();	
			}
			
			for (final String repositoryId : userBranchPathMapNode.keys()) {
			
				final String repositoryPath = userBranchPathMapNode.get(repositoryId, IBranchPath.MAIN_BRANCH);
				final IBranchPath repositoryBranchPath = BranchPathUtils.createPath(repositoryPath);
				sourceMap.put(repositoryPath, repositoryBranchPath);
			}
			
		} catch (final BackingStoreException e) {
			LOGGER.error(MessageFormat.format("Couldn''t load preferences for user {0}, defaulting to MAIN on all repositories.", userId), e);
			return new UserBranchPathMap();
		}
		
		return new UserBranchPathMap(sourceMap);
	}

	private void saveLastUsedVersion(final Preferences userPreferences, final IBranchPathMap userBranchPathMap, final String userId) {
		
		final Preferences userBranchPathMapNode = userPreferences.node(KEY_USER_BRANCH_PATH_MAP);
		
		for (final ICDORepository repository : getRepositoryManager()) {
			final String uuid = repository.getUuid();
			userBranchPathMapNode.put(uuid, userBranchPathMap.getBranchPath(uuid).getPath());
		}
		
		try {
			userPreferences.flush();
		} catch (final BackingStoreException e) {
			LOGGER.error(MessageFormat.format("Couldn''t save preferences for user {0}.", userId), e);
		}
	}

	private ICDORepositoryManager getRepositoryManager() {
		return ApplicationContext.getInstance().getService(ICDORepositoryManager.class);
	}
}