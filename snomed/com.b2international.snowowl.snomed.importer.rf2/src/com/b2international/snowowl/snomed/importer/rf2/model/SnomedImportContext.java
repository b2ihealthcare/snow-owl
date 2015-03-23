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
package com.b2international.snowowl.snomed.importer.rf2.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.slf4j.Logger;

import com.b2international.commons.collections.LongSet;
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
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSortedSet;

/**
 * Collects common import state objects and makes them available to importers.
 *
 */
public class SnomedImportContext implements ISnomedPostProcessorContext, AutoCloseable {

	private String commitMessage;
	private ComponentLookup<Component> componentLookup;
	private SnomedEditingContext editingContext;
	private org.slf4j.Logger logger;
	private ComponentLookup<SnomedRefSet> refSetLookup;
	private RefSetMemberLookup<SnomedRefSetMember> refSetMemberLookup;
	private boolean slicingDisabled;
	private String[] sortedIgnoredRefSetIds;
	private File stagingDirectoryRoot;
	private String languageRefSetId;
	private Connection connection;
	private String userId;
	
	private final LongSet visitedConcepts = new LongSet();
	private final LongSet visitedRefSets = new LongSet();
	private ContentSubType contentSubType;
	private boolean sendCommitNotificaion;
	private Supplier<ICDOTransactionAggregator> aggregatorSupplier;
	private long commitTime = CDOBranchPoint.UNSPECIFIED_DATE;
	private long previousTime = CDOBranchPoint.UNSPECIFIED_DATE;
	private boolean createVersions;
	
	@Override
	public void close() throws Exception {
		if (getEditingContext() != null) {
			getEditingContext().close();
		}
	}
	
	/**
	 * @return the languageRefSetId
	 */
	public String getLanguageRefSetId() {
		return languageRefSetId;
	}

	/**
	 * @return the commitTime
	 */
	public long getCommitTime() {
		return commitTime;
	}

	/**
	 * @param commitTime the commitTime to set
	 */
	public void setCommitTime(long commitTime) {
		this.commitTime = commitTime;
	}

	/**
	 * @return the previousTime
	 */
	public long getPreviousTime() {
		return previousTime;
	}

	/**
	 * @param previousTime the previousTime to set
	 */
	public void setPreviousTime(long previousTime) {
		this.previousTime = previousTime;
	}

	/**
	 * Returns with the requesting user ID.
	 * @return the user ID.
	 */
	@Override
	public String getUserId() {
		return userId;
	}

	/**
	 * Sets the user ID.
	 * @param userId the requesting user ID.
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @param languageRefSetId the languageRefSetId to set
	 */
	public void setLanguageRefSetId(final String languageRefSetId) {
		this.languageRefSetId = languageRefSetId;
	}

	public String getCommitMessage() {
		return commitMessage;
	}

	public ComponentLookup<Component> getComponentLookup() {
		return componentLookup;
	}

	@Override
	public SnomedEditingContext getEditingContext() {
		return editingContext;
	}

	public void setAggregatorSupplier(Supplier<ICDOTransactionAggregator> aggregatorSupplier) {
		this.aggregatorSupplier = aggregatorSupplier;
	}

	public synchronized ICDOTransactionAggregator getAggragator(final String effectiveTime) {
		
		Preconditions.checkNotNull(effectiveTime, "Effective time argument cannot be null.");
		Preconditions.checkNotNull(effectiveTime, "Aggregator supplier should be specified before getting transaction aggregator.");
			
		if (aggregatorSupplier instanceof EffectiveTimeBaseTransactionAggregatorSupplier) {
			
			return ((EffectiveTimeBaseTransactionAggregatorSupplier) aggregatorSupplier).get(effectiveTime);
			
		}
		
		return aggregatorSupplier.get();
		
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	public ComponentLookup<SnomedRefSet> getRefSetLookup() {
		return refSetLookup;
	}

	public File getStagingDirectoryRoot() {
		return stagingDirectoryRoot;
	}

	public boolean isRefSetIgnored(final String refSetId) {
		return Arrays.binarySearch(sortedIgnoredRefSetIds, refSetId) >= 0;
	}

	public boolean isSlicingDisabled() {
		return slicingDisabled;
	}

	public void setCommitMessage(final String commitMessage) {
		this.commitMessage = checkNotNull(commitMessage, "commitMessage");
	}

	public void setEditingContext(final SnomedEditingContext editingContext) {
		this.editingContext = checkNotNull(editingContext, "editingContext");
		componentLookup = new ComponentLookup<Component>(editingContext, Component.class);
		refSetLookup = new ComponentLookup<SnomedRefSet>(editingContext, SnomedRefSet.class);
		refSetMemberLookup = new RefSetMemberLookup<SnomedRefSetMember>(editingContext);
	}
	
	public void setIgnoredRefSetIds(final Collection<String> ignoredRefSetIds) {
		final ImmutableSortedSet<String> sortedSet = ImmutableSortedSet.copyOf(checkNotNull(ignoredRefSetIds, "ignoredRefSetIds"));
		this.sortedIgnoredRefSetIds = sortedSet.toArray(new String[sortedSet.size()]);
	}
	
	public void setLogger(final Logger logger) {
		this.logger = checkNotNull(logger, "logger");
	}
	
	public void setSlicingDisabled(final boolean slicingDisabled) {
		this.slicingDisabled = slicingDisabled;
	}
	
	public void setStagingDirectoryRoot(final File stagingDirectoryRoot) {
		this.stagingDirectoryRoot = checkNotNull(stagingDirectoryRoot, "stagingDirectoryRoot");
	}
	
	public void conceptVisited(final String conceptId) {
		visitedConcepts.add(ImportUtil.parseLong(conceptId));
	}
	
	public void refSetVisited(final String refSetId) {
		visitedRefSets.add(ImportUtil.parseLong(refSetId));
	}
	
	public LongSet getVisitedConcepts() {
		return visitedConcepts;
	}
	
	public LongSet getVisitedRefSets() {
		return visitedRefSets;
	}
	
	public RefSetMemberLookup<SnomedRefSetMember> getRefSetMemberLookup() {
		return refSetMemberLookup;
	}
	
	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	/**
	 * Cleans up the underlying lookup services.
	 */
	public void clears() {
		
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

	public void setContentSubType(ContentSubType contentSubTypes) {
		this.contentSubType = contentSubTypes;
	}
	
	public ContentSubType getContentSubType() {
		return contentSubType;
	}

	public void setSendCommitNotificaion(boolean sendCommitNotificaion) {
		this.sendCommitNotificaion = sendCommitNotificaion;
	}
	
	public boolean isSendCommitNotificaion() {
		return sendCommitNotificaion;
	}
	
	public void setCreateVersions(boolean createVersions) {
		this.createVersions = createVersions;
	}
	
	public boolean isCreateVersions() {
		return createVersions;
	}
}