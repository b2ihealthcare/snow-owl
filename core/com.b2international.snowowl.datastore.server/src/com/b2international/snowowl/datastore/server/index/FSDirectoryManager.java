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
import java.io.FileFilter;
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
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.ecore.EObject;

import com.b2international.commons.CompareUtils;
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
import com.b2international.snowowl.datastore.server.internal.lucene.store.CompositeDirectory;
import com.b2international.snowowl.datastore.server.internal.lucene.store.ReadOnlyDirectory;
import com.b2international.snowowl.terminologymetadata.CodeSystemVersion;
import com.b2international.snowowl.terminologymetadata.CodeSystemVersionGroup;
import com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage;
import com.b2international.snowowl.terminologyregistry.core.index.CodeSystemVersionIndexMappingStrategy;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

/**
 * Directory manager for a a file system based {@link FSDirectory}.
 */
public class FSDirectoryManager implements IDirectoryManager {

	private static final String INDEXES_CHILD_FOLDER = "indexes";

	private final File indexRelativeRootPath;
	private final IIndexPurgerPredicate indexPurgerPredicate;

	private String repositoryUuid;

	public FSDirectoryManager(final String repositoryUuid, final File indexRelativeRootPath, final IIndexPurgerPredicate indexPurgerPredicate) {
		this.repositoryUuid = checkNotNull(repositoryUuid, "repositoryUuid");
		this.indexPurgerPredicate = checkNotNull(indexPurgerPredicate, "indexPurgerPredicate");
		this.indexRelativeRootPath = checkNotNull(indexRelativeRootPath, "indexRelativeRootPath");
	}

	@Override
	public Directory createDirectory(final IBranchPath branchPath) throws IOException {
		if (BranchPathUtils.isMain(branchPath)) {
			return createDirectory(branchPath, getFolderForBranchPath(branchPath));
		} else {
			final IBranchPath parentPath = branchPath.getParent();
			final IndexBranchService baseService = indexPurgerPredicate.getBranchService(parentPath);
			final IndexCommit commit = baseService.getIndexCommit(branchPath);
			if (isBasePath(branchPath)) {
				return new ReadOnlyDirectory(commit);
			} else {
				return new CompositeDirectory(commit, createDirectory(branchPath, getFolderForBranchPath(branchPath)));
			}
		}
	}

	@Override
	public void cleanUp(final IBranchPath branchPath, final boolean force) {

		final File[] subDirectories = getFolderForBranchPath(branchPath).listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});

		if (CompareUtils.isEmpty(subDirectories)) {
			if (indexPurgerPredicate.apply(branchPath)) {
				FileUtils.deleteDirectory(getFolderForBranchPath(branchPath));
			}
			return;
		}

		for (final File subDirectory : subDirectories) {

			//absolute file to avoid e.g /home/user/MAIN/SnowOwl/resources/indexes/snomed/MAIN/task_300 issue
			final String dataDirectoryUri = getDataDirectory().toURI().toString();
			String subFolderUri = subDirectory.toURI().toString();
			subFolderUri = subFolderUri.replaceFirst(dataDirectoryUri, "");
			final int index = subFolderUri.lastIndexOf(branchPath.getPath());
			Preconditions.checkState(index > 0, "Cannot extract branch path from " + subFolderUri + ".");
			final String subDirectoryPath = subFolderUri.substring(index);
			final IBranchPath subFolderBranchPath = BranchPathUtils.createPath(subDirectoryPath);

			if (!force && !indexPurgerPredicate.apply(subFolderBranchPath)) {

				cleanUp(subFolderBranchPath, true);

			} else {

				Directory indexDirectory = null;

				try {

					indexDirectory = IndexUtils.open(subDirectory);

					if (DirectoryReader.indexExists(indexDirectory)) {
						indexDirectory.close();
						indexDirectory = null;
					}
					FileUtils.deleteDirectory(subDirectory);

				} catch (final IOException ignored) {
					// Don't mind if it is not a real directory
				} finally {

					if (null != indexDirectory) {
						try {
							indexDirectory.close();
						} catch (final IOException e) {
							try {
								indexDirectory.close();
							} catch (final IOException e1) {
								//intentionally ignored
							}
							throw new IndexException("Error while cleaning up index folder " + indexDirectory, e);
						}
					}

				}

			}

		}
	}

	@Override
	public List<String> listFiles(final IBranchPath branchPath) throws IOException {

		final Set<String> result = Sets.newHashSet();
		final File folderForBranchPath = getFolderForBranchPath(branchPath);

		final IPath base = new Path(getIndexRootPath().getAbsolutePath());
		final IPath actual = new Path(folderForBranchPath.getAbsolutePath());
		final IPath relativePath = actual.makeRelativeTo(base);

		try {

			final List<IndexCommit> commits = DirectoryReader.listCommits(indexPurgerPredicate.getBranchService(branchPath).getDirectory());

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
						public boolean apply(final CDORootResourceNameProvider provider) {
							return provider.isMetaRootResource(rootResourceName);
						}
					});

					if (metaRoot) {

						final ICDOConnection connection = getServiceForClass(ICDOConnectionManager.class).getByUuid(repositoryUuid);
						CDOUtils.apply(new CDOTransactionFunction<Void>(connection.getMainBranch()) {
							protected Void apply(final CDOTransaction transaction) {

								final CDOResource resource = transaction.getOrCreateResource(rootResourceName);

								if (!CDOUtils.isTransient(resource)) {

									final CDOResource cdoResource = (CDOResource) resource;
									final EObject object = find(cdoResource.getContents(), new Predicate<EObject>() {
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
												service.updateDocument(IndexUtils.getStorageKeyTerm(CDOIDUtils.asLong(group.cdoID())), doc);
											} catch (IOException e) {
												throw new IndexException("Failed to initialize index branch service for " + repositoryUuid);
											}
										}

									}


									if (shouldTag) {
										try {
											service.commit();
											service.createIndexCommit(createVersionPath(ICodeSystemVersion.INITIAL_STATE), -1L, true, false);
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

	private File getDataDirectory() {
		return SnowOwlApplication.INSTANCE.getEnviroment().getDataDirectory();
	}

	private File getFolderForBranchPath(final IBranchPath branchPath) {
		final File indexTerminologyRootPath = getIndexAbsolutePath();
		final File indexTerminologyBranchPath = new File(indexTerminologyRootPath, branchPath.getOsPath());
		return indexTerminologyBranchPath;
	}

	private File getIndexAbsolutePath() {
		final File indexRootPath = getIndexRootPath();
		final File indexTerminologyRootPath = new File(indexRootPath, indexRelativeRootPath.getPath());
		return indexTerminologyRootPath;
	}

	private File getIndexRootPath() {
		return new File(getDataDirectory(), INDEXES_CHILD_FOLDER);
	}

	private Directory createDirectory(final IBranchPath branchPath, final File indexPath) throws IOException {
		final Directory result = IndexUtils.open(indexPath);
		return result;
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