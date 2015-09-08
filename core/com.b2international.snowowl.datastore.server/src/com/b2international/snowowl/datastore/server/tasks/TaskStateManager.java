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
package com.b2international.snowowl.datastore.server.tasks;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.core.api.index.IIndexUpdater;
import com.b2international.snowowl.core.api.index.IndexException;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.TaskBranchPathMap;
import com.b2international.snowowl.datastore.UserBranchPathMap;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.config.RepositoryConfiguration;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.datastore.net4j.push.PushServiceException;
import com.b2international.snowowl.datastore.server.index.IndexServerServiceManager;
import com.b2international.snowowl.datastore.server.net4j.push.PushServerService;
import com.b2international.snowowl.datastore.store.SingleDirectoryIndexImpl;
import com.b2international.snowowl.datastore.tasks.ITaskContext;
import com.b2international.snowowl.datastore.tasks.ITaskStateManager;
import com.b2international.snowowl.datastore.tasks.Task;
import com.b2international.snowowl.datastore.tasks.TaskChangeNotificationMessage;
import com.b2international.snowowl.datastore.tasks.TaskClosedNotificationMessage;
import com.b2international.snowowl.datastore.tasks.TaskContextManager;
import com.b2international.snowowl.datastore.tasks.TaskHibernatedNotificationMessage;
import com.b2international.snowowl.datastore.tasks.TaskScenario;
import com.b2international.snowowl.eventbus.IEventBus;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.ImmutableList;

/**
 * 
 */
public class TaskStateManager extends SingleDirectoryIndexImpl implements ITaskStateManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(TaskStateManager.class);

	private final class UsageTimeLoader extends CacheLoader<String, AtomicLong> {
		@Override 
		public AtomicLong load(final String key) throws Exception {
			return new AtomicLong();
		}
	}

	private final class UsageTimeRemovalListener implements RemovalListener<String, AtomicLong> {

		private static final String UNKNOWN_TIME_STRING = "a while";

		@Override
		public void onRemoval(final RemovalNotification<String, AtomicLong> notification) {

			final String taskId = notification.getKey();
			final AtomicLong usageTime = notification.getValue();

			final TaskBranchPathMap taskBranchPathMap = getTaskBranchPathMap(taskId);
			if (null != taskBranchPathMap) {
				closeIndexServicesForTask(taskBranchPathMap);
			}

			final String message = MessageFormat.format("No modifications have happened on this task for {0}. "
					+ "Snow Owl will now deactivate the task to save disk space; to continue working on this task, "
					+ "please reactivate it from the Task List view.", getTimeString(usageTime));

			try {
				PushServerService.INSTANCE.push(ITaskStateManager.PROTOCOL_NAME, new TaskHibernatedNotificationMessage(taskId, message));
			} catch (final PushServiceException e) {
				LOGGER.error("Could not push task inactivity notification to recipients.", e);
			}
		}

		private String getTimeString(final AtomicLong elapsedMinutes) {

			for (final TimeUnit unitToCheck : ImmutableList.of(TimeUnit.HOURS, TimeUnit.MINUTES, TimeUnit.SECONDS)) {
				final long elapsedTimeInUnits = unitToCheck.convert(elapsedMinutes.get(), TimeUnit.MINUTES);

				if (elapsedTimeInUnits > 0L) {
					final String unitLabel = getUnitLabel(unitToCheck, elapsedTimeInUnits > 1L);
					return elapsedTimeInUnits + " " + unitLabel; 
				}
			}

			return UNKNOWN_TIME_STRING;
		}

		private String getUnitLabel(final TimeUnit timeUnit, final boolean plural) {

			final String unitSingular;

			switch (timeUnit) {
			case HOURS:
				unitSingular = "hour";
				break;
			case MINUTES:
				unitSingular = "minute";
				break;
			case SECONDS:
				unitSingular = "second";
				break;
			default:
				throw new IllegalStateException("Unexpected time unit: " + timeUnit);
			}

			return unitSingular + (plural ? "s" : "");
		}
	}

	private final class CleanupTimerTask extends TimerTask {

		/*
		 * (non-Javadoc)
		 * @see java.util.TimerTask#run()
		 */
		@Override 
		public void run() {
			closeIndexServices();
		}

		private void closeIndexServices() {

			for (final String taskId : usageTimeCache.asMap().keySet()) {

				final AtomicLong usageTime = usageTimeCache.getIfPresent(taskId);

				//expireTimeMinutes means infinite - do not invalidate the task index
				if (null == usageTime || expireTimeMinutes == 0) {
					continue;
				}

				if (usageTime.incrementAndGet() > expireTimeMinutes) {
					usageTimeCache.invalidate(taskId);
				}
			}
		}
	}

	private static final String FIELD_TASK_ID = "taskId";
	private static final String FIELD_BRANCH_PATH_ENTRY = "branchPathEntry";
	private static final String FIELD_IS_CLOSED = "isClosed";
	private static final String FIELD_CONTEXT_ID = "contextId";
	private static final String FIELD_REPOSITORY_URL = "repositoryUrl";
	private static final String FIELD_DESCRIPTION = "description";
	private static final String FIELD_SCENARIO_ORDINAL = "scenarioOrdinal";
	private static final String FIELD_USER_ID = "userId";
	private static final String FIELD_VERSION_CONFIGURATION_USER_ID = "versionConfigurationUserId";
	
	private final IEventBus eventBus;

	private static Supplier<Timer> CLEANUP_TIMER = Suppliers.memoize(new Supplier<Timer>() { @Override public Timer get() {
		return new Timer("Task cleanup timer", true);
	}});

	// Check every minute if there's something to be done
	private static final long CLEANUP_INTERVAL_MILLIS = TimeUnit.MINUTES.toMillis(1L);
	

	private final LoadingCache<String, AtomicLong> usageTimeCache;
	private final long expireTimeMinutes;
	private final TimerTask cleanupTask;

	/**
	 * 
	 * @param indexRootPath
	 */
	public TaskStateManager(final File indexRootPath) {
		super(indexRootPath);

		this.usageTimeCache = CacheBuilder.newBuilder()
				.removalListener(new UsageTimeRemovalListener())
				.build(new UsageTimeLoader());

		final RepositoryConfiguration serverConfiguration = ApplicationContext.getInstance().getServiceChecked(SnowOwlConfiguration.class).getModuleConfig(RepositoryConfiguration.class);
		this.expireTimeMinutes = serverConfiguration.getIndexTimeout();
		this.cleanupTask = new CleanupTimerTask();
		eventBus = ApplicationContext.getInstance().getServiceChecked(IEventBus.class);
		CLEANUP_TIMER.get().schedule(cleanupTask, CLEANUP_INTERVAL_MILLIS, CLEANUP_INTERVAL_MILLIS);
	}

	/**
	 * (non-API)
	 * 
	 * @param taskId the identifier of the task whose last access time should be updated
	 */
	public void touch(final String taskId) {

		if (null == getTask(taskId)) {
			return;
		}
		
		usageTimeCache.getUnchecked(taskId).set(0);
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.tasks.ITaskStateManager#isPromoted(java.lang.String)
	 */
	@Override
	public boolean isClosed(final String taskId) {
		return documentExists(Mappings.newQuery()
				.field(FIELD_TASK_ID, taskId)
				.field(FIELD_IS_CLOSED, "1")
				.matchAll());
	}
	
	@Override
	public List<Task> getTasksByVersionPath(String repositoryUuid, IBranchPath versionPath, boolean includePromoted) {
		checkNotNull(repositoryUuid, "Repository UUID may not be null.");
		checkNotNull(versionPath, "Version branch path may not be null.");
		
		final BooleanQuery taskQuery = new BooleanQuery(true);
		taskQuery.add(new PrefixQuery(new Term(FIELD_BRANCH_PATH_ENTRY, getPathEntry(repositoryUuid, versionPath))), Occur.MUST);
		
		if (!includePromoted) {
			taskQuery.add(new TermQuery(new Term(FIELD_IS_CLOSED, "1")), Occur.MUST_NOT);
		}
		
		IndexSearcher searcher = null;
		
		try {
			
			searcher = manager.acquire();
			final ImmutableList.Builder<Task> resultBuilder = ImmutableList.builder();
			final int numDocs = searcher.getIndexReader().numDocs();
			
			if (numDocs > 0) {
				final TopDocs results = searcher.search(taskQuery, numDocs);
				
				for (int i = 0; i < results.scoreDocs.length; i++) {
					final Document doc = searcher.doc(results.scoreDocs[i].doc);
					final String taskId = doc.get(FIELD_TASK_ID);
					final Task task = getTaskFromDoc(taskId, doc);
					
					resultBuilder.add(task);
				}
			}
			
			return resultBuilder.build();
			
		} catch (IOException e) {
			throw new IndexException(e);
		} finally {
			
			if (null != searcher) {
				try {
					manager.release(searcher);
				} catch (IOException e) {
					throw new IndexException(e);
				}
			}
		}
	}
	
	@Override
	public boolean exists(final String taskId) {
		return documentExists(Mappings.newQuery()
				.field(FIELD_TASK_ID, taskId)
				.matchAll());
	}

	private boolean documentExists(final Query taskQuery) {
		
		IndexSearcher searcher = null;
		final TopDocs results;

		try {
			searcher = manager.acquire();
			results = searcher.search(taskQuery, 1);
		} catch (final IOException e) {
			throw new IndexException(e);
 		} finally {
			if (null != searcher) {
				try {
					manager.release(searcher);
				} catch (final IOException e) {
					throw new IndexException(e);
				}
			}
		}

		return results.totalHits > 0;
	}

	@Override
	public Task createOrUpdate(final String taskId, final boolean promoted, final IBranchPathMap taskBranchPathMap, final String contextId, final String repositoryUrl, final String description, final TaskScenario scenario) {
		insertOrUpdate(taskId, promoted, taskBranchPathMap, contextId, repositoryUrl, description, scenario);
		return getTask(taskId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.tasks.ITaskStateManager#getTaskBranchPathMap(java.lang.String)
	 */
	@Override
	public TaskBranchPathMap getTaskBranchPathMap(final String taskId) {

		try {

			final Document document = getTaskDocument(taskId);
			
			if (null == document) {
				return null;
			}
			
			return createNewTaskBranchPathMap(document);

		} catch (final IOException e) {
			throw new IndexException(e);
		}
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.tasks.ITaskStateManager#setUserVersionConfiguration(java.lang.String, com.b2international.snowowl.datastore.IBranchPathMap)
	 */
	@Override
	public Throwable setUserVersionConfiguration(final String userId, final IBranchPathMap branchPathMap) {
		
		try {
			
			Preconditions.checkNotNull(userId, "User ID argument cannot be null.");
			Preconditions.checkNotNull(branchPathMap, "Branch path map argument cannot be null.");
			
			final Document document = new Document();
			document.add(new StringField(FIELD_VERSION_CONFIGURATION_USER_ID, userId, Store.NO));
			final Set<String> repositoryUuids = ApplicationContext.getInstance().getService(ICDOConnectionManager.class).uuidKeySet();
			for (final String uuid : repositoryUuids) {
				document.add(new StoredField(FIELD_BRANCH_PATH_ENTRY, uuid + ":" + branchPathMap.getBranchPath(uuid).getPath()));
			}
			
			writer.deleteDocuments(new TermQuery(new Term(FIELD_VERSION_CONFIGURATION_USER_ID, userId)));
			writer.addDocument(document);
			commit();
		
			return null;
			
		} catch (final IOException e) { 
			
			return new IndexException("Failed to update version configuration for user: " + userId,	 e);
			
		} catch (final Throwable t) {
			return t;
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.tasks.ITaskStateManager#getBranchPathMapConfiguration(java.lang.String, boolean)
	 */
	@Override
	public IBranchPathMap getBranchPathMapConfiguration(final String userId, final boolean taskAware) {
		
		Preconditions.checkNotNull(userId, "User ID argument cannot be null.");
		IndexSearcher searcher = null;
		
		try {

			searcher = manager.acquire();
			final TopDocs topDocs = searcher.search(new TermQuery(new Term(FIELD_VERSION_CONFIGURATION_USER_ID, userId)), 1);
			
			if (IndexUtils.isEmpty(topDocs)) {
				return new UserBranchPathMap();
			}
			
			final UserBranchPathMap localMap = new UserBranchPathMap();
			
			final Document document = searcher.doc(topDocs.scoreDocs[0].doc);
			
			for (final String branchPathEntry : document.getValues(FIELD_BRANCH_PATH_ENTRY)) {
				final String[] parts = branchPathEntry.split(":", 2);
				localMap.putBranchPath(parts[0], BranchPathUtils.createPath(parts[1]));
			}
			
			if (taskAware) {
			
				final String taskId = getActiveTaskId(userId);
				//user has an active task
				if (!StringUtils.isEmpty(taskId)) {
					
					final TaskBranchPathMap taskBranchPathMap = getTaskBranchPathMap(taskId);
					for (final Entry<String, IBranchPath> entry : taskBranchPathMap.getLockedEntries().entrySet()) {
						
						localMap.putBranchPath(entry.getKey(), entry.getValue());
						
					}
					
				}
			
			}
			
			return localMap;
			
		} catch (final IOException e) {
			throw new IndexException("Failed to retrieve version configuration for user: " + userId, e);
		} finally {
			if (null != searcher) {
				try {
					manager.release(searcher);
				} catch (final IOException e) {
					throw new IndexException(e);
				}
			}
		}
	}
	
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.tasks.ITaskStateManager#activateTask(java.lang.String, java.lang.String)
	 */
	@Override
	public Throwable activateTask(final String taskId, final String userId) {
		
		try {
			
			Preconditions.checkNotNull(taskId, "Task ID argument cannot be null.");
			Preconditions.checkNotNull(userId, "User ID argument cannot be null.");
			
			deleteUserIdTaskIdDoc(userId, false);
			addUserIdTaskIdDoc(taskId, userId);
			
			return null;
			
		} catch (final Throwable t) {
			return t;
		}
		
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.tasks.ITaskStateManager#deactiavteActiveTask(java.lang.String)
	 */
	@Override
	public Throwable deactiavteActiveTask(final String userId) {
		
		try {
			
			Preconditions.checkNotNull(userId, "User ID argument cannot be null.");
			deleteUserIdTaskIdDoc(userId, true);

			return null;
			
		} catch (final Throwable t) {
			return t;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.tasks.ITaskStateManager#getActiveTaskId(java.lang.String)
	 */
	@Override
	public String getActiveTaskId(final String userId) {
		Preconditions.checkNotNull(userId, "User ID argument cannot be null.");
		return getActiveTaskIdForUser(userId);
	}
	
	private TaskBranchPathMap createNewTaskBranchPathMap(final Document document) {
		
		final Map<String, IBranchPath> localMap = newHashMap();

		for (final String branchPathEntry : document.getValues(FIELD_BRANCH_PATH_ENTRY)) {
			final String[] parts = branchPathEntry.split(":", 3);
			localMap.put(parts[0], BranchPathUtils.createPath(parts[2]));
		}

		return new TaskBranchPathMap(localMap);
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.tasks.ITaskStateManager#getTask(java.lang.String)
	 */
	@Override
	public Task getTask(final String taskId) {

		try {

			//TODO consider multiple repositories (Bugzilla instances)
			final Document doc = getTaskDocument(taskId);
			
			if (null == doc) {
				return null;
			}
			
			return getTaskFromDoc(taskId, doc);

		} catch (final IOException e) {
			throw new IndexException(e);
		}
	}

	private Task getTaskFromDoc(final String taskId, final Document doc) {
		// XXX: the description is not part of the index. Should it be?
		final String taskContextId = doc.get(FIELD_CONTEXT_ID);
		final ITaskContext context = TaskContextManager.INSTANCE.createNewById(taskContextId);
		final String repositoryUrl = doc.get(FIELD_REPOSITORY_URL);
		final String description = doc.get(FIELD_DESCRIPTION);
		final TaskScenario scenario = TaskScenario.get(IndexUtils.getIntValue(doc.getField(FIELD_SCENARIO_ORDINAL)));
		final boolean promoted = IndexUtils.getBooleanValue(doc.getField(FIELD_IS_CLOSED)); 
		final Task result = new Task(taskId, promoted, createNewTaskBranchPathMap(doc), context, repositoryUrl, description, scenario);

		return result;
	}
	
	private Document getTaskDocument(final String taskId) throws IOException {
		
		final TermQuery taskQuery = new TermQuery(new Term(FIELD_TASK_ID, taskId));
		IndexSearcher searcher = null;
		
		try {
			
			searcher = manager.acquire();
			final TopDocs results = searcher.search(taskQuery, 1);
		
			if (results.totalHits > 1) {
				throw new IllegalStateException(MessageFormat.format("Multiple task state documents found for task {0}.", taskId));
			}
		
			if (results.totalHits < 1) {
				return null;
			}
		
			return searcher.doc(results.scoreDocs[0].doc);
			
		} finally {
			
			if (null != searcher) {
				manager.release(searcher);
			}
		}
	}

	@Override
	public void broadcastStateChange(final String address, final String taskId, final String initiatingUserId,
			final String trigger) {
		LOGGER.debug(MessageFormat.format("Change for task #{0} - triggered by {1} of user {2} - sent to address {3}",
				taskId, trigger, initiatingUserId, address));
		eventBus.publish(address, new TaskChangeNotificationMessage(taskId, initiatingUserId, trigger));
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.tasks.ITaskStateManager#endPromote(java.lang.String)
	 */
	@Override
	public synchronized void setClosed(final String taskId, final String initiatingUserId) {

		// Check first if we already got a report about this task
		if (isClosed(taskId)) {
			return;
		}

		final Task existingTask = getTask(taskId);
		final TaskBranchPathMap taskBranchPathMap = existingTask.getTaskBranchPathMap();
		final String contextId = existingTask.getTaskContext().getContextId();
		
		insertOrUpdate(taskId, true, taskBranchPathMap, contextId, existingTask.getRepositoryUrl(), existingTask.getDescription(), existingTask.getScenario());

		if (null != taskBranchPathMap) {
			closeIndexServicesForTask(taskBranchPathMap);
		}

		try {
			PushServerService.INSTANCE.push(ITaskStateManager.PROTOCOL_NAME, new TaskClosedNotificationMessage(taskId, initiatingUserId));
		} catch (final PushServiceException e) {
			LOGGER.error("Could not push task close notification to recipients.", e);
		}
	}

	@GuardedBy("taskLock")
	private void closeIndexServicesForTask(final TaskBranchPathMap branchPathMap) {

		for (final Entry<String, IBranchPath> entry : branchPathMap.getLockedEntries().entrySet()) {
			
			final String repositoryUuid = entry.getKey();
			final IIndexUpdater<IIndexEntry> indexService = IndexServerServiceManager.INSTANCE.getByUuid(repositoryUuid);
			indexService.inactiveClose(branchPathMap.getBranchPath(repositoryUuid));
			
		}
		
	}

	@Nullable private String getActiveTaskIdForUser(final String userId) {
		
		IndexSearcher searcher = null;
		
		try {

			searcher = manager.acquire();
			final TermQuery taskQuery = new TermQuery(new Term(FIELD_USER_ID, userId));
			final TopDocs results = searcher.search(taskQuery, 1);
			
			if (IndexUtils.isEmpty(results)) {
				return null;
			}
			
			return searcher.doc(results.scoreDocs[0].doc).get(FIELD_TASK_ID);
		
		} catch (final IOException e) {
			throw new IndexException("Failed to get activate task ID for user: " + userId, e);
 		} finally {
			if (null != searcher) {
				try {
					manager.release(searcher);
				} catch (final IOException e) {
					throw new IndexException(e);
				}
			}
		}
	}
	
	private void deleteUserIdTaskIdDoc(final String userId, final boolean commit) {
		
		try {
			writer.deleteDocuments(new TermQuery(new Term(FIELD_USER_ID, userId)));
			if (commit) {
				commit();
			}
		} catch (final IOException e) {
			throw new IndexException("Failed to deactivate task for user: " + userId, e);
		}
		
	}
	
	private void addUserIdTaskIdDoc(final String taskId, final String userId) {
		
		final Document document = new Document();
		document.add(new StringField(FIELD_USER_ID, userId, Store.NO));
		document.add(new StoredField(FIELD_TASK_ID, taskId));
		
		try {
			writer.addDocument(document);
			commit();
		} catch (final IOException e) {
			throw new IndexException("Failed to activate Task " + taskId + " for user: " + userId, e);
		}
		
	}
	
	private void insertOrUpdate(final String taskId, final boolean promoted, final IBranchPathMap taskBranchPathMap, final String contextId, final String repositoryUrl, 
			final String description, final TaskScenario scenario) {

		final Document document = Mappings.doc()
				.field(FIELD_TASK_ID, taskId)
				.field(FIELD_IS_CLOSED, promoted ? "1" : "0")
				.field(FIELD_CONTEXT_ID, contextId)
				.field(FIELD_REPOSITORY_URL, repositoryUrl)
				.field(FIELD_DESCRIPTION, description)
				.field(FIELD_SCENARIO_ORDINAL, scenario.ordinal())
				.build();

		if (null != taskBranchPathMap) {
			updateBranchPathMapFields(taskBranchPathMap, document);
		}

		final Term updateTerm = new Term(FIELD_TASK_ID, taskId);

		try {
			writer.updateDocument(updateTerm, document);
			commit();
		} catch (final IOException e) {
			throw new IndexException(e);
		}
	}

	private void updateBranchPathMapFields(final IBranchPathMap taskBranchPathMap, final Document document) {
		
		// XXX: only store entries which were explicitly set for this task (getLockedEntries provides us this information)
		for (final Entry<String, IBranchPath> entry : taskBranchPathMap.getLockedEntries().entrySet()) {
			document.add(new StringField(FIELD_BRANCH_PATH_ENTRY, getPathEntry(entry.getKey(), entry.getValue()), Store.YES));
		}
	}

	private String getPathEntry(final String repositoryUuid, final IBranchPath branchPath) {
		return MessageFormat.format("{0}:{1}:{2}", repositoryUuid, getVersionPath(branchPath), branchPath);
	}

	private String getVersionPath(final IBranchPath branchPath) {
		return BranchPathUtils.isMain(branchPath) ? IBranchPath.MAIN_BRANCH : branchPath.getParentPath();
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.server.index.SingleDirectoryIndexServerService#doDispose()
	 */
	@Override
	protected void doDispose() {

		cleanupTask.cancel();
		usageTimeCache.invalidateAll();
		usageTimeCache.cleanUp();

		super.doDispose();
	}
}
