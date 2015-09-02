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

import static com.b2international.commons.platform.Extensions.getExtensions;
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.BranchPathUtils.createVersionPath;
import static com.b2international.snowowl.datastore.BranchPathUtils.isBasePath;
import static com.b2international.snowowl.datastore.cdo.CDORootResourceNameProvider.ROOT_RESOURCE_NAMEPROVIDER_EXTENSION_POINT_ID;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.unmodifiableCollection;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.ecore.EObject;

import com.b2international.commons.FileUtils;
import com.b2international.snowowl.core.SnowOwlApplication;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.IndexException;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.ICodeSystemVersion;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.cdo.CDORootResourceNameProvider;
import com.b2international.snowowl.datastore.cdo.CDOTransactionFunction;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.datastore.server.internal.lucene.store.CompositeDirectory;
import com.b2international.snowowl.datastore.server.internal.lucene.store.ReadOnlyDirectory;
import com.b2international.snowowl.terminologymetadata.CodeSystemVersion;
import com.b2international.snowowl.terminologymetadata.CodeSystemVersionGroup;
import com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage;
import com.b2international.snowowl.terminologyregistry.core.index.CodeSystemVersionIndexMappingStrategy;
import com.google.common.base.Predicate;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

/**
 * Directory manager for a a file system based {@link FSDirectory}.
 */
public class FSDirectoryManager implements IDirectoryManager {

	private static final String INDEXES_CHILD_FOLDER = "indexes";

	private final String repositoryUuid;
	private final File indexRelativeRootPath;
	private final IndexServerService<?> indexServerService;

	public FSDirectoryManager(final String repositoryUuid, final File indexRelativeRootPath, final IndexServerService<?> indexServerService) {
		this.repositoryUuid = checkNotNull(repositoryUuid, "repositoryUuid");
		this.indexRelativeRootPath = checkNotNull(indexRelativeRootPath, "indexRelativeRootPath");
		this.indexServerService = checkNotNull(indexServerService, "indexServerService");
	}

	@Override
	public Directory createDirectory(final IBranchPath branchPath, final IndexBranchService baseService) throws IOException {
		final IndexCommit baseCommit = getBaseCommit(branchPath, baseService);
		final File folderForBranchPath = getFolderForBranchPath(branchPath, baseCommit);
		
		if (BranchPathUtils.isMain(branchPath)) {
			return IndexUtils.open(folderForBranchPath);
		} else if (isBasePath(branchPath)) {
			return new ReadOnlyDirectory(baseCommit);
		} else {
			final Directory writeableDirectory = IndexUtils.open(folderForBranchPath);
			return new CompositeDirectory(baseCommit, writeableDirectory);
		}
	}

	private File getDataDirectory() {
		return SnowOwlApplication.INSTANCE.getEnviroment().getDataDirectory();
	}

	private File getIndexRootPath() {
		return new File(getDataDirectory(), INDEXES_CHILD_FOLDER);
	}

	private File getIndexAbsolutePath() {
		final File indexRootPath = getIndexRootPath();
		final File indexTerminologyRootPath = new File(indexRootPath, indexRelativeRootPath.getPath());
		return indexTerminologyRootPath;
	}

	private File getFolderForBranchPath(final IBranchPath branchPath) throws IOException {
		if (BranchPathUtils.isMain(branchPath)) {
			return getFolderForBranchPath(branchPath, null);
		} else {
			IBranchPath parent = branchPath.getParent();
			IndexBranchService baseService = indexServerService.getBranchService(parent);
			IndexCommit baseCommit = getBaseCommit(branchPath, baseService);
			return getFolderForBranchPath(branchPath, baseCommit);
		}
	}
	
	private File getFolderForBranchPath(final IBranchPath branchPath, IndexCommit baseCommit) throws IOException {
		final File indexTerminologyRootPath = getIndexAbsolutePath();
		final String child;
		
		if (BranchPathUtils.isMain(branchPath)) {
			child = String.valueOf(CDOBranch.MAIN_BRANCH_ID);
		} else {
			child = baseCommit.getUserData().get(IndexUtils.INDEX_CDO_BRANCH_PATH_KEY);
		}
		
		return new File(indexTerminologyRootPath, child);
	}

	private IndexCommit getBaseCommit(IBranchPath branchPath, IndexBranchService baseService) throws IOException {
		if (BranchPathUtils.isMain(branchPath)) {
			return null;
		} else {
			return baseService.getIndexCommit(branchPath);
		}
	}

	@Override
	public void deleteIndex(final IBranchPath branchPath) throws IOException {
		FileUtils.deleteDirectory(getFolderForBranchPath(branchPath));
	}

	@Override
	public List<String> listFiles(final IBranchPath branchPath) throws IOException {

		final Set<String> result = Sets.newHashSet();
		final File folderForBranchPath = getFolderForBranchPath(branchPath);

		final IPath base = new Path(getIndexRootPath().getAbsolutePath());
		final IPath actual = new Path(folderForBranchPath.getAbsolutePath());
		final IPath relativePath = actual.makeRelativeTo(base);

		try {

			final List<IndexCommit> commits = DirectoryReader.listCommits(indexServerService.getBranchService(branchPath).getDirectory());

			for (final IndexCommit commit : commits) {
				final Collection<String> fileNames = commit.getFileNames();

				for (final String fileName : fileNames) {
					final File indexFilePath = new File(folderForBranchPath, fileName);

					// Only collect files from this folder
					if (indexFilePath.exists() && indexFilePath.isFile()) {
						result.add(relativePath.append(fileName).toString());
					}
				}
			}

		} catch (final IndexNotFoundException ignored) {
			// An empty result can be returned if no commits can be collected from the directory
		}

		return Ordering.natural().sortedCopy(result);
	}

	@Override
	public void fireFirstStartup(final IndexBranchService service) {
		try {
			service.getIndexWriter().commit();

			final Collection<CDORootResourceNameProvider> rootResourceNameProviders = getRootResourceNameProvidersForRepository(repositoryUuid);
			for (final CDORootResourceNameProvider rootResourceNameProvider : rootResourceNameProviders) {
				for (final String rootResourceName : rootResourceNameProvider.getRootResourceNames()) {

					final boolean metaRoot = any(rootResourceNameProviders, new Predicate<CDORootResourceNameProvider>() {
						@Override
						public boolean apply(final CDORootResourceNameProvider provider) {
							return provider.isMetaRootResource(rootResourceName);
						}
					});

					if (metaRoot) {

						final ICDOConnection connection = getServiceForClass(ICDOConnectionManager.class).getByUuid(repositoryUuid);
						CDOUtils.apply(new CDOTransactionFunction<Void>(connection.getMainBranch()) {
							@Override
							protected Void apply(final CDOTransaction transaction) {

								final CDOResource resource = transaction.getOrCreateResource(rootResourceName);

								if (!CDOUtils.isTransient(resource)) {

									final CDOResource cdoResource = (CDOResource) resource;
									final EObject object = find(cdoResource.getContents(), new Predicate<EObject>() {
										@Override
										public boolean apply(final EObject eObject) {
											return TerminologymetadataPackage.eINSTANCE.getCodeSystemVersionGroup().isSuperTypeOf(eObject.eClass());
										}
									}, null);

									boolean shouldTag = false;
									if (object instanceof CodeSystemVersionGroup) {
										final CodeSystemVersionGroup group = (CodeSystemVersionGroup) object;
										for (final CodeSystemVersion version : group.getCodeSystemVersions()) {
											shouldTag = true;
											final CodeSystemVersionIndexMappingStrategy mappingStrategy = new CodeSystemVersionIndexMappingStrategy(version);
											final Document doc = mappingStrategy.createDocument();
											try {
												final long storageKey = CDOIDUtils.asLong(group.cdoID());
												service.updateDocument(storageKey, doc);
											} catch (IOException e) {
												throw new IndexException("Failed to initialize index branch service for " + repositoryUuid);
											}
										}

									}


									if (shouldTag) {
										try {
											service.commit();
											service.createIndexCommit(createVersionPath(ICodeSystemVersion.INITIAL_STATE), new int[] { 0 }, -1L);
										} catch (IOException e) {
											throw new IndexException("Failed to initialize index branch service for " + repositoryUuid);
										}
									}
								}

								return com.b2international.commons.Void.VOID;
							}
						});
					}
				}
			}

		} catch (final IOException e) {
			throw new IndexException("Failed to initialize index branch service for " + repositoryUuid);
		}
	}

	private Collection<CDORootResourceNameProvider> getRootResourceNameProvidersForRepository(final String repsotiryUuid) {
		final Collection<CDORootResourceNameProvider> providers = newHashSet();
		for (final CDORootResourceNameProvider provider : getAllRootResourceProviders()) {
			if (repsotiryUuid.equals(checkNotNull(provider, "provider").getRepositoryUuid())) {
				providers.add(provider);
			}
		}
		return unmodifiableCollection(providers);
	}

	private Collection<CDORootResourceNameProvider> getAllRootResourceProviders() {
		return getExtensions(ROOT_RESOURCE_NAMEPROVIDER_EXTENSION_POINT_ID, CDORootResourceNameProvider.class);
	}
}
