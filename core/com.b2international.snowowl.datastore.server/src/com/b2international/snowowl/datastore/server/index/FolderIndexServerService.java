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
package com.b2international.snowowl.datastore.server.index;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.browser.SuperTypeIdProvider;
import com.b2international.snowowl.core.api.index.CommonIndexConstants;
import com.b2international.snowowl.datastore.ICodeSystem;
import com.b2international.snowowl.datastore.ICodeSystemVersion;
import com.b2international.snowowl.datastore.InternalTerminologyRegistryService;
import com.b2international.snowowl.datastore.index.DocumentWithScore;
import com.b2international.snowowl.datastore.index.FolderIndexEntry;
import com.b2international.snowowl.datastore.index.IFolderIndexService;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.index.ParentFolderAwareIndexEntry;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.datastore.server.TerminologyRegistryServiceWrapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Sets;

/**
 * Server-side implementation of folder index service.
 * 
 */
public abstract class FolderIndexServerService extends FSIndexServerService<ParentFolderAwareIndexEntry> implements IFolderIndexService, InternalTerminologyRegistryService {

	protected FolderIndexServerService(final File indexPath, final long timeout) {
		super(indexPath, timeout);
		InternalTerminologyRegistryServiceRegistry.INSTANCE.register(getRepositoryUuid(), this);
	}

	@Override
	public Set<FolderIndexEntry> getTopLevelFolders(final IBranchPath branchPath) {
		checkNotNull(branchPath, "Branch path must not be null.");
		return getSubFoldersByParentField(branchPath, Mappings.ROOT_ID_STRING);
	}
	
	private FolderIndexEntry createFolderResultObject(final IBranchPath branchPath, final Document document) {
		final String code = Mappings.id().getValue(document);
		final String displayName = Mappings.label().getValue(document);
		final String parentId = Mappings.unfilteredParent().getValue(document);
		final long storageKey = Mappings.storageKey().getValue(document);
		return new FolderIndexEntry(code, displayName, parentId, storageKey, hasChildren(branchPath, code));
	}

	@Override
	public FolderIndexEntry getFolderById(final IBranchPath branchPath, final String folderId) {
		checkNotNull(branchPath, "Branch path must not be null.");
		checkNotNull(folderId, "Folder id must not be null.");
		final Query query = Mappings.newQuery().id(folderId).matchAll();
		return createSingleFolderResultObject(branchPath, search(branchPath, query, 1));
	}
	
	private FolderIndexEntry createSingleFolderResultObject(final IBranchPath branchPath, final TopDocs docs) {
		if (CompareUtils.isEmpty(docs.scoreDocs)) {
			return null;
		}
		return createFolderResultObject(
				branchPath,
				document(branchPath, docs.scoreDocs[0].doc,
						Mappings.fieldsToLoad().id().label().parent().storageKey().field(CommonIndexConstants.COMPONENT_RELEASED).build()));
	}

	@Override
	public Set<FolderIndexEntry> getSubFoldersById(final IBranchPath branchPath, final String folderId) {
		checkNotNull(branchPath, "Branch path must not be null.");
		checkNotNull(folderId, "Folder id must not be null.");
		return getSubFoldersByParentField(branchPath, folderId);
	}

	private Set<FolderIndexEntry> getSubFoldersByParentField(final IBranchPath branchPath, final String parentId) {
		final Set<FolderIndexEntry> results = Sets.newHashSet();
		final Query query = Mappings.newQuery().type(getTerminologyFolderComponentNumber()).parent(parentId).matchAll();
		final Collection<DocumentWithScore> documents = searchUnordered(branchPath, query, null);
		for (final DocumentWithScore documentWithScore : documents) {
			final Document document = documentWithScore.getDocument();
			results.add(createFolderResultObject(branchPath, document));
		}
		return results;
	}

	@Override
	public Collection<FolderIndexEntry> getAllSubFolders(final IBranchPath branchPath, final String folderId) {
		checkNotNull(branchPath, "Branch path must not be null.");
		checkNotNull(folderId, "Folder id must not be null.");

		final Builder<FolderIndexEntry> builder = ImmutableList.builder();
		buildAllSubFolderListById(branchPath, folderId, builder);
		return builder.build();
	}
	
	/*
	 * Recursive folder collector
	 */
	private void buildAllSubFolderListById(final IBranchPath branchPath, final String id, final Builder<FolderIndexEntry> builder) {
		for (final FolderIndexEntry entry : getSubFoldersById(branchPath, id)) {
			builder.add(entry);
	
			if (getSubFoldersById(branchPath, entry.getId()).size() > 0) {
				buildAllSubFolderListById(branchPath, entry.getId(), builder);
			}
		}
	}

	@Override
	public FolderIndexEntry getTopLevelFolderByName(final IBranchPath branchPath, final String folderName) {
		checkNotNull(branchPath, "Branch path must not be null.");
		checkNotNull(folderName, "Folder name must not be null.");
		final Query query = Mappings.newQuery()
				.parent(Mappings.ROOT_ID_STRING)
				.field(CommonIndexConstants.COMPONENT_LABEL_SORT_KEY, IndexUtils.getSortKey(folderName))
				.type(getTerminologyFolderComponentNumber())
				.matchAll();
		return createSingleFolderResultObject(branchPath, search(branchPath, query, 1));
	}
	
	protected abstract short getTerminologyFolderComponentNumber();
	
	private boolean hasChildren(final IBranchPath branchPath, final String folderId) {
		return getHitCount(branchPath, Mappings.newQuery().parent(folderId).matchAll(), null) > 0;
	}
	
	// TODO: place this method somewhere which is more component set specific
	@Override
	public Set<? extends ParentFolderAwareIndexEntry> getAllPublishedComponentSetsByFolder(final IBranchPath branchPath, final String folderId) {
		checkNotNull(branchPath, "Branch path must not be null.");

		final Set<ParentFolderAwareIndexEntry> results = Sets.newHashSet();
		final boolean searchForAll = StringUtils.isEmpty(folderId);
		if (searchForAll) {
			final Query query = Mappings.newQuery()
					.type(getTerminologyComponentSetNumber())
					.field(CommonIndexConstants.COMPONENT_RELEASED, "1")
					.matchAll();
			final Collection<DocumentWithScore> documents = searchUnordered(branchPath, query, null);
			for (final DocumentWithScore documentWithScore : documents) {
				final Document document = documentWithScore.getDocument();
				results.add(createComponentSetResultObject(branchPath, document));
			}
			return results;

		} else {
			results.addAll(getPublishedComponentSetsByFolder(branchPath, folderId));
			final Collection<FolderIndexEntry> allSubFolders = getAllSubFolders(branchPath, folderId);
			for (final FolderIndexEntry subFolder : allSubFolders) {
				results.addAll(getPublishedComponentSetsByFolder(branchPath, subFolder.getId()));
			}
		}
		return results;
	}

	// TODO: place this method somewhere which is more component set specific
	protected abstract short getTerminologyComponentSetNumber();

	// TODO: place this method somewhere which is more component set specific
	private Set<? extends ParentFolderAwareIndexEntry> getPublishedComponentSetsByFolder(final IBranchPath branchPath, final String folderId) {
		checkNotNull(branchPath, "Branch path must not be null.");

		final Query query = Mappings.newQuery()
				.type(getTerminologyComponentSetNumber())
				.parent(StringUtils.isEmpty(folderId) ? Mappings.ROOT_ID_STRING : folderId)
				.field(CommonIndexConstants.COMPONENT_RELEASED, "1")
				.matchAll();

		final Set<ParentFolderAwareIndexEntry> results = Sets.newHashSet();
		final Collection<DocumentWithScore> documents = searchUnordered(branchPath, query, null);

		for (final DocumentWithScore documentWithScore : documents) {
			final Document document = documentWithScore.getDocument();
			results.add(createComponentSetResultObject(branchPath, document));
		}
		return results;
	}

	/**
	 * See {@link SuperTypeIdProvider#getSuperTypeIds(IBranchPath, Object)}.
	 */
	public Collection<String> getSuperTypeIds(final IBranchPath branchPath, final String conceptId) {
		checkNotNull(branchPath, "branchPath");
		checkNotNull(conceptId, "conceptId");
		final Query query = Mappings.newQuery().id(conceptId).matchAll();
		final TopDocs topDocs = search(branchPath, query, 1);
		if (IndexUtils.isEmpty(topDocs)) {
			return Collections.emptySet();
		}
		final Document doc = document(branchPath, topDocs.scoreDocs[0].doc, Mappings.fieldsToLoad().parent().build());
		return Mappings.parent().getValues(doc);
	}
	
	// TODO: place this method somewhere which is more component set specific
	protected abstract ParentFolderAwareIndexEntry createComponentSetResultObject(IBranchPath branchPath, Document document);
	

	@Override
	public Collection<ICodeSystem> getCodeSystems(final IBranchPath branchPath) {
		return new TerminologyRegistryServiceWrapper(this).getCodeSystems(checkNotNull(branchPath, "branchPath"));
	}

	@Override
	public Collection<ICodeSystemVersion> getCodeSystemVersions(final IBranchPath branchPath, final String codeSystemShortName) {
		return new TerminologyRegistryServiceWrapper(this).getCodeSystemVersions(branchPath, codeSystemShortName);
	}

	@Override
	public Collection<ICodeSystemVersion> getCodeSystemVersionsFromRepository(final IBranchPath branchPath, final String repositoryUuid) {
		return new TerminologyRegistryServiceWrapper(this).getCodeSystemVersionsFromRepository(branchPath, repositoryUuid);
	}
	
	@Override
	public Collection<ICodeSystemVersion> getCodeSystemVersionsFromRepositoryWithInitVersion(final IBranchPath branchPath, final String repositoryUuid) {
		return new TerminologyRegistryServiceWrapper(this).getCodeSystemVersionsFromRepositoryWithInitVersion(branchPath, repositoryUuid);
	}
	
	@Override
	public ICodeSystem getCodeSystemByShortName(final IBranchPath branchPath, final String codeSystemShortName) {
		return new TerminologyRegistryServiceWrapper(this).getCodeSystemByShortName(branchPath, codeSystemShortName);
	}

	@Override
	public ICodeSystem getCodeSystemByOid(final IBranchPath branchPath, final String codeSystemOid) {
		return new TerminologyRegistryServiceWrapper(this).getCodeSystemByOid(branchPath, codeSystemOid);
	}

	@Override
	public Map<String, ICodeSystem> getTerminologyComponentIdCodeSystemMap(final IBranchPath branchPath) {
		return new TerminologyRegistryServiceWrapper(this).getTerminologyComponentIdCodeSystemMap(branchPath);
	}

	@Override
	public Map<String, Collection<ICodeSystem>> getTerminologyComponentIdWithMultipleCodeSystemsMap(final IBranchPath branchPath) {
		return new TerminologyRegistryServiceWrapper(this).getTerminologyComponentIdWithMultipleCodeSystemsMap(branchPath);
	}

	@Override
	public String getTerminologyComponentIdByShortName(final IBranchPath branchPath, final String codeSystemShortName) {
		return new TerminologyRegistryServiceWrapper(this).getTerminologyComponentIdByShortName(branchPath, codeSystemShortName);
	}

	@Override
	public String getVersionId(final IBranchPath branchPath, final ICodeSystem codeSystem) {
		return new TerminologyRegistryServiceWrapper(this).getVersionId(branchPath, codeSystem);
	}
	
}