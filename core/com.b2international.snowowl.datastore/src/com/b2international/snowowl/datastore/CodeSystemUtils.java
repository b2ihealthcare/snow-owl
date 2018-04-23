/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.google.common.base.Strings.nullToEmpty;

import java.util.Comparator;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.NullBranchPath;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.cdo.ICDOManagedItem;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Contains various continent methods for {@link ICodeSystem} and {@link ICodeSystemVersion}.
 *
 */
public class CodeSystemUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(CodeSystemUtils.class);
	
	private static final LoadingCache<String, ICDOManagedItem<?>> TOOLING_ID_MANAGED_ITEM_CACHE = CacheBuilder.newBuilder().build(new CacheLoader<String, ICDOManagedItem<?>>() {
		@Override public ICDOManagedItem<?> load(final String toolingId) throws Exception {
			Preconditions.checkNotNull(toolingId, "Snow Owl terminology ID argument cannot be null.");
			
			final String terminologyId = CoreTerminologyBroker.getInstance().getAllRegisteredTerminologies().contains(toolingId)
					? toolingId
					: CoreTerminologyBroker.getInstance().getTerminologyIdForTerminologyComponentId(toolingId);
			
			for (final ICDOManagedItem<?> item : getServiceForClass(ICDOConnectionManager.class)) {
				if (terminologyId.equals(item.getSnowOwlTerminologyComponentId())) {
					return item;
				}
			}
			
			throw new NullPointerException("Cannot find managed item for: '" + toolingId + "'.");
		}
	});
	
	/**
	 * Comparator for sorting repository UUIDs in an alphabetic order based on the corresponding tooling feature name. 
	 */
	public static final Comparator<String> TOOLING_FEATURE_NAME_COMPARATOR = new Comparator<String>() {
		public int compare(final String leftUuid, final String rightUuid) {
			final String leftName = nullToEmpty(CodeSystemUtils.getSnowOwlToolingName(leftUuid));
			final String rightName = nullToEmpty(CodeSystemUtils.getSnowOwlToolingName(rightUuid));
			return leftName.compareToIgnoreCase(rightName);
		}
	};
	
	/**
	 * Returns with the {@link ICodeSystemVersion version} matching with the branch path argument from the given iterable of code system versions 
	 * @param branchPath the branch path.
	 * @param versions an iterable of code system version.
	 * @return the matching code system version, or {@code null} if not found.
	 */
	@Nullable public static CodeSystemVersionEntry findMatchingVersion(IBranchPath branchPath, final Iterable<CodeSystemVersionEntry> versions) {
		
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(versions, "Versions argument cannot be null.");
		
		while (NullBranchPath.INSTANCE != branchPath) {
			
			final String versionIdForRepository = branchPath.lastSegment();
			
			for (final CodeSystemVersionEntry candidate : versions) {
				if (candidate.getVersionId().equals(versionIdForRepository)) {
					return candidate;
				}
			}
			
			branchPath = branchPath.getParent();
		}
		
		return null;
	}
	
	/**
	 * Returns with the UUID of the repository associated with the given
	 * application specific tooling ID.
	 * @param snowOwlToolingId the application specific tooling ID. Could be both terminology and terminology component IDs.
	 * @return the UUID of the repository which is associated with a Snow Owl specific tooling feature. 
	 */
	@Nullable public static String getRepositoryUuid(final String snowOwlToolingId) {
		
		Preconditions.checkNotNull(snowOwlToolingId, "Snow Owl tooling ID argument cannot be null.");
		return getAttribute(snowOwlToolingId, new Function<ICDOManagedItem<?>, String>() {
			@Override public String apply(final ICDOManagedItem<?> item) {
				return item.getUuid();
			}
		});
		
	}
	
	/**
	 * Returns with the human readable name of the repository associated with the given
	 * application specific tooling ID.
	 * @param snowOwlToolingId the application specific tooling ID. Could be both terminology and terminology component IDs.
	 * @return the human readable name of the repository which is associated with a Snow Owl specific tooling feature. 
	 */
	@Nullable public static String getRepositoryName(final String snowOwlToolingId) {
		
		Preconditions.checkNotNull(snowOwlToolingId, "Snow Owl tooling ID argument cannot be null.");
		return getAttribute(snowOwlToolingId, new Function<ICDOManagedItem<?>, String>() {
			@Override public String apply(final ICDOManagedItem<?> item) {
				return item.getRepositoryName();
			}
		});
		
	}
	
	/**Sugar for {@link #getRepositoryUuid(String)}.<br>Returns with the repository UUID for the application specific 
	 *component identifier.*/
	@Nullable public static String getRepositoryUuid(final short terminologyComponentId) {
		
		if (CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT == terminologyComponentId) {
			return null;
		}
		
		final String terminologyComponentIdAsString = CoreTerminologyBroker.getInstance().getTerminologyComponentId(terminologyComponentId);
		return CodeSystemUtils.getRepositoryUuid(terminologyComponentIdAsString);
		
	}
	
	/**
	 * Returns with the application specific human readable tooling name associated with a repository given by its unique UUID.
	 * @param repositoryUuid the unique UUID of a repository.
	 * @return the application specific tooling name.
	 */
	public static String getSnowOwlToolingName(final String repositoryUuid) {
		Preconditions.checkNotNull(repositoryUuid, "Repository UUID argument cannot be null.");
		return getConnection(repositoryUuid).getSnowOwlTerminologyComponentName();
	}

	/**
	 * Returns with the application specific tooling ID associated with a repository given by its unique UUID.
	 * @param repositoryUuid the unique UUID of a repository.
	 * @return the application specific tooling ID.
	 */
	public static String getSnowOwlToolingId(final String repositoryUuid) {
		Preconditions.checkNotNull(repositoryUuid, "Repository UUID argument cannot be null.");
		return getConnection(repositoryUuid).getSnowOwlTerminologyComponentId();
	}
	
	/*returns with the connection for the given repository UUID*/
	private static ICDOConnection getConnection(final String repositoryUuid) {
		return getConnectionManager().getByUuid(repositoryUuid);
	}

	/*returns with the connection manager service*/
	private static ICDOConnectionManager getConnectionManager() {
		return ApplicationContext.getInstance().getService(ICDOConnectionManager.class);
	}
	
	/*applies the given function on a CDO manager item which application specific tooling ID equals with the argument.
	 *both terminology and terminology component IDs are allowed as Snow Owl tooling ID.*/
	private static <T> T getAttribute(final String toolingId, final Function<ICDOManagedItem<?>, T> f) {
		
		try {
			
			Preconditions.checkNotNull(toolingId, "Snow Owl terminology ID argument cannot be null.");
			return f.apply(TOOLING_ID_MANAGED_ITEM_CACHE.get(toolingId));
			
		} catch (final ExecutionException e) {
			
			LOGGER.error("Cannot get property on managed item for ID: " + toolingId, e);
			return null;
			
		}
		
	}
	
}