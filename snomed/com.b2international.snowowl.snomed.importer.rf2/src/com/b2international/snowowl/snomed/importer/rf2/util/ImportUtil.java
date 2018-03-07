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
package com.b2international.snowowl.snomed.importer.rf2.util;

import static com.b2international.commons.CompareUtils.isEmpty;
import static com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration.ImportSourceKind.FILES;
import static com.b2international.snowowl.snomed.importer.release.ReleaseFileSet.ReleaseComponentType.CONCEPT;
import static com.b2international.snowowl.snomed.importer.release.ReleaseFileSet.ReleaseComponentType.DESCRIPTION;
import static com.b2international.snowowl.snomed.importer.release.ReleaseFileSet.ReleaseComponentType.RELATIONSHIP;
import static com.b2international.snowowl.snomed.importer.release.ReleaseFileSet.ReleaseComponentType.STATED_RELATIONSHIP;
import static com.b2international.snowowl.snomed.importer.release.ReleaseFileSet.ReleaseComponentType.TEXT_DEFINITION;
import static com.b2international.snowowl.snomed.importer.rf2.util.RF2ReleaseRefSetFileCollector.collectUrlFromRelease;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.slf4j.LoggerFactory;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.platform.Extensions;
import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.RevisionIndexRead;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.LogUtils;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.ft.FeatureToggles;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.oplock.IOperationLockManager;
import com.b2international.snowowl.datastore.oplock.IOperationLockTarget;
import com.b2international.snowowl.datastore.oplock.OperationLockException;
import com.b2international.snowowl.datastore.oplock.OperationLockRunner;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContext;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.oplock.impl.IDatastoreOperationLockManager;
import com.b2international.snowowl.datastore.oplock.impl.SingleRepositoryAndBranchLockTarget;
import com.b2international.snowowl.datastore.server.CDOServerUtils;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.identity.domain.User;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.ContentSubType;
import com.b2international.snowowl.snomed.datastore.ISnomedImportPostProcessor;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.importer.ImportException;
import com.b2international.snowowl.snomed.importer.Importer;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration;
import com.b2international.snowowl.snomed.importer.net4j.SnomedImportResult;
import com.b2international.snowowl.snomed.importer.net4j.SnomedValidationDefect;
import com.b2international.snowowl.snomed.importer.release.ReleaseFileSet;
import com.b2international.snowowl.snomed.importer.release.ReleaseFileSetSelectors;
import com.b2international.snowowl.snomed.importer.rf2.RepositoryState;
import com.b2international.snowowl.snomed.importer.rf2.SnomedCompositeImportUnit;
import com.b2international.snowowl.snomed.importer.rf2.SnomedCompositeImporter;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportUnit;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportContext;
import com.b2international.snowowl.snomed.importer.rf2.refset.AbstractSnomedRefSetImporter;
import com.b2international.snowowl.snomed.importer.rf2.refset.SnomedRefSetImporterFactory;
import com.b2international.snowowl.snomed.importer.rf2.terminology.SnomedConceptImporter;
import com.b2international.snowowl.snomed.importer.rf2.terminology.SnomedDescriptionImporter;
import com.b2international.snowowl.snomed.importer.rf2.terminology.SnomedRelationshipImporter;
import com.b2international.snowowl.snomed.importer.rf2.validation.SnomedValidationContext;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.common.primitives.Longs;

/**
 * Provides utility methods for setting up and running importers.
 *
 */
public final class ImportUtil {

	private static final org.slf4j.Logger IMPORT_LOGGER = LoggerFactory.getLogger("snomed.importer.rf2");
	private static final String SNOMED_IMPORT_POST_PROCESSOR_EXTENSION = "com.b2international.snowowl.snomed.datastore.snomedImportPostProcessor";

	public SnomedImportResult doImport(final String requestingUserId, final ImportConfiguration configuration, final IProgressMonitor monitor) throws ImportException {
		try (SnomedImportContext context = new SnomedImportContext(getIndex())) {
			return doImportInternal(context, requestingUserId, configuration, monitor); 
		} catch (Exception e) {
			throw new ImportException("Failed to import RF2 release.", e);
		}
	}
	
	public SnomedImportResult doImport(
			final String codeSystemShortName,
			final ContentSubType contentSubType,
			final IBranchPath branchPath,
			final File releaseArchive,
			final boolean shouldCreateVersions) throws Exception {
		
		return doImport(codeSystemShortName, branchPath, contentSubType, releaseArchive, shouldCreateVersions, User.SYSTEM.getUsername(), new NullProgressMonitor());
	}
	
	public SnomedImportResult doImport(
			final String codeSystemShortName,
			final String userId,
			final ContentSubType contentSubType,
			final String branchPathName,
			final File releaseArchive,
			final boolean createVersions,
			final IProgressMonitor monitor) throws ImportException {
		
		return doImport(codeSystemShortName, BranchPathUtils.createPath(branchPathName), contentSubType, releaseArchive, createVersions, userId, monitor);
	}

	private SnomedImportResult doImport(
			final String codeSystemShortName,
			final IBranchPath branchPath,
			final ContentSubType contentSubType,
			final File releaseArchive,
			final boolean shouldCreateVersions,
			final String userId,
			final IProgressMonitor monitor) {
		
		checkNotNull(branchPath, "branchPath");
		checkNotNull(contentSubType, "contentSubType");
		checkNotNull(releaseArchive, "releaseArchive");
		checkArgument(releaseArchive.canRead(), "Cannot read SNOMED CT RF2 release archive content.");
		checkArgument(BranchPathUtils.exists(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath()));
		
		final ImportConfiguration config = new ImportConfiguration(branchPath.getPath());
		config.setCodeSystemShortName(codeSystemShortName);
		config.setContentSubType(contentSubType);
		config.setCreateVersions(shouldCreateVersions);
		config.setArchiveFile(releaseArchive);

		final List<String> zipFiles = listZipFiles(releaseArchive);
		final ReleaseFileSet archiveFileSet = ReleaseFileSetSelectors.SELECTORS.getFirstApplicable(zipFiles, contentSubType);
		
		if (archiveFileSet == null) {
			throw new ImportException("Archive file is an unrecognized SNOMED CT RF2 release archive.");
		}
		
		config.setReleaseFileSet(archiveFileSet);
		
		for (final URL refSetUrl : collectUrlFromRelease(config)) {
			config.addRefSetURL(refSetUrl);
		}
		
		config.setSourceKind(FILES);
		
		final File tempDir = Files.createTempDir();
		tempDir.deleteOnExit();

		try (final ZipFile archive = new ZipFile(releaseArchive)) {
			
			String conceptFilePath = archiveFileSet.getFileName(zipFiles, CONCEPT, contentSubType);
			if (!Strings.isNullOrEmpty(conceptFilePath)) {
				config.setConceptFile(createTemporaryFile(tempDir, archive, conceptFilePath));
			}
			
			String statedRelationshipFilePath = archiveFileSet.getFileName(zipFiles, STATED_RELATIONSHIP, contentSubType);
			if (!Strings.isNullOrEmpty(statedRelationshipFilePath)) {
				config.setStatedRelationshipFile(createTemporaryFile(tempDir, archive, statedRelationshipFilePath));
			}
			
			String relationshipFilePath = archiveFileSet.getFileName(zipFiles, RELATIONSHIP, contentSubType);
			if (!Strings.isNullOrEmpty(relationshipFilePath)) {
				config.setRelationshipFile(createTemporaryFile(tempDir, archive, relationshipFilePath));
			}
			
			for (String fileName : archiveFileSet.getAllFileName(zipFiles, DESCRIPTION, contentSubType)) {
				config.addDescriptionFile(createTemporaryFile(tempDir, archive, fileName));
			}
			
			for (String fileName : archiveFileSet.getAllFileName(zipFiles, TEXT_DEFINITION, contentSubType)) {
				config.addTextDefinitionFile(createTemporaryFile(tempDir, archive, fileName));
			}
			
		} catch (IOException e) {
			throw new ImportException("Failed to extract contents of release archive.", e);
		}

		return doImport(userId, config, monitor);
	}
	
	private File createTemporaryFile(final File tmpDir, final ZipFile archive, final String entryPath) throws IOException {
		final File file = new File(tmpDir, String.format("%s.%s", Files.getNameWithoutExtension(entryPath), Files.getFileExtension(entryPath)));
		java.nio.file.Files.copy(archive.getInputStream(archive.getEntry(entryPath)), file.toPath());
		return file;
	}

	private RepositoryState loadRepositoryState(RevisionSearcher searcher) throws IOException {
		final LongCollection conceptIds = getConceptIds(searcher);
		final Collection<String[]> statedStatements = getStatements(searcher, Concepts.STATED_RELATIONSHIP);
		final Collection<String[]> inferredStatements = getStatements(searcher, Concepts.INFERRED_RELATIONSHIP);
		return new RepositoryState(conceptIds, statedStatements, inferredStatements);
	}

	private Collection<String[]> getStatements(RevisionSearcher searcher, String characteristicTypeId) throws IOException {
		final Query<String[]> query = Query.select(String[].class)
				.from(SnomedRelationshipIndexEntry.class)
				.fields(SnomedDocument.Fields.ID, SnomedRelationshipIndexEntry.Fields.SOURCE_ID, SnomedRelationshipIndexEntry.Fields.DESTINATION_ID)
				.where(Expressions.builder()
						.filter(SnomedRelationshipIndexEntry.Expressions.active(true))
						.filter(SnomedRelationshipIndexEntry.Expressions.typeId(Concepts.IS_A))
						.filter(SnomedRelationshipIndexEntry.Expressions.characteristicTypeId(characteristicTypeId))
						.build())
				.limit(Integer.MAX_VALUE)
				.build();
		return searcher.search(query).getHits();
	}
	
	private LongCollection getConceptIds(RevisionSearcher searcher) throws IOException {
		final Query<SnomedConceptDocument> query = Query.select(SnomedConceptDocument.class)
				.fields(SnomedDocument.Fields.ID)
				.where(Expressions.matchAll())
				.limit(Integer.MAX_VALUE)
				.build();
		final Hits<SnomedConceptDocument> hits = searcher.search(query);
		final LongCollection conceptIds = PrimitiveSets.newLongOpenHashSetWithExpectedSize(hits.getTotal());
		for (SnomedConceptDocument hit : hits) {
			conceptIds.add(Long.parseLong(hit.getId()));
		}
		return conceptIds;
	}
	
	private SnomedImportResult doImportInternal(final SnomedImportContext context, final String requestingUserId, final ImportConfiguration configuration, final IProgressMonitor monitor) {
		final SubMonitor subMonitor = SubMonitor.convert(monitor, "Importing release files...", 17);
		final SnomedImportResult result = new SnomedImportResult();
		
		CodeSystemEntry codeSystem = CodeSystemRequests.prepareGetCodeSystem(configuration.getCodeSystemShortName())
			.build(SnomedDatastoreActivator.REPOSITORY_UUID)
			.execute(getEventBus())
			.getSync();
		
		IBranchPath codeSystemPath = BranchPathUtils.createPath(codeSystem.getBranchPath());
		String importPath = configuration.getBranchPath();
		final IBranchPath branchPath;
		
		if (importPath.startsWith(IBranchPath.MAIN_BRANCH)) {
			IBranchPath candidate = BranchPathUtils.createPath(importPath);
			Iterator<IBranchPath> iterator = BranchPathUtils.bottomToTopIterator(candidate);
			boolean found = false;
			
			while (iterator.hasNext()) {
				candidate = iterator.next();
				if (codeSystemPath.equals(candidate)) {
					found = true;
					break;
				}
			}
			
			if (!found) {
				throw new ImportException("Import path %s is not valid for code system %s.", importPath, configuration.getCodeSystemShortName());
			}
			
			branchPath = BranchPathUtils.createPath(importPath); // importPath is absolute
		} else {
			branchPath = BranchPathUtils.createPath(codeSystemPath, importPath); // importPath is relative to the code system's work branch
		}
		
		LogUtils.logImportActivity(IMPORT_LOGGER, requestingUserId, branchPath, "SNOMED CT import started from RF2 release format.");
		
		final RepositoryState repositoryState = getIndex().read(configuration.getBranchPath(), new RevisionIndexRead<RepositoryState>() {
			@Override
			public RepositoryState execute(RevisionSearcher searcher) throws IOException {
				return loadRepositoryState(searcher);
			}
		});
		
		if (!isContentValid(repositoryState, result, requestingUserId, configuration, branchPath, subMonitor)) {
			LogUtils.logImportActivity(IMPORT_LOGGER, requestingUserId, branchPath, "SNOMED CT import failed due to invalid RF2 release file(s).");
			return result;
		}
		
		final Set<URL> patchedRefSetURLs = Sets.newHashSet(configuration.getRefSetUrls());
		final Set<String> patchedExcludedRefSetIDs = Sets.newHashSet(configuration.getExcludedRefSetIds());
		final List<Importer> importers = Lists.newArrayList();

		final File stagingDirectoryRoot = new File(System.getProperty("java.io.tmpdir"));

		context.setVersionCreationEnabled(configuration.isCreateVersions());
		context.setLogger(IMPORT_LOGGER);
		context.setStagingDirectory(stagingDirectoryRoot);
		context.setContentSubType(configuration.getContentSubType());
		context.setIgnoredRefSetIds(patchedExcludedRefSetIDs);
		context.setCodeSystemShortName(configuration.getCodeSystemShortName());

		try {

			if (configuration.isValidReleaseFile(configuration.getConceptFile())) {
				final URL url = configuration.toURL(configuration.getConceptFile());
				importers.add(new SnomedConceptImporter(context, url.openStream(), configuration.getMappedName(url.getPath())));
			}
			
			for (File descriptionFile : configuration.getDescriptionFiles()) {
				if (configuration.isValidReleaseFile(descriptionFile)) {
					final URL url = configuration.toURL(descriptionFile);
					importers.add(new SnomedDescriptionImporter(context, url.openStream(), configuration.getMappedName(url.getPath()), ComponentImportType.DESCRIPTION));
				}
			}
		
			for (File textFile : configuration.getTextDefinitionFiles()) {
				if (configuration.isValidReleaseFile(textFile)) {
					final URL url = configuration.toURL(textFile);
					importers.add(new SnomedDescriptionImporter(context, url.openStream(), configuration.getMappedName(url.getPath()), ComponentImportType.TEXT_DEFINITION));
				}
				
			}

			if (configuration.isValidReleaseFile(configuration.getRelationshipFile())) {
				final URL url = configuration.toURL(configuration.getRelationshipFile());
				importers.add(new SnomedRelationshipImporter(context, url.openStream(), configuration.getMappedName(url.getPath()), ComponentImportType.RELATIONSHIP));
			}

			if (configuration.isValidReleaseFile(configuration.getStatedRelationshipFile())) {
				final URL url = configuration.toURL(configuration.getStatedRelationshipFile());
				importers.add(new SnomedRelationshipImporter(context, url.openStream(), configuration.getMappedName(url.getPath()), ComponentImportType.STATED_RELATIONSHIP));
			}

		} catch (final IOException e) {
			final String reason = null != e.getMessage() ? " Reason: '" + e.getMessage() + "'" : "";
			LogUtils.logImportActivity(IMPORT_LOGGER, requestingUserId, branchPath, "SNOMED CT import failed due to invalid RF2 release file URL." + reason);
			throw new ImportException("Invalid release file URL(s).", e);
		}

		for (final URL url : patchedRefSetURLs) {

			try {

				final AbstractSnomedRefSetImporter<?, ?> createRefSetImporter = SnomedRefSetImporterFactory.createRefSetImporter(url, context, configuration.getMappedName(url.getPath()));

				if (createRefSetImporter == null) {
					final String message = MessageFormat.format("Skipping unsupported reference set with URL ''{0}''.", url);
					LogUtils.logImportActivity(IMPORT_LOGGER, requestingUserId, branchPath, message);
					IMPORT_LOGGER.info(message);
				} else {
					importers.add(createRefSetImporter);
				}

			} catch (final IOException e) {
				final String reason = null != e.getMessage() ? " Reason: '" + e.getMessage() + "'" : "";
				LogUtils.logImportActivity(IMPORT_LOGGER, requestingUserId, branchPath, "SNOMED CT import failed due to I/O error while creating reference set importer." + reason);
				throw new ImportException("I/O error occurred while creating reference set importer.", e);
			}	
		}

		final boolean terminologyExistsBeforeImport = getIndex().read(BranchPathUtils.createMainPath().getPath(), new RevisionIndexRead<Boolean>() {
			@Override
			public Boolean execute(RevisionSearcher index) throws IOException {
				return index.search(Query.select(SnomedConceptDocument.class).where(SnomedConceptDocument.Expressions.id(Concepts.ROOT_CONCEPT)).limit(0).build()).getTotal() > 0;
			}
		});
		final boolean onlyRefSetImportersRegistered = Iterables.all(importers, Predicates.instanceOf(AbstractSnomedRefSetImporter.class));

		/*
		 * Commit notifications for changes made by the import should only be sent if the terminology already exists,
		 * and only changes for reference sets are coming in from the import files. 
		 */
		context.setCommitNotificationEnabled(terminologyExistsBeforeImport && onlyRefSetImportersRegistered);
		context.setUserId(requestingUserId);

		final ICDOConnectionManager connectionManager = ApplicationContext.getInstance().getService(ICDOConnectionManager.class);
		final CDOBranch branch = connectionManager.get(SnomedPackage.eINSTANCE).getBranch(branchPath);

		if (null == branch) {
			throw new ImportException("Branch does not exist. [" + branchPath + "]");
		}

		final SnomedEditingContext editingContext = new SnomedEditingContext(branchPath);
		context.setEditingContext(editingContext);
		context.setAggregatorSupplier(new EffectiveTimeBaseTransactionAggregatorSupplier(editingContext.getTransaction()));

		final IOperationLockTarget lockTarget = new SingleRepositoryAndBranchLockTarget(editingContext.getTransaction().getSession().getRepositoryInfo().getUUID(), branchPath);
		final DatastoreLockContext lockContext = new DatastoreLockContext(requestingUserId, DatastoreLockContextDescriptions.IMPORT);
		final SnomedImportResult[] resultHolder = new SnomedImportResult[1];
		final IDatastoreOperationLockManager lockManager = ApplicationContext.getInstance().getServiceChecked(IDatastoreOperationLockManager.class);
		
		final FeatureToggles features = ApplicationContext.getServiceForClass(FeatureToggles.class);
		try {
			features.enable(SnomedDatastoreActivator.REPOSITORY_UUID + ".import");
			OperationLockRunner.with(lockManager).run(new Runnable() { 
				@Override 
				public void run() {
					resultHolder[0] = doImportLocked(requestingUserId, configuration, result, branchPath, context, subMonitor, importers, editingContext, branch, repositoryState);
				}
			}, lockContext, IOperationLockManager.NO_TIMEOUT, lockTarget);
		} catch (final OperationLockException | InterruptedException e) {
			throw new ImportException("Caught exception while locking repository for import.", e);
		} catch (final InvocationTargetException e) {
			throw new ImportException("Failed to import RF2 release.", e.getCause());
		} finally {
			features.disable(SnomedDatastoreActivator.REPOSITORY_UUID + ".import");
		}
		
		return resultHolder[0];
	}

	private SnomedImportResult doImportLocked(final String requestingUserId, final ImportConfiguration configuration,
			final SnomedImportResult result, final IBranchPath branchPath, final SnomedImportContext context,
			final SubMonitor subMonitor, final List<Importer> importers, final SnomedEditingContext editingContext,
			final CDOBranch branch, final RepositoryState repositoryState) {

		try { 

			final long lastCommitTime = CDOServerUtils.getLastCommitTime(branch);
			context.setCommitTime(lastCommitTime);
			
			final SnomedCompositeImporter importer = new SnomedCompositeImporter(IMPORT_LOGGER, repositoryState, context, importers, ComponentImportUnit.ORDERING);

			importer.preImport(subMonitor.newChild(1, SubMonitor.SUPPRESS_NONE));
			final SnomedCompositeImportUnit snomedCompositeImportUnit = importer.getCompositeUnit(subMonitor.newChild(1, SubMonitor.SUPPRESS_NONE));
			snomedCompositeImportUnit.doImport(subMonitor.newChild(10, SubMonitor.SUPPRESS_NONE));
			importer.postImport(subMonitor.newChild(1, SubMonitor.SUPPRESS_NONE));

			// If there were no changes, no need to recreate semantic indexes
			if (context.getVisitedConcepts().size() == 0 && context.getVisitedRefSets().size() == 0) {
				return result;
			}

			// release specific post processing
			postProcess(context);
			
			result.getVisitedConcepts().addAll(getAsStringList(context.getVisitedConcepts()));

			return result;
		} finally {
			subMonitor.done();
			if (!result.getVisitedConcepts().isEmpty()) {
				LogUtils.logImportActivity(IMPORT_LOGGER, requestingUserId, branchPath, "SNOMED CT import successfully finished.");
			} else {
				LogUtils.logImportActivity(IMPORT_LOGGER, requestingUserId, branchPath, "SNOMED CT import finished. No changes could be found.");
			}
		}
	}

	private ImmutableList<String> getAsStringList(final LongSet longIds) {
		final long[] longIdArray = longIds.toArray();
		Arrays.sort(longIdArray);
		
		return FluentIterable.from(Longs.asList(longIdArray)).transform(new Function<Long, String>() {
			@Override
			public String apply(Long input) {
				return String.valueOf(input);
			}
		}).toList();
	}

	private IEventBus getEventBus() {
		return ApplicationContext.getInstance().getService(IEventBus.class);
	}

	// result is populated with validation errors if the return value is false
	private boolean isContentValid(final RepositoryState repositoryState,
			final SnomedImportResult result, final String requestingUserId, final ImportConfiguration configuration, final IBranchPath branchPath, final SubMonitor subMonitor) {
		return getIndex().read(configuration.getBranchPath(), new RevisionIndexRead<Boolean>() {
			@Override
			public Boolean execute(RevisionSearcher index) throws IOException {
				final SnomedValidationContext validator = new SnomedValidationContext(index, requestingUserId, configuration, IMPORT_LOGGER, repositoryState);
				
				final Set<SnomedValidationDefect> defects = result.getValidationDefects();
				defects.addAll(validator.validate(subMonitor.newChild(1)));
				
				if (!isEmpty(defects)) {
					
					List<String> flattenedDefects = defects.stream()
						.map( d -> d.getDefects())
						.flatMap( d -> d.stream())
						.collect(Collectors.toList());
					
					final String message = String.format("Validation encountered %s issue(s).", flattenedDefects.size());
					LogUtils.logImportActivity(IMPORT_LOGGER, requestingUserId, branchPath, message);
					
					if (flattenedDefects.size() > 100) {
						LogUtils.logImportActivity(IMPORT_LOGGER, requestingUserId, branchPath, "Logging the first hundred errors...");
					}
					
					flattenedDefects
						.stream()
						.limit(100) // FIXME only log the first 100 for now
						.forEach( d -> LogUtils.logImportActivity(IMPORT_LOGGER, requestingUserId, branchPath, d));
					
					return defects.stream().noneMatch(d -> d.getDefectType().isCritical());
				}
				
				return true;
			}
		});
	}

	private RevisionIndex getIndex() {
		return ApplicationContext.getInstance().getService(RepositoryManager.class).get(SnomedDatastoreActivator.REPOSITORY_UUID).service(RevisionIndex.class);
	}

	private void postProcess(final SnomedImportContext context) {
		for (final ISnomedImportPostProcessor processor : Extensions.getExtensions(SNOMED_IMPORT_POST_PROCESSOR_EXTENSION, ISnomedImportPostProcessor.class)) {
			processor.postProcess(context);
		}
	}

	public static long parseLong(final String componentId) {

		try {
			return Long.parseLong(Preconditions.checkNotNull(componentId, "componentId"));
		} catch (final NumberFormatException e) {
			throw new IllegalArgumentException(MessageFormat.format("Couldn''t convert component ID to a long: ''{0}''.", componentId));
		}
	}

	public static List<String> listZipFiles(final File filePath) {

		if (!filePath.canRead()) {
			return Collections.emptyList();
		}

		final Set<String> listOfFiles = new HashSet<String>();

		try {

			final ZipFile zipFile = new ZipFile(filePath);

			final Enumeration<? extends ZipEntry> files = zipFile.entries();

			while(files.hasMoreElements()) {

				final ZipEntry nextElement = files.nextElement();

				if (!nextElement.isDirectory()) {
					final String zipPath = nextElement.toString();
					listOfFiles.add(zipPath);
				}
			}

			zipFile.close();

		} catch (final IOException e) {
			return Collections.emptyList();
		}

		return ImmutableList.copyOf(listOfFiles);
	}
}
