/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.slf4j.Logger;

import com.b2international.commons.functions.UncheckedCastFunction;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.datastore.CodeSystemVersions;
import com.b2international.snowowl.datastore.cdo.CDOCommitInfoUtils;
import com.b2international.snowowl.datastore.cdo.CDOServerCommitBuilder;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.datastore.server.CDOServerUtils;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.common.ContentSubType;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.importer.AbstractImportUnit;
import com.b2international.snowowl.snomed.importer.AbstractLoggingImporter;
import com.b2international.snowowl.snomed.importer.ImportException;
import com.b2international.snowowl.snomed.importer.Importer;
import com.b2international.snowowl.snomed.importer.rf2.model.AbstractSnomedImporter;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportUnit;
import com.b2international.snowowl.snomed.importer.rf2.model.EffectiveTimeUnitOrdering;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportContext;
import com.b2international.snowowl.terminologymetadata.CodeSystem;
import com.b2international.snowowl.terminologymetadata.CodeSystemVersion;
import com.b2international.snowowl.terminologyregistry.core.builder.CodeSystemVersionBuilder;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

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
	
	private Set<String> existingVersions;
	private List<CodeSystemVersion> versionsToCreate;
	
	public SnomedCompositeImporter(final Logger logger,
			final SnomedImportContext importContext,
			final List<Importer> importers, 
			final Ordering<AbstractImportUnit> unitOrdering) {
		super(logger);
		this.importContext = Preconditions.checkNotNull(importContext, "Import context argument cannot be null.");
		this.importers = ImmutableList.copyOf(checkNotNull(importers, "importers"));
		this.unitOrdering = checkNotNull(unitOrdering, "unitOrdering");
		this.versionsToCreate = newArrayList();
	}
	
	@Override
	public void preImport(final SubMonitor subMonitor) {
		
		subMonitor.setWorkRemaining(importers.size());
		
		for (final Importer importer : importers) {
			importer.preImport(subMonitor.newChild(1, SubMonitor.SUPPRESS_NONE));
		}
		
		collectExistingVersions();
	}

	private void collectExistingVersions() {
		CodeSystemEntry codeSystem = getCodeSystem();
		CodeSystemVersions codeSystemVersions = CodeSystemRequests.prepareSearchCodeSystemVersion()
			.all()
			.filterByCodeSystemShortName(codeSystem.getShortName())
			.build(SnomedDatastoreActivator.REPOSITORY_UUID)
			.execute(getEventBus())
			.getSync();
		
		existingVersions = FluentIterable.from(codeSystemVersions)
				.transform(version -> EffectiveTimes.format(version.getEffectiveDate(), DateFormats.SHORT))
				.toSet();
	}

	private CodeSystemEntry getCodeSystem() {
		try {
			
			return CodeSystemRequests.prepareGetCodeSystem(importContext.getCodeSystemShortName())
				.build(SnomedDatastoreActivator.REPOSITORY_UUID)
				.execute(getEventBus())
				.getSync();
			
		} catch (NotFoundException e) {
			throw new ImportException("Unable to find code system for short name %s.", importContext.getCodeSystemShortName(), e);
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

		// Exit early if there is no sub-import unit to import
		if (units.isEmpty()) {
			return new SnomedCompositeImportUnit(this, units);	
		}
		
		if (ContentSubType.SNAPSHOT.equals(importContext.getContentSubType())) {
			AbstractImportUnit latestUnit = EffectiveTimeUnitOrdering.INSTANCE.max(units);
			String latestKey = ((ComponentImportUnit) latestUnit).getEffectiveTimeKey();
			
			for (AbstractImportUnit unit : units) {
				((ComponentImportUnit) unit).setEffectiveTimeKey(latestKey);
			}
		}
		
		Collections.sort(units, unitOrdering);
		return new SnomedCompositeImportUnit(this, units);
	}

	@Override
	public void doImport(final SubMonitor subMonitor, final AbstractImportUnit unit) {
		final SnomedCompositeImportUnit compositeUnit = (SnomedCompositeImportUnit) unit;
		final UncheckedCastFunction<AbstractImportUnit, ComponentImportUnit> castFunction = new UncheckedCastFunction<AbstractImportUnit, ComponentImportUnit>(ComponentImportUnit.class);
		final List<ComponentImportUnit> units = Lists.newArrayList(Iterables.transform(compositeUnit.getUnits(), castFunction));
		
		if (units.size() == 0) {
			return;
		}
		
		subMonitor.setWorkRemaining(units.size() + 1);

		if (isRefSetImport(units)) {
			// enable commit notifications in case of refset import
			importContext.setCommitNotificationEnabled(true);
			
			String lastUnitEffectiveTimeKey = units.get(0).getEffectiveTimeKey();
			
			for (final ComponentImportUnit subUnit : units) {
				
				final String currentUnitEffectiveTimeKey = subUnit.getEffectiveTimeKey();
				
				if (!Objects.equal(lastUnitEffectiveTimeKey, currentUnitEffectiveTimeKey)) {
					createSnomedVersionFor(lastUnitEffectiveTimeKey);
					lastUnitEffectiveTimeKey = currentUnitEffectiveTimeKey;
				}
				
				subUnit.doImport(subMonitor.newChild(1, SubMonitor.SUPPRESS_NONE));
			}
			
			createSnomedVersionFor(lastUnitEffectiveTimeKey);
			
		} else {
			
			String lastUnitEffectiveTimeKey = units.get(0).getEffectiveTimeKey();
			
			for (final ComponentImportUnit subUnit : units) {
				
				/*
				 * First import unit seen with an effective time different from the previous set of import units;
				 * initialize taxonomy builder and update import index server service, then perform tagging if
				 * required.
				 * 
				 * Note that different effective times should only be seen in FULL or DELTA import, and the 
				 * collected values can be used as is.
				 */
				final String currentUnitEffectiveTimeKey = subUnit.getEffectiveTimeKey();
				
				if (!Objects.equal(lastUnitEffectiveTimeKey, currentUnitEffectiveTimeKey)) {
					createSnomedVersionFor(lastUnitEffectiveTimeKey);
					lastUnitEffectiveTimeKey = currentUnitEffectiveTimeKey;
				}
					
				subUnit.doImport(subMonitor.newChild(1, SubMonitor.SUPPRESS_NONE));
			}
			
			createSnomedVersionFor(lastUnitEffectiveTimeKey);
		}
	}

	private IBranchPath getImportBranchPath() {
		return BranchPathUtils.createPath(importContext.getEditingContext().getBranch());
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
		
		createNewVersions();
	}

	private void createNewVersions() {
		
		try (SnomedEditingContext codeSystemEditingContext = new SnomedEditingContext(BranchPathUtils.createMainPath())) {
			
			final CodeSystem codeSystem = codeSystemEditingContext.lookup(importContext.getCodeSystemShortName(), CodeSystem.class);
			codeSystem.getCodeSystemVersions().addAll(versionsToCreate);
			
			if (codeSystemEditingContext.isDirty()) {
				String commitMessage = String.format("Create %s SNOMED CT versions for branch '%s'", versionsToCreate.size(), getImportBranchPath().getPath());
				
				new CDOServerCommitBuilder(importContext.getUserId(), commitMessage, codeSystemEditingContext.getTransaction())
					.sendCommitNotification(false)
					.parentContextDescription(DatastoreLockContextDescriptions.IMPORT)
					.commit();
				
				Iterable<String> versionIds = FluentIterable.from(versionsToCreate)
						.transform(version -> String.format("[%s]", version.getVersionId()));
				
				String versionIdList = Joiner.on(", ").join(versionIds);
				getLogger().info("Version tags created: {}", versionIdList);
			}
			
		} catch (CommitException e) {
			throw new ImportException(String.format("Unable to commit SNOMED CT versions for branch %s.", getImportBranchPath().getPath()), e);
		}
	}

	private void createSnomedVersionFor(final String lastUnitEffectiveTimeKey) {
		
		try {

			if (AbstractSnomedImporter.UNPUBLISHED_KEY.equals(lastUnitEffectiveTimeKey)) {
				return;
			}
			
			boolean existingVersionFound = false;

			if (importContext.isVersionCreationEnabled()) {
				
				Set<String> existingEffectiveTimes = Sets.union(FluentIterable.from(versionsToCreate).transform(new Function<CodeSystemVersion, String>() {
					@Override public String apply(CodeSystemVersion input) {
						return EffectiveTimes.format(input.getEffectiveDate(), DateFormats.SHORT);
					}
				}).toSet(), existingVersions);
				
				for (String existingEffectiveTime : existingEffectiveTimes) {
					if (lastUnitEffectiveTimeKey.equals(existingEffectiveTime)) {
						existingVersionFound = true;
						break;
					}
				}
				
				if (!existingVersionFound) {
					versionsToCreate.add(createVersion(lastUnitEffectiveTimeKey));
				} else {
					getLogger().warn("Existing SNOMED CT version found for effective time {}.", lastUnitEffectiveTimeKey);
				}
			}
			
			if (!existingVersionFound && importContext.isVersionCreationEnabled()) {
				
				final IBranchPath snomedBranchPath = getImportBranchPath();
				final Date effectiveDate = EffectiveTimes.parse(lastUnitEffectiveTimeKey, DateFormats.SHORT);
				final String formattedEffectiveDate = EffectiveTimes.format(effectiveDate);
				
				RepositoryRequests
					.branching()
					.prepareCreate()
					.setParent(snomedBranchPath.getPath())
					.setName(formattedEffectiveDate)
					.build(SnomedDatastoreActivator.REPOSITORY_UUID)
					.execute(ApplicationContext.getServiceForClass(IEventBus.class))
					.getSync();
			}
			
		} finally {
			importContext.setCommitTime(CDOServerUtils.getLastCommitTime(importContext.getEditingContext().getTransaction().getBranch()));
			if (!importContext.isCommitNotificationEnabled()) {
				final CDOCommitInfo commitInfo = createCommitInfo(importContext.getCommitTime(), importContext.getPreviousTime());
				CDOServerUtils.sendCommitNotification(commitInfo);
			}
		}
	}

	private CDOCommitInfo createCommitInfo(final long timestamp, final long previousTimestamp) {
		
		final CDOTransaction transaction = importContext.getEditingContext().getTransaction();
		final CDOSession session = transaction.getSession();
		final CDORepositoryInfo info = session.getRepositoryInfo();
		final String repositoryUuid = info.getUUID();
		final IBranchPath branchPath = getImportBranchPath();
		
		return CDOCommitInfoUtils.createEmptyCommitInfo(repositoryUuid, branchPath, importContext.getUserId(), String.format("%s%s", importContext.getCommitId(), Strings.nullToEmpty(importContext.getCommitMessage())), timestamp, previousTimestamp);
		
	}
	
	private CodeSystemVersion createVersion(final String version) {
		final Date effectiveDate = EffectiveTimes.parse(version, DateFormats.SHORT);
		final String formattedEffectiveDate = EffectiveTimes.format(effectiveDate);
		
		return new CodeSystemVersionBuilder()
			.withVersionId(formattedEffectiveDate)
			.withDescription("SNOMED CT version created by an RF2 import process")
			.withImportDate(new Date())
			.withEffectiveDate(effectiveDate)
			.withParentBranchPath(getImportBranchPath().getPath())
			.build();
	}

	private IEventBus getEventBus() {
		return ApplicationContext.getServiceForClass(IEventBus.class);
	}
}
