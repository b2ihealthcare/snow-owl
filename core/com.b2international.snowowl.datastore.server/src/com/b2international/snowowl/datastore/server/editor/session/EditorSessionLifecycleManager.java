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
package com.b2international.snowowl.datastore.server.editor.session;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.server.EditingContextFactory;
import com.b2international.snowowl.datastore.server.editor.operation.executor.OperationExecutorFactory;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;

/**
 * Generic {@link EditorSession editor session} lifecycle manager.
 * 
 * @param <S>: the editor session type
 * 
 * @since 2.9
 */
public class EditorSessionLifecycleManager {

	private static final Logger LOG = LoggerFactory.getLogger(EditorSessionLifecycleManager.class);

	private final ConcurrentMap<UUID, EditorSession<?>> sessionMap = new MapMaker().makeMap();
	private final EditorSessionFactory sessionFactory;

	private Optional<ILifecycleChangeHandler> lifecycleChangeHandler = Optional.absent();
	private boolean disposed;

	public EditorSessionLifecycleManager(final EditorSessionFactory sessionFactory) {
		Preconditions.checkNotNull(sessionFactory, "sessionFactory");
		this.sessionFactory = sessionFactory;
	}

	public void setLifecycleChangeHandler(final ILifecycleChangeHandler lifecycleChangeHandler) {
		this.lifecycleChangeHandler = Optional.fromNullable(lifecycleChangeHandler);
	}

	/**
	 * Creates a new editor session and returns its unique identifier.
	 * 
	 * @param userId
	 *            the user ID
	 * @param valueSetId
	 *            the ID of the edited component
	 * @param branchPathMap
	 *            the branch path map
	 * @return the unique identifier of the editor session
	 */
	public UUID openSession(final String userId, final String editedComponentId, final IBranchPathMap branchPathMap, final EditingContextFactory contextFactory,
			final OperationExecutorFactory executorFactory) {
		checkArgument(!StringUtils.isEmpty(userId), "userId");
		checkArgument(!StringUtils.isEmpty(editedComponentId), "editedComponentId");
		checkNotNull(branchPathMap, "branchPathMap");
		checkNotNull(executorFactory, "executorFactory");

		final UUID uuid = UUID.randomUUID();
		LOG.trace("[" + userId + ", " + uuid + ", " + branchPathMap + "] openSession()");
		final EditorSession<?> session = sessionFactory.createEditorSession(uuid, userId, editedComponentId, branchPathMap, contextFactory, executorFactory);
		final EditorSession<?> existingSession = sessionMap.putIfAbsent(uuid, session);
		if (existingSession != null) {
			throw new IllegalStateException("Session with UUID " + uuid + " already exists.");
		} else {
			session.init();
		}
		if (lifecycleChangeHandler.isPresent()) {
			lifecycleChangeHandler.get().handleOpen(session);
		}
		return uuid;
	}

	/**
	 * Closes the editor session associated with the specified unique
	 * identifier.
	 * 
	 * @param sessionId
	 */
	public void closeSession(final UUID sessionId) {
		checkNotNull(sessionId, "sessionId");
		LOG.trace("[" + sessionId + "] closeSession()");
		if (lifecycleChangeHandler.isPresent()) {
			lifecycleChangeHandler.get().handleClose(sessionId);
		}
		final EditorSession<?> removedSession = sessionMap.remove(sessionId);
		if (removedSession != null) {
			removedSession.dispose();
		} else {
			LOG.error("Can't close non-existing editor session: " + sessionId);
		}
	}

	public void dispose() {
		LOG.trace("dispose()");
		disposed = true;
		if (lifecycleChangeHandler.isPresent()) {
			lifecycleChangeHandler.get().dispose();
		}
		for (final EditorSession<?> editorSession : sessionMap.values()) {
			editorSession.dispose();
		}
		sessionMap.clear();
	}

	public boolean isDisposed() {
		return disposed;
	}

	/**
	 * Returns the editor session associated with the specified ID, or null if
	 * it doesn't exist.
	 * 
	 * @param sessionId
	 * @return
	 */
	public EditorSession<?> getSession(final UUID sessionId) {
		return sessionMap.get(sessionId);
	}

}