/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.importer.rf2.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.slf4j.Logger;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongSet;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.datastore.cdo.ICDOTransactionAggregator;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.common.ContentSubType;
import com.b2international.snowowl.snomed.datastore.ISnomedPostProcessorContext;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.importer.rf2.refset.RefSetMemberLookup;
import com.b2international.snowowl.snomed.importer.rf2.terminology.ComponentLookup;
import com.b2international.snowowl.snomed.importer.rf2.util.EffectiveTimeBaseTransactionAggregatorSupplier;
import com.b2international.snowowl.snomed.importer.rf2.util.ImportUtil;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.google.common.base.Supplier;

/**
 * Collects common import state objects and makes them available to importers.
 */
public class SnomedImportContext implements ISnomedPostProcessorContext, AutoCloseable {

	private Logger logger;

	private ComponentLookup<Component> componentLookup;
	private ComponentLookup<SnomedRefSet> refSetLookup;
	private RefSetMemberLookup refSetMemberLookup;

	private String userId;
	private String commitId;
	private String commitMessage;

	private SnomedEditingContext editingContext;
	private Supplier<ICDOTransactionAggregator> aggregatorSupplier;
	private boolean commitNotificationEnabled;

	private long commitTime = CDOBranchPoint.UNSPECIFIED_DATE;
	private long previousTime = CDOBranchPoint.UNSPECIFIED_DATE;

	private ContentSubType contentSubType;
	private boolean versionCreationEnabled;

	private String[] sortedIgnoredRefSetIds;
	private File stagingDirectory;

	private final LongSet visitedConcepts = PrimitiveSets.newLongOpenHashSet();
	private final LongSet visitedRefSets = PrimitiveSets.newLongOpenHashSet();

	private String codeSystemShortName;
	private final RevisionIndex index;

	public SnomedImportContext(RevisionIndex index) {
		this.index = index;
	}

	@Override
	public void close() throws Exception {
		clear();

		if (getEditingContext() != null) {
			getEditingContext().close();
		}
	}

	/**
	 * Returns the last commit timestamp issued to the SNOMED CT repository.
	 * 
	 * @return the last commit timestamp, or {@link CDOBranchPoint#UNSPECIFIED_DATE} if no commit timestamp was set
	 */
	public long getCommitTime() {
		return commitTime;
	}

	/**
	 * Sets a new commit timestamp after committing to the SNOMED CT repository; the previous value gets stored, and it
	 * can be retrieved later via {@link #getPreviousTime()}.
	 * 
	 * @param commitTime the new commit timestamp to set
	 */
	public void setCommitTime(final long commitTime) {
		this.previousTime = this.commitTime;
		this.commitTime = commitTime;
	}

	/**
	 * Returns the previously set commit timestamp after a call to {@link #setCommitTime(long)}.
	 * 
	 * @return the previous commit timestamp, or {@link CDOBranchPoint#UNSPECIFIED_DATE} if the commit time has not been
	 * updated yet
	 */
	public long getPreviousTime() {
		return previousTime;
	}

	/**
	 * Returns the identifier of the user requesting the import (appearing as the author for import-related commit log
	 * entries).
	 * 
	 * @return the importing user's identifier
	 */
	@Override
	public String getUserId() {
		return userId;
	}

	/**
	 * Sets the identifier of the user requesting the import.
	 * 
	 * @param userId the new user identifier to set
	 */
	public void setUserId(final String userId) {
		this.userId = userId;
	}

	/**
	 * Returns the commit message to use for import commits.
	 * 
	 * @return the import commit message
	 */
	public String getCommitMessage() {
		return commitMessage;
	}

	/**
	 * Sets the commit message to use for import commits.
	 * 
	 * @param commitMessage the new import commit message to set (may not be {@code null})
	 */
	public void setCommitMessage(final String commitMessage) {
		this.commitMessage = checkNotNull(commitMessage, "Commit message argument may not be null.");
	}
	
	public String getCommitId() {
		return commitId;
	}
	
	public void setCommitId(final String commitId) {
		this.commitId = checkNotNull(commitId, "Commit id argument may not be null.");
	}

	/**
	 * Returns the in-memory component lookup map used during the import, which maps component identifiers to unsaved
	 * components/persisted storage keys.
	 * 
	 * @return the component lookup map
	 */
	public ComponentLookup<Component> getComponentLookup() {
		return componentLookup;
	}

	/**
	 * Returns the in-memory reference set lookup map used during the import, which maps reference set identifiers to
	 * unsaved reference sets/persisted storage keys.
	 * 
	 * @return the reference set lookup map
	 */
	public ComponentLookup<SnomedRefSet> getRefSetLookup() {
		return refSetLookup;
	}

	/**
	 * Returns the in-memory reference set member lookup map used during the import, which maps reference set member
	 * UUIDs to unsaved reference set members/persisted storage keys.
	 * 
	 * @return the reference set member lookup map
	 */
	public RefSetMemberLookup getRefSetMemberLookup() {
		return refSetMemberLookup;
	}

	/**
	 * Clears up the lookup maps associated with this import context.
	 */
	public void clear() {

		if (null != componentLookup) {
			componentLookup.clear();
		}

		if (null != refSetLookup) {
			refSetLookup.clear();
		}

		if (null != refSetMemberLookup) {
			refSetMemberLookup.clear();
		}
	}
	
	@Override
	public String branch() {
		return editingContext.getBranch();
	}

	/**
	 * Returns the editing context used for applying modifications on the SNOMED CT terminology, based on the incoming
	 * import files.
	 * 
	 * @return the editing context for this import run
	 */
	public SnomedEditingContext getEditingContext() {
		return editingContext;
	}

	/**
	 * Sets a new editing context to use for this import run, and initializes lookup mappings.
	 * 
	 * @param editingContext the new editing context to use (may not be {@code null})
	 */
	public void setEditingContext(final SnomedEditingContext editingContext) {
		this.editingContext = checkNotNull(editingContext, "Editing context argument may not be null.");

		componentLookup = new ComponentLookup<Component>(index, editingContext, Component.class);
		refSetLookup = new ComponentLookup<SnomedRefSet>(index, editingContext, SnomedRefSet.class);
		refSetMemberLookup = new RefSetMemberLookup(index, editingContext);
	}

	/**
	 * Checks whether commit notifications should be broadcast to connected clients, or if the import should remain
	 * "silent" until the very end of the process.
	 * 
	 * @return {@code true} if commit notifications should be sent on each import commit, {@code false} otherwise
	 */
	public boolean isCommitNotificationEnabled() {
		return commitNotificationEnabled;
	}

	/**
	 * Enables or disables commit notifications for this import run.
	 * 
	 * @param commitNotificationEnabled the desired new state for commit notifications
	 */
	public void setCommitNotificationEnabled(final boolean commitNotificationEnabled) {
		this.commitNotificationEnabled = commitNotificationEnabled;
	}

	/**
	 * Checks whether a tag should be created on each effective time transition, or at the end of the import process,
	 * depending on the import type.
	 * <p>
	 * A corresponding code system version is also registered as part of the tagging process.
	 * 
	 * @return {@code true} if {@code FULL} and {@code DELTA} imports should be tagged on each effective time transition
	 * and {@code SNAPSHOT} imports at the end, {@code false} if no version should be created
	 */
	public boolean isVersionCreationEnabled() {
		return versionCreationEnabled;
	}

	/**
	 * Enables or disables tag creation for this import run.
	 * 
	 * @param versionCreationEnabled the desired new state for tag creation
	 */
	public void setVersionCreationEnabled(final boolean versionCreationEnabled) {
		this.versionCreationEnabled = versionCreationEnabled;
	}

	/**
	 * Sets a {@link Supplier} returning a {@link ICDOTransactionAggregator transaction aggregator} for grouping
	 * separate CDO commits as a single entry.
	 * 
	 * @param aggregatorSupplier the new transaction aggregator supplier to set
	 */
	public void setAggregatorSupplier(final Supplier<ICDOTransactionAggregator> aggregatorSupplier) {
		this.aggregatorSupplier = aggregatorSupplier;
	}

	/**
	 * Returns a {@link ICDOTransactionAggregator transaction aggregator} instance for the specified effective time,
	 * which is either re-used for all collected effective times throughout the import (for {@code SNAPSHOT}s), or
	 * re-initalized on each effective time transition (for {@code FULL} imports and {@code DELTA}s).
	 * 
	 * @see #isSlicingEnabled()
	 * @param effectiveTime the effective time key for the aggregator to retrieve
	 * @return the transaction aggregator to use for committing changes
	 */
	public synchronized ICDOTransactionAggregator getAggregator(final String effectiveTime) {
		checkNotNull(effectiveTime, "Effective time argument cannot be null.");
		checkNotNull(aggregatorSupplier, "Aggregator supplier should be specified before getting transaction aggregator.");

		if (aggregatorSupplier instanceof EffectiveTimeBaseTransactionAggregatorSupplier) {
			return ((EffectiveTimeBaseTransactionAggregatorSupplier) aggregatorSupplier).get(effectiveTime);
		}

		return aggregatorSupplier.get();
	}

	/**
	 * Returns a {@link Logger} instance, which can be used to report diagnostic messages throughout the import process.
	 * 
	 * @return the logger to use for reporting messages
	 */
	@Override
	public Logger getLogger() {
		return logger;
	}

	/**
	 * Sets a new {@link Logger} instance to use during the import.
	 * 
	 * @param logger the new logger to use (may not be {@code null})
	 */
	public void setLogger(final Logger logger) {
		this.logger = checkNotNull(logger, "Logger argument may not be null.");
	}

	/**
	 * Returns the (usually temporary) directory in which split segments of import files are placed.
	 * 
	 * @return the staging directory for this import run 
	 */
	public File getStagingDirectory() {
		return stagingDirectory;
	}

	/**
	 * Registers a new staging directory for use during the import.
	 * 
	 * @param stagingDirectory the new staging directory to use (may not be {@code null})
	 */
	public void setStagingDirectory(final File stagingDirectory) {
		this.stagingDirectory = checkNotNull(stagingDirectory, "Staging directory argument may not be null.");
	}

	/**
	 * Checks whether the reference set with the specified identifier should be imported.
	 * 
	 * @param refSetId the reference set identifier to check
	 * @return {@code true} if contents for the given reference set should be ignored, {@code false} otherwise
	 */
	public boolean isRefSetIgnored(final String refSetId) {
		return Arrays.binarySearch(sortedIgnoredRefSetIds, refSetId) >= 0;
	}

	/**
	 * Specifies a new collections of reference sets to ignore.
	 * 
	 * @param ignoredRefSetIds the reference sets to ignore (may not be {@code null})
	 */
	public void setIgnoredRefSetIds(final Collection<String> ignoredRefSetIds) {
		checkNotNull(ignoredRefSetIds, "Ignored reference set identifiers collection may not be null.");
		final String[] sortedIgnoredRefSetIds = ignoredRefSetIds.toArray(new String[ignoredRefSetIds.size()]);
		Arrays.sort(sortedIgnoredRefSetIds);

		this.sortedIgnoredRefSetIds = sortedIgnoredRefSetIds;
	}

	/**
	 * Returns the currently set content subtype (import release type) for the import.
	 * 
	 * @see <a href="http://www.snomed.org/tig?t=tsg2_release_type_comp">7.2.1.1 Release Types</a> in the IHTSDO
	 * Technical Implementation Guide
	 * @return the content subtype for this import run
	 */
	public ContentSubType getContentSubType() {
		return contentSubType;
	}

	/**
	 * Sets a new content subtype (import release type) for this import run.
	 * 
	 * @param contentSubTypes the new content subtype to use
	 */
	public void setContentSubType(final ContentSubType contentSubTypes) {
		this.contentSubType = contentSubTypes;
	}

	/**
	 * Registers a concept which was "touched" by the import run. Visited concepts can be retrieved later for an import
	 * summary.
	 * <p>
	 * Concepts are registered here if an RF2 row for the concept, or an associated description or relationship appears
	 * in incoming release files, and any of the mentioned components is changed as a result.
	 * 
	 * @param conceptId the identifier of the visited concept
	 */
	public void conceptVisited(final String conceptId) {
		visitedConcepts.add(ImportUtil.parseLong(conceptId));
	}

	/**
	 * Registers a reference set which was "touched" by the import run. Visited reference sets can be retrieved later
	 * for an import summary.
	 * <p>
	 * Reference sets are registered here if an RF2 row for a reference set member appears in incoming release files,
	 * and the reference set or the member is changed as a result.
	 * 
	 * @param refSetId the identifier of the visited reference set
	 */
	public void refSetVisited(final String refSetId) {
		visitedRefSets.add(ImportUtil.parseLong(refSetId));
	}

	/**
	 * Returns a set of concept identifiers visited by the importer.
	 * 
	 * @return a set of visited concept identifiers
	 */
	public LongSet getVisitedConcepts() {
		return visitedConcepts;
	}

	/**
	 * Returns a set of reference set identifiers visited by the importer.
	 * 
	 * @return a set of visited reference set identifiers
	 */
	public LongSet getVisitedRefSets() {
		return visitedRefSets;
	}
	
	/**
	 * @return the snomedReleaseShortName
	 */
	public String getCodeSystemShortName() {
		return codeSystemShortName;
	}

	/**
	 * @param codeSystemShortName the snomedReleaseShortName to set
	 */
	public void setCodeSystemShortName(String codeSystemShortName) {
		this.codeSystemShortName = codeSystemShortName;
	}
}
