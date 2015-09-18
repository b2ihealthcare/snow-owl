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
package com.b2international.snowowl.datastore.server.snomed;

import static com.b2international.commons.StringUtils.isEmpty;
import static com.b2international.snowowl.datastore.BranchPathUtils.createPath;
import static java.util.Collections.emptyList;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.slf4j.Logger;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.IPostStoreUpdateListener2;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedTaxonomy;
import com.b2international.snowowl.snomed.datastore.SnomedTaxonomyService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * Service side {@link SnomedTaxonomyService} implementation. This service implementation
 * uses caching mechanism for performance improvement considerations.
 *
 */
public class SnomedTaxonomyServiceImpl implements SnomedTaxonomyService, IPostStoreUpdateListener2 {

	private static final Logger LOGGER = getLogger(SnomedTaxonomyServiceImpl.class);
	
	private static final long ONE = 1L;
	private static final long MAX_BRANCH_CACHING_SUPPORT = 10L;
	
	private final LoadingCache<IBranchPath, SnomedTaxonomy> taxonomyCache = CacheBuilder.newBuilder()
			.expireAfterAccess(ONE, TimeUnit.MINUTES)
			.maximumSize(MAX_BRANCH_CACHING_SUPPORT)
			.removalListener(new RemovalListener<IBranchPath, SnomedTaxonomy>() {
				@Override
				public void onRemoval(final RemovalNotification<IBranchPath, SnomedTaxonomy> notification) {
					LOGGER.info("SNOMED CT taxonomy has been successfully released from the cache on '{}' branch.", notification.getKey());
				}
			})
			.build(new CacheLoader<IBranchPath, SnomedTaxonomy>() {
				@Override
				public SnomedTaxonomy load(final IBranchPath branchPath) throws Exception {
					LOGGER.info("Initializing SNOMED CT taxonomy service for '{}' branch...", branchPath);
					final SnomedTaxonomy taxonomy = new SnomedTaxonomyImpl(branchPath);
					return taxonomy;
				}
			});
	
	@Override
	public boolean isActive(final IBranchPath branchPath, final String conceptId) {
		return getTaxonomy(branchPath).isActive(conceptId);
	}
	
	@Override
	public String getSnomedRoot(final IBranchPath branchPath) {
		return getTaxonomy(branchPath).getSnomedRoot();
	}

	@Override
	public Collection<String> getSubtypes(final IBranchPath branchPath, final String conceptId) {
		if (isEmpty(conceptId)) {
			return emptyList();
		}
		return getTaxonomy(branchPath).getSubtypes(conceptId);
	}

	@Override
	public Collection<String> getAllSubtypes(final IBranchPath branchPath, final String conceptId) {
		if (isEmpty(conceptId)) {
			return emptyList();
		}
		return getTaxonomy(branchPath).getAllSubtypes(conceptId);
	}

	@Override
	public int getSubtypesCount(final IBranchPath branchPath, final String conceptId) {
		if (isEmpty(conceptId)) {
			return 0;
		}
		return getTaxonomy(branchPath).getSubtypesCount(conceptId);
	}

	@Override
	public int getAllSubtypesCount(final IBranchPath branchPath, final String conceptId) {
		if (isEmpty(conceptId)) {
			return 0;
		}
		return getTaxonomy(branchPath).getAllSubtypesCount(conceptId);
	}

	@Override
	public Collection<String> getSupertypes(final IBranchPath branchPath, final String conceptId) {
		if (isEmpty(conceptId)) {
			return emptyList();
		}
		return getTaxonomy(branchPath).getSupertypes(conceptId);
	}

	@Override
	public Collection<String> getAllSupertypes(final IBranchPath branchPath, final String conceptId) {
		if (isEmpty(conceptId)) {
			return emptyList();
		}
		return getTaxonomy(branchPath).getAllSupertypes(conceptId);
	}

	@Override
	public int getSupertypesCount(final IBranchPath branchPath, final String conceptId) {
		if (isEmpty(conceptId)) {
			return 0;
		}
		return getTaxonomy(branchPath).getSupertypesCount(conceptId);
	}

	@Override
	public int getAllSupertypesCount(final IBranchPath branchPath, final String conceptId) {
		if (isEmpty(conceptId)) {
			return 0;
		}
		return getTaxonomy(branchPath).getAllSupertypesCount(conceptId);
	}

	@Override
	public Collection<String> getOutboundConcepts(final IBranchPath branchPath, final String conceptId) {
		if (isEmpty(conceptId)) {
			return emptyList();
		}
		return getTaxonomy(branchPath).getOutboundConcepts(conceptId);
	}

	@Override
	public Collection<String> getOutboundConcepts(final IBranchPath branchPath, final String conceptId, final String typeId) {
		if (isEmpty(conceptId) || isEmpty(typeId)) {
			return emptyList();
		}
		return getTaxonomy(branchPath).getOutboundConcepts(conceptId, typeId);
	}

	@Override
	public Collection<String> getAllOutboundConcepts(final IBranchPath branchPath, final String conceptId) {
		if (isEmpty(conceptId)) {
			return emptyList();
		}
		return getTaxonomy(branchPath).getAllOutboundConcepts(conceptId);
	}
	
	@Override
	public boolean hasOutboundRelationshipOfType(final IBranchPath branchPath, final String conceptId, final String typeId) {
		return getTaxonomy(branchPath).hasOutboundRelationshipOfType(conceptId, typeId);
	}

	@Override
	public Collection<String> getOutboundRelationshipTypes(final IBranchPath branchPath, final String conceptId) {
		if (isEmpty(conceptId)) {
			return emptyList();
		}
		return getTaxonomy(branchPath).getOutboundRelationshipTypes(conceptId);
	}
	
	@Override
	public int getOutboundConceptsCount(final IBranchPath branchPath, final String conceptId) {
		if (isEmpty(conceptId)) {
			return 0;
		}
		return getTaxonomy(branchPath).getOutboundConceptsCount(conceptId);
	}
	
	@Override
	public Collection<String> getInboundConcepts(final IBranchPath branchPath, final String conceptId) {
		if (isEmpty(conceptId)) {
			return emptyList();
		}
		return getTaxonomy(branchPath).getInboundConcepts(conceptId);
	}

	@Override
	public Collection<String> getInboundConcepts(final IBranchPath branchPath, final String conceptId, final String typeId) {
		if (isEmpty(conceptId)) {
			return emptyList();
		}
		return getTaxonomy(branchPath).getInboundConcepts(conceptId, typeId);
	}

	@Override
	public Collection<String> getAllInboundConcepts(final IBranchPath branchPath, final String conceptId) {
		if (isEmpty(conceptId)) {
			return emptyList();
		}
		return getTaxonomy(branchPath).getAllInboundConcepts(conceptId);
	}
	
	@Override
	public boolean hasInboundRelationshipOfType(final IBranchPath branchPath, final String conceptId, final String typeId) {
		return getTaxonomy(branchPath).hasInboundRelationshipOfType(conceptId, typeId);
	}

	@Override
	public int getDepth(final IBranchPath branchPath, final String conceptId) {
		if (isEmpty(conceptId)) {
			return 0;
		}
		return getTaxonomy(branchPath).getDepth(conceptId);
	}

	@Override
	public int getHeight(final IBranchPath branchPath, final String conceptId) {
		if (isEmpty(conceptId)) {
			return 0;
		}
		return getTaxonomy(branchPath).getHeight(conceptId);
	}

	@Override
	public boolean isLeaf(final IBranchPath branchPath, final String conceptId) {
		if (isEmpty(conceptId)) {
			return false;
		}
		return getTaxonomy(branchPath).isLeaf(conceptId);
	}

	@Override
	public Collection<String> getContainerRefSetIds(final IBranchPath branchPath, final String conceptId) {
		if (isEmpty(conceptId)) {
			return emptyList();
		}
		return getTaxonomy(branchPath).getContainerRefSetIds(conceptId);
	}
	
	@Override
	public Collection<String> evaluateEscg(final IBranchPath branchPath, final String expression) {
		if (isEmpty(expression)) {
			return emptyList();
		}
		return getTaxonomy(branchPath).evaluateEscg(expression);
	}

	@Override
	public void storeUpdated(final CDOCommitInfo commitInfo) {
		if (null != commitInfo && null != commitInfo.getBranch()) {
			final IBranchPath branchPath = createPath(commitInfo.getBranch());
			taxonomyCache.invalidate(branchPath);
		}
	}

	@Override
	public String getRepositoryUuid() {
		return SnomedDatastoreActivator.REPOSITORY_UUID;
	}

	private SnomedTaxonomy getTaxonomy(final IBranchPath branchPath) {
		return taxonomyCache.getUnchecked(branchPath);
	}

}