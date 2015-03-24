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
package com.b2international.snowowl.snomed.importer.rf2;

import static com.b2international.commons.FileUtils.copyContentToTempFile;
import static com.b2international.snowowl.datastore.cdo.CDOUtils.check;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.slf4j.Logger;

import com.b2international.commons.functions.UncheckedCastFunction;
import com.b2international.commons.pcj.LongSets;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.CDOCommitInfoUtils;
import com.b2international.snowowl.datastore.cdo.ICDOTransactionAggregator;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.server.CDOServerCommitBuilder;
import com.b2international.snowowl.datastore.server.CDOServerUtils;
import com.b2international.snowowl.datastore.server.snomed.index.SnomedTaxonomyBuilder;
import com.b2international.snowowl.datastore.server.snomed.index.init.ImportIndexServerService;
import com.b2international.snowowl.datastore.server.snomed.index.init.IndexBasedImportIndexServiceFeeder;
import com.b2international.snowowl.datastore.server.snomed.index.init.Rf2BasedImportIndexServiceFeeder;
import com.b2international.snowowl.datastore.server.snomed.index.init.Rf2BasedSnomedTaxonomyBuilder;
import com.b2international.snowowl.datastore.version.ITagConfiguration;
import com.b2international.snowowl.datastore.version.ITagService;
import com.b2international.snowowl.datastore.version.TagConfigurationBuilder;
import com.b2international.snowowl.importer.AbstractImportUnit;
import com.b2international.snowowl.importer.AbstractLoggingImporter;
import com.b2international.snowowl.importer.ImportException;
import com.b2international.snowowl.importer.Importer;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedFactory;
import com.b2international.snowowl.snomed.datastore.SnomedCodeSystemFactory;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportUnit;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportContext;
import com.b2international.snowowl.snomed.importer.rf2.util.Rf2EffectiveTimeCollector;
import com.b2international.snowowl.snomed.mrcm.core.server.MrcmFileRegistryImpl;
import com.b2international.snowowl.snomed.mrcm.core.server.MrcmImporter;
import com.b2international.snowowl.terminologymetadata.CodeSystemVersion;
import com.b2international.snowowl.terminologymetadata.CodeSystemVersionGroup;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

/**
 * A composite importer that coordinates the operation of its child importers:
 * 
 * <ul>
 * <li>runs all pre-import steps are in order
 * <li>gathers units from all nested importers
 * <li>reorders units are according to the specified ordering
 * <li>carries out the import on the nested units
 * <li>runs all post-import steps in order
 * </ul>
 * 
 */
public class SnomedCompositeImporter extends AbstractLoggingImporter {

	private final List<Importer> importers;
	private final Ordering<AbstractImportUnit> unitOrdering;
	private final SnomedImportContext importContext; //will be used when tagging version (Snow Owl 3.1)
	
	public SnomedCompositeImporter(final Logger logger, final SnomedImportContext importContext, final List<Importer> importers, final Ordering<AbstractImportUnit> unitOrdering) {
		super(logger);
		this.importContext = Preconditions.checkNotNull(importContext, "Import context argument cannot be null.");
		this.importers = ImmutableList.copyOf(checkNotNull(importers, "importers"));
		this.unitOrdering = checkNotNull(unitOrdering, "unitOrdering");
	}

	@Override
	public void preImport(final SubMonitor subMonitor) {
		
		subMonitor.setWorkRemaining(importers.size());
		
		for (final Importer importer : importers) {
			importer.preImport(subMonitor.newChild(1, SubMonitor.SUPPRESS_NONE));
		}
	}

	@Override
	public List<AbstractImportUnit> getImportUnits(final SubMonitor subMonitor) {
		return ImmutableList.<AbstractImportUnit>of(getCompositeUnit(subMonitor));
	}

	public SnomedCompositeImportUnit getCompositeUnit(final SubMonitor subMonitor) {
		
		subMonitor.setWorkRemaining(importers.size());
		
		final List<AbstractImportUnit> units = Lists.newArrayList();
		
		for (final Importer importer : importers) {
			units.addAll(importer.getImportUnits(subMonitor.newChild(1, SubMonitor.SUPPRESS_NONE)));
		}
		
		Collections.sort(units, unitOrdering);
		
		return new SnomedCompositeImportUnit(this, units);
	}

	@Override
	public void doImport(final SubMonitor subMonitor, final AbstractImportUnit unit) {

		try {
			
			final boolean terminologyAvailable = isTerminologyAvailable();
			final IBranchPath branchPath = getImportBranchPath();
			
			final SnomedCompositeImportUnit compositeUnit = (SnomedCompositeImportUnit) unit;
			final UncheckedCastFunction<AbstractImportUnit, ComponentImportUnit> castFunction = new UncheckedCastFunction<AbstractImportUnit, ComponentImportUnit>(ComponentImportUnit.class);
			final List<ComponentImportUnit> units = Lists.newArrayList(Iterables.transform(compositeUnit.getUnits(), castFunction));
			
			if (isRefSetImport(units)) {
			
				for (final ComponentImportUnit subUnit : units) {
					subUnit.doImport(subMonitor.newChild(1, SubMonitor.SUPPRESS_NONE));
				}
				
			} else {
			
				Preconditions.checkState(!ApplicationContext.getInstance().exists(ImportIndexServerService.class), "SNOMED CT import already in progress.");
	
				final int size = units.size();
				
				subMonitor.setWorkRemaining(size + 1);
	
				final ImportIndexServerService importIndexServerService = new ImportIndexServerService(branchPath, importContext.getLanguageRefSetId());
				final IndexBasedImportIndexServiceFeeder feeder = new IndexBasedImportIndexServiceFeeder();
				feeder.initContent(importIndexServerService, branchPath, subMonitor.newChild(1, SubMonitor.SUPPRESS_NONE));
				
				ApplicationContext.getInstance().registerService(ImportIndexServerService.class, importIndexServerService);
	
				Date lastUnitEffectiveTime = units.get(0).getEffectiveTime();
				
				for (final ComponentImportUnit subUnit : units) {
					
					/*
					 * First import unit seen with an effective time different from the previous set of import units;
					 * initialize taxonomy builder and update import index server service, then perform tagging if
					 * required.
					 * 
					 * Note that different effective times should only be seen in FULL or DELTA import, and the 
					 * collected values can be used as is.
					 */
					final Date currentUnitEffectiveTime = subUnit.getEffectiveTime();
					
					if (!Objects.equal(lastUnitEffectiveTime, currentUnitEffectiveTime)) {
						updateInfrastructure(units, branchPath, lastUnitEffectiveTime);
						updateCodeSystemMetadata(lastUnitEffectiveTime, importContext.isVersionCreationEnabled());
						lastUnitEffectiveTime = currentUnitEffectiveTime;
					}
						
					subUnit.doImport(subMonitor.newChild(1, SubMonitor.SUPPRESS_NONE));
				}
				
				updateInfrastructure(units, branchPath, lastUnitEffectiveTime);
				
				/*
				 * Use the last effective time as seen in import files for the final version creation.
				 * "lastUnitEffectiveTime" can not be used here, since this may be a SNAPSHOT import.
				 */
				updateCodeSystemMetadata(findMaximumEffectiveTime(units), importContext.isVersionCreationEnabled());
			}
			
		} finally {	
				
			if (ApplicationContext.getInstance().exists(ImportIndexServerService.class)) {
				ApplicationContext.getInstance().getService(ImportIndexServerService.class).dispose();
			}

			//dispose services
			if (ApplicationContext.getInstance().exists(ImportIndexServerService.class)) {
				ApplicationContext.getInstance().unregisterService(ImportIndexServerService.class);
			}

			if (ApplicationContext.getInstance().exists(Rf2BasedSnomedTaxonomyBuilder.class)) {
				ApplicationContext.getInstance().unregisterService(Rf2BasedSnomedTaxonomyBuilder.class);
			}
		}
		
	}

	private Date findMaximumEffectiveTime(List<ComponentImportUnit> units) {
		return new Rf2EffectiveTimeCollector().collectMaximumEffectiveTime(Iterables.transform(units, new Function<ComponentImportUnit, File>() {
			@Override public File apply(ComponentImportUnit unit) {
				return unit.getUnitFile();
			}
		}));
	}

	private boolean isTerminologyAvailable() {
		return ApplicationContext.getInstance().getService(SnomedTerminologyBrowser.class).isTerminologyAvailable(getImportBranchPath());
	}

	private IBranchPath getImportBranchPath() {
		return BranchPathUtils.createPath(importContext.getEditingContext().getTransaction());
	}

	private boolean isRefSetImport(final Iterable<? extends ComponentImportUnit> units) {
		for (final ComponentImportUnit unit : units) {
			if (!ComponentImportType.isRefSetType(unit.getType())) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void postImport(final SubMonitor subMonitor) {
	
		subMonitor.setWorkRemaining(importers.size());
		
		for (final Importer importer : importers) {
			importer.postImport(subMonitor.newChild(1, SubMonitor.SUPPRESS_NONE));
		}
	}

	private void updateInfrastructure(final List<ComponentImportUnit> units, final IBranchPath branchPath, final Date effectiveTime) {

		if (0 == importContext.getVisitedConcepts().size() && 0 == importContext.getVisitedRefSets().size()) {
			//nothing changed
			return;
		}
		
		String conceptFilePath = null;
		String descriptionFilePath = null;
		String relationshipFilePath = null;
		Set<String> languageFilePaths = newHashSet();
		
		for (final ComponentImportUnit unit : units) {
			
			// Consider all reference set files if importing a SNAPSHOT, check matching effective time otherwise 
			if (!importContext.isSlicingEnabled() || Objects.equal(effectiveTime, unit.getEffectiveTime())) {

				final String path = unit.getUnitFile().getAbsolutePath();
				
				switch (unit.getType()) {
					case CONCEPT: if (null == conceptFilePath) conceptFilePath = path;  break;
					case DESCRIPTION: if (null == descriptionFilePath) descriptionFilePath = path; break;
					case LANGUAGE_TYPE_REFSET: languageFilePaths.add(path); break;
					case RELATIONSHIP: if (null == relationshipFilePath) relationshipFilePath = path; break;
					default: /*intentionally ignored*/ break;
				}
			}
		}
		
		Rf2BasedSnomedTaxonomyBuilder currentBuilder = ApplicationContext.getInstance().getService(Rf2BasedSnomedTaxonomyBuilder.class);
		
		if (null == currentBuilder) {
			
			// First iteration: initialize release file-based builder with existing contents (if any)
			final SnomedTaxonomyBuilder baseBuilder = new SnomedTaxonomyBuilder(branchPath);
			baseBuilder.build();
			
			if (baseBuilder.getNodes().size() > 0) {
				final Rf2BasedSnomedTaxonomyBuilder rf2TaxonomyBuilder = Rf2BasedSnomedTaxonomyBuilder.newInstance(baseBuilder, conceptFilePath, relationshipFilePath);
				rf2TaxonomyBuilder.applyNodeChanges(conceptFilePath);
				rf2TaxonomyBuilder.applyEdgeChanges(relationshipFilePath);
				rf2TaxonomyBuilder.build();
				currentBuilder = rf2TaxonomyBuilder;
			} else {
				currentBuilder = new Rf2BasedSnomedTaxonomyBuilder(conceptFilePath, relationshipFilePath);
				currentBuilder.build();
			}
			
			ApplicationContext.getInstance().registerService(Rf2BasedSnomedTaxonomyBuilder.class, currentBuilder);
		
		} else {

			// ...then apply changes to the current builder to have the most up to date state
			((Rf2BasedSnomedTaxonomyBuilder) currentBuilder).applyNodeChanges(conceptFilePath);
			((Rf2BasedSnomedTaxonomyBuilder) currentBuilder).applyEdgeChanges(relationshipFilePath);
			currentBuilder.build();
		}
		
		final Set<String> synonymAndDescendants = LongSets.toStringSet(currentBuilder.getAllDescendantNodeIds(Concepts.SYNONYM));
		synonymAndDescendants.add(Concepts.SYNONYM);
		
		final ImportIndexServerService importIndexService = ApplicationContext.getInstance().getService(ImportIndexServerService.class);
		
		final Rf2BasedImportIndexServiceFeeder feeder = new Rf2BasedImportIndexServiceFeeder(
				descriptionFilePath, 
				languageFilePaths, 
				synonymAndDescendants, 
				getImportBranchPath());
		
		try {

			feeder.initContent(importIndexService, branchPath, new NullProgressMonitor());
			importMrcmRules(effectiveTime);
			initializeIndex(branchPath, importContext.isSlicingEnabled(), effectiveTime, units);
			
		} catch (final SnowowlServiceException e) {
			throw new ImportException(e);
		}
	}

	private void importMrcmRules(final Date effectiveTime) {
		final String userId = importContext.getUserId();
		final URI mrcmFileUri = MrcmFileRegistryImpl.INSTANCE.getMrcmFileUri();
		if (null != mrcmFileUri) {
			try {
				final File mrcmFile = copyContentToTempFile(mrcmFileUri.toURL());
				MrcmImporter.INSTANCE.doImport(userId, mrcmFile, false, importContext.getAggregator(EffectiveTimes.format(effectiveTime)));
			} catch (final MalformedURLException e) {
				getLogger().warn("Error while trying to load the content of the MRCM file. Ignoring MRCM import for SNOMED CT.", e);
			}
		}
	}

	private void initializeIndex(final IBranchPath branchPath, final boolean slicingEnabled, final Date effectiveTime, final List<ComponentImportUnit> units) {

		final SnomedRf2IndexInitializer snomedRf2IndexInitializer = new SnomedRf2IndexInitializer(branchPath, slicingEnabled, effectiveTime, units, importContext.getLanguageRefSetId());
		snomedRf2IndexInitializer.run(new NullProgressMonitor());
	}

	private void updateCodeSystemMetadata(final Date tagEffectiveTime, final boolean shouldCreateVersionAndTag) {
		
		final String formattedTagEffectiveTime = EffectiveTimes.format(tagEffectiveTime);
		final ICDOTransactionAggregator aggregator = importContext.getAggregator(formattedTagEffectiveTime);
		final SnomedEditingContext editingContext = importContext.getEditingContext();
		final CDOTransaction transaction = editingContext.getTransaction();
		
		try {
			
			final CodeSystemVersionGroup group = check(editingContext.getCodeSystemVersionGroup());
			
			if (group.getCodeSystems().isEmpty()) {
				group.getCodeSystems().add(new SnomedCodeSystemFactory().createNewCodeSystem());
			}
			
			boolean existingVersionFound = false;
			
			if (shouldCreateVersionAndTag) {
				for (final CodeSystemVersion codeSystemVersion : group.getCodeSystemVersions()) {
					if (tagEffectiveTime.equals(codeSystemVersion.getEffectiveDate())) {
						existingVersionFound = true;
						break;
					}
				}
				
				if (!existingVersionFound) {
					group.getCodeSystemVersions().add(createVersion(formattedTagEffectiveTime, tagEffectiveTime));
				} else {
					getLogger().warn("Not adding code system version entry for {}, a previous entry with the same effective time exists.", formattedTagEffectiveTime);
				}
			}
			
			new CDOServerCommitBuilder(importContext.getUserId(), importContext.getCommitMessage(), aggregator)
					.sendCommitNotification(false)
					.parentContextDescription(DatastoreLockContextDescriptions.IMPORT)
					.commit();
			
			if (!existingVersionFound && shouldCreateVersionAndTag) {
				final IBranchPath snomedBranchPath = BranchPathUtils.createPath(transaction);
				
				final ITagConfiguration configuration = TagConfigurationBuilder.createForRepositoryUuid(SnomedDatastoreActivator.REPOSITORY_UUID, formattedTagEffectiveTime)
					.setBranchPath(snomedBranchPath)
					.setUserId(importContext.getUserId())
					.setParentContextDescription(DatastoreLockContextDescriptions.IMPORT)
					.setShouldOptimizeIndex(true)
					.build();
				
				ApplicationContext.getInstance().getService(ITagService.class).tag(configuration);
			}
			
		} catch (final CommitException e) {
			throw new ImportException("Cannot create tag for SNOMED CT " + formattedTagEffectiveTime, e);
		} finally {
			importContext.setCommitTime(CDOServerUtils.getLastCommitTime(editingContext.getTransaction().getBranch()));
			final CDOCommitInfo commitInfo = createCommitInfo(importContext.getCommitTime(), importContext.getPreviousTime());
			CDOServerUtils.sendCommitNotification(commitInfo);
		}
	}

	private CDOCommitInfo createCommitInfo(final long timestamp, final long previousTimestamp) {
		
		final CDOTransaction transaction = importContext.getEditingContext().getTransaction();
		final CDOSession session = transaction.getSession();
		final CDORepositoryInfo info = session.getRepositoryInfo();
		final String repositoryUuid = info.getUUID();
		final IBranchPath branchPath = getImportBranchPath();
		
		return CDOCommitInfoUtils.createEmptyCommitInfo(repositoryUuid, branchPath, importContext.getUserId(), Strings.nullToEmpty(importContext.getCommitMessage()), timestamp, previousTimestamp);
		
	}
	
	private CodeSystemVersion createVersion(final String version, final Date effectiveDate) {

		final CodeSystemVersion codeSystemVersion = SnomedFactory.eINSTANCE.createCodeSystemVersion();
		codeSystemVersion.setImportDate(new Date());
		codeSystemVersion.setVersionId(version); 
		codeSystemVersion.setDescription("RF2 import of SNOMED Clinical Terms");
		codeSystemVersion.setEffectiveDate(effectiveDate);
		return codeSystemVersion;
	}
}
