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
package com.b2international.snowowl.datastore.server;

import static com.b2international.snowowl.datastore.BranchPathUtils.createPath;
import static com.google.common.base.Preconditions.checkNotNull;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.server.TransactionCommitContext;
import org.eclipse.emf.cdo.server.IRepository.WriteAccessHandler;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;
import org.eclipse.emf.cdo.spi.server.InternalView;
import org.eclipse.emf.cdo.spi.server.ObjectWriteAccessHandler;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.exceptions.ApiException;
import com.b2international.snowowl.datastore.CDOCommitChangeSet;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.cdo.CDOServerCommitBuilder;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapMaker;

/**
 * Delegates to a {@link DelegateCDOServerChangeManager} instance, based on the affected branch.
 * Uses per-branch locking to avoid overlap while processing subsequent commits on the same branch.
 * 
 * @see WriteAccessHandler
 * @see ObjectWriteAccessHandler
 */
@SuppressWarnings("restriction")
public class CDOServerChangeManager extends ObjectWriteAccessHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger("repository");
	private static final String COMMIT_CONTEXT_INFO_TEMPLATE = "[User ID: {0}, Repository: {1}, Branch: {2}]";

	private final ConcurrentMap<TransactionCommitContext, DelegateCDOServerChangeManager> activeChangeManagers;
	private final Collection<CDOChangeProcessorFactory> factories;
	private final String repositoryName;

	
	public CDOServerChangeManager(final String repositoryUuid, final String repositoryName) {
		super(false);
		this.repositoryName = repositoryName;
		this.factories = CDOChangeProcessorFactoryManager.INSTANCE.getFactories(checkNotNull(repositoryUuid, "Repository UUID argument cannot be null."));
		this.activeChangeManagers = new MapMaker().makeMap();
	}

	@Override
	public void handleTransactionBeforeCommitting(final OMMonitor monitor, final TransactionCommitContext commitContext) throws RuntimeException {
		
		final String commitContextInfo = getCommitContextInfo(commitContext);
		LOGGER.info("Processing changes for semantic indexes... " + commitContextInfo);
		monitor.begin();
		
		Async async = null;
		
		try {
			
			async = monitor.forkAsync();
			final DelegateCDOServerChangeManager delegate = createChangeManager(commitContext);
			
			activeChangeManagers.put(commitContext, delegate);
			delegate.handleTransactionBeforeCommitting();
			
			LOGGER.info("Semantic index change processing successfully finished. " + commitContextInfo);
			LOGGER.info("Persisting changes into repository... " + commitContextInfo);
		} catch (ApiException e) {
			throw e;
		} catch (final Throwable t) {
			throw SnowowlRuntimeException.wrap(t);
		} finally {
			if (null != async) {
				async.stop();
			}
			monitor.done();
		}
	}

	@Override
	public void handleTransactionAfterCommitted(final OMMonitor monitor, final TransactionCommitContext commitContext) {
		try {
			final String commitContextInfo = getCommitContextInfo(commitContext);
			final DelegateCDOServerChangeManager delegate = activeChangeManagers.remove(commitContext);
//			LOGGER.info("Changes have been successfully persisted into repository. {}", commitContextInfo);
//			LOGGER.info("Flushing changes into semantic indexes... {}", commitContextInfo);
			delegate.handleTransactionAfterCommitted();
//			LOGGER.info("Changes have been successfully persisted into semantic indexes. {}", commitContextInfo);
			closeChangeSetView(commitContext, delegate);
		} catch (final Throwable e) {
			throw SnowowlRuntimeException.wrap(e);
		}
	}

	private void closeChangeSetView(final TransactionCommitContext commitContext, final DelegateCDOServerChangeManager delegate) {
		final CDOView changeSetView = delegate.getCommitChangeSet().getView();
		
		if (changeSetView.isClosed()) {
			return;
		}
		
		LOGGER.info("Cleaning up resources... " + getCommitContextInfo(commitContext));
		
		changeSetView.close();
		
		final int sessionViewId = changeSetView.getViewID();
		
		if (sessionViewId < InternalSession.TEMP_VIEW_ID) {
			final InternalSession session = commitContext.getTransaction().getSession();
			if (session != null) {
				final InternalView internalView = session.getView(sessionViewId);
				session.viewClosed(internalView);
			}
		}
		
		LOGGER.info("Resources have been properly cleaned up. " + getCommitContextInfo(commitContext));
	}

	@Override
	public void handleTransactionRollback(final TransactionCommitContext commitContext) {
		
		LOGGER.info("Discarding changes in semantic indexes... " + getCommitContextInfo(commitContext));
		
		final DelegateCDOServerChangeManager delegate = activeChangeManagers.remove(commitContext);
		
		if (null == delegate) {
			LOGGER.info("No changes to discard in semantic indexes. Reason: no change managers were registered. " + getCommitContextInfo(commitContext));
			return;
		}
		
		try {
			delegate.handleTransactionRollback();
		} catch (final Throwable e) {
			throw SnowowlRuntimeException.wrap(e);
		} finally {
			closeChangeSetView(commitContext, delegate);
		}
		LOGGER.info("Changes have been successfully discarded in semantic indexes. " + getCommitContextInfo(commitContext));
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("Change processor factories", Arrays.toString(getFactoryNames().toArray()))
				.toString();
	}

	@Override
	protected CDOView getView(final TransactionCommitContext commitContext) {
		
		final InternalTransaction transaction = commitContext.getTransaction();
		final InternalRepository repository = transaction.getRepository();
		final CDORevisionProvider sessionRevisionProvider = new RepositoryRevisionProvider(repository, commitContext.getBranchPoint());
		final CDODelegatingCommitContext delegatingCommitContext = new CDODelegatingCommitContext(commitContext, sessionRevisionProvider);
		
		final InternalView sessionView = transaction.getSession().openView(InternalSession.TEMP_VIEW_ID, transaction);

		final org.eclipse.emf.cdo.internal.server.ServerCDOView view = new ServerCDOView2(sessionView, transaction.getSession(), transaction, false, delegatingCommitContext);
		
		return view;
	}
	
	public static final class ServerCDOView2 extends org.eclipse.emf.cdo.internal.server.ServerCDOView {

		private final InternalView internalView;

		public ServerCDOView2(InternalView internalView, InternalSession session, CDOBranchPoint branchPoint, boolean legacyModeEnabled, CDORevisionProvider revisionProvider) {
			super(session, branchPoint, legacyModeEnabled, revisionProvider);
			this.internalView = internalView;
		}
		
		@Override public int getViewID() {
			return internalView.getViewID();
		}
		
	}

	private String getCommitContextInfo(final TransactionCommitContext context) {
		return MessageFormat.format(COMMIT_CONTEXT_INFO_TEMPLATE, getUserId(context), repositoryName, getBranchPath(context));
	}
	
	/*returns with a collection of human readable name of the factories. could be empty.*/
	private Collection<String> getFactoryNames() {
		return Collections2.transform(factories, new Function<CDOChangeProcessorFactory, String>() {
			@Override public String apply(final CDOChangeProcessorFactory factory) {
				return String.valueOf(factory.getFactoryName());
			}
		});
	}

	private String getUserId(final TransactionCommitContext context) {
		return checkNotNull(context, "context").getUserID();
	}
	
	/*creates the commit change set from the underlying transaction commit context*/
	private ICDOCommitChangeSet getCommitChangeSet(final TransactionCommitContext commitContext) {
		
		final CDOView view = getView(commitContext);
		return new CDOCommitChangeSet(
				view,
				commitContext.getUserID(),
				commitContext.getCommitComment(),
				getCdoObjects(commitContext.getNewObjects(), view), 
				getCdoObjects(commitContext.getDirtyObjects(), view), 
				getDetachedObjectTypes(commitContext),
				getRevisionDeltas(commitContext.getDirtyObjectDeltas()),
				commitContext.getTimeStamp());
	}

	private Collection<CDOObject> getCdoObjects(final CDORevision[] revisions, final CDOView view) {
		return FluentIterable
				.from(ImmutableList.copyOf(revisions))
				.transform(new Function<CDORevision, CDOObject>() {
					@Override public CDOObject apply(final CDORevision input) { return view.getObject(input.getID()); }
				})
				.toList();
	}

	private Map<CDOID, CDORevisionDelta> getRevisionDeltas(final CDORevisionDelta[] revisionDeltas) {
		return FluentIterable
				.from(ImmutableList.copyOf(revisionDeltas))
				.uniqueIndex(new Function<CDORevisionDelta, CDOID>() {
					@Override public CDOID apply(final CDORevisionDelta input) { return input.getID(); }
				});
	}
	
	/*creates a change manager instance from the underlying transaction commit context*/
	private DelegateCDOServerChangeManager createChangeManager(final TransactionCommitContext commitContext) {
		final boolean isCommitNotificationEnabled = isCommitNotificationEnabled(commitContext);
		return new DelegateCDOServerChangeManager(getCommitChangeSet(commitContext), factories, true, isCommitNotificationEnabled);
	}
	
	private boolean isCommitNotificationEnabled(TransactionCommitContext commitContext) {
		if (commitContext instanceof CDOServerCommitBuilder.ServerTransactionCommitContext) {
			return ((CDOServerCommitBuilder.ServerTransactionCommitContext) commitContext).isCommitNotificationEnabled();
		} else {
			// by default all non server commits should send out commit notifications
			return true;
		}
	}

	/*returns with the branch path extracted from the underlying transaction commit context.*/
	private IBranchPath getBranchPath(final TransactionCommitContext commitContext) {
		return createPath(checkNotNull(checkNotNull(checkNotNull(commitContext, "commitContext").getTransaction(), "transaction").getBranch()));
	}

	/*returns with a map of CDO IDs and class identifying the detached objects*/
	private Map<CDOID, EClass> getDetachedObjectTypes(final TransactionCommitContext commitContext) {
		final IBranchPath branchPath = getBranchPath(commitContext);
		final CDOID[] detachedObjects = commitContext.getDetachedObjects();
		final ImmutableMap.Builder<CDOID, EClass> builder = ImmutableMap.builder();
		
		for (final CDOID detachedObject : detachedObjects) {
			final EClass eClass = EClassProviderBroker.INSTANCE.getEClass(branchPath, detachedObject);
			
			if (eClass != null) {
				builder.put(detachedObject, eClass);
			} else {
				// Not all EClasses can be retrieved via a detached object EClass provider, but log it regardless
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("EClass cannot be found for CDO ID " + detachedObject);
				}
			}
		}			
		
		return builder.build();
	}
	
	/**
	 * CDO revision provider working from the specified repository.
	 */
	private static class RepositoryRevisionProvider implements CDORevisionProvider {

		private final InternalRepository repository;
		private final CDOBranchPoint branchPoint;
		
		public RepositoryRevisionProvider(final InternalRepository repository, final CDOBranchPoint branchPoint) {
			this.repository = repository;
			this.branchPoint = branchPoint;
		}

		@Override
		public CDORevision getRevision(final CDOID id) {
			final InternalCDORevision revision = repository.getRevisionManager().getRevision(id, branchPoint, CDORevision.UNCHUNKED, 0, true);
			repository.ensureChunks(revision);
			return revision;
		}
	}
}
