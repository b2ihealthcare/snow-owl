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
package com.b2international.snowowl.datastore.cdo;

import static com.b2international.snowowl.datastore.BranchPointUtils.create;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.ref.ReferenceType;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IBranchPoint;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.connection.RepositoryConnectionConfiguration;
import com.b2international.snowowl.identity.domain.User;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.primitives.Longs;

/**
 * The CDOConnection object wraps the client side elements of the connection to the server:
 * <li>the {@link CDOSession}
 * <li>the {@link User} credentials
 * <li>the custom client side {@link SnowowlClientProtocol} for net4j communication
 * <li>{@link ICDOBranchActionManager}
 *
 */
/*default*/ class CDOConnection extends CDOManagedItem<ICDOConnection> implements ICDOConnection {

	private static final int COMMIT_MONITOR_TIMEOUT_SECONDS = 1000;
	
	/**
	 * Comparator for comparing branches based on their base timestamps.
	 */
	Comparator<CDOBranch> BRANCH_BASE_COMPARATOR = new Comparator<CDOBranch>() {
		@Override public int compare(final CDOBranch o1, final CDOBranch o2) {
			return Longs.compare(o1.getBase().getTimeStamp(), o2.getBase().getTimeStamp());
		}
	};

	/**
	 * Predicate for comparing branches by their paths.
	 * 
	 * @see ICDOBranchActionManager
	 */
	public static class BranchPathPredicate implements Predicate<CDOBranch> {
		
		private final String branchPath;

		public BranchPathPredicate(final IBranchPath branchPath) {
			checkNotNull(branchPath, "Branch path argument cannot be null.");
			this.branchPath = branchPath.getPath();
		}

		@Override public boolean apply(final CDOBranch branch) {
			checkNotNull(branch, "CDO branch cannot be null.");
			checkNotNull(branch.getPathName(), "Branch path cannot be null for branch: %s", branch);
			return branchPath.equals(branch.getPathName());
		}
	}

	/*default*/ CDOConnection(final String repositoryUuid, @Nullable final String repositoryName, final byte namespaceId, 
			@Nullable final String toolingId, @Nullable final String dependsOnRepositoryUuid, final boolean meta) {
		super(repositoryUuid, repositoryName, namespaceId, toolingId, dependsOnRepositoryUuid, meta);
	}
	
	@Override
	public CDOView createView() {
		return createView(getMainBranch());
	}
	
	@Override
	public CDOView createView(final IBranchPoint branchPoint) {
		checkNotNull(branchPoint, "Branch point argument cannot be null.");
		final CDOBranch branch = checkNotNull(getBranch(branchPoint.getBranchPath()), "Branch '%s' does not exist.",
				branchPoint.getBranchPath());
		return createView(branch, branchPoint.getTimestamp());
	}
	
	@Override
	public CDOView createView(final IBranchPath branchPath) {
		return createView(create(getUuid(), branchPath));
	}
	
	@Override
	public CDOView createView(final CDOBranch branch) {
		return getSession().openView(branch);
	}

	@Override
	public CDOView createView(final CDOBranch branch, final long timeStamp) {
		return createView(branch, timeStamp, true);
	}
	
	@Override
	public CDOView createView(final CDOBranch branch, final long timeStamp, final boolean shouldInvalidate) {
		return getSession().openView(branch, timeStamp, shouldInvalidate);
	}
	
	@Override
	public CDOTransaction createTransaction(final CDOBranch branch) {
		final CDOTransaction transaction = getSession().openTransaction(branch);
		transaction.options().setCacheReferenceType(ReferenceType.WEAK);
		return transaction;
	}
	
	@Override
	public CDOTransaction createTransaction(final IBranchPath branchPath) {
		final CDOBranch branch = Preconditions.checkNotNull(getBranch(branchPath), "Branch '%s' does not exist.", branchPath);
		return createTransaction(branch);
	}
	
	@Override
	public CDONet4jSession getSession() {
		return getSessionConfiguration().openNet4jSession();
	}

	@Override
	public CDOBranch getMainBranch() {
		return getSession().getBranchManager().getMainBranch();
	}

	@Override
	public CDOBranch getBranch(final IBranchPath branchPath) {
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		final List<CDOBranch> branches = getBranches(branchPath);
		return Iterables.getLast(branches, null);
	}
	
	@Override
	public CDOBranch getOldestBranch(final IBranchPath branchPath) {
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		final List<CDOBranch> branches = getBranches(branchPath);
		return Iterables.getFirst(branches, null);
	}
	
	@Override
	public CDOPackageRegistry getPackageRegistry() {
		return getSession().getPackageRegistry();
	}
	
	@Override
	public String toString() {
		return getRepositoryName();
	}
	
	@Override
	protected void doDeactivate() throws Exception {
		LifecycleUtil.deactivate(getSession());
	}

	@Override
	protected void doActivate() throws Exception {
		openCdoSession();
	}

	@Override
	protected CDOConnectionManager getContainer() {
		return (CDOConnectionManager) super.getContainer();
	}
	
	@Override
	public CDONet4jSessionConfiguration getSessionConfiguration() {
		return getContainer().getSessionConfiguration(this);
	}
	
	/* 
	 * Returns all branches matching the specified path, sorted by base timestamp (ascending) --per level--. 
	 *
	 * The last item of the list is the most recent branch with the specified path, 
	 * on the most recent branch of the parent path,
	 * on the most recent branch of the parent's parent path, 
	 * etc.
	 */
	private List<CDOBranch> getBranches(final IBranchPath branchPath) {
		if (BranchPathUtils.isMain(branchPath)) {
			return ImmutableList.of(getMainBranch());
		}
		
		final List<CDOBranch> parents = getBranches(branchPath.getParent());
		final List<CDOBranch> results = Lists.newArrayList();
		
		for (final CDOBranch parent : parents) {
			final ImmutableSortedSet<CDOBranch> resultsForParent = FluentIterable
				.from(ImmutableList.copyOf(parent.getBranches()))
				.filter(new BranchPathPredicate(branchPath))
				.toSortedSet(BRANCH_BASE_COMPARATOR);
			
			results.addAll(resultsForParent);
		}
		
		return results;
	}

	private CDONet4jSession openCdoSession() {
		
		// Open the CDO session while the signal timeout is still set to the same value as the connection timeout
		final CDONet4jSession cdoSession = getSession();
		final CDONet4jSession.Options cdoSessionOptions = cdoSession.options();
		final RepositoryConnectionConfiguration configuration = getRepositoryConfiguration();

		/*
		 * Update signal timeout to the configured value, allow commit progress monitor to stall for a longer time (when
		 * committing lots of changes with MySQL, the monitor can not be updated every 10 seconds as the default value
		 * would require).
		 */
		cdoSessionOptions.getNet4jProtocol().setTimeout(configuration.getSignalTimeout());
		cdoSessionOptions.setCommitTimeout(COMMIT_MONITOR_TIMEOUT_SECONDS);

		// Adjust collection loading policy; it needs to be the same both on the client and the server
		cdoSessionOptions.setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(0, configuration.getResolveChunkSize()));
		
		return cdoSession;
	}

	private RepositoryConnectionConfiguration getRepositoryConfiguration() {
		return ApplicationContext.getServiceForClass(SnowOwlConfiguration.class).getModuleConfig(RepositoryConnectionConfiguration.class);
	}
}